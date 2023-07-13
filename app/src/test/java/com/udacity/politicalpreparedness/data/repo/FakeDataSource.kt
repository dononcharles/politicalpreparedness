package com.udacity.politicalpreparedness.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.AdministrationBody
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.Office
import com.udacity.politicalpreparedness.data.network.models.Official
import com.udacity.politicalpreparedness.data.network.models.RepresentativeResponse
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result

/**
 * @author Komi Donon
 * @since 7/12/2023
 */
// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val elections: MutableList<Election>? = mutableListOf()) : ElectionDataSource {

    private var returnError = false

    // Create a fake data source to act as a double to the real data source
    fun returnError(value: Boolean) {
        returnError = value
    }

    override suspend fun getElections(): Result<List<Election>> {
        return if (returnError) {
            Result.Error(Exception("No election found"))
        } else {
            Result.Success(ArrayList(elections ?: emptyList()))
        }
    }

    override suspend fun saveElections(elections: List<Election>) {
        this.elections?.addAll(elections)
    }

    override suspend fun deleteAllElections() {
        elections?.clear()
    }

    override suspend fun getElectionDetails(address: String, electionId: Int): Result<State?> {
        return if (returnError) {
            Result.Error(Exception("No details found"))
        } else {
            Result.Success(fakeStateData())
        }
    }

    private fun fakeStateData(): State {
        return State(
            name = "Fake State",
            electionAdministrationBody = AdministrationBody(
                name = "Fake Administration Body",
                electionInfoUrl = "https://fakeelectioninfourl.com",
                votingLocationFinderUrl = "https://fakevotinglocationfinderurl.com",
                ballotInfoUrl = "https://fakeballotinfourl.com",
                correspondenceAddress = Address(
                    line1 = "Fake Line 1",
                    line2 = "Fake Line 2",
                    city = "Fake City",
                    state = "Fake State",
                    zip = "Fake Zip",
                ),
            ),
        )
    }

    override suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse> {
        return if (returnError) {
            Result.Error(Exception("No representatives found"))
        } else {
            Result.Success(
                RepresentativeResponse(
                    listOf(
                        Office(
                            name = "Fake Office",
                            division = Division("Fake Division", "us", "ca"),
                            listOf(1, 2),
                        ),
                        Office(
                            name = "Fake Office 2",
                            division = Division("Fake Division 2", "us", "ca"),
                            listOf(1, 2),
                        ),
                        Office(
                            name = "Fake Office 3",
                            division = Division("Fake Division 3", "us", "ca"),
                            listOf(1, 2),
                        ),
                    ),
                    listOf(
                        Official(
                            name = "Fake Official 1",
                            address = emptyList(),
                            party = "Fake Party",
                            phones = listOf("11111111111", "22222222222"),
                            urls = listOf("https://fakeurl.com"),
                            photoUrl = "https://fakephotourl.com",
                            channels = emptyList(),
                        ),
                        Official(
                            name = "Fake Official 2",
                            address = emptyList(),
                            party = "Fake Party 2",
                            phones = listOf("11111111111", "22222222222"),
                            urls = listOf("https://fakeurl.com"),
                            photoUrl = "https://fakephotourl.com",
                            channels = emptyList(),
                        ),
                        Official(
                            name = "Fake Official 3",
                            address = emptyList(),
                            party = "Fake Party 3",
                            phones = listOf("11111111111", "22222222222"),
                            urls = listOf("https://fakeurl.com"),
                            photoUrl = "https://fakephotourl.com",
                            channels = emptyList(),
                        ),
                    ),
                ),
            )
        }
    }

    override fun observerElections(): LiveData<Result<List<Election>>> {
        return MutableLiveData<Result<List<Election>>>().map {
            Result.Success(elections ?: emptyList())
        }
    }

    override suspend fun updateElection(election: Election) {
        elections?.removeIf { it.id == election.id }
        elections?.add(election)
    }

    override suspend fun saveElection(election: Election) {
        elections?.add(election)
    }

    override suspend fun getElectionById(id: Int): Result<Election?> {
        return if (returnError) {
            Result.Error(Exception("No election found"))
        } else {
            Result.Success(elections?.find { it.id == id })
        }
    }
}
