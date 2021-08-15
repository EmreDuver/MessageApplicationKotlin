package com.emreduver.messageapplication.entities.receive.user

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val UserId: String,
    @SerializedName("userName") val Username: Long,
    @SerializedName("email") val Email: String,
    @SerializedName("firstName") val Firstname: Long,
    @SerializedName("lastName") val Lastname: String
)
