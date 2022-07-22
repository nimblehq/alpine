package co.nimblehq.alpine.sample.extension

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
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
