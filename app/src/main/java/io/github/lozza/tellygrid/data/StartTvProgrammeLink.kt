package io.github.lozza.tellygrid.data

/**
 * Start.ca Start TV (Ericsson MediaFirst UC) deep-link contract on Fire TV.
 *
 * Live-channel tuning is driven by the platform's search-suggestion extra,
 * `intent_extra_data_key` (= SearchManager.EXTRA_DATA_KEY), sent to the single
 * exported activity `com.ericsson.MediaFirstUC.tv.MainActivity`. The extra is a
 * JSON payload consumed by `com.ericsson.MediaFirstUC.media.e` (LaunchMediaItem):
 *
 *   {"actionType":"tune","params":{"Id":"LIVE$STARTCA-<numeric>-<SLUG>","ItemType":"on_now"}}
 *
 * `Id` is the provider's media identity; `ItemType` `on_now` selects the
 * current live airing. The intent is honoured at the web-ready/onResume gate
 * (`handleWebReadyNotification`), so it works on a cold start (after the
 * profile-pick) and on a warm re-launch.
 *
 * Some channels use a bare numeric media id (e.g. `LIVE$38283`) rather than the
 * `LIVE$STARTCA-...` form — both are accepted verbatim.
 */
object StartTvContract {
    const val PACKAGE_NAME = "com.tangerineglobal.start.tv"
    const val MAIN_ACTIVITY = "com.ericsson.MediaFirstUC.tv.MainActivity"

    /** Extra key carrying the tune JSON. This is `SearchManager.EXTRA_DATA_KEY`. */
    const val EXTRA_DATA_KEY = "intent_extra_data_key"

    /** Build the tune payload for a media id such as `LIVE$STARTCA-38113-RDIHD`. */
    fun tunePayload(mediaId: String): String =
        """{"actionType":"tune","params":{"Id":"$mediaId","ItemType":"on_now"}}"""
}

object StartTvProgrammeLink {
    /**
     * Build the explicit launch intent that tunes `mediaId` in Start TV.
     * Returns a [ProviderProgrammeIntent] that `AppHandoffLauncher` converts to
     * an Intent via `toIntent()` — equivalent to:
     *
     *   am start -n com.tangerineglobal.start.tv/com.ericsson.MediaFirstUC.tv.MainActivity \
     *     --es intent_extra_data_key '{"actionType":"tune",...}'
     */
    fun tuneIntentForChannel(mediaId: String): ProviderProgrammeIntent =
        ProviderProgrammeIntent(
            packageName = StartTvContract.PACKAGE_NAME,
            // Explicit component launch; VIEW is required by isComplete() semantics only
            // when a dataUri is present, so MAIN is correct here.
            action = android.content.Intent.ACTION_MAIN,
            componentClassName = StartTvContract.MAIN_ACTIVITY,
            stringExtras = mapOf(
                StartTvContract.EXTRA_DATA_KEY to StartTvContract.tunePayload(mediaId),
            ),
        )
}
