package io.github.lozza.tellygrid.data

import android.content.Context
import io.github.lozza.tellygrid.BuildConfig
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class GuideLoadResult(
    val channels: List<GuideChannel>,
    val status: String,
)

class SkyGuideRepository(private val context: Context) {
    private val parser = XmlTvGuideParser()
    private val sqliteGuide = SqliteGuideRepository(context)
    private val cacheFile = File(context.filesDir, "sky-guide.xml")
    private val demoFallback by lazy { SampleGuideRepository().channels() }
    private val plutoGuide = PlutoGuideRepository(context)

    fun load(): GuideLoadResult {
        val bundled by lazy { context.assets.open("sky-guide.xml").use(parser::parse) }
        val now = System.currentTimeMillis()
        val cachedFresh = cacheFile.exists() && now - cacheFile.lastModified() < 30 * 60 * 1000L
        val cachedResult = if (cachedFresh) {
            runCatching { cacheFile.inputStream().use(parser::parse) }.getOrNull()
                ?.takeIf(::hasUsableSkyListings)
                ?.let { channels ->
                    GuideLoadResult(
                        mergeGuideSchedules(channels, bundled, demoFallback),
                        "Saved Sky listings",
                    )
                }
        } else null
        val isDbFeed = BuildConfig.EPG_URL.substringBefore('?').endsWith(".db", ignoreCase = true)
        // Fresh-enough cached db (same 30-min window as the xml path): serve
        // straight from disk. Stale cache still gets painted instantly by
        // loadCachedDbChannels() while this download runs.
        val cachedDbChannels = if (isDbFeed) sqliteGuide.loadFromCacheFresh() else null
        // Live feed attempt (sqlite or xmltv, chosen by URL suffix). A null
        // result falls through to the cache->bundled->demo chain below.
        val liveFeed: GuideLoadResult? = if (BuildConfig.EPG_URL.isBlank()) null
        else if (isDbFeed) {
            // SQLite feed: indexed per-channel queries, no full-doc parse.
            // Fresh cache renders without a network hit; stale cache falls
            // through to the download while early paint covers the wait.
            (cachedDbChannels?.let { channels ->
                if (hasUsableSkyListings(channels)) {
                    GuideLoadResult(
                        mergeGuideSchedules(channels, bundled, demoFallback),
                        "Saved listings (db)",
                    )
                } else {
                    null
                }
            }) ?: runCatching { sqliteGuide.load(BuildConfig.EPG_URL) }.also {
                android.util.Log.i("StartTvGuide", "db-feed load url=${BuildConfig.EPG_URL} ok=${it.isSuccess} err=${it.exceptionOrNull()?.let{e->"${e.javaClass.simpleName}:${e.message}"}}")
            }.getOrNull()?.let { channels ->
                val usable = hasUsableSkyListings(channels)
                android.util.Log.i("StartTvGuide", "db-feed channels=${channels.size} usable=$usable")
                if (usable) GuideLoadResult(
                    mergeGuideSchedules(channels, bundled, demoFallback),
                    "Live listings (db)",
                ) else null
            }
        } else {
            cachedResult ?: runCatching { download(BuildConfig.EPG_URL) }.also {
                android.util.Log.i("StartTvGuide", "download url=${BuildConfig.EPG_URL} ok=${it.isSuccess} err=${it.exceptionOrNull()?.message}")
            }.getOrNull()?.let { bytes ->
                val channels = runCatching { bytes.inputStream().use(parser::parse) }.also {
                    android.util.Log.i("StartTvGuide", "parse bytes=${bytes.size} ok=${it.isSuccess} err=${it.exceptionOrNull()?.let{e->"${e.javaClass.simpleName}:${e.message}"}}")
                }.getOrNull() ?: return@let null
                val usable = hasUsableSkyListings(channels)
                android.util.Log.i("StartTvGuide", "feed parsed channels=${channels.size} usable=$usable withListings=${channels.count{c->c.programmes.any{!it.id.endsWith("-listings-unavailable")}}}")
                if (usable) {
                    cacheFile.writeBytes(bytes)
                    GuideLoadResult(
                        mergeGuideSchedules(channels, bundled, demoFallback),
                        "Live Sky listings",
                    )
                } else {
                    null
                }
            }
        }
        val base: GuideLoadResult = liveFeed ?: if (cacheFile.exists()) {
            runCatching { cacheFile.inputStream().use(parser::parse) }.getOrNull()?.let { channels ->
                if (hasUsableSkyListings(channels)) {
                    GuideLoadResult(
                        mergeGuideSchedules(channels, bundled, demoFallback),
                        "Saved Sky listings — refresh unavailable",
                    )
                } else {
                    null
                }
            }
        } else {
            null
        } ?: if (hasUsableSkyListings(bundled)) {
            GuideLoadResult(
                mergeGuideSchedules(bundled, emptyList(), demoFallback),
                "Sky listings bundled with this build",
            )
        } else {
            // An APK can outlive its two-day snapshot. Keep the guide usable with
            // relative demo rows rather than rendering a full page of dead
            // "Listings unavailable" placeholders.
            GuideLoadResult(
            SampleGuideRepository().channels(),
            "Sky listings unavailable — showing demo data",
            )
        }

        val pluto = plutoGuide.enrich(base.channels)
        return base.copy(
            channels = pluto.channels,
            status = if (pluto.refreshedChannelCount > 0) {
                "${base.status} · Pluto TV listings updated"
            } else {
                base.status
            },
        )
    }

