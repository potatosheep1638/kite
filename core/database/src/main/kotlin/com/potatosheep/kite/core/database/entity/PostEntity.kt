package com.potatosheep.kite.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.potatosheep.kite.core.model.FlairComponent
import com.potatosheep.kite.core.model.MediaLink
import com.potatosheep.kite.core.model.Post
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

@Entity(
    tableName = "post",
    primaryKeys = ["post_id", "subreddit_name"]
)
data class PostEntity(

    @ColumnInfo(name = "post_id")
    val id: String,

    @ColumnInfo(name = "subreddit_name")
    val subredditName: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "media_links")
    val mediaLinks: List<MediaLink>,

    @ColumnInfo(name = "text_content")
    val textContent: String,

    @ColumnInfo(name = "time_posted")
    val timePosted: Instant,

    @ColumnInfo(name = "upvote_count")
    val upvoteCount: Int,

    @ColumnInfo(name = "comment_count")
    val commentCount: Int,

    @ColumnInfo(name = "flairComponents")
    val flair: List<FlairComponent>,

    @ColumnInfo(name = "flair_id")
    val flairId: String,

    @ColumnInfo(name = "is_nsfw")
    val isNsfw: Boolean,

    @ColumnInfo(name = "is_spoiler")
    val isSpoiler: Boolean,

    @ColumnInfo(name = "time_created")
    val timeCreated: Instant,
)

fun PostEntity.toExternalModel() = Post(
    id = id,
    title = title,
    subredditName = subredditName,
    userName = userName,
    mediaLinks = mediaLinks,
    textContent = textContent,
    timeAgo = timePosted.format(
        DateTimeComponents.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            char(' ')
            year()
        }
    ),
    timePosted = timePosted,
    upvoteCount = upvoteCount,
    commentCount = commentCount,
    flair = flair,
    flairId = flairId,
    isNsfw = isNsfw,
    isSpoiler = isSpoiler
)