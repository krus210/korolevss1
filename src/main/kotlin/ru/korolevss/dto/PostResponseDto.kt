package ru.korolevss.dto

import ru.korolevss.model.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PostResponseDto(
    val id: Long,
    val textOfPost: String? = null,
    val dateOfPost: String? = null,
    val nameAuthor: String?,
    var sharesCount: Int,
    var commentsCount: Int,
    var likesCount: Int,
    var isLikedByUser: Boolean,
    var isCommentedByUser: Boolean,
    var isSharedByUser: Boolean,
    val postType: PostType = PostType.POST,
    val sourceId: Long? = null,
    val address: String? = null,
    val coordinates: Coordinates? = null,
    val sourceVideo: String? = null,
    val sourceAd: String? = null,
    val attachmentId: String? = null
) {
    companion object {
        fun fromModel(postModel: PostModel, userId: Long): PostResponseDto {
            val isLikedByUser = postModel.likedUserIdList.contains(userId)
            val isCommentByUser = postModel.commentUserIdList.contains(userId)
            val isShareByUser = postModel.shareUserIdList.contains(userId)
            val likesCount = postModel.likedUserIdList.size
            val commentsCount = postModel.commentUserIdList.size
            val sharesCount = postModel.shareUserIdList.size

            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
            val dateOfPostString = postModel.dateOfPost?.format(formatter)

            return PostResponseDto(
                id = postModel.id,
                textOfPost = postModel.textOfPost,
                dateOfPost = dateOfPostString,
                nameAuthor = postModel.user?.username,
                sharesCount = likesCount,
                commentsCount = commentsCount,
                likesCount = sharesCount,
                isLikedByUser = isLikedByUser,
                isCommentedByUser = isCommentByUser,
                isSharedByUser = isShareByUser,
                postType = postModel.postType,
                sourceId = postModel.sourceId,
                address = postModel.address,
                coordinates = postModel.coordinates,
                sourceVideo = postModel.sourceVideo,
                sourceAd = postModel.sourceAd,
                attachmentId = postModel.attachment?.id
            )
        }
    }
}