    /**
     * Instant paint for the db feed: channels from the on-disk sqlite copy,
     * before any network work. Safe to call on a background thread; returns
     * null when no cached db exists yet.
     */
    fun loadCachedDbChannels(): List<GuideChannel>? {
        if (!BuildConfig.EPG_URL.substringBefore('?').endsWith(".db", ignoreCase = true)) return null
        return sqliteGuide.loadFromCache()?.takeIf(::hasUsableSkyListings)
    }

    private fun download(feedUrl: String): ByteArray {
        val connection = URL(feedUrl).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", "application/xml, text/xml")
            check(connection.responseCode in 200..299) { "EPG server returned ${connection.responseCode}" }
            connection.inputStream.use { it.readBytes() }
        } finally {
            connection.disconnect()
        }
    }
}

internal fun hasUsableListings(channels: List<GuideChannel>): Boolean =
    channels.any { channel ->
        channel.programmes.any { programme ->
            !programme.id.endsWith("-listings-unavailable")
        }
    }

/**
 * A feed is only a valid Sky guide when it contains current data for the
 * main terrestrial rows. Pluto/FAST rows can refresh independently and must
 * not make a mostly-empty Sky feed look healthy.
 */
internal fun hasUsableSkyListings(channels: List<GuideChannel>): Boolean {
    // Start.ca build: the feed is usable when a meaningful share of catalog
    // channels carry real listings (not the listings-unavailable placeholder).
    // Replaces the upstream BBC/ITV core-id check, which can never match a
    // non-UK catalog.
    val withListings = channels.count { channel ->
        channel.programmes.any { !it.id.endsWith("-listings-unavailable") }
    }
    return channels.isNotEmpty() && withListings >= 3
}

/** Keeps a fresh hosted feed authoritative while filling newly-added rows from the APK. */
internal fun mergeGuideSchedules(
    primary: List<GuideChannel>,
    bundledFallback: List<GuideChannel>,
    demoFallback: List<GuideChannel> = emptyList(),
): List<GuideChannel> {
    val bundledById = bundledFallback.associateBy { it.id }
    val demoById = demoFallback.associateBy { it.id }
    return primary.map { channel ->
        val hasListings = channel.programmes.any { !it.id.endsWith("-listings-unavailable") }
        val bundled = bundledById[channel.id]
        val bundledHasListings = bundled?.programmes?.any { !it.id.endsWith("-listings-unavailable") } == true
        val demo = demoById[channel.id]
        val demoHasListings = demo?.programmes?.any { !it.id.endsWith("-listings-unavailable") } == true
        when {
            hasListings -> channel
            bundledHasListings -> channel.copy(programmes = bundled!!.programmes)
            demoHasListings -> channel.copy(programmes = demo!!.programmes)
            else -> channel
        }
    }
}
