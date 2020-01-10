package application

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.ResponseBody

class MockInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val mediaJson = "application/json".toMediaType()
        val path = chain.request().url.encodedPath
        var code = 200
        val json = StringBuilder()

        when {
            path.contains("/api/user") -> {
                json.append(Gson().toJson(User("Super Loko", "Abreu", "loko@email.com", "(21) 99999-9999")))
            }
            else -> code = 404
        }

        return okhttp3.Response.Builder()
            .body(ResponseBody.create(mediaJson, json.toString()))
            .request(chain.request())
            .protocol(Protocol.HTTP_2)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
            .message(String())
            .code(code)
            .build()
    }
}