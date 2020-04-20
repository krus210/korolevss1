package ru.korolevss.dto

import ru.korolevss.model.MediaModel
import ru.korolevss.model.MediaType

data class MediaResponseDto(val id: String, val mediaType: MediaType) {
    companion object {
        fun fromModel(model: MediaModel) = MediaResponseDto(
                id = model.id,
                mediaType = model.mediaType
        )
    }
}