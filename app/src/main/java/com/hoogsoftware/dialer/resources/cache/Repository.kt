package com.hoogsoftware.dialer.resources.cache

class Repository(private val pref:Cache) {
    suspend fun saveAuthToken(token: Long){
        pref.saveToken(token)
    }
}