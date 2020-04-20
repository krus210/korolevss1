package ru.korolevss.repository

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.korolevss.model.MediaModel
import ru.korolevss.model.UserModel

class UserRepositoryInMemoryWithAtomicImpl : UserRepository {
    private var nextId = atomic(0L)
    private val items = mutableListOf<UserModel>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<UserModel> = items.toList()


    override suspend fun getById(id: Long): UserModel? = items.find { it.id == id }


    override suspend fun getByIds(ids: Collection<Long>): List<UserModel> = items.filter { ids.contains(it.id) }

    override suspend fun getByUsername(username: String): UserModel? = items.find { it.username == username }

    override suspend fun save(item: UserModel): UserModel {
        return when (val index = items.indexOfFirst { it.id == item.id }) {
            -1 -> {
                val copy = item.copy(id = nextId.incrementAndGet())
                mutex.withLock {
                    items.add(copy)
                }
                copy
            }
            else -> {
                val copy = items[index].copy(username = item.username, password = item.password)
                mutex.withLock {
                    items[index] = copy
                }
                copy
            }
        }
    }
}