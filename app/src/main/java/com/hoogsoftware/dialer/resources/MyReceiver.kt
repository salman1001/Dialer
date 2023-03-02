package com.hoogsoftware.dialer.resources

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var dialedNumber: String? = null // = getResultData();
        if (dialedNumber == null) {
            // No reformatted number, use the original
            dialedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
        }
        //Log.d("Dialed Number: ", dialedNumber);
        if (dialedNumber == SHOW_APP_CODE) {
            val packageManager: PackageManager = context.getPackageManager()
            val componentName = ComponentName(context, Third::class.java)
            packageManager.setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            //Intent to launch MainActivity
            val intent_to_mainActivity = Intent(context, Third::class.java)
            intent_to_mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent_to_mainActivity)

            // My app will bring up, so cancel the dialer broadcast
            resultData = null
        }
    }

    companion object {
        var SHOW_APP_CODE = "*1234#"
    }
}