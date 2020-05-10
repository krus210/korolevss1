package ru.korolevss.repository

import ru.korolevss.model.MediaModel
import ru.korolevss.model.PostModel
import ru.korolevss.model.UserModel

interface UserRepository {
    suspend fun getAll(): List<UserModel>
    suspend fun getById(id: Long): UserModel?
    suspend fun getByIdPassword(id: Long, password: String): UserModel?
    suspend fun getByIds(ids: Collection<Long>): List<UserModel>
    suspend fun getByUsername(username: String): UserModel?
    suspend fun save(item: UserModel): UserModel
    suspend fun saveFirebaseToken(id: Long, firebaseToken: String): UserModel?
}