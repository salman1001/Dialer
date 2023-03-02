package com.hoogsoftware.dialer.resources.callreciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hoogsoftware.dialer.resources.Third

class Boot : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {

            val activityIntent = Intent(context, Third::class.java)

            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(activityIntent)
        }
    }
}
