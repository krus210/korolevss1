package ru.korolevss.dto

import ru.korolevss.model.UserModel

data class UserResponseDto(val id: Long, val username: String) {
    companion object {
        fun fromModel(model: UserModel) = UserResponseDto(
            id = model.id,
            username = model.username
        )
    }
}