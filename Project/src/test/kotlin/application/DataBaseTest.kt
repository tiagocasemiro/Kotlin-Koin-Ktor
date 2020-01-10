package application

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

class DataBaseTest {
    fun dataSource() : DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .build()
    }

    fun jdbcTemplate() : JdbcTemplate {
        return JdbcTemplate(dataSource())
    }
}