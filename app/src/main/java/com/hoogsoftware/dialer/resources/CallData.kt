package com.hoogsoftware.dialer.resources

//import androidx.test.core.app.ApplicationProvider
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.CallLog
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CallData(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var str_number: String? = null
    private var str_contact_name: String? = null
    private var str_call_type: String? = null
    private var str_call_full_date: String? = null
    private var str_call_date: String? = null
    private var str_call_time: String? = null
    private var str_call_duration: String? = null
    override fun doWork(): Result {
        val context: Context = applicationContext
//        val context:Context=this.applicationContext
        //  Log.i(TAG, "Sending data to Server started")
        try {
            SendData(context)
        } catch (e: Exception) {
            //Result.retry()
        }
        return Result.success()
    }

    @SuppressLint("Range")
    private fun SendData(ctx: Context) {
        // reading all data in descending order according to DATE
        val sortOrder = CallLog.Calls.DATE + " DESC"
        val cursor: Cursor = ctx.getContentResolver().query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        )!!

        //looping through the cursor to add data into arraylist
        while (cursor.moveToNext()) {
            str_number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            str_contact_name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            str_contact_name =
                if (str_contact_name == null || str_contact_name == "") "Unknown" else str_contact_name
            str_call_type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))
            str_call_full_date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))
            str_call_duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            val dateFormatter = SimpleDateFormat(
                "dd MMM yyyy"
            )
            str_call_date = dateFormatter.format(Date(str_call_full_date!!.toLong()))
            val timeFormatter = SimpleDateFormat(
                "HH:mm:ss"
            )
            str_call_time = timeFormatter.format(Date(str_call_full_date!!.toLong()))
            str_call_type = when (str_call_type!!.toInt()) {
                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                CallLog.Calls.MISSED_TYPE -> "Missed"
                CallLog.Calls.VOICEMAIL_TYPE -> "Voicemail"
                CallLog.Calls.REJECTED_TYPE -> "Rejected"
                CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Externally Answered"
                else -> "NA"
            }
            str_call_duration = DurationFormat(str_call_duration)
            val callLogItem = CallLogModel(
                str_number!!, str_contact_name!!, str_call_type!!,
                str_call_date!!, str_call_time!!, str_call_duration!!
            )
            SendDataToServer(callLogItem)
        }
    }

    private fun SendDataToServer(callLogItem: CallLogModel) {
//        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//        val myRef: DatabaseReference = database.getReference("CallLog")
//            .child(deviceName)
//            .child(callLogItem.callDate)
//            .child(callLogItem.callTime)
//        myRef.setValue(callLogItem)
    }

    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }

    private fun DurationFormat(duration: String?): String {
        var durationFormatted: String? = null
        durationFormatted = if (duration!!.toInt() < 60) {
            "$duration sec"
        } else {
            val min = duration.toInt() / 60
            val sec = duration.toInt() % 60
            if (sec == 0) "$min min" else "$min min $sec sec"
        }
        return durationFormatted
    }

    private fun getFormatedDateTime(
        dateStr: String,
        strInputFormat: String,
        strOutputFormat: String
    ): String {
        var formattedDate = dateStr
        val inputFormat: DateFormat = SimpleDateFormat(strInputFormat, Locale.getDefault())
        val outputFormat: DateFormat = SimpleDateFormat(strOutputFormat, Locale.getDefault())
        var date: Date? = null
        try {
            date = inputFormat.parse(dateStr)
        } catch (e: ParseException) {
        }
        if (date != null) {
            formattedDate = outputFormat.format(date)
        }
        return formattedDate
    }

    companion object {
        private val TAG = CallData::class.java.simpleName
    }





}