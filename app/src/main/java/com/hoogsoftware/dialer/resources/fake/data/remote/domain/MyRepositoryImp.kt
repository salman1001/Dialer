package com.hoogsoftware.dialer.resources.fake.data.remote.domain

import android.content.Context
import com.hoogsoftware.dialer.resources.fake.data.remote.MyApi
import javax.inject.Inject

class MyRepositoryImp @Inject constructor( private val api:MyApi,private val context: Context):Repository {
    override suspend fun doetworkCall() {

    }
}