package ru.korolevss.service

import io.ktor.features.NotFoundException
import io.ktor.util.KtorExperimentalAPI
import ru.korolevss.dto.PostRequestDto
import ru.korolevss.dto.PostResponseDto
import ru.korolevss.exception.UserAccessException
import ru.korolevss.model.MediaModel
import ru.korolevss.model.MediaType
import ru.korolevss.model.PostModel
import ru.korolevss.model.UserModel
import ru.korolevss.repository.PostRepository

class PostService(private val repo: PostRepository) {
    suspend fun getAll(userId: Long): List<PostResponseDto> {
        return repo.getAll().map { PostResponseDto.fromModel(it, userId) }
    }

    @KtorExperimentalAPI
    suspend fun getById(id: Long, userId: Long): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model, userId)
    }

    @KtorExperimentalAPI
    suspend fun likeById(id: Long, userId: Long): PostResponseDto {
        val model = repo.likeById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model, userId)
    }

    @KtorExperimentalAPI
    suspend fun dislikeById(id: Long, userId: Long): PostResponseDto {
        val model = repo.dislikeById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model, userId)
    }

    @KtorExperimentalAPI
    suspend fun commentById(id: Long, userId: Long): PostResponseDto {
        val model = repo.commentById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model, userId)
    }

    @KtorExperimentalAPI
    suspend fun shareById(id: Long, userId: Long): PostResponseDto {
        val model = repo.shareById(id, userId) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model, userId)
    }

    @KtorExperimentalAPI
    suspend fun removeById(id: Long, me: UserModel): Boolean {
        val model = repo.getById(id) ?: throw NotFoundException()
        return if (model.user == me) {
            repo.removeById(id)
            true
        } else {
            false
        }
    }

    suspend fun save(input: PostRequestDto, me: UserModel): PostResponseDto {
        val model = PostModel(
            id = 0L,
            textOfPost = input.textOfPost,
            postType = input.postType, address = input.address,
            coordinates = input.coordinates, sourceVideo = input.sourceVideo, sourceAd = input.sourceAd, user = me,
            attachment = input.attachmentId?.let { MediaModel(id = it, mediaType = MediaType.IMAGE) }
        )
        return PostResponseDto.fromModel(repo.save(model), me.id)
    }

    @KtorExperimentalAPI
    suspend fun saveById(id: Long, input: PostRequestDto, me: UserModel): PostResponseDto {
        val model = PostModel(
            id = id,
            textOfPost = input.textOfPost,
            postType = input.postType, address = input.address,
            coordinates = input.coordinates, sourceVideo = input.sourceVideo, sourceAd = input.sourceAd, user = me,
            attachment = input.attachmentId?.let { MediaModel(id = it, mediaType = MediaType.IMAGE) }
        )
        val existingPostModel = repo.getById(id) ?: throw NotFoundException()
        if (existingPostModel.user?.id != me.id) {
            throw UserAccessException("Access denied, Another user posted this post")

        }
        return PostResponseDto.fromModel(repo.save(model), me.id)
    }
}