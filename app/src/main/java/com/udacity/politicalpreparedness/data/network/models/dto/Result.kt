package com.udacity.politicalpreparedness.data.network.models.dto

import java.lang.Exception

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
    class Loading<T> : Result<T>()
}
