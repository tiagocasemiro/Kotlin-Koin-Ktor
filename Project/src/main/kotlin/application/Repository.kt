package application

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.ResultSet

class Repository (private val jdbcTemplate: JdbcTemplate) {
    fun addUser(user : User) {
        SimpleJdbcInsert(jdbcTemplate).withTableName("USERS").apply {
            execute(
                mapOf("first_name" to user.firstName,
                    "last_name" to user.lastName,
                    "email" to user.email,
                    "phone" to user.phone)
            )
        }
    }

    fun allUsers(): List<User> = jdbcTemplate.query("SELECT FIRST_NAME, LAST_NAME, EMAIL, PHONE FROM USERS") { rs: ResultSet, _: Int ->
        User(rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"))
    }
}