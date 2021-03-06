package com.emreduver.messageapplication.services.api

import android.util.Log
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.token.Token
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.auth.LoginByRefreshToken
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.services.retrofit.ApiClient
import com.emreduver.messageapplication.services.retrofit.RetrofitAuthService
import com.emreduver.messageapplication.utilities.HelperService

class AuthService {
    companion object{

        private var retrofitAuthServiceWithoutInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitAuthService::class.java,false)
        private var retrofitAuthServiceWithInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitAuthService::class.java,true)

        suspend fun register (register: Register) : ApiResult<Unit>{
            try {
                val result = retrofitAuthServiceWithoutInterceptor.register(register)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                Log.i("OkHTTP","Kullanıcı başarıyla kayıt olmuştur.")

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun login (login: Login) : ApiResult<Token>{
            try {
                val result = retrofitAuthServiceWithoutInterceptor.login(login)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                val token =  result.body()!!.Data!!
                HelperService.saveToSharedPreferences(token)

                return result.body() as ApiResult<Token>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun loginByRefreshToken () : ApiResult<Token>{
            try {
                val loginByRefreshToken = LoginByRefreshToken(HelperService.getTokenSharedPreference()!!.UserId,HelperService.getTokenSharedPreference()!!.RefreshToken)
                val result = retrofitAuthServiceWithoutInterceptor.loginByRefreshToken(loginByRefreshToken)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                val token =  result.body()!!.Data!!
                HelperService.saveToSharedPreferences(token)

                return result.body() as ApiResult<Token>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun logout() : ApiResult<Unit>{
            try {
                val refreshToken = HelperService.getTokenSharedPreference()?.RefreshToken
                if (refreshToken.isNullOrEmpty()){
                    HelperService.deleteTokenSharedPreference()
                    return ApiResult(true)
                }
                val result = retrofitAuthServiceWithoutInterceptor.logout(refreshToken)

                if (!result.isSuccessful){
                    HelperService.deleteTokenSharedPreference()
                    return ApiResult(true)
                }

                HelperService.deleteTokenSharedPreference()

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                HelperService.deleteTokenSharedPreference()
                return ApiResult(true)
            }
        }

        suspend fun checkToken() : ApiResult<Unit>{
            try {
                val result = retrofitAuthServiceWithInterceptor.checkToken()

                if (!result.isSuccessful){
                    if (HelperService.getTokenSharedPreference()==null){
                        return HelperService.handleApiError(result)
                    }
                    loginByRefreshToken()
                }

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }
    }
}