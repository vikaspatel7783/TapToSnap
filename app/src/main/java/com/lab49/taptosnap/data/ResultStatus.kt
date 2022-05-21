package com.lab49.taptosnap.data

import com.lab49.taptosnap.model.Item

sealed class ResultStatus {

    data class Success(val data: List<Item>): ResultStatus()

    data class Failure(val exception: Exception): ResultStatus()
}