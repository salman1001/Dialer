package com.hoogsoftware.dialer.resources.nPac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBootCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action==Intent.ACTION_BOOT_COMPLETED){
            println("Please send the data to Firebase for notification")
        }
    }
}