package application

import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import kotlin.test.assertEquals

class ServiceTest : AutoCloseKoinTest() {
    @MockK
    lateinit var repository: Repository

    private val applicationModuleTest = module {
        factory {
            Service(get(), get())
        }
        single {
            repository
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
        StandAloneContext.startKoin(listOf(applicationModuleTest))
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun testAllUsers() {
        //Arrange
        val user = User("Loko", "Abreu", "loko@email.com", "(21) 99999-9999")
        val expectedNumberOfUsers = 1
        val expectedFirstNameOfUser = "Loko"
        every { repository.allUsers() } returns listOf(user)
        val service: Service by inject()

        //Action
        val allUsers = service.allUsers()

        //Assert
        assertEquals(expectedNumberOfUsers, allUsers.size)
        assertEquals(expectedFirstNameOfUser, allUsers[0].firstName)
    }

    @Test
    fun testAddUser() {
        //Arrange
        val user = User("Loko", "Abreu", "loko@email.com", "(21) 99999-9999")
        val slotUser = slot<User>()
        every { repository.addUser(capture(slotUser)) } answers { nothing }
        val service: Service by inject()

        //Action
        service.addUser(user)

        //Assert
        assertEquals(user.firstName, slotUser.captured.firstName)
        assertEquals(user.lastName, slotUser.captured.lastName)
        assertEquals(user, slotUser.captured)
    }
}