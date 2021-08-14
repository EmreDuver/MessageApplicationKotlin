package com.emreduver.messageapplication.utilities

import androidx.lifecycle.MutableLiveData

interface IViewModelState {
    var loadingState:MutableLiveData<LoadingState>
    var errorState:MutableLiveData<String>
}