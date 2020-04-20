package ru.korolevss.repository

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.korolevss.model.PostModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class PostRepositoryMutex : PostRepository {

    private var nextId = atomic(0L)
    private val items = mutableListOf<PostModel>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<PostModel> = items.reversed()


    override suspend fun getById(id: Long): PostModel? = items.find { it.id == id }


    override suspend fun save(item: PostModel): PostModel =
        when (val index = items.indexOfFirst { it.id == item.id }) {
            -1 -> {
                val todayDate = LocalDateTime.now()
                val dateId = ZoneId.of("Europe/Moscow")
                val zonedDateTime = ZonedDateTime.of(todayDate, dateId)
                val copy = item.copy(id = nextId.incrementAndGet(), dateOfPost = zonedDateTime)
                mutex.withLock {
                    items.add(copy)
                }
                copy
            }
            else -> {
                mutex.withLock {
                    items[index] = item
                }
                item
            }
        }

    override suspend fun removeById(id: Long) {
        mutex.withLock {
            items.removeIf { it.id == id }
        }
    }

    override suspend fun likeById(id: Long): PostModel? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) {
            return null
        }
        with(items[index]) {
            likesCount++
            isLikedByUser = true
            return this
        }
    }

    override suspend fun dislikeById(id: Long): PostModel? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) {
            return null
        }
        with(items[index]) {
            likesCount--
            isLikedByUser = false
            return this
        }
    }

    override suspend fun commentById(id: Long): PostModel? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) {
            return null
        }
        with(items[index]) {
            commentsCount--
            isCommentedByUser = false
            return this
        }
    }

    override suspend fun shareById(id: Long): PostModel? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) {
            return null
        }
        with(items[index]) {
            sharesCount--
            isSharedByUser = false
            return this
        }
    }
}