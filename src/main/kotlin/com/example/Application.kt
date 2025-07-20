package com.example

import io.ktor.http.ContentType
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun main() {
    // Connect to PostgreSQL

    Database.connect(
        url = "jdbc:postgresql://localhost:5432/ktor_demo",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    transaction {
        create(Users)
        create(TimelinePost)
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respondText("Ktor server is running!", ContentType.Text.Plain)
            }
            post("/login") {
                /*val request = call.receive<LoginRequest>()
                val user = transaction {
                    UserEntity.find { Users.email eq request.email }.firstOrNull()
                }
                if (user != null && user.password == request.password) {
                    call.respond(LoginResponse(success = true, message = "Login successful"))
                } else {
                    call.respond(LoginResponse(success = false, message = "Invalid credentials"))
                }*/
            }
            post("/signup") {
                val user = call.receive<SignupRequest>()
                val hashedPassword = HashUtil.hashPassword(user.password)

                var userId: Int? = null
                try {
                    transaction {
                        userId = Users.insert {
                            it[email] = user.email
                            it[mobile] = user.mobile
                            it[password] = hashedPassword
                        } get Users.id
                    }
                    call.respond(mapOf("success" to true, "message" to "User registered"))
                } catch (e: Exception) {
                    call.respond(mapOf("success" to false, "message" to "Error: ${e.localizedMessage}"))
                }
            }
        }

    }.start(wait = true)
}

@Serializable
data class SignupRequest(val email: String, val password: String,val mobile:String)

@Serializable
data class LoginResponse(val success: Boolean, val message: String)