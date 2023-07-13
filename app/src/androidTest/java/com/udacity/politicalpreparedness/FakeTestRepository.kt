package com.udacity.politicalpreparedness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.AdministrationBody
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.Office
import com.udacity.politicalpreparedness.data.network.models.Official
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.ui.representative.model.Representative
import kotlinx.coroutines.runBlocking

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
class FakeTestRepository : CommonDataSource {

    var electionsServiceData: LinkedHashMap<Int, Election> = LinkedHashMap()

    private val observableElections = MutableLiveData<Result<List<Election>>>()

    private var returnError = false

    // Create a fake data source to act as a double to the real data source
    fun returnError(value: Boolean) {
        returnError = value
    }

    override suspend fun getElections(forceUpdate: Boolean): Result<List<Election>> {
        if (returnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(electionsServiceData.values.toList())
    }

    override fun observeOnElections(): LiveData<Result<List<Election>>> {
        runBlocking { reloadElections() }
        return observableElections
    }

    override suspend fun getElectionDetails(address: String, electionId: Int): Result<State?> {
        return if (returnError) {
            Result.Error(Exception("Test exception"))
        } else {
            Result.Success(fakeStateData())
        }
    }

    override suspend fun getRepresentatives(address: Address): Result<List<Representative>> {
        return if (returnError) {
            Result.Error(Exception("Test exception"))
        } else {
            Result.Success(
                listOf(fakeRepresentativeData("Fake official 0"), fakeRepresentativeData("Fake official 1"), fakeRepresentativeData("Fake official 2")),
            )
        }
    }

    override suspend fun reloadElections() {
        observableElections.value = getElections(true)
    }

    override suspend fun updateElection(election: Election, isSaved: Boolean) {
        electionsServiceData[election.id] = election.copy(isSaved = isSaved)
    }

    fun addElections(vararg elections: Election) {
        for (election in elections) {
            electionsServiceData[election.id] = election
        }
    }

    fun clearElections() {
        electionsServiceData = LinkedHashMap()
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

    private fun fakeRepresentativeData(name: String): Representative {
        return Representative(
            official = Official(
                name = name,
                address = emptyList(),
                party = "Fake Party",
                phones = listOf("11111111111", "22222222222"),
                urls = listOf("https://fakeurl.com"),
                photoUrl = "https://fakephotourl.com",
                channels = emptyList(),
            ),
            office = Office(name = "Fake Office", division = Division(id = "Fake division id", country = "us", state = "ca"), officials = emptyList()),
        )
    }
}
