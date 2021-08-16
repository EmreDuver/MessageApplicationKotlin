package com.emreduver.messageapplication.entities.send.user

import java.util.*

data class UpdateProfileDto(
    var Id:String,
    var FirstName:String,
    var LastName:String,
    var StatusMessage:String,
    var BirthDay: String
)