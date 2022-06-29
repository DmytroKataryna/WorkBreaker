package kataryna.app.work.breaker.presentation.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kataryna.app.work.breaker.R
import kataryna.app.work.breaker.databinding.FragmentMapBinding
import kataryna.app.work.breaker.utils.getViewDataBinding
import kataryna.app.work.breaker.utils.observe

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private val requestedPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    private lateinit var binding: FragmentMapBinding

    private var map: GoogleMap? = null
    private var userPickedLocation: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = getViewDataBinding<FragmentMapBinding>(container, R.layout.fragment_map).apply {
        binding = this@apply
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        observe(viewModel.state) {
            when (it) {
                MapViewModel.MapUiState.Loading -> progressBarVisibility(true)
                is MapViewModel.MapUiState.GeoLocation -> {
                    progressBarVisibility(false)
                    updateMarkerOnMap(it.location)
                    moveCameraToPosition(it.location)
                }
                is MapViewModel.MapUiState.Error, null -> showErrorDialog()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.fetchGeoLocation()

        with(googleMap) {
            isMyLocationEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            setOnMapClickListener {
                updateMarkerOnMap(it)
                viewModel.saveUserPickedLocation(it)
            }
        }
    }

    override fun onDestroy() {
        userPickedLocation = null
        map = null
        super.onDestroy()
    }

    private fun updateMarkerOnMap(location: LatLng) {
        userPickedLocation?.remove()
        userPickedLocation = map?.addMarker(MarkerOptions().position(location))
    }

    private fun moveCameraToPosition(location: LatLng) {
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun requestLocationPermissions() {
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val hasBgLocation = perms.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            if (!hasBgLocation) {
                showBackgroundLocNeedDialog()
            }
        }.launch(requestedPermissions)
    }

    private fun showBackgroundLocNeedDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_permission_title)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_permission_pos_btn) { _, _ -> requireContext().openAppSystemSettings() }
            .setNegativeButton(R.string.dialog_permission_neg_btn) { _, _ -> findNavController().popBackStack() }
            .show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_error_location_msg)
            .setCancelable(true)
            .show()
    }

    private fun progressBarVisibility(visible: Boolean) {
        binding.progressView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun Map<String, Boolean>.hasPermission(accessFineLocation: String) =
        this.getOrDefault(accessFineLocation, false)

    private fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }
}

