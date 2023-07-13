package com.udacity.politicalpreparedness.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.politicalpreparedness.data.network.models.dto.ErrorResponseDto
import retrofit2.HttpException
import java.lang.Exception

/**
 * @author Komi Donon
 * @since 7/9/2023
 */

fun Exception.mapErrorResponse(): Exception {
    return when (this) {
        is HttpException -> {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(ErrorResponseDto::class.java)
            val error = jsonAdapter.fromJson(requireNotNull(response()?.errorBody()?.string()))
            IllegalStateException(error?.error?.message)
        }

        else -> this
    }
}
