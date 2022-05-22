package com.lab49.taptosnap.viewmodel

import androidx.lifecycle.ViewModel
import com.lab49.taptosnap.data.DataRepository
import com.lab49.taptosnap.model.Item
import com.lab49.taptosnap.network.Lab49Service

class GameViewModel: ViewModel() {

    private val dataRepository = DataRepository()
    private val lab49Service = Lab49Service.create()

    fun getItemList(): List<Item> {
        return dataRepository.getCachedItemList()
    }
}