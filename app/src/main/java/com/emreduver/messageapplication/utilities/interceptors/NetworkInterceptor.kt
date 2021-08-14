package com.emreduver.messageapplication.utilities.interceptors

import com.emreduver.messageapplication.utilities.NetworkListener
import com.emreduver.messageapplication.utilities.OfflineException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        if (!NetworkListener.isConnected())
            throw OfflineException("")

        var request = chain.request()

        return  chain.proceed(request)
    }
}