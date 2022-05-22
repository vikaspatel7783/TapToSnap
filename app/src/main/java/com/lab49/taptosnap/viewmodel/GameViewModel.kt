package com.lab49.taptosnap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.taptosnap.data.DataRepository
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.model.ItemResponse
import com.lab49.taptosnap.model.ItemMatchRequest
import com.lab49.taptosnap.network.Lab49Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {

    private val _itemMatchResponse = MutableLiveData<ResultStatus>()
    private val itemMatchResponse: LiveData<ResultStatus> = _itemMatchResponse

    private val dataRepository = DataRepository()
    private val lab49Service = Lab49Service.create()

    fun getItemList(): List<ItemResponse> {
        return dataRepository.getCachedItemList()
    }

    fun matchItem(matchRequest: ItemMatchRequest): LiveData<ResultStatus> {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.verifyItem(lab49Service, matchRequest).let {
                _itemMatchResponse.postValue(it)
            }
        }
        return itemMatchResponse
    }
}