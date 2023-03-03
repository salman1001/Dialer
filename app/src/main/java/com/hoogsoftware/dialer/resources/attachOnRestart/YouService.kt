package com.hoogsoftware.dialer.resources.attachOnRestart

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.annotation.Nullable


class YouService : Service() {
   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      // register your broadcast receiver here
      println("HI SALMAN1 HOW ARE YOU")

      val receiver = Booter()

      val filter = IntentFilter()
      filter.addAction("android.intent.action.PHONE_STATE")
      registerReceiver(receiver, filter)
      return START_STICKY
   }

   @Nullable
   override fun onBind(intent: Intent?): IBinder? {
      return null
   }
}