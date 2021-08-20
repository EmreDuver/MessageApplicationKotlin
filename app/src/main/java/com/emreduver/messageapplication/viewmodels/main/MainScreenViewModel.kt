package com.emreduver.messageapplication.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.receive.message.MessageHistoryDto
import com.emreduver.messageapplication.entities.send.message.GetMessageDto
import com.emreduver.messageapplication.services.api.AuthService
import com.emreduver.messageapplication.services.api.MessageService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel(), IViewModelState {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    var messageHistory: MutableLiveData<ArrayList<MessageHistoryDto>> = MutableLiveData()

    fun logout(): LiveData<Boolean> {
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = AuthService.logout()
            status.value = response.Success
            loadingState.value = LoadingState.Loaded
            if (!response.Success)
                errorState.value = response.Message!!
        }
        return status
    }
    fun getMessageHistory(userId:String): LiveData<Boolean> {
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = MessageService.getMessageHistory(userId)
            status.value = response.Success

            when(status.value){
                false -> errorState.value = response.Message!!
                true -> messageHistory.value = response.Data!!
            }
            loadingState.value = LoadingState.Loaded
        }
        return status
    }
}