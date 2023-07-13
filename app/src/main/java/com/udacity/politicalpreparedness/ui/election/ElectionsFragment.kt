package com.udacity.politicalpreparedness.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseFragment
import com.udacity.politicalpreparedness.databinding.FragmentElectionBinding
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionListAdapter
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionListener
import com.udacity.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.udacity.politicalpreparedness.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : BaseFragment() {

    override val _viewModel by viewModel<ElectionsViewModel>()

    private val binding by lazy { DataBindingUtil.inflate<FragmentElectionBinding>(layoutInflater, R.layout.fragment_election, null, false) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.upcoming_saved_elections))

        binding.viewModel = _viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.swipeRefreshLayout.setOnRefreshListener {
            _viewModel.refreshElectionsList()
        }

        binding.savedElectionsRv.adapter = ElectionListAdapter(
            ElectionListener { electionModel ->
                _viewModel.onSavedItemClicked(electionModel)
            },
        )

        binding.upcomingElectionsRv.adapter = ElectionListAdapter(
            ElectionListener { electionModel ->
                _viewModel.onUpComingItemClicked(electionModel)
            },
        )
    }

    // Refresh adapters when fragment loads
    override fun onResume() {
        super.onResume()
        _viewModel.refreshElectionsList()
    }
}
