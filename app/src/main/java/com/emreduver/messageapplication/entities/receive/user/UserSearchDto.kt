package com.emreduver.messageapplication.entities.receive.user

import com.google.gson.annotations.SerializedName

data class UserSearchDto(
    @SerializedName("id") val Id: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("firstName") var firstName: String,
    @SerializedName("lastName") var lastName: String,
    @SerializedName("photoPath") var photoPath: String
)
