package com.emreduver.messageapplication.utilities

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.constants.Messages
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.token.Token
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Response


class HelperService {

    companion object {
        lateinit var hubConnectionInstance: HubConnection
        var control: Boolean = false

        fun saveToSharedPreferences(token: Token) {

            var sharedPreference =
                GlobalApp.getAppContext().getSharedPreferences("token_api", Context.MODE_PRIVATE)

            var editor = sharedPreference.edit()

            editor.putString("token", Gson().toJson(token))
            editor.apply()
        }

        fun getTokenSharedPreference(): Token? {
            var preference =
                GlobalApp.getAppContext().getSharedPreferences("token_api", Context.MODE_PRIVATE)

            var tokenString: String? = preference.getString("token", null) ?: return null

            return Gson().fromJson(tokenString, Token::class.java)
        }

        fun deleteTokenSharedPreference() {
            var sharedPreference =
                GlobalApp.getAppContext().getSharedPreferences("token_api", Context.MODE_PRIVATE)

            var editor = sharedPreference.edit()
            editor.clear()
            editor.apply()
        }

        fun <T1, T2> handleApiError(response: Response<T2>): ApiResult<T1> {

            var getData: ApiResult<Unit>? = null
            var errorMessage = ""

            if (response.errorBody() != null) {
                val errorBody = response.errorBody()!!.string()
                getData = Gson().fromJson(errorBody, ApiResult::class.java) as ApiResult<Unit>?
                errorMessage = getData!!.Message!!
                Log.i("OkHttp", errorMessage)
            }

            return ApiResult(false, null, errorMessage)
        }

        fun <T> handleException(ex: Exception): ApiResult<T> {
            return when (ex) {

                is OfflineException -> {
                    val exceptionMessage = Messages.OfflineException
                    ApiResult(false, null, exceptionMessage)
                }
                else -> {
                    val exceptionMessage = Messages.NotDefinedException
                    ApiResult(false, null, exceptionMessage)
                }
            }
        }

        fun showMessageByToast(message:String) {

            if (message.isNullOrEmpty()) return

            Toast.makeText(GlobalApp.getAppContext(), message, Toast.LENGTH_SHORT).show()
        }

        fun loadImageFromPicasso(url: String, imageView: ImageView){
            val baseImageUrl = "${Api.baseImageURL}/userImages/${url}"
            Picasso.get().load(baseImageUrl).into(imageView)
            Log.i("Message", baseImageUrl)
        }

        fun loadImageForMessageFromPicasso(url: String, imageView: ImageView){
            val baseImageUrl = "${Api.baseUrl}/${url}"
            Picasso.get().load(baseImageUrl).into(imageView)
        }

        fun connectHub(){
            val userAccessToken = getTokenSharedPreference()!!.AccessToken

            if(!control){
                hubConnectionInstance = HubConnectionBuilder.create(Api.baseHubUrl).withHeader("user_access_token", userAccessToken).build()
            }

            if (hubConnectionInstance.connectionState == HubConnectionState.DISCONNECTED){
                tryToConnectHub(hubConnectionInstance)
            }
        }

        private fun tryToConnectHub(hubConnection: HubConnection)
        {
            var runnable: Runnable = Runnable { }
            var handler = Handler(Looper.myLooper()!!)
            runnable = Runnable {
                if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                    connectWithHandler(hubConnection)
                    handler.removeCallbacks(runnable)
                }
                handler.postDelayed(runnable, 3000)
            }
            handler.post(runnable)
        }

        private fun connectWithHandler(hubConnection: HubConnection) {
            try {
                var condition = false
                var counter = 0
                var runnable: Runnable = Runnable { }
                var handler = Handler(Looper.myLooper()!!)
                runnable = object : Runnable {
                    override fun run() {
                        hubConnection.start()
                        counter++
                        if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
                            hubConnection.send("OnConnectedAsync")
                            Log.i("OkHttp", "$counter. adımda bağlantı sağlandı")
                            condition = true
                        }
                        if (condition) {
                            handler.removeCallbacks(runnable)
                            return
                        }
                        handler.postDelayed(runnable, 1000)
                    }
                }
                handler.post(runnable)
            } catch (e: Exception) {
                Log.i("OkHttp", "Burada Çökecekti $e")
            }
        }
    }
}