package application

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

class DataBase {
    fun dataSource() : DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver")
        dataSource.url = "jdbc:mysql://127.0.0.1/user_db"
        dataSource.username = "root"
        dataSource.password = "root"

        return dataSource
    }

    fun jdbcTemplate() : JdbcTemplate {
        return JdbcTemplate(dataSource())
    }
}