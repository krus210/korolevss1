package ru.korolevss.dto

import ru.korolevss.model.Coordinates
import ru.korolevss.model.MediaModel
import ru.korolevss.model.PostModel
import ru.korolevss.model.PostType
import java.time.ZonedDateTime

class PostResponseDto(
    val id: Long,
    val textOfPost: String? = null,
    val dateOfPost: ZonedDateTime? = null,
    val nameAuthor: String?,
    var sharesCount: Int = 0,
    var commentsCount: Int = 0,
    var likesCount: Int = 0,
    var isLikedByUser: Boolean = false,
    var isCommentedByUser: Boolean = false,
    var isSharedByUser: Boolean = false,
    val postType: PostType = PostType.POST,
    val source: PostModel? = null,
    val address: String? = null,
    val coordinates: Coordinates? = null,
    val sourceVideo: String? = null,
    val sourceAd: String? = null
) {
    companion object {
        fun fromModel(postModel: PostModel) = PostResponseDto(
            id = postModel.id,
            textOfPost = postModel.textOfPost,
            dateOfPost = postModel.dateOfPost,
            nameAuthor = postModel.user?.username,
            sharesCount = postModel.sharesCount,
            commentsCount = postModel.commentsCount,
            likesCount = postModel.likesCount,
            isLikedByUser = postModel.isLikedByUser,
            isCommentedByUser = postModel.isCommentedByUser,
            isSharedByUser = postModel.isSharedByUser,
            postType = postModel.postType,
            source = postModel.source,
            address = postModel.address,
            coordinates = postModel.coordinates,
            sourceVideo = postModel.sourceVideo,
            sourceAd = postModel.sourceAd
        )
    }
}