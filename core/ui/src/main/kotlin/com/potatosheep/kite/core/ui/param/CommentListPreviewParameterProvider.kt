package com.potatosheep.kite.core.ui.param

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.FlairComponent
import com.potatosheep.kite.core.model.FlairComponentType
import com.potatosheep.kite.core.ui.param.CommentListPreviewParameterData.comments

class CommentListPreviewParameterProvider : PreviewParameterProvider<List<Comment>> {
    override val values: Sequence<List<Comment>> = sequenceOf(comments)
}

object CommentListPreviewParameterData {
    val comments = listOf(
        Comment(
            id = "testId123",
            postId = "postId123",
            userName = "u/testUser",
            subredditName = "r/testsub",
            textContent =
            "<blockquote>" +
                    "<p>Allow me to pose</p> " +
                    "<p>to you a question:</p>" +
                    "</blockquote>" +
                    "<p><strong><em>if you dry yourself</em> <del>with a towel after</del></strong></p>" +
                    "<p>you've showered (i.e., you're clean) why do you still wash your towel?</p>",
            upvoteCount = 18,
            timeAgo = "2h ago",
            parentCommentId = "873avc1",
            postTitle = null,
            isPostAuthor = true,
            flair = listOf(
                FlairComponent("Test", FlairComponentType.TEXT)
            )
        ),
        Comment(
            id = "testId456",
            postId = "postId123",
            userName = "u/testUser",
            subredditName = "r/testsub",
            textContent = "Allow me to pose to you a question: if you dry yourself with a towel after " +
                    "you've showered ~~(i.e., you're clean)~~, why ||do|| you still wash your towel?",
            upvoteCount = 18,
            timeAgo = "2h ago",
            parentCommentId = "testId123",
            postTitle = null,
            isPostAuthor = false,
            flair = emptyList()
        ),
        Comment(
            id = "testId789",
            postId = "postId123",
            userName = "u/testUser",
            subredditName = "r/testsub",
            textContent = "Allow me to pose to you a question if you dry yourself with a towel after " +
                    "you've showered (i.e., you're clean), why do you still wash your towel?",
            upvoteCount = 18,
            timeAgo = "Jun 12, 2024",
            parentCommentId = "testId456",
            postTitle = null,
            isPostAuthor = false,
            flair = emptyList()
        ),
        Comment(
            id = "testId012",
            postId = "postId123",
            userName = "",
            subredditName = "/r/testsub",
            textContent = "I like lemons.",
            upvoteCount = 22,
            timeAgo = "2h ago",
            parentCommentId = "testId123",
            postTitle = "Lemons, lemons, lemons, lemons, and more lemons",
            isPostAuthor = false,
            flair = emptyList()
        )
    )
}