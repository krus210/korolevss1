package ru.korolevss.model

import java.time.ZonedDateTime

data class PostModel(
    val id: Long,
    val textOfPost: String? = null,
    val dateOfPost: ZonedDateTime? = null,
    val postType: PostType = PostType.POST,
    val source: PostModel? = null,
    val address: String? = null,
    val coordinates: Coordinates? = null,
    val sourceVideo: String? = null,
    val sourceAd: String? = null,
    val user: UserModel? = null,
    val attachment: MediaModel? = null,
    var likedUserIdList: MutableList<Long> = mutableListOf(),
    var commentUserIdList: MutableList<Long> = mutableListOf(),
    var shareUserIdList: MutableList<Long> = mutableListOf()
    )
enum class PostType {
    POST, EVENT, REPOST, YOUTUBE, AD_POST
}

data class Coordinates(
    val longitude: String,
    val latitude: String
)