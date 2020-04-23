package ru.korolevss.dto

import ru.korolevss.model.*
import java.time.ZonedDateTime

class PostResponseDto(
    val id: Long,
    val textOfPost: String? = null,
    val dateOfPost: ZonedDateTime? = null,
    val nameAuthor: String?,
    var sharesCount: Int,
    var commentsCount: Int,
    var likesCount: Int,
    var isLikedByUser: Boolean,
    var isCommentedByUser: Boolean,
    var isSharedByUser: Boolean,
    val postType: PostType = PostType.POST,
    val source: PostModel? = null,
    val address: String? = null,
    val coordinates: Coordinates? = null,
    val sourceVideo: String? = null,
    val sourceAd: String? = null
) {
    companion object {
        fun fromModel(postModel: PostModel, userId: Long): PostResponseDto {
            val isLikedByUser = postModel.likedUserIdList.contains(userId)
            val isCommentByUser = postModel.commentUserIdList.contains(userId)
            val isShareByUser = postModel.shareUserIdList.contains(userId)
            val likesCount = postModel.likedUserIdList.size
            val commentsCount = postModel.commentUserIdList.size
            val sharesCount = postModel.shareUserIdList.size

            return PostResponseDto(
                id = postModel.id,
                textOfPost = postModel.textOfPost,
                dateOfPost = postModel.dateOfPost,
                nameAuthor = postModel.user?.username,
                sharesCount = likesCount,
                commentsCount = commentsCount,
                likesCount = sharesCount,
                isLikedByUser = isLikedByUser,
                isCommentedByUser = isCommentByUser,
                isSharedByUser = isShareByUser,
                postType = postModel.postType,
                source = postModel.source,
                address = postModel.address,
                coordinates = postModel.coordinates,
                sourceVideo = postModel.sourceVideo,
                sourceAd = postModel.sourceAd
            )
        }
    }
}