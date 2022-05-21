package com.lab49.taptosnap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.taptosnap.data.CachedData
import com.lab49.taptosnap.data.DataRepository
import com.lab49.taptosnap.data.ResultStatus
import com.lab49.taptosnap.model.Item
import com.lab49.taptosnap.network.Lab49Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {

    private val dataRepository = DataRepository()
    private val lab49Service = Lab49Service.create()

    private val _getItemListResponse = MutableLiveData<ResultStatus>()
    private val getItemListResponse: LiveData<ResultStatus> = _getItemListResponse

    fun getItemList(): List<Item> {
        return CachedData.itemList
    }
}