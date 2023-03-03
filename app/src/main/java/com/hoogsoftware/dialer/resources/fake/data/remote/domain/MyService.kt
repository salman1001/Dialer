package com.hoogsoftware.dialer.resources.fake.data.remote.domain

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyService: Service() {
    @Inject
    lateinit var repository: Repository
    override fun onBind(p0: Intent?): IBinder? {
        return null

    }

    override fun onCreate() {
        super.onCreate()
        //code here
    }
}