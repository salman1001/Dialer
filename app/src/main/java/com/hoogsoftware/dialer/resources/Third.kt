package com.hoogsoftware.dialer.resources

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.CallLog
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoogsoftware.dialer.R
import com.hoogsoftware.dialer.resources.extra.CallRecord
import com.hoogsoftware.dialer.resources.recorder.AudioPlayerImp
import com.hoogsoftware.dialer.resources.recorder.RecorderImple
import com.hoogsoftware.dialer.resources.trail.DeviceAdminDemo
import com.hoogsoftware.dialer.resources.trialJava.Looker
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Third : AppCompatActivity() {
private var callLogModelArrayList= ArrayList<CallLogModel>()
    private var rv_call_logs: RecyclerView? = null
    private var callLogAdapter: CallLogAdapter? = null
    var str_number: String? = null
    var str_contact_name: String? = null
    var str_call_type: String? = null
    var str_call_full_date: String? = null
    var str_call_date: String? = null
  //  var viewModel=ViewModelSaveToken()
    var str_call_time: String? = null
    var str_call_time_formatted: String? = null
    var str_call_duration: String? = null

    private fun Init() {


        rv_call_logs = findViewById(R.id.activity_main_rv)
        rv_call_logs!!.setHasFixedSize(true)
        rv_call_logs!!.layoutManager = LinearLayoutManager(this)
        callLogModelArrayList = ArrayList()
        callLogAdapter = CallLogAdapter(this, callLogModelArrayList)
        rv_call_logs!!.adapter = callLogAdapter



    }
    @SuppressLint("Range")
   fun FetchCallLogs() {
        // reading all data in descending order according to DATE
        val sortOrder = CallLog.Calls.DATE + " DESC"
        val cursor: Cursor? = this.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        )

        var counter=200;

        while (cursor!!.moveToNext()&&counter>=0) {
            counter--
            //cursor.getString()
            str_number = cursor!!.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            str_contact_name =
                cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            str_contact_name = if (str_contact_name == null || str_contact_name == "") "Unknown" else str_contact_name
            str_call_type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))
            str_call_full_date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))
            str_call_duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
            val dateFormatter = SimpleDateFormat("dd MMM yyyy")
            str_call_date = dateFormatter.format(Date(str_call_full_date!!.toLong()))
            //   Toast.makeText(this,str_contact_name+"     "+str_call_duration,Toast.LENGTH_LONG).show()
            val timeFormatter = SimpleDateFormat("HH:mm:ss")
           // var text:TextView = findViewById(R.id.tt)
            //  text.append("phone number =  $str_contact_name\n"  )
            str_call_time = timeFormatter.format(Date(str_call_full_date!!.toLong()))
            //str_call_time = getFormatedDateTime(str_call_time, "HH:mm:ss", "hh:mm ss");
            str_call_duration = DurationFormat(str_call_duration)
          //  text.append(str_contact_name+"     "+str_call_duration+"   "+str_call_date)

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
            val callLogItem = CallLogModel(
                str_number!!, str_contact_name!!, str_call_type!!,
                str_call_date!!, str_call_time!!, str_call_duration!!
            )


            callLogModelArrayList!!.add(callLogItem)
            // callLogAdapter!!.notifyDataSetChanged()

            //  SendDataToServer(callLogItem)
        }
        callLogAdapter!!.notifyDataSetChanged()
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



    //start


    private val recorder by lazy {
        RecorderImple(applicationContext)
    }

    private val player by lazy {
        AudioPlayerImp(applicationContext)
    }

    private var audioFile: File? = null






@RequiresApi(Build.VERSION_CODES.O)
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportActionBar!!.title = "Call Logs"
    Init()
    supportActionBar!!.setBackgroundDrawable( ColorDrawable(
                Color.parseColor("#486BD3")
            ))

    getPermission()
//    if (activity is AppCompatActivity) {
//        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
//        (activity as AppCompatActivity).supportActionBar?.title = "Physics"
//        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
//            ColorDrawable(
//                Color.parseColor("#486BD3")
//            )
//        )
//    }






   // output = Environment.getExternalStorageDirectory().absolutePath + "/recording1232.mp3"
    mediaRecorder = MediaRecorder()

    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    mediaRecorder?.setOutputFile(output)

    val d=Looker()
    val str=d.di()
//    Toast.makeText(this,str,Toast.LENGTH_LONG).show()
  // Log.d(TAG, "onCreate: $str")



