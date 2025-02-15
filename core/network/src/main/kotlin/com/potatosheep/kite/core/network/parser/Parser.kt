package com.potatosheep.kite.core.network.parser

import com.potatosheep.kite.core.common.RedlibErrorException
import com.potatosheep.kite.core.common.Metadata
import com.potatosheep.kite.core.markdown.converter.MarkdownConverter
import com.potatosheep.kite.core.network.model.NetworkComment
import com.potatosheep.kite.core.network.model.NetworkMediaLink
import com.potatosheep.kite.core.network.model.NetworkPost
import com.potatosheep.kite.core.network.model.NetworkSubreddit
import com.potatosheep.kite.core.network.model.NetworkSubredditWiki
import com.potatosheep.kite.core.network.model.NetworkUser
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.NodeVisitor

/**
 * Binds data parsed from a HTML document to Network model classes (e.g., [NetworkPost],
 * [NetworkSubreddit], etc).
 */
// TODO: Consider putting this into its own module (:core:parser).
internal class Parser(
    private val markdownConverter: MarkdownConverter
) {
    fun parsePostList(html: Document, instanceUrl: String): List<NetworkPost> {

        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        val postList = emptyList<NetworkPost>().toMutableList()

        val htmlPostList = html.select("div")
            .first { it.id() == "posts" }

        htmlPostList.forEachNode { post ->
            if (post.hasAttr("id") &&
                post is Element &&
                post.attr("id") != "posts")
            {
                postList += NetworkPost(
                    id = post.attr("id"),
                    title = extractPostListingTitle(post),
                    subRedditName = extractPostSubreddit(post),
                    userName = extractPostUser(post),
                    mediaLinks = extractPostListingMediaLinks(post, instanceUrl),
                    postTextContent = "",
                    postTimeAgo = extractPostTimeAgo(post),
                    timePosted = extractTimestamp(post),
                    upvoteCount = extractPostUpvoteCount(post),
                    postCommentCount = extractPostListingCommentCount(post),
                    flair = extractPostFlair(post, instanceUrl),
                    flairId = extractPostFlairText(post),
                    isNsfw = extractPostNsfw(post),
                    isSpoiler = extractPostSpoiler(post)
                )
            }
        }

        return postList
    }

    fun parsePost(html: Document, instanceUrl: String): NetworkPost {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        return NetworkPost(
            id = extractPostId(html),
            title = extractPostTitle(html),
            subRedditName = extractPostSubreddit(html),
            userName = extractPostUser(html),
            mediaLinks = extractPostMediaLinks(html, instanceUrl), //TODO: Make overload method for single post page
            postTextContent = markdownConverter.convert(extractPostContent(html, instanceUrl)),
            postTimeAgo = extractPostTimeAgo(html),
            timePosted = extractTimestamp(html),
            upvoteCount = extractPostUpvoteCount(html),
            postCommentCount = extractPostCommentCount(html),
            flair = extractPostFlair(html, instanceUrl),
            flairId = extractPostFlairText(html),
            isNsfw = extractPostNsfw(html),
            isSpoiler = extractPostSpoiler(html)
        )
    }

    fun parseComments(html: Document, instanceUrl: String): List<NetworkComment> {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        val commentList = emptyList<NetworkComment>().toMutableList()
        val postId = extractPostId(html)
        val subredditName = extractPostSubreddit(html)

        val visitor = NodeVisitor { node, _ ->
            if (node is Element) {
                if (node.hasClass("comment")) {
                    commentList.add(
                        NetworkComment(
                            id = node.attr("id"),
                            postId = postId,
                            parentCommentId =
                            when {
                                node.hasParent() &&
                                        node.parent() is Element &&
                                        node.parent()!!.hasClass("replies") -> {

                                    node.parent()!!.parent()!!.parent()!!.attr("id")
                                }

                                html.select("a")
                                    .find { it.attr("href") == "?context=9999" } !=
                                        null -> Metadata.HAS_PARENTS

                                else -> postId
                            },
                            subredditName = subredditName,
                            textContent = markdownConverter.convert(
                                node.select("div")
                                    .first { it.hasClass("comment_body") }
                                    .select("div.md")
                                    .first()!!
                                    .html()
                                    .addDomain(instanceUrl)
                            ),
                            timeAgo = node.select("a")
                                .first { it.hasClass("created") }
                                .text(),
                            upvoteCount = extractCommentUpvoteCount(node),
                            userName = node.select("*")
                                .first { it.hasClass("comment_author") }
                                .text(),
                            isPostAuthor = node.select("a").first()!!.hasClass("op"),
                        )
                    )
                } else if (node.hasClass("deeper_replies")) {
                    val moreRepliesId = node.attr("href").split("/")[6]

                    commentList.add(
                        NetworkComment(
                            id = moreRepliesId,
                            postId = postId,
                            parentCommentId = node.parent()!!.parent()!!.parent()!!.attr("id"),
                            subredditName = subredditName,
                            textContent = node.text(),
                            timeAgo = "",
                            upvoteCount = 0,
                            userName = "",
                            isPostAuthor = false,
                        )
                    )
                }
            }
        }

        html.traverse(visitor)
        return commentList
    }

    fun parseImageGallery(html: Document, instanceUrl: String): List<NetworkMediaLink> {
        return extractPostImageGallery(html, instanceUrl)
    }

    fun parseSubreddit(html: Document, instanceUrl: String): NetworkSubreddit {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        return NetworkSubreddit(
            subredditName = extractSubredditName(html),
            subscribers = extractSubredditMembers(html),
            activeUsers = extractSubredditActiveUsers(html),
            iconLink = extractSubredditIconLink(html, instanceUrl),
            summary = markdownConverter.convert(extractSubredditDescription(html)),
            sidebar = markdownConverter.convert(extractSubredditSidebar(html))
        )
    }

    fun parseSubredditWiki(html: Document, instanceUrl: String): NetworkSubredditWiki {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        return NetworkSubredditWiki(
            content = markdownConverter.convert(extractSubredditWiki(html))
        )
    }

    fun parseUser(html: Document, instanceUrl: String): NetworkUser {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        return NetworkUser(
            userName = extractUserName(html),
            description = extractUserDescription(html),
            karma = extractUserKarma(html),
            iconLink = extractUserIconLink(html, instanceUrl)
        )
    }

    fun parseUserPostsAndComments(
        html: Document,
        userName: String,
        instanceUrl: String
    ): List<Any> {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        val postsAndComments = mutableListOf<Any>()

        val postsDiv = html.select("div").first { it.id() == "posts" }
        postsDiv.forEachNode { node ->
            val element = node as? Element

            when {
                element == null -> Unit
                element.hasClass("user-comment") -> {
                    postsAndComments.add(
                        NetworkComment(
                            id = extractUserCommentId(element),
                            postId = extractUserCommentPostId(element),
                            postTitle = extractUserCommentPostTitle(element),
                            parentCommentId = null,
                            subredditName = extractUserCommentSubreddit(element),
                            textContent = markdownConverter.convert(extractUserCommentTextContent(element, instanceUrl)),
                            timeAgo = element.select("span")
                                .first { it.hasClass("created") }
                                .text(),
                            upvoteCount = extractCommentUpvoteCount(element),
                            userName = userName,
                            isPostAuthor = false
                        )
                    )
                }
                element.hasClass("post") -> {
                    postsAndComments.add(
                        NetworkPost(
                            id = element.id(),
                            title = extractPostListingTitle(element),
                            subRedditName = extractPostSubreddit(element),
                            userName = userName,
                            mediaLinks = extractPostListingMediaLinks(element, instanceUrl), //TODO: Make overload method for single post page
                            postTextContent = markdownConverter.convert(extractPostContent(element, instanceUrl)),
                            postTimeAgo = extractPostTimeAgo(element),
                            timePosted = extractTimestamp(element),
                            upvoteCount = extractPostUpvoteCount(element),
                            postCommentCount = extractUserPostCommentCount(element),
                            flair = extractPostFlair(element, instanceUrl),
                            flairId = extractPostFlairText(element),
                            isNsfw = extractPostNsfw(element),
                            isSpoiler = extractPostSpoiler(element)
                        )
                    )
                }
            }
        }

        return postsAndComments
    }

    fun parseSearchResult(html: Document, instanceUrl: String): List<NetworkPost> {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        val postList = emptyList<NetworkPost>().toMutableList()

        if (html.select("center").first() == null) {
            val htmlPostList = html.select("div")
                .first { it.id() == "column_one" }

            htmlPostList.forEachNode { post ->
                if (post.hasAttr("id") &&
                    post is Element &&
                    post.hasClass("post"))
                {
                    postList += NetworkPost(
                        id = post.attr("id"),
                        title = extractPostListingTitle(post),
                        subRedditName = extractPostSubreddit(post),
                        userName = extractPostUser(post),
                        mediaLinks = extractPostListingMediaLinks(post, instanceUrl),
                        postTextContent = extractPostContent(post, instanceUrl),
                        postTimeAgo = extractPostTimeAgo(post),
                        timePosted = extractTimestamp(post),
                        upvoteCount = extractPostUpvoteCount(post),
                        postCommentCount = extractPostListingCommentCount(post),
                        flair = extractPostFlair(post, instanceUrl),
                        flairId = extractPostFlairText(post),
                        isNsfw = extractPostNsfw(post),
                        isSpoiler = extractPostSpoiler(post)
                    )
                }
            }
        }

        return postList
    }

    fun parseSearchSubreddits(html: Document, instanceUrl: String): List<NetworkSubreddit> {
        val error = extractErrorMessage(html, instanceUrl)
        if (!error.isNullOrBlank()) {
            throw RedlibErrorException(error)
        }

        val subredditList = emptyList<NetworkSubreddit>().toMutableList()
        val htmlSubredditList = html.select("div#search_subreddits").first()

        if (html.select("center").first() == null && htmlSubredditList != null) {
            htmlSubredditList.forEachNode { subreddit ->
                if (subreddit is Element &&
                    subreddit.hasClass("search_subreddit") &&
                    !subreddit.hasAttr("id")) {

                    subredditList.add(
                        NetworkSubreddit(
                            subredditName = extractSearchSubredditName(subreddit),
                            subscribers = extractSearchSubredditMembers(subreddit),
                            activeUsers = 0,
                            iconLink = extractSearchSubredditIconLink(subreddit, instanceUrl),
                            summary = extractSearchSubredditDescription(subreddit),
                            sidebar = ""
                        )
                    )
                }
            }
        }

        return subredditList
    }
}