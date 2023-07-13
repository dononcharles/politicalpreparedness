package com.udacity.politicalpreparedness.ui.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseViewModel
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.ui.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val commonDataSource: CommonDataSource) : BaseViewModel() {

    private val _line1 = MutableLiveData("")
    val line1: MutableLiveData<String> = _line1
    private val _line2 = MutableLiveData("")
    val line2: MutableLiveData<String> = _line2
    private val _city = MutableLiveData("")
    val city: MutableLiveData<String> = _city
    private val _state = MutableLiveData("")
    val state: MutableLiveData<String> = _state
    private val _zip = MutableLiveData("")
    val zip: MutableLiveData<String> = _zip

    private val _representatives: MutableLiveData<Result<List<Representative>>> = MutableLiveData()
    val representatives: LiveData<List<Representative>> = _representatives.map {
        when (it) {
            is Result.Success -> it.data
            else -> emptyList()
        }
    }

    fun findMyRepresentatives() {
        val address = Address(
            line1.value.orEmpty(),
            line2.value.orEmpty(),
            city.value.orEmpty(),
            state.value.orEmpty(),
            zip.value.orEmpty(),
        )
        if (validateEnteredData(address)) {
            startSearchingRepresentatives(address)
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(address: Address): Boolean {
        if (address.line1.isBlank()) {
            showSnackBarInt.value = R.string.error_missing_first_line_address
            return false
        }
        if (address.city.isBlank()) {
            showSnackBarInt.value = R.string.error_missing_city
            return false
        }
        if (address.state.isBlank()) {
            showSnackBarInt.value = R.string.error_missing_state
            return false
        }
        if (address.zip.isBlank()) {
            showSnackBarInt.value = R.string.error_missing_zip
            return false
        }
        return true
    }

    private fun startSearchingRepresentatives(address: Address) {
        showLoading.value = true
        viewModelScope.launch {
            _representatives.value = commonDataSource.getRepresentatives(address)
            when (val r = _representatives.value) {
                is Result.Error -> showSnackBar.value = r.exception.message
                is Result.Success,
                is Result.Loading,
                null,
                -> {
                    // do nothing
                }
            }
            showLoading.value = false
        }
    }

    fun findRepresentativesByGeoLocation(address: Address?) {
        if (address == null) {
            showSnackBarInt.value = R.string.error_missing_location
            return
        }

        _line1.value = address.line1
        _line2.value = address.line2
        _city.value = address.city
        _state.value = address.state
        _zip.value = address.zip

        if (validateEnteredData(address)) {
            startSearchingRepresentatives(address)
        }
    }

    fun setState(newState: String?) {
        _state.value = newState.orEmpty()
    }
}
