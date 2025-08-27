package com.potatosheep.kite.core.data.repo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.common.KiteServices
import com.potatosheep.kite.core.common.Service
import com.potatosheep.kite.core.common.constants.IntentData
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.data.model.PostExport
import com.potatosheep.kite.core.data.model.toEntity
import com.potatosheep.kite.core.data.model.toPostExport
import com.potatosheep.kite.core.database.dao.PostDao
import com.potatosheep.kite.core.database.entity.toExternalModel
import com.potatosheep.kite.core.media.KiteDownloadService
import com.potatosheep.kite.core.media.model.DownloadData
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.model.toExternalModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.ParameterizedType
import javax.inject.Inject

interface PostRepository {
    suspend fun getPost(
        instanceUrl: String,
        subredditName: String,
        postId: String,
        replyId: String? = null,
        isShareLink: Boolean = false,
        findParentComments: Boolean = false
    ): Triple<Post, List<Comment>, String>

    suspend fun getPosts(
        instanceUrl: String,
        sort: String = SortOption.Post.HOT.uri,
        timeframe: String = SortOption.Timeframe.DAY.uri,
        after: String? = null,
        subredditName: String? = null
    ): List<Post>

    suspend fun searchPostsAndSubreddits(
        instanceUrl: String,
        query: String,
        sort: String = SortOption.Search.RELEVANCE.uri,
        timeframe: String = SortOption.Timeframe.ALL.uri,
        after: String? = null,
        subredditName: String? = null
    ): Pair<List<Post>, List<Subreddit>>

    suspend fun checkIfPostHasRecord(postId: String): Int
    fun getSavedPosts(
        query: String,
        showNsfw: Boolean
    ): Flow<List<Post>>
    suspend fun savePost(post: Post)
    suspend fun removeSavedPost(post: Post)
    suspend fun exportSavedPosts(uri: Uri, context: Context)
    suspend fun importSavedPosts(uri: Uri, context: Context)

    suspend fun downloadVideo(
        url: String,
        fileName: String,
        isHLS: Boolean,
        uri: Uri,
        context: Context
    )

    suspend fun downloadImage(
        url: String,
        uri: Uri,
        context: Context
    )
}

internal class DefaultPostRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val postDao: PostDao,
    private val moshi: dagger.Lazy<Moshi>,
    @Service(KiteServices.Download) private val downloadService: LifecycleService,
    @Dispatcher(KiteDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PostRepository {

    override suspend fun getPost(
        instanceUrl: String,
        subredditName: String,
        postId: String,
        replyId: String?,
        isShareLink: Boolean,
        findParentComments: Boolean
    ): Triple<Post, List<Comment>, String> {
        val postAndComments = networkDataSource.getPost(
            instanceUrl = instanceUrl,
            subredditName = subredditName,
            postId = postId,
            replyId = replyId,
            isShareLink = isShareLink,
            findParentComments = findParentComments
        )

        return Triple(
            postAndComments.first.toExternalModel(),
            postAndComments.second.map { it.toExternalModel() },
            postAndComments.third
        )
    }

    override suspend fun getPosts(
        instanceUrl: String,
        sort: String,
        timeframe: String,
        after: String?,
        subredditName: String?
    ): List<Post> {
        val posts = networkDataSource.getPosts(
            instanceUrl = instanceUrl,
            sort = sort,
            timeframe = timeframe,
            after = after,
            subredditName = subredditName
        ).map { it.toExternalModel() }

        return posts
    }

    // TODO: Move this to SearchRepository
    override suspend fun searchPostsAndSubreddits(
        instanceUrl: String,
        query: String,
        sort: String,
        timeframe: String,
        after: String?,
        subredditName: String?
    ): Pair<List<Post>, List<Subreddit>> {
        val postsAndSubreddits = networkDataSource.searchPostsAndSubreddits(
            query = query,
            instanceUrl = instanceUrl,
            sort = sort,
            timeframe = timeframe,
            after = after,
            subredditName = subredditName
        )

        return Pair(
            postsAndSubreddits.first.map { it.toExternalModel() },
            postsAndSubreddits.second.map { it.toExternalModel() }
        )
    }

    override suspend fun checkIfPostHasRecord(postId: String): Int = postDao.getPostCount(postId)

    override fun getSavedPosts(
        query: String,
        showNsfw: Boolean
    ): Flow<List<Post>> {
        return if (query.isEmpty()) {
            postDao.getAll(showNsfw)
                .map { postList ->
                    postList.map { it.toExternalModel() }
                }
        } else {
            postDao.searchPostsByTitle(query, showNsfw)
                .map { postList ->
                    postList.map { it.toExternalModel() }
                }
        }
    }

    override suspend fun savePost(post: Post) = postDao.insertPost(post.toEntity())

    override suspend fun removeSavedPost(post: Post) = postDao.deletePost(post.toEntity())

    override suspend fun exportSavedPosts(uri: Uri, context: Context) {
        val posts = postDao.getAll(true)
            .first()
            .map { it.toPostExport() }

        val type: ParameterizedType =
            Types.newParameterizedType(List::class.java, PostExport::class.java)

        val jsonAdapter: JsonAdapter<List<PostExport>> = moshi.get().adapter(type)

        context.contentResolver.openOutputStream(uri)?.let { outputStream ->
            withContext(ioDispatcher) {
                outputStream.write(jsonAdapter.toJson(posts).toByteArray())
                outputStream.flush()
                outputStream.close()
            }
        }
    }

    override suspend fun importSavedPosts(uri: Uri, context: Context) {
        val type: ParameterizedType =
            Types.newParameterizedType(List::class.java, PostExport::class.java)

        val jsonAdapter: JsonAdapter<List<PostExport>> = moshi.get().adapter(type)

        var rawData = ""

        context.contentResolver.openInputStream(uri)?.let { inputStream ->
            withContext(ioDispatcher) {
                val reader = BufferedReader(InputStreamReader(inputStream))

                while (true) {
                    val line = reader.readLine() ?: break
                    rawData += line
                }
            }
        }

        if (rawData.isNotBlank()) {
            jsonAdapter.fromJson(rawData)?.let { posts ->
                postDao.insertPosts(posts.map { it.toEntity() })
            }
        }
    }

    override suspend fun downloadVideo(
        url: String,
        fileName: String,
        isHLS: Boolean,
        uri: Uri,
        context: Context
    ) {
        val downloadData = DownloadData(
            mediaUrl = url,
            filename = fileName,
            contentUri = uri.toString(),
            flags =
                if (isHLS)
                    DownloadData.IS_VIDEO or DownloadData.IS_HLS
                else
                    DownloadData.IS_VIDEO
        )

        val intent = Intent(context, downloadService::class.java)
            .putExtra(IntentData.DOWNLOAD_DATA, downloadData)

        ContextCompat.startForegroundService(context, intent)
    }

    override suspend fun downloadImage(
        url: String,
        uri: Uri,
        context: Context
    ) {
        val downloadData = DownloadData(
            mediaUrl = url,
            filename = "",
            contentUri = uri.toString(),
            flags = DownloadData.IS_IMAGE
        )

        val intent = Intent(context, downloadService::class.java)
            .putExtra(IntentData.DOWNLOAD_DATA, downloadData)

        ContextCompat.startForegroundService(context, intent)
    }
}