package application

import retrofit2.Call
import retrofit2.http.GET

interface RestApi {

    @GET("/api/user")
    fun invoices() : Call<User>
}