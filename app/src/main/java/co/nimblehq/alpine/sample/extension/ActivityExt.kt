package co.nimblehq.alpine.sample.extension

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import co.nimblehq.alpine.R
import java.io.File

fun Activity.setLightStatusBar() {
    window?.decorView?.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    window?.statusBarColor = Color.TRANSPARENT
}

fun Activity.setWindowFlag(bits: Int, on: Boolean) {
    window?.run {
        val winParams = attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        attributes = winParams
    }
}

fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun Activity.getMediaOutputDirectory(): File {
    val mediaDir = applicationContext.externalMediaDirs.firstOrNull()?.let {
        File(it, applicationContext.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) {
        mediaDir
    } else {
        applicationContext.filesDir
    }
}
