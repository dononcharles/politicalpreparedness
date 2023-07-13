package com.udacity.politicalpreparedness.ui.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseViewModel
import com.udacity.politicalpreparedness.base.NavigationCommand
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.network.models.toElectionModels
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import kotlinx.coroutines.launch

class ElectionsViewModel(private val commonDataSource: CommonDataSource) : BaseViewModel() {

    private var electionsData: LiveData<List<ElectionModel>?> = commonDataSource.observeOnElections().map { r ->
        when (r) {
            is Result.Success -> {
                r.data.toElectionModels()
            }

            is Result.Error -> {
                showToast.value = R.string.error_message
                emptyList()
            }

            is Result.Loading -> {
                upcomingElections.value
            }
        }
    }

    // Create live data val for saved elections
    val electionSaved: LiveData<List<ElectionModel>?> = electionsData.map { r ->
        r?.filter { it.isSaved }
    }

    // Create live data val for upcoming elections
    val upcomingElections: LiveData<List<ElectionModel>?> = electionsData

    // refresh elections list
    fun refreshElectionsList() {
        showLoading.value = true
        viewModelScope.launch {
            commonDataSource.reloadElections()
            showLoading.value = false
        }
    }

    fun onUpComingItemClicked(electionModel: ElectionModel) {
        navigationCommand.value = NavigationCommand.To(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(electionModel))
    }

    fun onSavedItemClicked(electionModel: ElectionModel) {
        navigationCommand.value = NavigationCommand.To(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(electionModel))
    }
}
