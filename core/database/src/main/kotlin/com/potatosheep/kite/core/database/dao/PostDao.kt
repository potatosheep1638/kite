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

    @Query("SELECT * FROM post ORDER BY time_created DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM post WHERE title LIKE '%' || :query || '%' ORDER BY time_created DESC")
    fun searchPostsByTitle(query: String): Flow<List<PostEntity>>

    @Query("SELECT COUNT(1) FROM post WHERE post_id = :id")
    suspend fun getPostCount(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(postEntities: List<PostEntity>)

    @Delete
    suspend fun deletePost(postEntity: PostEntity)
}