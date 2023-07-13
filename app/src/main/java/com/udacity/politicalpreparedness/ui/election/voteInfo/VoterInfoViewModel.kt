package com.udacity.politicalpreparedness.ui.election.voteInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseViewModel
import com.udacity.politicalpreparedness.base.NavigationCommand
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.adapter.toElection
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val commonDataSource: CommonDataSource, var electionModel: ElectionModel) : BaseViewModel() {

    // Add live data to hold voter info
    private val _voterInfo: MutableLiveData<Result<State?>> = MutableLiveData()
    val voterInfo: LiveData<State?>
        get() = _voterInfo.map { result ->
            when (result) {
                is Result.Success -> result.data
                else -> {
                    if (result is Result.Error) {
                        showToast.postValue(R.string.error_occurred_while_loading_voter_info)
                    }
                    null
                }
            }
        }

    fun loadDetailsByAddress(address: android.location.Address?) {
        viewModelScope.launch {
            val exactAddress = "${address?.getAddressLine(0)}"
            val response = commonDataSource.getElectionDetails(exactAddress, electionModel.id)
            _voterInfo.value = response
        }
    }

    /**
     * This function will be called when the user clicks on "Follow / Unfollow" button.
     */
    fun onSavedBtnClicked() {
        viewModelScope.launch {
            if (electionModel.isSaved) {
                // remove elections to local database
                commonDataSource.updateElection(electionModel.toElection(), isSaved = false)
            } else {
                // save elections to local database
                commonDataSource.updateElection(electionModel.toElection(), isSaved = true)
            }
            navigationCommand.value = NavigationCommand.Back
        }
    }
}
