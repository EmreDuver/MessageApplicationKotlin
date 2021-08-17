package com.emreduver.messageapplication.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreduver.messageapplication.entities.receive.user.UserSearchDto
import com.emreduver.messageapplication.services.api.UserService
import com.emreduver.messageapplication.utilities.IViewModelState
import com.emreduver.messageapplication.utilities.LoadingState
import kotlinx.coroutines.launch

class UserSearchViewModel : ViewModel() , IViewModelState {
    override var loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    override var errorState: MutableLiveData<String> = MutableLiveData<String>()

    var users = MutableLiveData<ArrayList<UserSearchDto>>()

    fun userSearch(username:String): LiveData<Boolean> {
        val status = MutableLiveData<Boolean>()
        loadingState.value = LoadingState.Loading

        viewModelScope.launch {
            val response = UserService.userSearch(username)
            status.value = response.Success
            when(status.value){
                false -> errorState.value = response.Message!!
                true  -> {
                    users.value = arrayListOf()
                    users.value = response.Data!!
                }
            }
            loadingState.value = LoadingState.Loaded
        }
        return status
    }
}