package com.hoogsoftware.dialer.resources

data class CallHistory(var number:String,
                       var name:String?,
                       var type:Int,
                       var date:Long,
                       var duration: Long,
                       var subscriberId:String="2") {

    val cachedName:String  // Separate property for cachedName
        get() {
            return  if(name==null) number else name as String
        }

}