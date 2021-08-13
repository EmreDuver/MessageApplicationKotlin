package com.emreduver.messageapplication.services.api

import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.token.Token
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.services.retrofit.ApiClient
import com.emreduver.messageapplication.services.retrofit.RetrofitAuthService

class AuthService {
    companion object{

        private var retrofitAuthServiceWithoutInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitAuthService::class.java,false)

        suspend fun register (register: Register) : ApiResult<Unit>{
            var result = retrofitAuthServiceWithoutInterceptor.register(register)

            if (!result.isSuccessful)
                return ApiResult(false)

            return ApiResult(true)
        }

        suspend fun login (login: Login) : ApiResult<Token>{
            var result = retrofitAuthServiceWithoutInterceptor.login(login)

            if (!result.isSuccessful)
                return ApiResult(false)

            return ApiResult(true, result.body() as Token)
        }
    }
}