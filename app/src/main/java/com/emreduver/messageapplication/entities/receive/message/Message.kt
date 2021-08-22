package com.emreduver.messageapplication.entities.receive.message

import com.google.gson.annotations.SerializedName
import java.util.*

data class Message(
    @SerializedName("id") val MessageId: String,
    @SerializedName("senderId") val SenderUserId: String,
    @SerializedName("receiverId") val ReceiverUserId: String,
    @SerializedName("text") val Text: String? = null,
    @SerializedName("status") var status: Int,
    @SerializedName("sendDateUnix") val SendDateUnix: Long,
    @SerializedName("readDateUnix") var ReadDateUnix: Long,
    @SerializedName("file") var IsFile: Boolean,
    @SerializedName("fileType") var FileType: Int,
    @SerializedName("filePath") var FilePath: String? = null,
    @SerializedName("fileName") var FileName: String? = null,
    @SerializedName("longitude") var Longitude: Double? = null,
    @SerializedName("latitude") var Latitude: Double? = null
)