//    bt.setOnClickListener {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            ActivityCompat.requestPermissions(this, permissions,0)
//        } else {
//            if (countyer==0){
//                File(cacheDir, "audio.mp3").also {
//                    recorder.start(it)
//                    audioFile = it
//                }
//                Log.d(TAG, "onCreate: s1")
//
//                recorder.start(audioFile!!)
//                Log.d(TAG, "onCreate: s2")
//
//
////                startRecording()
////                Log.d(TAG, "onCreate: called1")
//               countyer++;
//
//            }
//            else{
//                Log.d(TAG, "onCreate: s3")
//                recorder.stop()
//                Log.d(TAG, "onCreate: s4")
//
//                player.playFile(audioFile!!)
//                Log.d(TAG, "onCreate: s5")
//
//
////                stopRecording()
////               // mediaRecorder
////
////                Log.d(TAG, "onCreate: called2")
////
////                    Log.d(TAG, "onCreate: 3232")
////                    mediaPlayer!!.setDataSource(output)
////                    mediaPlayer!!.prepare()
////                    mediaPlayer!!.start()
//
//
////                GlobalScope.launch {
////                    delay(3000)
////                    playAudio(mediaRecorder!!)
////                }
//
//            }
//        }
//    }

    try {
        if (!mDPM!!.isAdminActive(mAdminName!!)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Click on Activate button to secure your application."
            )
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            // mDPM.lockNow();
            // Intent intent = new Intent(MainActivity.this,
            // TrackDeviceService.class);
            // startService(intent);
        }
    }catch (i:java.lang.Exception){

    }
    mDPM = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    mAdminName = ComponentName(this, DeviceAdminDemo::class.java)

}
    private var countyer=0

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if(state) {
            if(!recordingStopped){
                Toast.makeText(this,"Stopped!", Toast.LENGTH_SHORT).show()
                mediaRecorder?.pause()
                recordingStopped = true
               // button_pause_recording.text = "Resume"
            }else{
                resumeRecording()
//                GlobalScope.launch {
//                    Log.d(TAG, "pauseRecording: called")
//                    mediaPlayer!!.setDataSource(output)
//                    mediaPlayer!!.prepare()
//                    mediaPlayer!!.start()
//
//                }
                mediaPlayer!!.setDataSource(output)
            }
        }
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        Toast.makeText(this,"Resume!", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
      //  button_pause_recording.text = "Pause"
        recordingStopped = false
    }

 fun playAudio(mediaRecorder: MediaRecorder) {
//        playButton.isEnabled = false
//        recordButton.isEnabled = false
//        stopButton.isEnabled = true


//        mediaPlayer = MediaPlayer()
//        mediaPlayer?.setDataSource(audioFilePath)
//        mediaPlayer?.prepare()
//        mediaPlayer?.start()
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    private var   output = Environment.getExternalStorageDirectory()
        .absolutePath + "/recording1232.mp3"

    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    private var mediaPlayer:MediaPlayer?=null

lateinit var callRecord:CallRecord













    ////Dexter

    lateinit var dexter : DexterBuilder
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS
                ,Manifest.permission.READ_PHONE_NUMBERS
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                @RequiresApi(Build.VERSION_CODES.R)
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {

                        if (report.areAllPermissionsGranted()) {
                            getSimSubcription()
                            getAllCallHistory()
                            FetchCallLogs()

                            Toast.makeText(this@Third, "Permissions Granted Finally ", Toast.LENGTH_SHORT).show()
                            // showSelectionDialog()

                        } else {
                            AlertDialog.Builder(this@Third, R.style.Theme_Dialer).apply {
                                setMessage("please allow the required permissions")
                                    .setCancelable(false)
                                    .setPositiveButton("Settings") { _, _ ->
                                        val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .apply {
                                                val uri = Uri.fromParts("package", packageName, null)
                                                data = uri
                                            }
                                        resultLauncher.launch(reqIntent)
                                    }
                                // setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                val alert = this.create()
                                alert.show()
                            }
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener{
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
        dexter.check()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getSimSubcription() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
                telephonyManager.line1Number
                Log.d(TAG, "getSimSubcription: ${telephonyManager.line1Number}")



                val activeSubscriptionInfoList = SubscriptionManager.from(applicationContext)
                    .activeSubscriptionInfoList

                for (subscriptionInfo in activeSubscriptionInfoList) {
                    Log.d(TAG, "getSimSubcription: ${subscriptionInfo.iccId}${subscriptionInfo.displayName}")
                    //  val simSubscriptionId = subscriptionInfo.subscriptionId
                    // Do something with the subscription ID
                }


                val subscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
                val phoneNumbers = activeSubscriptions.mapNotNull { it.number }
                Log.d(TAG, "getSimSubcription2: $phoneNumbers")




            } else {
            }


        } else {
            // Handle the case for devices with API level lower than 22
        }









    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> dexter.check()
    }


@SuppressLint("Range")
fun getAllCallHistory() {


    val projection = arrayOf(
        CallLog.Calls._ID,
        CallLog.Calls.NUMBER,
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION,
        CallLog.Calls.TYPE
    )

    // Define the selection criteria for a particular SIM card number
 //   val selection = "${CallLog.Calls.NUMBER} = '918287258430'"

    // Access the call log with a query
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            CallLog.Calls.DEFAULT_SORT_ORDER
        )

        // Process the call log results
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get the call details
                val callNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))
                val callDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
                val callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))

            } while (cursor.moveToNext())
        }
        cursor?.close()
    } else {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), 1)
    }
}

    private val REQUEST_CODE = 0
    private var mDPM: DevicePolicyManager? = null
    private var mAdminName: ComponentName? = null
    }