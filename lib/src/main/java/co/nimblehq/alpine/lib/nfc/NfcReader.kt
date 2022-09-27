package co.nimblehq.alpine.lib.nfc

import android.content.Context
import android.nfc.Tag
import androidx.annotation.WorkerThread
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.model.PassportInfo

internal const val ISO_DEP_TIMEOUT_IN_MILLIS = 60000

internal interface NfcReader {

    fun initialize(context: Context)

    @WorkerThread
    fun readNfc(tag: Tag, mrzInfo: MrzInfo, timeout: Int): PassportInfo?
}
