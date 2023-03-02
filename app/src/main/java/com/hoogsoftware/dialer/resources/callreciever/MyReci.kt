package com.hoogsoftware.dialer.resources.callreciever

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast


class MyReci : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            showToast(context, "Call started...")
            Toast.makeText(context,"Hi",Toast.LENGTH_LONG).show()
            Log.d(TAG, "onReceive: 1111")
        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_IDLE) {
            showToast(context, "Call ended...")
            Toast.makeText(context,"Hi",Toast.LENGTH_LONG).show()


            Log.d(TAG, "onReceive: 2222")

        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
            showToast(context, "Incoming call...")
            Toast.makeText(context,"Hi",Toast.LENGTH_LONG).show()

            Log.d(TAG, "onReceive: 3333")
            //Log.d(TAG, "onReceive: ")

        }
    }

    fun showToast(context: Context?, message: String?) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}