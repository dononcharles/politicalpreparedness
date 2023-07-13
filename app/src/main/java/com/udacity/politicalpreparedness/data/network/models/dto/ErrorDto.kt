package com.udacity.politicalpreparedness.data.network.models.dto

import com.squareup.moshi.JsonClass

/**
 * @author Komi Donon
 * @since 7/8/2023
 */
@JsonClass(generateAdapter = true)
data class ErrorDto(val code: String, val message: String)
