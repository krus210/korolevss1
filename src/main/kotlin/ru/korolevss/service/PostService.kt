package ru.korolevss.service

import io.ktor.features.NotFoundException
import io.ktor.util.KtorExperimentalAPI
import ru.korolevss.dto.PostRequestDto
import ru.korolevss.dto.PostResponseDto
import ru.korolevss.exception.InvalidOwnerException
import ru.korolevss.model.MediaModel
import ru.korolevss.model.MediaType
import ru.korolevss.model.PostModel
import ru.korolevss.model.UserModel
import ru.korolevss.repository.PostRepository

class PostService(private val repo: PostRepository) {
    suspend fun getAll(): List<PostResponseDto> {
        return repo.getAll().map { PostResponseDto.fromModel(it) }
    }

    @KtorExperimentalAPI
    suspend fun getById(id: Long): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun likeById(id: Long): PostResponseDto {
        val model = repo.likeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun dislikeById(id: Long): PostResponseDto {
        val model = repo.dislikeById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun commentById(id: Long): PostResponseDto {
        val model = repo.commentById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun shareById(id: Long): PostResponseDto {
        val model = repo.shareById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun removeById(id: Long, me: UserModel?): Boolean {
        val model = repo.getById(id) ?: throw NotFoundException()
        return if (model.user == me) {
            repo.removeById(id)
            true
        } else {
            false
        }
    }

    suspend fun save(input: PostRequestDto, me: UserModel?): PostResponseDto? {
        val model = PostModel(
            id = input.id,
            textOfPost = input.textOfPost,
            postType = input.postType, source = input.source, address = input.address,
            coordinates = input.coordinates, sourceVideo = input.sourceVideo, sourceAd = input.sourceAd, user = me,
            attachment = input.attachmentId?.let { MediaModel(id = it, mediaType = MediaType.IMAGE) }
        )
        if (input.id != 0L) {
            val existingPostModel = repo.getById(input.id)
            if (existingPostModel?.user?.id != me?.id) {
                return null
            }
        }
        return PostResponseDto.fromModel(repo.save(model))
    }
}