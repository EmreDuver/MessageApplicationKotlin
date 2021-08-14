package com.emreduver.messageapplication.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkListener {
    companion object {

        private fun getConnectionType(context: Context): Int {
            var result = 0
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            cm.run {
                this.getNetworkCapabilities(this.activeNetwork)?.run {
                    if(hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        result = 1
                    }
                    else if(hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        result = 2
                    }
                }
            }
            return result
        }

        fun isConnected():Boolean{
            val context = GlobalApp.getAppContext()
            val result = getConnectionType(context)
            return result != 0
        }
    }
}