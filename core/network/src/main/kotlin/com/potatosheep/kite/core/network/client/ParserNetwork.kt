package com.potatosheep.kite.core.network.client

import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.network.NetworkDataSource
import com.potatosheep.kite.core.network.model.NetworkComment
import com.potatosheep.kite.core.network.model.NetworkInstance
import com.potatosheep.kite.core.network.model.NetworkMediaLink
import com.potatosheep.kite.core.network.model.NetworkPost
import com.potatosheep.kite.core.network.model.NetworkSubreddit
import com.potatosheep.kite.core.network.model.NetworkSubredditWiki
import com.potatosheep.kite.core.network.model.NetworkUser
import com.potatosheep.kite.core.network.parser.Parser
import com.potatosheep.kite.core.network.util.parseHtml
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ParserNetwork @Inject constructor(
    okHttpCallFactory: dagger.Lazy<Call.Factory>,
    parser: dagger.Lazy<Parser>,
    moshi: dagger.Lazy<Moshi>,
    @Dispatcher(KiteDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(KiteDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) : NetworkDataSource {

    private val client = okHttpCallFactory.get()
    private val parser = parser.get()
    private val moshi = moshi.get()

    override suspend fun getPreferences(
        instanceUrl: String,
        sort: String,
        subreddits: List<String>,
        redirect: String
    ): List<NetworkPost> =
        withContext(defaultDispatcher) {
            val request = Request.Builder()
                .url("${instanceUrl}/settings/restore/?use_hls=on" +
                        "&subscriptions=${subreddits.joinToString("%2B")}" +
                        "&front_page=default" +
                        "&post_sort=$sort" +
                        "&redirect=$redirect"
                )
                .build()

            var html: Document

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                }
            }

            return@withContext parser.parsePostList(html, instanceUrl)
        }

    override suspend fun getPost(
        instanceUrl: String,
        subredditName: String,
        postId: String,
        replyId: String?,
        isShareLink: Boolean,
        findParentComments: Boolean
    ): Triple<NetworkPost, List<NetworkComment>, String> =
        withContext(defaultDispatcher) {
            val request = Request.Builder()
                .url(
                    when {
                        isShareLink -> {
                            "${instanceUrl}/r/${subredditName}/s/${postId}"
                        }

                        replyId.isNullOrBlank() -> {
                            "${instanceUrl}/r/${subredditName}/comments/${postId}"
                        }

                        findParentComments -> {
                            "${instanceUrl}/r/${subredditName}/comments/${postId}/comment/${replyId}/?context=9999"
                        }

                        else -> {
                            "${instanceUrl}/r/${subredditName}/comments/${postId}/comment/${replyId}"
                        }
                    }
                )
                .build()

            var html: Document
            var path: String

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                    path = response.request.url.encodedPath
                }
            }

            val post = parser.parsePost(html, instanceUrl)
            val comments = parser.parseComments(html, instanceUrl)

            if (path.last() != '/') {
                path += '/'
            }

            return@withContext Triple(post, comments, path)
        }

    override suspend fun getPosts(
        instanceUrl: String,
        sort: String,
        timeframe: String,
        after: String?,
        subredditName: String?
    ): List<NetworkPost> =
        withContext(defaultDispatcher) {
            val request = Request.Builder()
                .url(
                    when {
                        subredditName.isNullOrBlank() && after.isNullOrBlank() -> {
                            "${instanceUrl}/${sort}?t=${timeframe}"
                        }

                        subredditName.isNullOrBlank() && !after.isNullOrBlank() -> {
                            "${instanceUrl}/${sort}?sort=${sort}&t=${timeframe}&after=t3_${after}"
                        }

                        !subredditName.isNullOrBlank() && after.isNullOrBlank() -> {
                            "${instanceUrl}/r/${subredditName}/${sort}?t=${timeframe}"
                        }

                        !subredditName.isNullOrBlank() && !after.isNullOrBlank() -> {
                            "${instanceUrl}/r/${subredditName}/${sort}?sort=${sort}&t=${timeframe}&after=t3_${after}"
                        }

                        else -> {
                            instanceUrl
                        }
                    }
                )
                .build()

            var html: Document

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                }
            }

            return@withContext parser.parsePostList(html, instanceUrl)
        }

    override suspend fun searchPostsAndSubreddits(
        instanceUrl: String,
        query: String,
        sort: String,
        timeframe: String,
        after: String?,
        subredditName: String?
    ): Pair<List<NetworkPost>, List<NetworkSubreddit>> = withContext(defaultDispatcher) {

        val request = Request.Builder()
            .url(
                when {
                    subredditName.isNullOrBlank() && after.isNullOrBlank() -> {
                        "${instanceUrl}/search?q=${query}&sort=${sort}&t=${timeframe}"
                    }

                    subredditName.isNullOrBlank() && !after.isNullOrBlank() -> {
                        "${instanceUrl}/search?q=${query}&restrict_sr=&sort=${sort}&t=${timeframe}&after=t3_${after}"
                    }

                    !subredditName.isNullOrBlank() && after.isNullOrBlank() -> {
                        "${instanceUrl}/r/${subredditName}/search?q=${query}&restrict_sr=on&sort=${sort}&t=${timeframe}"
                    }

                    !subredditName.isNullOrBlank() && !after.isNullOrBlank() -> {
                        "${instanceUrl}/r/${subredditName}/search?q=${query}&restrict_sr=on&sort=${sort}&t=${timeframe}&after=t3_${after}"
                    }

                    else -> {
                        // TODO: Replace with an error URL to handle invalid url params
                        instanceUrl
                    }
                }
            )
            .build()

        var html: Document

        withContext(ioDispatcher) {
            client.newCall(request).execute().use { response ->
                html = response.parseHtml()
            }
        }

        val posts = parser.parseSearchResult(html, instanceUrl)

        val subreddits =
            if (after.isNullOrBlank() && subredditName.isNullOrBlank())
                parser.parseSearchSubreddits(html, instanceUrl)
            else
                emptyList()

        return@withContext Pair(posts, subreddits)
    }

    override suspend fun searchSubreddits(
        instanceUrl: String,
        query: String
    ): List<NetworkSubreddit> = withContext(defaultDispatcher) {
        val request = Request.Builder()
            .url("${instanceUrl}/search?q=${query}&type=sr_user")
            .build()

        var html: Document

        withContext(ioDispatcher) {
            client.newCall(request).execute().use { response ->
                html = response.parseHtml()
            }
        }

        return@withContext parser.parseSearchSubreddits(html, instanceUrl)
    }

    override suspend fun getPostImageGallery(
        instanceUrl: String,
        subredditName: String,
        postId: String
    ): List<NetworkMediaLink> =
        withContext(defaultDispatcher) {
            val request = Request.Builder()
                .url("${instanceUrl}/r/${subredditName}/comments/${postId}")
                .build()

            var html: Document

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                }
            }

            return@withContext parser.parseImageGallery(html, instanceUrl)
        }

    override suspend fun getSubreddit(
        subredditName: String,
        instanceUrl: String,
    ): Pair<NetworkSubreddit, List<NetworkPost>> =
        withContext(defaultDispatcher) {

            val request = Request.Builder()
                .url("${instanceUrl}/r/${subredditName}")
                .build()

            var html: Document

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                }
            }

            val subredditMeta = parser.parseSubreddit(html, instanceUrl)
            val subredditPosts = parser.parsePostList(html, instanceUrl)

            return@withContext Pair(subredditMeta, subredditPosts)
        }

    override suspend fun getSubredditWiki(
        subredditName: String,
        instanceUrl: String
    ): NetworkSubredditWiki = withContext(defaultDispatcher) {

        val request = Request.Builder()
            .url("${instanceUrl}/r/${subredditName}/wiki/index")
            .build()

        var html: Document

        withContext(ioDispatcher) {
            client.newCall(request).execute().use { response ->
                html = response.parseHtml()
            }
        }

        return@withContext parser.parseSubredditWiki(html, instanceUrl)
    }

    override suspend fun getUser(
        userName: String,
        instanceUrl: String
    ): Pair<NetworkUser, List<Any>> =
        withContext(defaultDispatcher) {

            val request = Request.Builder()
                .url("${instanceUrl}/user/${userName}/overview?sort=hot")
                .build()

            var html: Document

            withContext(ioDispatcher) {
                client.newCall(request).execute().use { response ->
                    html = response.parseHtml()
                }
            }

            val user = parser.parseUser(html, instanceUrl)
            val postsAndComments = parser.parseUserPostsAndComments(
                html = html,
                userName = userName,
                instanceUrl = instanceUrl
            )

            return@withContext Pair(user, postsAndComments)
        }

    override suspend fun getUserPostAndComments(
        instanceUrl: String,
        userName: String,
        sort: String,
        after: String?
    ): List<Any> = withContext(defaultDispatcher) {
        var url = "${instanceUrl}/user/${userName}/overview?sort=${sort}&t="

        if (!after.isNullOrBlank())
            url += "&after=${after}"

        val request = Request.Builder()
            .url(url)
            .build()

        var html: Document

        withContext(ioDispatcher) {
            client.newCall(request).execute().use { response ->
                html = response.parseHtml()
            }
        }

        val postsAndComments = parser.parseUserPostsAndComments(
            html = html,
            userName = userName,
            instanceUrl = instanceUrl
        )

        return@withContext postsAndComments
    }

    override suspend fun getInstances(): List<String> = withContext(defaultDispatcher) {
        val url = "https://raw.githubusercontent.com/redlib-org/redlib-instances/refs/heads/main/instances.json"
        val request = Request.Builder()
            .url(url)
            .build()

        val type = Types.newParameterizedType(List::class.java, NetworkInstance::class.java)
        val jsonAdapter: JsonAdapter<List<NetworkInstance>> = moshi.adapter(type)

        val instances: List<NetworkInstance>
        var responseBody: String?
        withContext(ioDispatcher) {
            client.newCall(request).execute().use { response ->
                responseBody = response.body?.string()
            }
        }

        if (responseBody.isNullOrBlank()) {
            instances = emptyList()
        } else {
            val jsonObject = JSONObject(responseBody!!)
            val nestedJsonArray = jsonObject.getJSONArray("instances")

            instances = jsonAdapter.fromJson(nestedJsonArray.toString())!!
        }

        return@withContext instances.mapNotNull {
            it.url
        }
    }
}
