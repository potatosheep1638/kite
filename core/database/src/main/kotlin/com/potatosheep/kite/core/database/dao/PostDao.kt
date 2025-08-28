package com.potatosheep.kite.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.potatosheep.kite.core.database.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query(
        "SELECT * FROM post " +
                "WHERE (:showNsfw = 0 AND is_nsfw = :showNsfw) OR (:showNsfw = 1 AND 1 = 1) " +
                "ORDER BY time_created DESC"
    )
    fun getAll(showNsfw: Boolean): Flow<List<PostEntity>>

    @Query(
        "SELECT * FROM post " +
                "WHERE ((:showNsfw = 0 AND is_nsfw = :showNsfw) OR (:showNsfw = 1 AND 1 = 1)) " +
                "AND title LIKE '%' || :query || '%' " +
                "ORDER BY time_created DESC"
    )
    fun searchPostsByTitle(
        query: String,
        showNsfw: Boolean
    ): Flow<List<PostEntity>>

    @Query("SELECT COUNT(1) FROM post WHERE post_id = :id")
    suspend fun getPostCount(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(postEntities: List<PostEntity>)

    @Delete
    suspend fun deletePost(postEntity: PostEntity)
}