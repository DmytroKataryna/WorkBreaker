package kataryna.app.work.breaker.presentation.features.map

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kataryna.app.work.breaker.R
import kataryna.app.work.breaker.databinding.FragmentMapBinding
import kataryna.app.work.breaker.presentation.broadcast.GeofenceBroadcastReceiver
import kataryna.app.work.breaker.extensions.getViewDataBinding
import kataryna.app.work.breaker.extensions.observeFlow
import timber.log.Timber

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
    private var geofencingClient: GeofencingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeFlow(viewModel.state) {
            progressBarVisibility(it.isLoading)
            if (it.exception != null) {
                showErrorDialog(R.string.dialog_error_location_msg)
            }
            it.location?.let { location ->
                updateMarkerOnMap(location)
                moveCameraToPosition(location)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = getViewDataBinding<FragmentMapBinding>(container, R.layout.fragment_map).apply {
        binding = this@apply
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            loadMap()
        } else {
            requestLocationPermissions()
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
                updateGeofenceLocation(it)
            }
            setOnMarkerClickListener {
                removeMarkerAndGeofence()
                true
            }
        }
    }

    override fun onDestroy() {
        userPickedLocation = null
        geofencingClient = null
        map = null
        super.onDestroy()
    }

    private fun updateMarkerOnMap(location: LatLng) {
        userPickedLocation?.remove()
        userPickedLocation = map?.addMarker(MarkerOptions().position(location))
    }

    private fun moveCameraToPosition(location: LatLng) {
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
    }

    private fun requestLocationPermissions() {
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            if (perms.containsPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                loadMap()
            } else {
                showBackgroundLocNeedDialog()
            }
        }.launch(requestedPermissions)
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun showBackgroundLocNeedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.dialog_permission_title)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_permission_pos_btn) { _, _ -> requireContext().openAppSystemSettings() }
            .setNegativeButton(R.string.dialog_permission_neg_btn) { _, _ -> findNavController().popBackStack() }
            .show()
    }

    private fun showErrorDialog(titleRes: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(titleRes)
            .setCancelable(true)
            .show()
    }

    private fun progressBarVisibility(visible: Boolean) {
        binding.progressView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun updateGeofenceLocation(loc: LatLng) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(loc.latitude, loc.longitude, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        geofencingClient?.addGeofences(getGeofencingRequest(geofence), geofencePendingIntent())
            ?.run {
                addOnSuccessListener {
                    Timber.d("Successfully added geofencing point")
                    viewModel.saveUserPickedLocation(loc)
                }
                addOnFailureListener {
                    Timber.d("Failed to added geofencing point")
                    removeMarkerAndGeofence()
                    showErrorDialog(R.string.dialog_error_geo_add_msg)
                }
            }
    }

    private fun getGeofencingRequest(geofence: Geofence) = GeofencingRequest.Builder()
        .apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

    private fun removeMarkerAndGeofence() {
        userPickedLocation?.remove()
        geofencingClient?.removeGeofences(listOf(GEOFENCE_ID))
        viewModel.clearUserPickedLocation()
    }

    private fun geofencePendingIntent(): PendingIntent {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @Suppress("SameParameterValue")
    private fun hasPermission(context: Context, permission: String) =
        checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun Map<String, Boolean>.containsPermission(accessFineLocation: String) =
        this.getOrDefault(accessFineLocation, false)

    private fun Context.openAppSystemSettings() {
        startActivity(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
        )
    }

    companion object {
        private const val GEOFENCE_ID = "geofence_id"
        private const val GEOFENCE_RADIUS_IN_METERS = 25f
        private const val ZOOM_LEVEL = 18f
    }
}
