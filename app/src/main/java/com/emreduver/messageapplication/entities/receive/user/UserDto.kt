package com.emreduver.messageapplication.entities.receive.user

import com.google.gson.annotations.SerializedName
import java.util.*

data class UserDto(
    @SerializedName("id") val UserId: String,
    @SerializedName("userName") val Username: String,
    @SerializedName("email") val Email: String,
    @SerializedName("firstName") val Firstname: String,
    @SerializedName("lastName") val Lastname: String,
    @SerializedName("photoPath") val PhotoPath: String,
    @SerializedName("birthDay") val BirthDay: Date,
    @SerializedName("statusMessage") val StatusMessage: String
)
