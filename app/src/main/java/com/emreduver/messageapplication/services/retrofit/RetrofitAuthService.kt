package com.emreduver.messageapplication.services.retrofit

import com.emreduver.messageapplication.constants.RetrofitUrl
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.auth.LoginByRefreshToken
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.entities.receive.token.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitAuthService {

    @POST(RetrofitUrl.Register)
    suspend fun register(@Body register: Register) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.Login)
    suspend fun login(@Body login: Login) : Response<ApiResult<Token>>

    @POST(RetrofitUrl.LoginByRefreshToken)
    suspend fun loginByRefreshToken(@Body loginByRefreshToken: LoginByRefreshToken) : Response<ApiResult<Token>>

    @POST(RetrofitUrl.Logout)
    suspend fun logout(@Body refreshToken:String) : Response<ApiResult<Unit>>

    @GET(RetrofitUrl.CheckToken)
    suspend fun checkToken() : Response<ApiResult<Unit>>
}