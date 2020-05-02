package ru.korolevss.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.ParameterConversionException
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import ru.korolevss.dto.AuthenticationRequestDto
import ru.korolevss.dto.PasswordChangeRequestDto
import ru.korolevss.dto.PostRequestDto
import ru.korolevss.dto.UserResponseDto
import ru.korolevss.me
import ru.korolevss.service.FileService
import ru.korolevss.service.PostService
import ru.korolevss.service.UserService

class RoutingV1(
    private val staticPath: String,
    private val postService: PostService,
    private val fileService: FileService,
    private val userService: UserService
) {
    @KtorExperimentalAPI
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1/") {
                static("/static") {
                    files(staticPath)
                }

                route("/") {
                    post("/registration") {
                        val input = call.receive<AuthenticationRequestDto>()
                        val username = input.username
                        val password = input.password
                        val response = userService.save(username, password)
                        call.respond(response)
                        }

                    post("/authentication") {
                        val input = call.receive<AuthenticationRequestDto>()
                        val response = userService.authenticate(input)
                        call.respond(response)
                    }
                }

                authenticate("basic", "jwt") {
                    route("/me") {
                        get {
                            call.respond(UserResponseDto.fromModel(me!!))
                        }
                        post("/change-password"){
                            val input = call.receive<PasswordChangeRequestDto>()
                            val response = userService.changePassword(me!!.id, input)
                            call.respond(response)
                        }
                    }

                    route("/posts") {
                        get {
                            val response = postService.getAll(me!!.id)
                            call.respond(response)
                        }
                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.getById(id, me!!.id)
                            call.respond(response)
                        }
                        get("/recent") {
                            val response = postService.getRecent(me!!.id)
                            call.respond(response)
                        }
                        get("{id}/get-posts-after") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.getPostsAfter(id, me!!.id)
                            call.respond(response)
                        }
                        get("{id}/get-posts-before") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.getPostsBefore(id, me!!.id)
                            call.respond(response)
                        }
                        post("/{id}/like") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.likeById(id, me!!.id)
                            call.respond(response)
                        }
                        delete("/{id}/dislike") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.dislikeById(id, me!!.id)
                            call.respond(response)
                        }
                        post("/{id}/repost") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val input = call.receive<PostRequestDto>()
                            val response = postService.repostById(id, me!!, input)
                            call.respond(response)
                        }
                        post {
                            val input = call.receive<PostRequestDto>()
                            postService.save(input, me!!)
                            call.respond(HttpStatusCode.OK)
                        }
                        post("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val input = call.receive<PostRequestDto>()
                            postService.saveById(id, input, me!!)
                            call.respond(HttpStatusCode.OK)
                        }
                        delete("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            if (!postService.removeById(id, me!!)) {
                                println("You can't delete post of another user")
                            }
                        }
                    }
                }

                route("/media") {
                    post {
                        val multipart = call.receiveMultipart()
                        val response = fileService.save(multipart)
                        call.respond(response)
                    }
                }
            }
        }
    }
}
