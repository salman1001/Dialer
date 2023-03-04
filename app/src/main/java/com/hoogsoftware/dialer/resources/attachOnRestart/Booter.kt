package com.hoogsoftware.dialer.resources.attachOnRestart

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log


class Booter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // start a service to re-register the broadcast receiver
            println("HI SALMAN HOW ARE YOU")
//            val serviceIntent = Intent(context, YouService::class.java)
//            serviceIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
//            Log.d(TAG, "onReceive: starting NEW Activity")
//            context.startService(serviceIntent)
//            val intent = Intent(context, YouService::class.java)
//            intent.putExtra("key", "value")
//            startActivity(intent)
                        val serviceIntent = Intent(context, YouService::class.java)
            serviceIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            Log.d(TAG, "onReceive: starting NEW Activity")
            context.startService(serviceIntent)



        }
    }
}