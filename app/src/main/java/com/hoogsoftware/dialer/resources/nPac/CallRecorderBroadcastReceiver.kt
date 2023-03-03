package com.hoogsoftware.dialer.resources.nPac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CallRecorderBroadcastReceiver : BroadcastReceiver() {

    private var isIncoming: Boolean = false
    private lateinit var phoneNumber: String
    var recorder= MediaRecorder()
    private lateinit var fileName: String
    private lateinit var filePath: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                isIncoming = true
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: ""
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                if (context != null) {
                    startRecording(context)
                }
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                if (context != null) {
                    stopRecording(context)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startRecording(context: Context) {
        Toast.makeText(context,"Calling Started....",Toast.LENGTH_LONG).show()

        try{
            val path = Environment.getExternalStorageDirectory().absolutePath + "/CallRecordings"
            val dir = File(path)

            if (!dir.exists()) {
                //  Log.d(TAG, "startRecording: calleddddddddddd1")
                dir.mkdirs()
            }

            fileName = "Recording_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.mp3"
            filePath = "$path/$fileName"
            // Log.d(TAG, "startRecording92: $filePath")




            val fil3ename = "myFile.acc"
            val directoryPath = "/path/to/directory" // Replace with your desired directory path

            val file = File(directoryPath, fil3ename)

// Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile()
            }

// Optional: Write data to the file using a FileOutputStream
            val outputStream = FileOutputStream(file)
// Write MP3 data to the outputStream
            outputStream.close()
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setOutputFile(fil3ename)

        }catch (e:java.lang.Exception){

        }
        //  val path = Environment.getExternalStorageDirectory().absolutePath + "/CallRecordings"


        try {
             recorder.prepare()
             recorder.start()
        } catch (e: IOException) {
            Log.e("CallRecorde11r", "start failed  ${e.message}")
            //  Log.d(TAG, "startRecording: ")
            // Timber.tag(TAG).e("startRecording: %s", e.toString())
            //System.out.ptintln
        }
    }

    private fun stopRecording(context: Context) {
        Toast.makeText(context,"Calling Done....",Toast.LENGTH_LONG).show()


        try {
            recorder.stop()
            recorder.release()
        } catch (e: Exception) {
            Log.e("CallRecorder", "stop failed")
        }

        val file = File(filePath)

        if (file.exists()) {
            Log.d("CallRecorder", "Recorded call saved to $filePath")
        } else {
            Log.e("CallRecorder", "Recording file not found!")
        }
    }
}