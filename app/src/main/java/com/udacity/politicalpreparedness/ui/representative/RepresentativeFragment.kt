package com.udacity.politicalpreparedness.ui.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.base.BaseFragment
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.udacity.politicalpreparedness.ui.representative.adapter.RepresentativeListAdapter
import com.udacity.politicalpreparedness.ui.representative.adapter.RepresentativeListener
import com.udacity.politicalpreparedness.utils.fadeIn
import com.udacity.politicalpreparedness.utils.fadeOut
import com.udacity.politicalpreparedness.utils.requirePermissionSnackBar
import com.udacity.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.udacity.politicalpreparedness.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class RepresentativeFragment : BaseFragment() {

    override val _viewModel by viewModel<RepresentativeViewModel>()

    private val binding by lazy { DataBindingUtil.inflate<FragmentRepresentativeBinding>(layoutInflater, R.layout.fragment_representative, null, false) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.find_my_representatives))

        binding.viewModel = _viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.motionLayout.setTransition(R.id.start, R.id.start)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        listenToObservers()

        binding.buttonLocation.setOnClickListener { enableMyLocation() }
        binding.representativeRc.adapter = RepresentativeListAdapter(RepresentativeListener { })
        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                _viewModel.setState(requireContext().resources.getStringArray(R.array.states)[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun listenToObservers() {
        _viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) hideKeyboard()
        }

        _viewModel.representatives.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.motionLayout.setTransition(R.id.start, R.id.start)
            } else {
                binding.motionLayout.setTransition(R.id.start, R.id.end)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun geoCodeLocation(location: Location, addressCallback: (address: Address?) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) {
                addressCallback.invoke(
                    it.map { address ->
                        Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                    }.firstOrNull(),
                )
            }
        } else {
            addressCallback.invoke(
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    ?.map { address ->
                        Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                    }
                    ?.firstOrNull(),
            )
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun enableMyLocation() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.representativesLoading.fadeIn()
                fusedLocationClient.getCurrentLocation(
                    100,
                    object : CancellationToken() {
                        override fun isCancellationRequested(): Boolean {
                            return false
                        }

                        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                            return this
                        }
                    },
                ).addOnSuccessListener { locate: Location ->
                    geoCodeLocation(locate) { address ->
                        _viewModel.findRepresentativesByGeoLocation(address)
                    }
                }.addOnCanceledListener { binding.representativesLoading.fadeOut() }
                    .addOnCompleteListener { binding.representativesLoading.fadeOut() }
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
}
