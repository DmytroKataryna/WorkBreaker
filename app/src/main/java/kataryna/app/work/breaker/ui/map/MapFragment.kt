package kataryna.app.work.breaker.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kataryna.app.work.breaker.R

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private val requestedPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {}

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

    private fun Map<String, Boolean>.hasPermission(accessFineLocation: String) =
        this.getOrDefault(accessFineLocation, false)

    private fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }
}

