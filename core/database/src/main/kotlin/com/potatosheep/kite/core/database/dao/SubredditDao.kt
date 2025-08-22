package com.potatosheep.kite.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.potatosheep.kite.core.database.entity.SubredditEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubredditDao {

    @Query("SELECT * FROM subreddit ORDER BY subreddit_name")
    fun getAllSubreddits(): Flow<List<SubredditEntity>>

    @Query("SELECT * FROM subreddit WHERE subreddit_name LIKE :query")
    fun searchSubreddits(query: String): Flow<List<SubredditEntity>>

    @Query("SELECT COUNT(1) FROM subreddit WHERE subreddit_name = :subredditName")
    fun getSubredditCount(subredditName: String) : Flow<Int>

    @Insert
    suspend fun insertSubreddit(subredditEntity: SubredditEntity)

    @Delete
    suspend fun deleteSubreddit(subredditEntity: SubredditEntity)
}