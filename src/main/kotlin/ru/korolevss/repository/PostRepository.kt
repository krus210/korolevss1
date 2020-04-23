package ru.korolevss.repository

import ru.korolevss.model.PostModel

interface PostRepository {
    suspend fun getAll(): List<PostModel>
    suspend fun getById(id: Long): PostModel?
    suspend fun save(item: PostModel): PostModel
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long, userId: Long): PostModel?
    suspend fun dislikeById(id: Long, userId: Long): PostModel?
    suspend fun commentById(id: Long, userId: Long): PostModel?
    suspend fun shareById(id: Long, userId: Long): PostModel?
}