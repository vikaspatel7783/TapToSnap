package com.lab49.taptosnap.data

sealed class ResultStatus {

    data class Success(val data: Any): ResultStatus()

    data class Failure(val exception: Exception): ResultStatus()
}