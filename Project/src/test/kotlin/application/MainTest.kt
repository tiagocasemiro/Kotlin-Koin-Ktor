package application

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.verify
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import kotlin.test.assertEquals

class MainTest : AutoCloseKoinTest() {
    private val applicationModuleTest = module {
        factory {
            Service(get(), get())
        }
        single {
            Repository(DataBaseTest().jdbcTemplate())
        }
        factory {
            Retrofit.Builder()
                .baseUrl("http://localhost:8081")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .connectTimeout(20000, TimeUnit.MILLISECONDS)
                        .addInterceptor(HeaderInterceptor())
                        .addInterceptor(MockInterceptor())
                        .hostnameVerifier(HostnameVerifier { hostname, _ ->
                            "http://localhost:8081".contains(hostname)
                        })
                        .build()
                )
                .build()
        }
        factory {
            get<Retrofit>().create(RestApi::class.java)
        }
    }

    @Before
    fun setup() {
        org.apache.log4j.BasicConfigurator.configure()
        startKoin(listOf(applicationModuleTest))
    }

    @Test
    fun testAddUser() = withTestApplication(Application::main) {
        val user = User("Loko", "Abreu", "loko@email.com", "(21) 99999-9999")
        val jsonUser = Gson().toJson(user)

        with(handleRequest(HttpMethod.Get, "/user/$jsonUser")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(jsonUser, response.content)
        }
    }

    @Test
    fun testAllUsers() = withTestApplication(Application::main) {
        val service: Service by inject()
        val user = User("Loko", "Abreu", "loko@email.com", "(21) 99999-9999")
        val jsonUser = Gson().toJson(user)
        service.addUser(user)

        with(handleRequest(HttpMethod.Get, "/users")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("[$jsonUser]", response.content)
        }
    }
}