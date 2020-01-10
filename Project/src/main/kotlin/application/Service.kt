package application

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Service (private val repository : Repository, private val restApi: RestApi){

    init {
        println("Service")
    }

    fun addUser(user: User) {
        repository.addUser(user)
    }

    fun allUsers() : List<User> {
        return repository.allUsers()
    }

    fun userFromApi() {
        Thread(Runnable {
            restApi.invoices().enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {
                    println("Falha")
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    println(Gson().toJson(response.body()))
                }
            })
        }).start()
    }
}