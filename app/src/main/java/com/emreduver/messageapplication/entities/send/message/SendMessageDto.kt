package com.emreduver.messageapplication.entities.send.message

data class SendMessageDto(
    var SenderUserId:String,
    var ReceiverUserId:String,
    var Message:String
)
