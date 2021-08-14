package com.emreduver.messageapplication.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.send.auth.Login
import com.emreduver.messageapplication.entities.send.auth.Register
import com.emreduver.messageapplication.services.api.AuthService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(), IViewModelState {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    fun login(login: Login): LiveData<Boolean> {
        loadingState.value = LoadingState.Loading
        val status = MutableLiveData<Boolean>()

        viewModelScope.launch {
            val response = AuthService.login(login)
            status.value = response.Success
            loadingState.value = LoadingState.Loaded
            if (!response.Success)
                errorState.value = response.Message!!
        }
        return status
    }
}