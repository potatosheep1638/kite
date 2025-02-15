package com.potatosheep.kite.core.ui.param

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.potatosheep.kite.core.model.MediaLink
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterData.posts
import kotlinx.datetime.Clock

class PostListPreviewParameterProvider : PreviewParameterProvider<List<Post>> {
    override val values: Sequence<List<Post>> = sequenceOf(posts)
}

object PostListPreviewParameterData {
    val posts = listOf(
        Post(
            id = "432qwe1",
            title = "Test Title",
            subredditName = "TestSub1",
            userName = "u/TestUser1",
            mediaLinks = emptyList(),
            textContent = "Testing 1 2 3...",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 10,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
        Post(
            id = "123abc4",
            title = "Preview Image",
            subredditName = "previews",
            userName = "u/mediaman",
            mediaLinks = listOf(
                MediaLink(
                    link = "www.example.com/image.jpg",
                    caption = "",
                    mediaType = MediaType.IMAGE
                )
            ),
            textContent = "Testing 1 2 3...",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 14,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
        Post(
            id = "1d9ff02",
            title = "Report on OBJECT",
            subredditName = "leaked_reports",
            userName = "u/the_leak_master",
            mediaLinks = emptyList(),
            textContent = "There simply is not enough information to conclusively determine " +
                    "for a fact whether or not the 'object' was previously housed at Site Z2, if " +
                    "it exists at all, of which I am highly doubtful. Indeed, I firmly believe " +
                    "that, after scourging over 200 different locations across the globe, that " +
                    "the object you speak of is simply a figment of your imagination. No offense " +
                    "intended, of course.",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 10,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
        Post(
            id = "873avc1",
            title = "Preview Gallery",
            subredditName = "previews",
            userName = "u/mediaman",
            mediaLinks = listOf(
                MediaLink(
                    link = "www.example.com/image1.jpg",
                    caption = "Test caption",
                    mediaType = MediaType.IMAGE
                ),
                MediaLink(
                    link = "www.example.com/image2.jpg",
                    caption = "Test caption",
                    mediaType = MediaType.IMAGE
                )
            ),
            textContent = "Testing 1 2 3...",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 10,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
        Post(
            id = "324pft5",
            title = "BREAKING: Bob kills Alice in brutal attack on 7th Street, Leytown",
            subredditName = "news",
            userName = "u/news_lover",
            mediaLinks = listOf(
                MediaLink(
                    link = "www.example.com/newsImage.jpg",
                    caption = "",
                    mediaType = MediaType.ARTICLE_THUMBNAIL
                ),
                MediaLink(
                    link = "https://theexamplenews.com/newsArticle",
                    caption = "",
                    mediaType = MediaType.ARTICLE_LINK
                )
            ),
            textContent = "Testing 1 2 3...",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 14,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
        Post(
            id = "879tgt5",
            title = "BREAKING: John kills Bob in revenge attack on 7th Street, Leytown",
            subredditName = "news",
            userName = "u/news_lover",
            mediaLinks = listOf(
                MediaLink(
                    link = "",
                    caption = "",
                    mediaType = MediaType.ARTICLE_THUMBNAIL
                ),
                MediaLink(
                    link = "https://theexamplenews.com/newsArticle",
                    caption = "",
                    mediaType = MediaType.ARTICLE_LINK
                )
            ),
            textContent = "Testing 1 2 3...",
            timeAgo = "2h ago",
            timePosted = Clock.System.now(),
            upvoteCount = 14,
            commentCount = 10,
            flair = emptyList(),
            flairId = "",
            isNsfw = false,
            isSpoiler = false,
        ),
    )
}