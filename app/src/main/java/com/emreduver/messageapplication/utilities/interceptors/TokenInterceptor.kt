package com.emreduver.messageapplication.utilities.interceptors

import android.content.Context
import android.util.Log
import com.emreduver.messageapplication.entities.receive.token.Token
import com.emreduver.messageapplication.utilities.GlobalApp
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var token: Token? = null

        var request = chain.request()

        val preference = GlobalApp.getAppContext().getSharedPreferences("token_api", Context.MODE_PRIVATE)

        val tokenString = preference.getString("token", null)

        if (tokenString != null) {
            token = Gson().fromJson(tokenString, Token::class.java)

            Log.i("okHttp", token.AccessToken)

            request = request.newBuilder().addHeader("Authorization", "Bearer ${token.AccessToken}").build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.i("OkHttp", "Accesstoken geçersiz 401'e girdi")
        }

        return response
    }
}