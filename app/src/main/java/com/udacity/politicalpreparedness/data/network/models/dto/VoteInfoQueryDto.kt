package com.udacity.politicalpreparedness.data.network.models.dto

/**
 * @author Komi Donon
 * @since 7/8/2023
 */
data class VoteInfoQueryDto(
    val address: String,
    val electionId: Int,
    val officialOnly: Boolean,
    val returnAllAvailableData: Boolean,
    val productionDataOnly: Boolean,
)
