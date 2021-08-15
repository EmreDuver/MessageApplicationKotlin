package com.emreduver.messageapplication.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.send.user.ChangeUsernameDto
import com.emreduver.messageapplication.services.api.UserService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class ChangeUsernameViewModel : ViewModel(), IViewModelState {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    fun changeUsername(changeUsernameDto: ChangeUsernameDto): LiveData<Boolean> {
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = UserService.changeUsername(changeUsernameDto)
            status.value = response.Success
            loadingState.value = LoadingState.Loaded
            if (!response.Success)
                errorState.value = response.Message!!
        }
        return status
    }

}