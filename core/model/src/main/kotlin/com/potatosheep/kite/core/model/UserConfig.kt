package com.potatosheep.kite.core.model

data class UserConfig(
    val instance: String,
    val shouldHideOnboarding: Boolean,
    val showNsfw: Boolean,
    val blurNsfw: Boolean,

    /*
     * In a post listing page, Redlib uses a thumbnail for gallery posts. However, when a user
     * views the gallery post itself, ONLY the actual images within the gallery are displayed.
     *
     * To ensure consistency, when a user bookmarks a gallery post, regardless of whether they
     * do so in a post listing screen or a post screen, only the thumbnail link will be saved,
     * NOT an actual image from the gallery. We can achieve this by taking advantage of the fact
     * that a post screen is almost always preceded by a post listing screen, where we can extract
     * the thumbnail link and pass it on to the post screen.
     *
     * However, if a user were to open a gallery post link from an external app (e.g., a browser)
     * OR via a comment card in a User screen, there is no way to retrieve the gallery thumbnail.
     * Users who save gallery posts this way can enable this option to have the app retrieve the
     * gallery thumbnail.
     *
     * This option is disabled by default as it results in an additional, arguably unnecessary,
     * request being made.
     */
    // TODO: Implement this.
    // val resolveThumbnail: Boolean

    val shouldUseCustomInstance: Boolean,
    val customInstance: String,
    val blurSpoiler: Boolean
)