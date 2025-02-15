package com.potatosheep.kite.core.ui.param

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostsAndCommentsPreviewParameterData.postsAndComments

data class PostsAndComments(
    val posts: List<Post>,
    val comments: List<Comment>
)

class PostsAndCommentsPreviewParameterProvider : PreviewParameterProvider<PostsAndComments> {
    override val values: Sequence<PostsAndComments> = sequenceOf(postsAndComments)
}

object PostsAndCommentsPreviewParameterData {
    val postsAndComments = PostsAndComments(
        posts = PostListPreviewParameterData.posts,
        comments = CommentListPreviewParameterData.comments
    )
}