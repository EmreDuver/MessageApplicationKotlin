package com.emreduver.messageapplication.utilities

import android.content.Context
import com.emreduver.messageapplication.constants.Messages
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.receive.token.Token
import com.google.gson.Gson
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

            var errorMessage = ""

            if (response.errorBody() != null) {
                errorMessage = response.errorBody()!!.string()
            }

            return ApiResult(false, null, errorMessage)
        }

        fun <T> handleException(ex: Exception): ApiResult<T> {
            return when (ex) {

                is OfflineException -> {
                    var exceptionMessage = Messages.OfflineException
                    ApiResult(false,null,exceptionMessage)
                }
                else -> {
                    var exceptionMessage = Messages.NotDefinedException
                    ApiResult(false,null,exceptionMessage)
                }
            }
        }

        /*fun showErrorMessageByToast(apiGenericError: APIGenericError?) {

            if (apiGenericError == null) return
            var errorBuilder = StringBuilder()

            if (apiGenericError.error!!.IsShow) {
                for (error in apiGenericError.error!!.Errors) {
                    errorBuilder.append(error + "\n")
                }
            }
            Toast.makeText(GlobalApp.getAppContext(), errorBuilder.toString(), Toast.LENGTH_SHORT).show()
        }*/
    }
}