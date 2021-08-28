package com.emreduver.messageapplication.services.api

import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.receive.message.MessageHistoryDto
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.entities.send.message.SendMessageDto
import com.emreduver.messageapplication.services.retrofit.ApiClient
import com.emreduver.messageapplication.services.retrofit.RetrofitMessageService
import com.emreduver.messageapplication.utilities.HelperService

class MessageService {
    companion object{
        private var retrofitMessageServiceWithInterceptor = ApiClient.buildService(Api.baseUrl,RetrofitMessageService::class.java,true)

        suspend fun getMessage (getMessageDto: GetMessageDto) : ApiResult<ArrayList<Message>> {
            try {
                val result = retrofitMessageServiceWithInterceptor.getMessage(getMessageDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<ArrayList<Message>>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun sendMessage (sendMessageDto: SendMessageDto) : ApiResult<Unit>{
            try {
                val result = retrofitMessageServiceWithInterceptor.sendMessage(sendMessageDto)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun updateMessageStatus (messageId: String) : ApiResult<Unit>{
            try {
                val result = retrofitMessageServiceWithInterceptor.updateMessageStatus(messageId)

                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun getMessageHistory(userId:String) : ApiResult<ArrayList<MessageHistoryDto>>{
            try {
                val result = retrofitMessageServiceWithInterceptor.getMessageHistory(userId)
                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<ArrayList<MessageHistoryDto>>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }

        suspend fun messageAnalysis() : ApiResult<Unit>{
            try {
                val result = retrofitMessageServiceWithInterceptor.messageAnalysis()
                if (!result.isSuccessful)
                    return HelperService.handleApiError(result)

                return result.body() as ApiResult<Unit>
            }
            catch (e:Exception){
                return HelperService.handleException(e)
            }
        }
    }
}