package com.udacity.politicalpreparedness.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.politicalpreparedness.data.network.models.Election

@Dao
interface ElectionDao {

    // Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elections: List<Election>)

    // Add select all election query
    @Query("SELECT * FROM election_table ORDER BY electionDay ASC")
    fun getAll(): List<Election>

    // Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getById(id: Int): Election?

    // Add delete query
    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clearAll()

    // Select by id and division_id query
    @Query("SELECT * FROM election_table WHERE id = :id AND division_id = :divisionId")
    suspend fun getByIdAndDivisionId(id: Int, divisionId: String): Election?

    @Query("SELECT * FROM election_table ORDER BY electionDay ASC")
    fun getAllLiveData(): LiveData<List<Election>>

    // update election query with isSaved
    @Query("UPDATE election_table SET isSaved = :isSaved WHERE id = :id AND division_id = :divisionId")
    suspend fun updateElection(id: Int, divisionId: String, isSaved: Boolean)
}
