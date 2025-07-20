package com.example

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.security.MessageDigest

@Serializable
data class User(
    val id: Int? = null,
    val email: String,
    val mobile: String,
    val password: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val mobile = varchar("mobile", 20).uniqueIndex()
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(id)
}

object TimelinePost :Table(){
    val id=integer("id").autoIncrement()
    val postText=varchar("postText",20000).uniqueIndex()
}

object HashUtil {
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
