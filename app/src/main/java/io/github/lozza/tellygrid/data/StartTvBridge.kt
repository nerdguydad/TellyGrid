package io.github.lozza.tellygrid.data

import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Remote-adb zap bridge for Start.ca Start TV on Fire TV.
 *
 * The companion `starttv-bridge` service runs on the home-automation host and
 * drives `input keyevent` digits / profile-gate navigation on the stick over
 * the already-authorized adb connection. TellyGrid only needs to POST the
 * channel number — no on-device shell, pairing, or accessibility injection.
 *
 * The bridge chooses which physical stick to tune from the HTTP source IP, so
 * no per-device configuration is needed. If the bridge is unreachable the
 * caller falls back to the StartTvContract deep-link launch, which still tunes
 * correctly on a cold start of the provider app.
 */
object StartTvBridge {
    private const val TAG = "StartTvBridge"
    private const val CONNECT_TIMEOUT_MS = 4000
    private const val READ_TIMEOUT_MS = 45000 // a cold start + profile pick can take ~10s

    /**
     * POST {"channel": N} to the bridge's /zap endpoint on a background thread.
     * Result is reported through [onResult]; failures are non-fatal (the
     * deep-link launch is the caller's fallback).
     */
    fun zap(bridgeBase: String, channel: Int, onResult: (Boolean) -> Unit = {}) {
        val base = bridgeBase.trimEnd('/')
        if (base.isEmpty()) {
            onResult(false)
            return
        }
        thread(name = "starttv-zap") {
            val res = runCatching { post(base, channel) }
            val ok = res.getOrDefault(false)
            Log.i(TAG, "zap channel=$channel base=$base ok=$ok err=${res.exceptionOrNull()?.let { "${it.javaClass.simpleName}:${it.message}" }}")
            onResult(ok)
        }
    }

    private fun post(base: String, channel: Int): Boolean {
        val conn = (URL("$base/zap").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        return try {
            OutputStreamWriter(conn.outputStream).use { it.write("""{"channel":$channel}""") }
            conn.responseCode in 200..299
        } finally {
            conn.disconnect()
        }
    }
}
