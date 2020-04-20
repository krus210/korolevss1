package ru.korolevss.route

import com.google.gson.Gson
import com.google.gson.JsonObject
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
import org.json.JSONObject
import ru.korolevss.dto.AuthenticationRequestDto
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
                        val input = call.receive<JSONObject>()
                        val username = input.getString("username")
                        val password = input.getString("password")
                        val response = userService.save(username, password)
                        when {
                            response.token.contains("response 400") -> {
                                val message = JSONObject().put("error", "This user is already existed")
                                call.respond(HttpStatusCode.BadRequest, message)
                            }
                            response.token.contains("response 503") -> {
                                val message =
                                    JSONObject().put("error", "another user make registration at the same time")
                                call.respond(HttpStatusCode.ServiceUnavailable, message)
                            }
                            else -> {
                                call.respond(response)
                            }
                        }
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
                    }

                    route("/posts") {
                        get {
                            val response = postService.getAll()
                            call.respond(response)
                        }
                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.getById(id)
                            call.respond(response)
                        }
                        get("/{id}/like") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.likeById(id)
                            call.respond(response)
                        }
                        get("/{id}/dislike") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.dislikeById(id)
                            call.respond(response)
                        }
                        get("/{id}/comment") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.commentById(id)
                            call.respond(response)
                        }
                        get("/{id}/share") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            val response = postService.shareById(id)
                            call.respond(response)
                        }
                        post {
                            val input = call.receive<PostRequestDto>()
                            val response = postService.save(input, me) ?: HttpStatusCode.Forbidden
                            call.respond(response)
                        }
                        delete("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException(
                                "id",
                                "Long"
                            )
                            if (!postService.removeById(id, me)) {
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
