package com.hoogsoftware.dialer.resources.callreciever

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat

class CallBroadcastReceiver : BroadcastReceiver() {
    private fun handleIncomingCall(context: Context, phoneCall: PhoneCall.Incoming) {
        Toast.makeText(context, "Incoming: ${phoneCall.number}", Toast.LENGTH_SHORT).show()
    }

    private fun handleOutgoingCall(context: Context, phoneCall: PhoneCall.Outgoing) {
        Toast.makeText(context, "Outgoing11: ${phoneCall.number}", Toast.LENGTH_SHORT).show()
    }


    @RequiresPermission(allOf = [
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.PROCESS_OUTGOING_CALLS
    ])    override fun onReceive(context: Context, intent: Intent) {
        if (!context.checkPermissions(
                Manifest.permission.READ_CALL_LOG,

                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            return
        }
        when (val phoneCall = intent.phoneCallInformation()) {
            is PhoneCall.Incoming -> handleIncomingCall(context, phoneCall)
            is PhoneCall.Outgoing -> handleOutgoingCall(context, phoneCall)
            else -> {}
        }
    }

    @RequiresPermission(allOf = [
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE
    ])
    fun Intent.phoneCallInformation(): PhoneCall {

        val action = action
        val extras = extras
        if (extras != null) {
            if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                // Incoming Call
                val state = getStringExtra(TelephonyManager.EXTRA_STATE)!!
                if (hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) && state == TelephonyManager.EXTRA_STATE_RINGING) {
                    val number = getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)!!

                    return PhoneCall.Incoming(state, number)
                }
            } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                val number = getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                return PhoneCall.Outgoing(number)
            }
        }
        return PhoneCall.Unknown
    }

    fun Context.checkPermissions(vararg permissions: String): Boolean {
        return listOf(permissions.forEach {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }).any()
    }

}