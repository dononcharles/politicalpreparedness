package com.udacity.politicalpreparedness.ui.election.adapter

import android.os.Parcelable
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/9/2023
 */

@Parcelize
data class ElectionModel(
    val id: Int,
    val name: String,
    val electionDay: Date,
    val division: Division,
    var isSaved: Boolean,
) : Parcelable

fun ElectionModel.toElection(): Election {
    return Election(id = id, name = name, electionDay = electionDay, division = division, isSaved = isSaved)
}
