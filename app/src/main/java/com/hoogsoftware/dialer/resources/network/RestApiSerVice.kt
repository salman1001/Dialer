package com.hoogsoftware.dialer.resources.network


import RestApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RestApiSerVice {
    fun addUser(userData: CallInfo, onResult: (CallInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.addUser(userData).enqueue(
            object : Callback<CallInfo> {
                override fun onFailure(call: Call<CallInfo>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse( call: Call<CallInfo>, response: Response<CallInfo>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }
}