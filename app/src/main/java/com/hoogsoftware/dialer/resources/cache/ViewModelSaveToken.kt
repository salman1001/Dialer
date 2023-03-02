package com.hoogsoftware.dialer.resources.cache

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelSaveToken(pref:Cache):ViewModel( ) {
    var repository=Repository(pref)
    fun  saveAuthToken(token: Long)=viewModelScope.launch {
        repository.saveAuthToken(token)
    }
}