package com.emreduver.messageapplication.entities.receive.message

import com.google.gson.annotations.SerializedName

data class MessageHistoryDto(
    @SerializedName("userId") val UserId: String,
    @SerializedName("firstName") val Firstname: String,
    @SerializedName("lastName") val Lastname: String,
    @SerializedName("photoPath") val PhotoPath: String,
    @SerializedName("messageText") var MessageText: String?=null,
    @SerializedName("messageDate") val MessageDate: String,
    @SerializedName("sender") var Sender: Boolean,
    @SerializedName("readStatus") var ReadStatus: Boolean,
    @SerializedName("file") var File: Boolean,
    @SerializedName("fileType") var FileType: Int

)
