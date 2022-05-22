package com.lab49.taptosnap.data

import com.lab49.taptosnap.model.ItemResponse
import com.lab49.taptosnap.model.ItemMatchRequest
import com.lab49.taptosnap.network.Lab49Service

class DataRepository {

    suspend fun getItemList(lab49Service: Lab49Service): ResultStatus {
        return try {
            val itemList = lab49Service.getItemList()

//            if (api1Model.ErrorCode != "9999" && api1Model.Message.toUpperCase(Locale.ROOT) != "SUCCESS") {
//                throw Exception("Api1 returned failure code")
//            }

            ResultStatus.Success(data = itemList)
        } catch (ex: Exception) {
            ResultStatus.Failure(ex)
        }
    }

    suspend fun verifyItem(lab49Service: Lab49Service, itemMatchRequest: ItemMatchRequest): ResultStatus {
        return try {
            val matchResponse = lab49Service.matchItem(itemMatchRequest)
            ResultStatus.Success(data = matchResponse)
        } catch (ex: Exception) {
            ResultStatus.Failure(ex)
        }
    }

    fun getCachedItemList(): List<ItemResponse> {
        return CachedData.itemList
    }


}