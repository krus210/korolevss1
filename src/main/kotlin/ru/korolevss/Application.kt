package ru.korolevss

import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.auth.jwt.jwt
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.server.cio.EngineMain
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.*
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.korolevss.dto.PostRequestDto
import ru.korolevss.dto.PostResponseDto
import ru.korolevss.exception.ConfigurationException
import ru.korolevss.model.PostModel
import ru.korolevss.repository.PostRepository
import ru.korolevss.repository.PostRepositoryMutex
import ru.korolevss.repository.UserRepository
import ru.korolevss.repository.UserRepositoryInMemoryWithAtomicImpl
import ru.korolevss.route.RoutingV1
import ru.korolevss.service.FileService
import ru.korolevss.service.JWTTokenService
import ru.korolevss.service.PostService
import ru.korolevss.service.UserService

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@KtorExperimentalAPI
fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {
        exception<NotImplementedError> {
            call.respond(HttpStatusCode.NotImplemented)
        }
        exception<ParameterConversionException> {
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(KodeinFeature) {
        constant(tag = "upload-dir") with (environment.config.propertyOrNull("korolevss.upload.dir")?.getString()
                ?: throw ConfigurationException("Upload dir is not specified"))
        bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
        bind<JWTTokenService>() with eagerSingleton { JWTTokenService() }
        bind<PostRepository>() with eagerSingleton { PostRepositoryMutex() }
        bind<PostService>() with eagerSingleton { PostService(instance()) }
        bind<FileService>() with eagerSingleton { FileService(instance(tag = "upload-dir")) }
        bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithAtomicImpl() }
        bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance())}
        bind<RoutingV1>() with eagerSingleton { RoutingV1(instance(tag = "upload-dir"), instance(), instance(), instance()) }
    }

    install(Authentication) {
        jwt("jwt") {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getModelById(id)
            }
        }
        basic("basic") {

            val encoder by kodein().instance<PasswordEncoder>()
            val userService by kodein().instance<UserService>()
            validate { credentials ->
                val user = userService.getByUserName(credentials.name)

                if (encoder.matches(credentials.password, user?.password)) {
                    user
                } else {
                    null
                }
            }
        }
    }

    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}




