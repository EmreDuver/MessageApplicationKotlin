package com.emreduver.messageapplication.entities.receive.result

import com.google.gson.annotations.SerializedName

data class ApiResult<T>(
    @SerializedName("success") var Success:Boolean,
    @SerializedName("data") var Data: T? = null,
    @SerializedName("message") var Message:String? = null,
    @SerializedName("statusCode") var StatusCode:Int?= null
)
