package com.potatosheep.kite.core.data.model

import com.potatosheep.kite.core.database.entity.PostEntity
import com.potatosheep.kite.core.model.FlairComponent
import com.potatosheep.kite.core.model.MediaLink
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.squareup.moshi.JsonClass
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@JsonClass(generateAdapter = true)
data class PostExport(
    val id: String,
    val subredditName: String,
    val title: String,
    val userName: String,
    val mediaLinks: List<MediaLink>,
    val textContent: String,
    val timePosted: Long,
    val upvoteCount: Int,
    val commentCount: Int,
    val flair: List<FlairComponent>,
    val flairId: String,
    val isNsfw: Boolean,
    val isSpoiler: Boolean,
    val timeCreated: Long
)

fun PostEntity.toPostExport() = PostExport(
    id = id,
    title = title,
    subredditName = subredditName,
    userName = userName,
    mediaLinks = mediaLinks,
    textContent = textContent,
    timePosted = timePosted.toEpochMilliseconds(),
    upvoteCount = upvoteCount,
    commentCount = commentCount,
    flair = flair,
    flairId = flairId,
    isNsfw = isNsfw,
    isSpoiler = isSpoiler,
    timeCreated = timeCreated.toEpochMilliseconds()
)

fun PostExport.toEntity() = PostEntity(
    id = id,
    title = title,
    subredditName = subredditName,
    userName = userName,
    mediaLinks = mediaLinks,
    textContent = textContent,
    timePosted = Instant.fromEpochMilliseconds(timePosted),
    upvoteCount = upvoteCount,
    commentCount = commentCount,
    flair = flair,
    flairId = flairId,
    isNsfw = isNsfw,
    isSpoiler = isSpoiler,
    timeCreated = Instant.fromEpochMilliseconds(timeCreated),
)

fun Post.toEntity() = PostEntity(
    id = id,
    title = title,
    subredditName = subredditName,
    userName = userName,
    mediaLinks = mediaLinks
        .map { mediaLink ->
            when (mediaLink.mediaType) {
                MediaType.ARTICLE_THUMBNAIL, MediaType.ARTICLE_LINK -> {
                    mediaLink
                }

                else -> {
                    val linkPath = mediaLink.link.split("/")

                    if (mediaLink.link.isEmpty()) {
                        mediaLink
                    } else {
                        mediaLink.copy(
                            link = "/" + linkPath.subList(3, linkPath.size).joinToString("/"),
                            mediaType = mediaLink.mediaType
                        )
                    }
                }
            }
        },
    textContent = textContent,
    timePosted = timePosted,
    upvoteCount = upvoteCount,
    commentCount = commentCount,
    flair = flair,
    flairId = flairId,
    isNsfw = isNsfw,
    isSpoiler = isSpoiler,
    timeCreated = Clock.System.now()
)