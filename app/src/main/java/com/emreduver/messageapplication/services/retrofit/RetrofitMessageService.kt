package com.emreduver.messageapplication.services.retrofit

import com.emreduver.messageapplication.constants.RetrofitUrl
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.receive.message.MessageHistoryDto
import com.emreduver.messageapplication.entities.receive.result.ApiResult
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.entities.send.message.SendMessageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitMessageService {
    @POST(RetrofitUrl.SendMessage)
    suspend fun sendMessage(@Body sendMessageDto: SendMessageDto) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.GetMessage)
    suspend fun getMessage(@Body getMessageDto: GetMessageDto) : Response<ApiResult<ArrayList<Message>>>

    @POST(RetrofitUrl.UpdateMessageStatus)
    suspend fun updateMessageStatus(@Body messageId: String) : Response<ApiResult<Unit>>

    @POST(RetrofitUrl.GetMessageHistory)
    suspend fun getMessageHistory(@Body userId: String) : Response<ApiResult<ArrayList<MessageHistoryDto>>>

}