package com.ngthphong92.trackme.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.ngthphong92.trackme.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

const val REQUEST_SUCCESS = 1
const val REQUEST_FAILURE = -1

open class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private var requestPermissionCallback: ((Int) -> Unit)? = null

    fun checkNeededPermission(callback: (Int) -> Unit) {
        requestPermissionCallback = callback
        hasLocationAndCoarsePermissions()
    }

    @AfterPermissionGranted(RC_LOCATION_COARSE_PERM)
    private fun hasLocationAndCoarsePermissions() {
        if (!EasyPermissions.hasPermissions(this, *LOCATION_AND_COARSE)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.rationale_location_coarse), RC_LOCATION_COARSE_PERM, *LOCATION_AND_COARSE
            )
        } else {
            requestPermissionCallback?.invoke(REQUEST_SUCCESS)
            clearCallback()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        requestPermissionCallback?.invoke(REQUEST_SUCCESS)
        clearCallback()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissionCallback?.invoke(REQUEST_FAILURE)
            clearCallback()
        }
    }

    private fun clearCallback() {
        requestPermissionCallback = null
    }

    companion object {
        private val LOCATION_AND_COARSE =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val RC_LOCATION_COARSE_PERM = 124
    }
}