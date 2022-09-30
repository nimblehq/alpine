package co.nimblehq.alpine.sample.extension

import android.app.Activity
import android.graphics.Color
import androidx.core.view.WindowCompat
import co.nimblehq.alpine.R
import java.io.File

fun Activity.setLightStatusBar() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window?.statusBarColor = Color.TRANSPARENT
}

fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun Activity.getMediaOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, applicationContext.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) {
        mediaDir
    } else {
        applicationContext.filesDir
    }
}
