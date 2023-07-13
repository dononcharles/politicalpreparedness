package com.udacity.politicalpreparedness.ui.election.voteInfo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseFragment
import com.udacity.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.udacity.politicalpreparedness.utils.requirePermissionSnackBar
import com.udacity.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.udacity.politicalpreparedness.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class VoterInfoFragment : BaseFragment() {

    private val args: VoterInfoFragmentArgs by navArgs()

    override val _viewModel by viewModel<VoterInfoViewModel> { parametersOf(args.argElection) }

    private val binding by lazy { DataBindingUtil.inflate<FragmentVoterInfoBinding>(layoutInflater, R.layout.fragment_voter_info, null, false) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setDisplayHomeAsUpEnabled(true)
        setTitle(args.argElection.name)

        binding.stateLocations.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        binding.stateBallot.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        enableMyLocation()
    }

    private fun enableMyLocation() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation.addOnSuccessListener { locate: Location ->
                    geoCodeLocation(locate) { address ->
                        _viewModel.loadDetailsByAddress(address = address)
                    }
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requirePermissionSnackBar(R.string.permission_denied_explanation)
            }

            else -> {
                activityResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.entries.filter { it.value }.map { it.key }
        if (granted.contains(Manifest.permission.ACCESS_FINE_LOCATION) && granted.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            enableMyLocation()
        } else {
            requirePermissionSnackBar(R.string.permission_denied_explanation)
        }
    }

    @Suppress("DEPRECATION")
    private fun geoCodeLocation(location: Location, addressCallback: (address: android.location.Address?) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addressCallback.invoke(it.firstOrNull()) }
        } else {
            addressCallback.invoke(geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull())
        }
    }
}
