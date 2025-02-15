package com.potatosheep.kite.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.potatosheep.kite.core.model.Subreddit

@Entity(tableName = "subreddit")
data class SubredditEntity(

    @PrimaryKey
    @ColumnInfo(name = "subreddit_name")
    val subredditName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon_link")
    val iconLink: String,
)

// Fields with blanks and zeroes are to be ignored.
fun SubredditEntity.toExternalModel() = Subreddit(
    subredditName = subredditName,
    subscribers = 0,
    activeUsers = 0,
    iconLink = iconLink,
    description = description,
    sidebar = ""
)