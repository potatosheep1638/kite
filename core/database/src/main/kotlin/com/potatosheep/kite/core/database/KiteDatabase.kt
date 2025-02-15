package com.potatosheep.kite.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.potatosheep.kite.core.database.dao.PostDao
import com.potatosheep.kite.core.database.dao.SubredditDao
import com.potatosheep.kite.core.database.entity.PostEntity
import com.potatosheep.kite.core.database.entity.SubredditEntity

@Database(entities = [PostEntity::class, SubredditEntity::class], version = 1)
@TypeConverters(
    MediaLinksConverter::class,
    FlairComponentsConverter::class,
    InstantConverter::class
)
abstract class KiteDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun subredditDao(): SubredditDao
}