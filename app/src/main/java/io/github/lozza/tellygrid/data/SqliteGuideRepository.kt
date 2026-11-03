package io.github.lozza.tellygrid.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

/**
 * SQLite-backed EPG reader for the hosted `startca.db` feed (written by the
 * addon's `sd_to_xmltv.py` alongside the XMLTV file). Replaces the 27MB XML
 * pull+parse on launch — the tank's 1GB RAM couldn't hold the parsed model
 * plus UI and was getting OOM-killed mid-scroll. Here the app downloads a
 * few-MB database and runs one indexed SELECT per channel; nothing else is
 * materialised.
 *
 * Schema (producer side, epoch seconds):
 *   channels(station_id PK, lcn, label)
 *   programmes(station_id, start, stop, title, subtitle, description,
 *              episode_label, icon, PK(station_id,start))
 */
class SqliteGuideRepository(private val context: Context) {

    /** Fetch the feed to a temp file then rename — never serve a half-written db. */
    private fun download(feedUrl: String): File {
        val tmp = File(context.cacheDir, "sky-guide-feed.db.tmp")
        val out = File(context.cacheDir, "sky-guide-feed.db")
        val connection = URL(feedUrl).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 15_000
            check(connection.responseCode in 200..299) { "EPG server returned ${connection.responseCode}" }
            tmp.outputStream().use { os -> connection.inputStream.use { it.copyTo(os) } }
        } finally {
            connection.disconnect()
        }
        tmp.renameTo(out)
        return out
    }

    private val cacheDb get() = File(context.cacheDir, "sky-guide-feed.db")

    /**
     * Read channels out of the already-downloaded db (no network). Lets the
     * guide paint instantly on startup while a fresh copy downloads.
     */
    fun loadFromCache(now: Instant = Instant.now()): List<GuideChannel>? {
        if (!cacheDb.exists()) return null
        return runCatching { query(cacheDb, now) }.getOrNull()
    }

    /** Same as [loadFromCache] but only within the feed's 30-minute freshness window. */
    fun loadFromCacheFresh(now: Instant = Instant.now()): List<GuideChannel>? {
        if (!cacheDb.exists()) return null
        val fresh = System.currentTimeMillis() - cacheDb.lastModified() < 30 * 60 * 1000L
        if (!fresh) return null
        return loadFromCache(now)
    }

    fun load(feedUrl: String, now: Instant = Instant.now()): List<GuideChannel> =
        query(download(feedUrl), now)

    private fun query(dbFile: File, now: Instant): List<GuideChannel> {
        val db = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
        val programmesByStation = mutableMapOf<String, MutableList<Programme>>()
        try {
            // Per-station queries (83 of them, all indexed) instead of one
            // bulk scan: the bulk SELECT materialised ~49k Programme objects
            // before assembleGuideChannels could apply its per-channel
            // take(96), and the tank's LMK was killing foreground apps under
            // the resulting peak. Querying each catalog channel's window
            // keeps live allocations at one channel's slice.
            for (catalog in StartTvChannelCatalog.channels) {
                val stationId = catalog.xmlTvId
                val list = mutableListOf<Programme>()
                db.rawQuery(
                    """SELECT start, stop, title, subtitle, description,
                              episode_label, icon
                         FROM programmes
                        WHERE station_id = ? AND stop >= ?
                        ORDER BY start LIMIT 96""",
                    arrayOf(stationId, now.epochSecond.toString()),
                ).use { c ->
                    while (c.moveToNext()) {
                        val start = Instant.ofEpochSecond(c.getLong(0))
                        val stop = Instant.ofEpochSecond(c.getLong(1))
                        list.add(
                            Programme(
                                id = "$stationId-${start.epochSecond}",
                                title = c.getString(2)?.ifBlank { null } ?: "Programme information unavailable",
                                synopsis = c.getString(4) ?: "",
                                startsAt = start,
                                endsAt = stop,
                                // proxy serves plain http on the LAN
                                imageUri = c.getString(6)?.takeIf { it.startsWith("http") },
                                episodeLabel = c.getString(5)?.takeIf { it.isNotBlank() },
                                contentUri = null,
                            ),
                        )
                    }
                }
                if (list.isNotEmpty()) programmesByStation[stationId] = list
            }
        } finally {
            db.close()
        }
        return assembleGuideChannels(programmesByStation, now)
    }
}
