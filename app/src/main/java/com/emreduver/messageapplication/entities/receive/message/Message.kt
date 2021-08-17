package com.emreduver.messageapplication.entities.receive.message

import com.google.gson.annotations.SerializedName
import java.util.*

data class Message(
    @SerializedName("id") val MessageId: String,
    @SerializedName("senderId") val SenderUserId: String,
    @SerializedName("receiverId") val ReceiverUserId: String,
    @SerializedName("text") val Text: String,
    @SerializedName("status") var status: Int,
    @SerializedName("sendDateUnix") val SendDateUnix: Long,
    @SerializedName("readDateUnix") var ReadDateUnix: Long
)
