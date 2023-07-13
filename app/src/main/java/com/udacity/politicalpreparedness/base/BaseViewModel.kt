package com.udacity.politicalpreparedness.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.politicalpreparedness.utils.SingleLiveEvent
import org.koin.core.component.KoinComponent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel() : ViewModel(), KoinComponent {
    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<Int> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showNoData: MutableLiveData<Boolean> = MutableLiveData()
}
