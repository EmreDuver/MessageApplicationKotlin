package com.emreduver.messageapplication.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.entities.send.message.SendMessageDto
import com.emreduver.messageapplication.services.api.MessageService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel(), IViewModelState  {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    var messages: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    fun sendMessage(sendMessageDto: SendMessageDto): LiveData<Boolean>{
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = MessageService.sendMessage(sendMessageDto)
            status.value = response.Success
            loadingState.value = LoadingState.Loaded
            if (!response.Success)
                errorState.value = response.Message!!
        }
        return status
    }

    fun getMessage(getMessageDto: GetMessageDto): LiveData<Boolean> {
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = MessageService.getMessage(getMessageDto)
            status.value = response.Success

            when(status.value){
                false -> errorState.value = response.Message!!
                true -> messages.value = response.Data!!
            }

            loadingState.value = LoadingState.Loaded
        }
        return status
    }
}