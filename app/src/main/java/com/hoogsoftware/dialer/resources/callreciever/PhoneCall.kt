package com.hoogsoftware.dialer.resources.callreciever

sealed class PhoneCall {
    data class Incoming(
        val state: String,
        val number: String?
    ): PhoneCall()

    data class Outgoing(
        val number: String?
    ): PhoneCall()

    object Unknown : PhoneCall()
}