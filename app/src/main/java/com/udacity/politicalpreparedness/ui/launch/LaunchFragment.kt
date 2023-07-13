package com.udacity.politicalpreparedness.ui.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseFragment
import com.udacity.politicalpreparedness.base.NavigationCommand
import com.udacity.politicalpreparedness.databinding.FragmentLaunchBinding
import com.udacity.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.udacity.politicalpreparedness.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchFragment : BaseFragment() {

    private val binding by lazy { DataBindingUtil.inflate<FragmentLaunchBinding>(layoutInflater, R.layout.fragment_launch, null, false) }

    override val _viewModel by viewModel<LaunchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.findRepresentativesButton.setOnClickListener { navToRepresentatives() }
        binding.upcomingElectionsBtn.setOnClickListener { navToElections() }
    }

    private fun navToElections() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment()),
        )
    }

    private fun navToRepresentatives() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment()),
        )
    }
}
