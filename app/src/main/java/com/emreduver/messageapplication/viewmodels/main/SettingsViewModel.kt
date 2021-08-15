package com.emreduver.messageapplication.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.receive.user.UserDto
import com.emreduver.messageapplication.services.api.UserService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() , IViewModelState {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    fun getUser(): LiveData<UserDto> {
        val userDto = MutableLiveData<UserDto>()
        loadingState.value = LoadingState.Loading

        viewModelScope.launch {
            val response = UserService.getUser()
            userDto.value = response.Data!!
            loadingState.value = LoadingState.Loaded
            if (!response.Success)
                errorState.value = response.Message!!
        }
        return userDto
    }
}