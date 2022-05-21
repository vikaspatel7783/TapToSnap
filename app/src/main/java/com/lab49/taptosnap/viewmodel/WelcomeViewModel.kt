package com.lab49.taptosnap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.taptosnap.data.DataRepository
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.network.Lab49Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeViewModel: ViewModel() {

    private val dataRepository = DataRepository()
    private val lab49Service = Lab49Service.create()

    private val _getItemListResponse = MutableLiveData<ResultStatus>()
    private val getItemListResponse: LiveData<ResultStatus> = _getItemListResponse

    fun getItemList(): LiveData<ResultStatus> {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.getItemList(lab49Service).let {
                _getItemListResponse.postValue(it)
            }
        }
        return getItemListResponse
    }
}