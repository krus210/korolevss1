package ru.korolevss.service

import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.sync.Mutex
import org.springframework.security.crypto.password.PasswordEncoder
import ru.korolevss.dto.AuthenticationRequestDto
import ru.korolevss.dto.AuthenticationResponseDto
import ru.korolevss.dto.PasswordChangeRequestDto
import ru.korolevss.dto.UserResponseDto
import ru.korolevss.exception.InvalidPasswordException
import ru.korolevss.exception.PasswordChangeException
import ru.korolevss.model.MediaModel
import ru.korolevss.model.UserModel
import ru.korolevss.repository.UserRepository
import java.io.IOException

class UserService(
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    private val mutex = Mutex()

    suspend fun getModelById(id: Long): UserModel? {
        return repo.getById(id)
    }

    suspend fun getByUserName(username: String): UserModel? {
        return repo.getByUsername(username)
    }

    @KtorExperimentalAPI
    suspend fun getById(id: Long): UserResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return UserResponseDto.fromModel(model)
    }

    @KtorExperimentalAPI
    suspend fun changePassword(id: Long, input: PasswordChangeRequestDto) {
        try {
            val model = repo.getById(id) ?: throw NotFoundException()
            if (!passwordEncoder.matches(input.old, model.password)) {
                throw PasswordChangeException("Wrong password!")
            }
            val copy = model.copy(password = passwordEncoder.encode(input.new))
            repo.save(copy)
        } catch (e: IOException) {
            println("New password not saved")
        }
    }

    @KtorExperimentalAPI
    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw NotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw InvalidPasswordException("Wrong password!")
        }

        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(token)
    }

    suspend fun save(username: String, password: String) : AuthenticationResponseDto {
        return try {
            if (repo.getByUsername(username) != null) {
                AuthenticationResponseDto("response 400")
            } else {
                val model = repo.save(UserModel(username = username, password = passwordEncoder.encode(password)))
                val token = tokenService.generate(model.id)
                AuthenticationResponseDto(token)
            }
        } catch (e: IOException) {
            AuthenticationResponseDto("response 503")
        }
    }
}