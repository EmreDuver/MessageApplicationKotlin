package com.emreduver.messageapplication.utilities

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.constants.Messages
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.token.Token
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Response


class HelperService {

    companion object {

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
            Picasso.get().load(baseImageUrl).into(imageView);
            Log.i("Message", baseImageUrl)
        }
    }
}