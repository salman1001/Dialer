package com.hoogsoftware.dialer.resources

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.CallLog
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
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
import com.hoogsoftware.dialer.resources.trail.DeviceAdminDemo
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Third : AppCompatActivity() {



//    class DeviceAdminDemo : DeviceAdminReceiver() {
//          @Override fun onReceive(context: Context?, intent: Intent?) {
//            super.onReceive(context!!, intent!!)
//        }
//
//         fun onEnabled(context: Context?, intent: Intent?) {}
//          fun onDisabled(context: Context?, intent: Intent?) {}
//    }


    class TService : Service() {
        var recorder: MediaRecorder? = null
        var audiofile: File? = null
        var name: String? = null
        var phonenumber: String? = null
        var audio_format: String? = null
        var Audio_Type: String? = null
        var audioSource = 0
        var context: Context? = null
        private val handler: Handler? = null
        var timer: Timer? = null
      //  var offHook = false
        var ringing = false
        var toast: Toast? = null
        var isOffHook = false
        private var recordstarted = false
        private var br_call: CallBr? = null
        override fun onBind(arg0: Intent?): IBinder? {
            // TODO Auto-generated method stub
            return null
        }

        override fun onDestroy() {
            Log.d("service", "destroy")
            super.onDestroy()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            // final String terminate =(String)
            // intent.getExtras().get("terminate");//
            // intent.getStringExtra("terminate");
            // Log.d("TAG", "service started");
            //
            // TelephonyManager telephony = (TelephonyManager)
            // getSystemService(Context.TELEPHONY_SERVICE); // TelephonyManager
            // // object
            // CustomPhoneStateListener customPhoneListener = new
            // CustomPhoneStateListener();
            // telephony.listen(customPhoneListener,
            // PhoneStateListener.LISTEN_CALL_STATE);
            // context = getApplicationContext();
            val filter = IntentFilter()
            filter.addAction(ACTION_OUT)
            filter.addAction(ACTION_IN)
            br_call = CallBr()
            registerReceiver(br_call, filter)


            // if(terminate != null) {
            // stopSelf();
            // }
            return START_NOT_STICKY
        }

        inner class CallBr : BroadcastReceiver() {
            var bundle: Bundle? = null
            var state: String? = null
            var inCall: String? = null
            var outCall: String? = null
            var wasRinging = false
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == ACTION_IN) {
                    if (intent.extras.also { bundle = it } != null) {
                        state = bundle!!.getString(TelephonyManager.EXTRA_STATE)
                        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                            inCall = bundle!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                            wasRinging = true
                            Toast.makeText(context, "IN : $inCall", Toast.LENGTH_LONG).show()
                        } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                            if (wasRinging == true) {
                                Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show()
                                val out = SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(Date())
                                val sampleDir = File(
                                    Environment.getExternalStorageDirectory(),
                                    "/TestRecordingDasa1"
                                )
                                if (!sampleDir.exists()) {
                                    sampleDir.mkdirs()
                                }
                                val file_name = "Record"
                                try {
                                    audiofile = File.createTempFile(file_name, ".amr", sampleDir)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                val path: String =
                                    Environment.getExternalStorageDirectory().getAbsolutePath()
                                recorder = MediaRecorder()
                                //                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                                recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                                recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                                recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                recorder!!.setOutputFile(audiofile!!.getAbsolutePath())
                                try {
                                    recorder!!.prepare()
                                } catch (e: IllegalStateException) {
                                    e.printStackTrace()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                recorder!!.start()
                                recordstarted = true
                            }
                        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                            wasRinging = false
                            Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show()
                            if (recordstarted) {
                                recorder!!.stop()
                                recordstarted = false
                            }
                        }
                    }
                } else if (intent.action == ACTION_OUT) {
                    if (intent.extras.also { bundle = it } != null) {
                        outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                        Toast.makeText(context, "OUT : $outCall", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        companion object {
            private const val ACTION_IN = "android.intent.action.PHONE_STATE"
            private const val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
        }
    }




  //  lateinit var reciver : CallBroadcastReceiver

//    fun loadData(){
//        val sharedPreferences: SharedPreferences = this.getSharedPreferences("getTime", Context.MODE_PRIVATE)
//        val saved=sharedPreferences.getString("time",null)
//        Toast.makeText(this, "Well This is $saved", Toast.LENGTH_SHORT).show()
//        Log.d(TAG, "saveData: $saved")
//
//
//    }

//    fun saveData(){
//        val sharedPreferences= this.getSharedPreferences("getTime", Context.MODE_PRIVATE)
//        val edit=sharedPreferences.edit()
//        edit.apply{
//            putString("time", currentTimeMillis().toString())
//            Log.d(TAG, "saveData: ${currentTimeMillis()}")
//        }
       



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

//    private fun doBan() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Name")
////89918720400291790994
//
//        // set the custom layout
//        val customLayout: View = layoutInflater.inflate(R.layout.alert, null)
//        builder.setView(customLayout)
//        val textView=customLayout.findViewById<TextView>(R.id.sim1)
//        val textView2=customLayout.findViewById<TextView>(R.id.sim2)
//        val dialog = builder.create()
//         dialog.show()
//        textView.setOnClickListener {
//            flag=1
//            dialog.dismiss()
//
//
//        }
//        textView2.setOnClickListener {
//            val dialog = builder.create()
//            dialog.dismiss()
//            flag=2
//        }
//    }


  //  @RequiresApi(Build.VERSION_CODES.P)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        supportActionBar!!.title = "Call Logs"
     //   createNotificationChannel()

       // Init()
        //getPermission()
      //  reciver = CallBroadcastReceiver()
//        IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL).also {
//            registerReceiver(reciver,it)
//
//        }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.PROCESS_OUTGOING_CALLS
//            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
     //   intent.phoneCallInformation()

//        registerReceiver(userDataChangeReceiver,
//            IntentFilter("TEST")
//        )
        //doBan()
       // FetchCallLogs()



//        for(log in getCallHistoryOfSim( getSimCardInfos()?.get(0),getAllCallHistory()) )
//        {
//            Log.d("Sim1LogDetails", "$log\n____________________________________")
//        }

//        for(log in getCallHistoryOfSim( this.getSimCardInfos()?.get(1),getAllCallHistory()) )
//        {
//            Log.d("Sim2LogDetails", "$log\n____________________________________")
//
//        }
//        saveData()
//        loadData()
  //  }

    private fun Init() {
        //89918720400291790994
        //89918720400291790994
        //89918720400498406808
        rv_call_logs = findViewById(R.id.activity_main_rv)
        rv_call_logs!!.setHasFixedSize(true)
        rv_call_logs!!.layoutManager = LinearLayoutManager(this)
        callLogModelArrayList = ArrayList()
        callLogAdapter = CallLogAdapter(this, callLogModelArrayList)
        rv_call_logs!!.adapter = callLogAdapter
    }
   // @SuppressLint("Range", "SuspiciousIndentation", "NotifyDataSetChanged")
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

        var counter=100;

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
            var text:TextView = findViewById(R.id.tt)
            //  text.append("phone number =  $str_contact_name\n"  )
            str_call_time = timeFormatter.format(Date(str_call_full_date!!.toLong()))
            //str_call_time = getFormatedDateTime(str_call_time, "HH:mm:ss", "hh:mm ss");
            str_call_duration = DurationFormat(str_call_duration)
            text.append(str_contact_name+"     "+str_call_duration+"   "+str_call_date)

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

//    private fun getFormatedDateTime(
//        dateStr: String,
//        strInputFormat: String,
//        strOutputFormat: String
//    ): String {
//        var formattedDate = dateStr
//        val inputFormat: DateFormat = SimpleDateFormat(strInputFormat, Locale.getDefault())
//        val outputFormat: DateFormat = SimpleDateFormat(strOutputFormat, Locale.getDefault())
//        var date: Date? = null
//        try {
//            date = inputFormat.parse(dateStr)
//        } catch (e: ParseException) {
//        }
//        if (date != null) {
//            formattedDate = outputFormat.format(date)
//        }
//        return formattedDate
//    }

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






//    fun getCallHistoryOfSim(simInfo:SubscriptionInfo?
//                            , allCallList:MutableList<CallHistory> ) : MutableList<CallHistory> {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
//            return allCallList.filter { it.subscriberId==simInfo?.subscriptionId.toString() || it.subscriberId!!.contains(simInfo?.iccId?:"_")}.toMutableList()
//        }else{
//            throw Exception("This Feature Is Not Available On This Device")
//        }
//
//    }

//    @RequiresApi(Build.VERSION_CODES.P)
//    private fun getSimCardInfos() : List<SubscriptionInfo>?{
//        val subscriptionManager: SubscriptionManager =
//            getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            throw Exception("Permission Not Granted -> Manifest.permission.READ_PHONE_STATE")
//        }
//        Log.d(TAG, "getSimCardInfos: ${subscriptionManager.accessibleSubscriptionInfoList}")
//        return subscriptionManager.activeSubscriptionInfoList
//    }


//    val deviceName: String
//        get() {
//            val manufacturer = Build.MANUFACTURER
//            val model = Build.MODEL
//            return if (model.startsWith(manufacturer)) {
//                capitalize(model)
//            } else {
//                capitalize(manufacturer) + " " + model
//            }
//        }
//    private fun capitalize(s: String?): String {
//        if (s == null || s.length == 0) {
//            return ""
//        }
//        val first = s[0]
//        return if (Character.isUpperCase(first)) {
//            s
//        } else {
//            first.uppercaseChar().toString() + s.substring(1)
//        }
//    }
//    companion object {
//     //   private const val TAG_SEND_DATA = "Sending data to server"
//       // private const val PERMISSIONS_REQUEST_CODE = 999
//    }


   // @SuppressLint("Range")
//    fun getAllCallHistory() : MutableList<CallHistory>{
//
//        val managedCursor = if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            throw Exception("Permission Not Granted -> Manifest.permission.READ_CALL_LOG")
//        }else{
//            this.contentResolver?.query(
//                CallLog.Calls.CONTENT_URI, null, null,
//                null, null)
//        }
//
//        managedCursor?.let {
//            val callHistoryList= mutableListOf<CallHistory>()
//            while (it.moveToNext())
//            {
//                if (it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))==null) continue
//                Log.d(TAG, "getAllCallHistory: ${it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))}")
//                callHistoryList.add(CallHistory(number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)),
//                    name = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME))?:null,
//                    type = it.getString(it.getColumnIndex(CallLog.Calls.TYPE)).toInt(),
//                    date = it.getString(it.getColumnIndex(CallLog.Calls.DATE)).toLong(),
//                    duration = it.getString(it.getColumnIndex(CallLog.Calls.DURATION)).toLong(),
//
//                    subscriberId = it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)))
//                     //   subscriberId ="89918720400291790994")
//                )
//            }
//            it.close()
//            return callHistoryList
//        }
//
//        return mutableListOf<CallHistory>()
//
//    }
  //  var viewModel=ViewModelSaveToken()
//    fun getTIME(){
//        val pref=Cache(this)
//        pref.authToken.asLiveData().observe(this, Observer {
//
//            if (it==null){
//                timer=currentTimeMillis();
//               //viewModel.saveAuthToken(timer)
//
//            }
//            else{
//                timer=it
//            }
//        })
//    }


//    val userDataChangeReceiver = object: BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d(TAG, "onReceive11: 11")
//        }
//    }





//
//    lateinit var dexter : DexterBuilder
//    private fun getPermission() {
//        dexter = Dexter.withContext(this)
//            .withPermissions(
//                Manifest.permission.READ_CALL_LOG,
//                android.Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.PROCESS_OUTGOING_CALLS
//            ).withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    report.let {
//
//                        if (report.areAllPermissionsGranted()) {
//
//                            Toast.makeText(this@Third, "Permissions Granted", Toast.LENGTH_SHORT).show()
//                            FetchCallLogs()
//                            FetchCallLogs()
//                        } else {
//                            AlertDialog.Builder(this@Third, R.style.Theme_Dialer).apply {
//                                setMessage("please allow the required permissions")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Settings") { _, _ ->
//                                        val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                            .apply {
//                                                val uri = Uri.fromParts("package", packageName, null)
//                                                data = uri
//                                            }
//                                        resultLauncher.launch(reqIntent)
//                                    }
//                                // setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
//                                val alert = this.create()
//                                alert.show()
//                            }
//                        }
//                    }
//                }
//                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
//                    token?.continuePermissionRequest()
//                }
//            }).withErrorListener{
//                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
//            }
//        dexter.check()
//    }
//    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            result -> dexter.check()
//    }
//    fun addDummyUser() {
//        val apiService = RestApiSerVice()
//        val userInfo = CallInfo( userId = null,
//            userName = "Alex",
//            userEmail = "alex@gmail.com",
//            userAge = 32.toString(),
//            userUid = "164E92FC-D37A-4946-81CB-29DE7EE4B124" )
//
//        apiService.addUser(userInfo) {
//            if (it?.userId != null) {
//                // it = newly added user parsed as response
//                // it?.id = newly added user ID
//            } else {
//                //Timber.d("Error registering new user")
//            }
//        }
//    }



//    @RequiresPermission(allOf = [
//        Manifest.permission.READ_CALL_LOG,
//        Manifest.permission.READ_PHONE_STATE,
//        Manifest.permission.PROCESS_OUTGOING_CALLS
//    ])
//    fun Intent.phoneCallInformation(): PhoneCall {
//        val action = action
//        val extras = extras
//        if (extras != null) {
//            if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
//                // Incoming Call
//                val state = getStringExtra(TelephonyManager.EXTRA_STATE)!!
//                if (hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) && state == TelephonyManager.EXTRA_STATE_RINGING) {
//                    val number = getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)!!
//
//                    return PhoneCall.Incoming(state, number)
//                }
//            } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//                val number = getStringExtra(Intent.EXTRA_PHONE_NUMBER)
//                return PhoneCall.Outgoing(number)
//            }
//        }
//        return PhoneCall.Unknown
//    }
  //  val CHANNEL_ID = "autoStartServiceChannel"
  //  val CHANNEL_NAME = "Auto Start Service Channel"

//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val serviceChannel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val manager = getSystemService(
//                NotificationManager::class.java
//            )
//            manager.createNotificationChannel(serviceChannel)
//        }
//    }


//    fun startService(v: View?) {
//        val serviceIntent = Intent(this, Mess::class.java)
//        serviceIntent.putExtra("inputExtra", "passing any text")
//        ContextCompat.startForegroundService(this, serviceIntent)
//    }
//
//    fun stopService(v: View?) {
//        val serviceIntent = Intent(this, Mess::class.java)
//        stopService(serviceIntent)
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            val intent = Intent(this@Third, TService::class.java)
            startService(intent)
        }
    }

@RequiresApi(Build.VERSION_CODES.O)
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportActionBar!!.title = "Call Logs"
    Init()

    getPermission()

    // Initiate DevicePolicyManager.
    // Initiate DevicePolicyManager.
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





    // Register for incoming call broadcasts
//    val filter = IntentFilter().apply {
//        addAction(Intent.ACTION_NEW_OUTGOING_CALL)
//        addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
//    }
//    val receiver = CallBroadcastReceiver()
//
//    registerReceiver(receiver, filter)

//        callRecord = CallRecord.Builder(this)
//            .setLogEnable(true)
//            .setRecordFileName("CallRecorderTestFile")
//            .setRecordDirName("CallRecorderTest")
//            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//            .setShowSeed(true)
//            .build()
//        callRecord.startCallReceiver()
}
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

                            Toast.makeText(this@Third, "Permissions Granted", Toast.LENGTH_SHORT).show()
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


    //Done Dexter


//     @SuppressLint("Range")
//    fun getAllCallHistory() : MutableList<CallHistory>{
//
//        val managedCursor = if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            throw Exception("Permission Not Granted -> Manifest.permission.READ_CALL_LOG")
//        }else{
//            this.contentResolver?.query(
//                CallLog.Calls.CONTENT_URI, null, null,
//                null, null)
//        }
//
//        managedCursor?.let {
//            val callHistoryList= mutableListOf<CallHistory>()
//            while (it.moveToNext())
//            {
//            //    if (it.getString(it.getColumnIndex(CallLog.Calls.)))
//                if (it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))==null) continue
//                Log.d(TAG, "getAllCallHistory: ${it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))}")
//                callHistoryList.add(CallHistory(number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)),
//                    name = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME))?:null,
//                    type = it.getString(it.getColumnIndex(CallLog.Calls.TYPE)).toInt(),
//                    date = it.getString(it.getColumnIndex(CallLog.Calls.DATE)).toLong(),
//                    duration = it.getString(it.getColumnIndex(CallLog.Calls.DURATION)).toLong(),
//
//                    subscriberId = it.getString(it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)))
//                     //   subscriberId ="89918720400291790994")
//                )
//            }
//            it.close()
//            return callHistoryList
//        }
//
//        return mutableListOf<CallHistory>()
//
//    }
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
                //val cc=cursor.getString(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))
               // Log.d(TAG, "getAllCallHistory111:   $cc")
               // Log.d("CallLog1222", "Number: $callNumber, Date: $callDate, Duration: $callDuration, Type: $callType")



//
//                val columnIndex = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
//                Log.d(TAG, "getAllCallHistory: 444  $columnIndex")
//                if (columnIndex == -1) {
//                    // The column does not exist in the cursor
//                    Log.e("Error", "Column not found")
//                } else {
//                    // The column exists in the cursor
//                    val columnValue = cursor.getString(columnIndex)
//                     Log.d("CallLog2", "Number: $callNumber, Date: $callDate, Duration: $callDuration, Type: $callType, Name: $columnValue")
//
//
//                    // Do something with the column value
//                }







//                val simCardId =
//                    cursor.getInt(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID))
//
//                // Retrieve the SIM card information from the SubscriptionManager
//                val subscriptionManager =
//                    getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//                val subscriptionInfo =
//                    subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simCardId)
//                val carrierName = subscriptionInfo?.carrierName
//                val countryIso = subscriptionInfo?.countryIso
//
//                // Log the call details and SIM card information
//                Log.d(
//                    "CallLog",
//                    "Number: $callNumber, SIM Card ID: $simCardId, Carrier: $carrierName, Country: $countryIso"
//                )






                // Do something with the call details
               // Log.d("CallLog", "Number: $callNumber, Date: $callDate, Duration: $callDuration, Type: $callType")
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