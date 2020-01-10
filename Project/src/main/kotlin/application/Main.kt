package application

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin
import java.util.concurrent.TimeUnit




import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier

val applicationModule = module {
    factory {
        Service(get(), get())
    }
    factory {
        DataBase()
    }
    factory {
        Repository(get<DataBase>().jdbcTemplate())
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

fun main(args: Array<String>) {
    org.apache.log4j.BasicConfigurator.configure()
    startKoin(listOf(applicationModule))
    embeddedServer(
        Netty,
        port = 8081,
        module = Application::main
    ).apply {
        start(wait = true)
    }
}

fun Application.main() {
    install(ContentNegotiation) { gson() }
    val service: Service by inject()
    routing {
        get("/user/{user}") {
            val user = Gson().fromJson(call.parameters["user"], User::class.java)
            service.addUser(user)
            call.respond(user)
        }

        get("/users") {
            val users = service.allUsers()
            call.respond(users)
        }

        get("/api/user") {
            call.respond(User("El Loko", "Abreu", "loko@email.com", "(21) 99999-9999"))
        }

        get("/rest") {
            service.userFromApi()
            call.respondText("OK")
        }
    }
}