package com.hoogsoftware.dialer.resources.fake.data.remote

import retrofit2.http.GET

interface MyApi {
    @GET("/test")
    suspend fun donetWork()
}