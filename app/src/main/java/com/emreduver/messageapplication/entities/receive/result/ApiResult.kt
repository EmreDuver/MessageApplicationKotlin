package com.emreduver.messageapplication.entities.receive.result

data class ApiResult<T>(
    var Success:Boolean,
    var Data: T? = null,
    var Message:String? = null,
    var StatusCode:Int?= null
)
