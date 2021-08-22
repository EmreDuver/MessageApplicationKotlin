package com.emreduver.messageapplication.entities.send.message

data class SendMessageDto(
    var SenderUserId:String,
    var ReceiverUserId:String,
    var Message:String? = null,
    var File:Boolean,
    var FileType:Int,
    var FileBase64:String? = null,
    var FileName:String? = null,
    var FileExtension:String? = null,
    var Longitude:Double? = null,
    var Latitude:Double? = null
)
