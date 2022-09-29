package co.nimblehq.alpine.sample.extension

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import co.nimblehq.alpine.R

fun Context.createLoadingDialog(): Dialog {
    val inflate = LayoutInflater.from(this).inflate(R.layout.view_loading, null)
    return Dialog(this).apply {
        setContentView(inflate)
        setCancelable(false)
        window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
    }
}

fun Context.displayAlertDialog(
    title: String? = null,
    message: String,
    negativeButtonTitle: String? = null,
    negativeCallback: () -> Unit
) {
    val dialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
    if (negativeButtonTitle.isNullOrBlank().not()) {
        dialog.setNegativeButton(negativeButtonTitle) { _, _ ->
            negativeCallback.invoke()
        }
    }
    dialog.show()
}
