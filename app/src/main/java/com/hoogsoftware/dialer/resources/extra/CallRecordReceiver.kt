package com.hoogsoftware.dialer.resources.extra

import android.content.Context
import android.media.MediaDrm
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.log

open class CallRecordReceiver(private var callRecord: CallRecord) : PhoneCallReciever() {

    companion object {
        private val TAG = CallRecordReceiver::class.java.simpleName

        const val ACTION_IN = "android.intent.action.PHONE_STATE"
        const val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
        const val EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER"
        private var recorder: MediaRecorder? = null
    }

    private var audioFile: File? = null
    private var isRecordStarted = false

    override fun onIncomingCallReceived(context: Context, number: String?, start: Date) {
    }

    override fun onIncomingCallAnswered(context: Context, number: String?, start: Date) {
        startRecord(context, "incoming", number)
    }

    override fun onIncomingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        stopRecord(context)
    }

    override fun onOutgoingCallStarted(context: Context, number: String?, start: Date) {
        startRecord(context, "outgoing", number)
    }

    override fun onOutgoingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        stopRecord(context)
    }

    override fun onMissedCall(context: Context, number: String?, start: Date) {
    }

    // Derived classes could override these to respond to specific events of interest
    protected open fun onRecordingStarted(context: Context, callRecord: CallRecord, audioFile: File?) {}

    protected open fun onRecordingFinished(context: Context, callRecord: CallRecord, audioFile: File?) {}

    private fun startRecord(context: Context, seed: String, phoneNumber: String?) {
        try {
            val isSaveFile = PrefsHelper.readPrefBool(context, CallRecord.PREF_SAVE_FILE)
            LogUtils.i(TAG, "isSaveFile: $isSaveFile")

            // is save file?
            if (!isSaveFile) {
                return
            }

            if (isRecordStarted) {
                try {
                    recorder?.stop()  // stop the recording
                } catch (e: RuntimeException) {
                    // RuntimeException is thrown when stop() is called immediately after start().
                    // In this case the output file is not properly constructed ans should be deleted.
                    LogUtils.d(TAG, "RuntimeException: stop() is called immediately after start()")
                    audioFile?.delete()
                }

                releaseMediaRecorder()
                isRecordStarted = false
            } else {
                if (prepareAudioRecorder(context, seed, phoneNumber)) {
                    recorder!!.start()
                    isRecordStarted = true
                    onRecordingStarted(context, callRecord, audioFile)
                    LogUtils.i(TAG, "record start11")
                } else {
                    LogUtils.i(TAG, "record start22")


                    releaseMediaRecorder()
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.d(TAG, "startRecord10: ${e.message}")
            releaseMediaRecorder()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Log.d(TAG, "startRecord19: ${e.message}")

            releaseMediaRecorder()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "startRecord18: ${e.message}")

            releaseMediaRecorder()
        }
    }

    private fun stopRecord(context: Context) {
        try {
            if (recorder != null && isRecordStarted) {
                releaseMediaRecorder()
                isRecordStarted = false
                onRecordingFinished(context, callRecord, audioFile)
                LogUtils.i(TAG, "record stop")
            }
        } catch (e: Exception) {
            releaseMediaRecorder()
            e.printStackTrace()
        }
    }

    private fun prepareAudioRecorder(
        context: Context, seed: String, phoneNumber: String?
    ): Boolean {
        try {
            var fileName = PrefsHelper.readPrefString(context, CallRecord.PREF_FILE_NAME)
            val dirPath = PrefsHelper.readPrefString(context, CallRecord.PREF_DIR_PATH)
            val dirName = PrefsHelper.readPrefString(context, CallRecord.PREF_DIR_NAME)
            val showSeed = PrefsHelper.readPrefBool(context, CallRecord.PREF_SHOW_SEED)
            val showPhoneNumber =
                PrefsHelper.readPrefBool(context, CallRecord.PREF_SHOW_PHONE_NUMBER)
            val outputFormat = PrefsHelper.readPrefInt(context, CallRecord.PREF_OUTPUT_FORMAT)
            val audioSource = PrefsHelper.readPrefInt(context, CallRecord.PREF_AUDIO_SOURCE)
            val audioEncoder = PrefsHelper.readPrefInt(context, CallRecord.PREF_AUDIO_ENCODER)

            val sampleDir = File("$dirPath/$dirName")

            if (!sampleDir.exists()) {
                sampleDir.mkdirs()
            }

            val fileNameBuilder = StringBuilder()
            fileNameBuilder.append(fileName)
            fileNameBuilder.append("_")

            if (showSeed) {
                fileNameBuilder.append(seed)
                fileNameBuilder.append("_")
            }

            if (showPhoneNumber && phoneNumber != null) {
                fileNameBuilder.append(phoneNumber)
                fileNameBuilder.append("_")
            }

            fileName = fileNameBuilder.toString()

            val suffix: String
            when (outputFormat) {
                MediaRecorder.OutputFormat.AMR_NB -> {
                    suffix = ".amr"
                }
                MediaRecorder.OutputFormat.AMR_WB -> {
                    suffix = ".amr"
                }
                MediaRecorder.OutputFormat.MPEG_4 -> {
                    suffix = ".mp4"
                }
                MediaRecorder.OutputFormat.THREE_GPP -> {
                    suffix = ".3gp"
                }
                else -> {
                    suffix = ".amr"
                }
            }
//            if (!sampleDir.exists()){
//                sampleDir.createNewFile()
//            }
//
            audioFile = File.createTempFile(fileName, suffix, sampleDir)
//
//            if (!audioFile!!.exists()){
//                audioFile!!.createNewFile()
//
////            }
//            val fil3ename = "myFile.acc"
//            val directoryPath = "/path/to/directory" // Replace with your desired directory path
//
//            val file = File(directoryPath, fil3ename)

// Create the file if it doesn't exist
//            scope.launch {
//
//                val fil3ename = "myFile.acc"
//                val directoryPath = "/path/to/directory" // Replace with your desired directory path
//
//                val file = File(directoryPath, fil3ename)
//                if (!file.exists()) {
//                    file.createNewFile()
//                }
//                val outputStream = FileOutputStream(file)
//// Write MP3 data to the outputStream
//                outputStream.close()
//
//                recorder = MediaRecorder()
//                recorder?.apply {
//                    setAudioSource(audioSource)
//                    setOutputFormat(outputFormat)
//                    setAudioEncoder(audioEncoder)
//                    setOutputFile(fil3ename!!)
//                    setOnErrorListener { _, _, _ -> }
//                }
//            }
//
//// Optional: Write data to the file using a FileOutputStream
//            val outputStream = FileOutputStream(file)
//// Write MP3 data to the outputStream
//            outputStream.close()
//
            recorder = MediaRecorder()
            recorder?.apply {
                setAudioSource(audioSource)
                setOutputFormat(outputFormat)
                setAudioEncoder(audioEncoder)
                setOutputFile(fileName!!)
                setOnErrorListener { _, _, _ -> }
            }

            try {
                recorder?.prepare()
            } catch (e: IllegalStateException) {
                LogUtils.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
                releaseMediaRecorder()
                return false
            } catch (e: IOException) {
                LogUtils.d(TAG, "IOException preparing MediaRecorder: " + e.message)
                releaseMediaRecorder()
                return false
            }

            return true
        } catch (e: Exception) {
            LogUtils.e(e)
            e.printStackTrace()
            return false
       }
    }

    private fun releaseMediaRecorder() {
        recorder?.apply {
            reset()
            release()
        }
        recorder = null
    }
}

private val scope = CoroutineScope(Dispatchers.IO)

