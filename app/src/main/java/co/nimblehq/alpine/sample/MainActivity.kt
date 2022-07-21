package co.nimblehq.alpine.sample

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import co.nimblehq.alpine.R

class MainActivity : ComponentActivity(), View.OnClickListener {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                navigateToCamera()
            } else {
                showRationale()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listOf<Button>(
            findViewById(R.id.btn_main_start),
            findViewById(R.id.btn_main_enter_manually)
        ).forEach { it.setOnClickListener(this) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_main_start -> checkCameraPermissionAndNavigateToCamera()
            R.id.btn_main_enter_manually -> {
                // TODO: navigate to the manual MRZ info screen.
                Toast.makeText(this, "Not implemented yet :(", Toast.LENGTH_SHORT).show()
            }
            else -> IllegalArgumentException("").printStackTrace()
        }
    }

    private fun checkCameraPermissionAndNavigateToCamera() {
        when {
            checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED -> navigateToCamera()
            shouldShowRequestPermissionRationale(CAMERA) -> showRationale()
            else -> requestPermissionLauncher.launch(CAMERA)
        }
    }

    private fun navigateToCamera() {
        CameraCaptureActivity.start(this)
    }

    private fun showRationale() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.rationale_permission_camera))
            .setPositiveButton(R.string.ok) { _, _ -> navigateToSettings() }
            .setNegativeButton(R.string.no_thanks, null)
            .show()
    }

    private fun navigateToSettings() {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }.let(::startActivity)
    }
}
