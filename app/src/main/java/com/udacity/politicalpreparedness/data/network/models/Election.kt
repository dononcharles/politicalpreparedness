package com.udacity.politicalpreparedness.data.network.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import java.util.Date

@Entity(tableName = "election_table")
data class Election(
    @PrimaryKey val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "electionDay")
    val electionDay: Date,

    @Embedded(prefix = "division_")
    @Json(name = "ocdDivisionId")
    val division: Division,

    @ColumnInfo(name = "isSaved")
    val isSaved: Boolean = false,
)

fun List<Election>.toElectionModels(): List<ElectionModel> {
    return map {
        ElectionModel(
            id = it.id,
            name = it.name,
            electionDay = it.electionDay,
            division = it.division,
            isSaved = it.isSaved,
        )
    }
}
