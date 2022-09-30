package co.nimblehq.alpine.lib.nfc

import android.content.Context
import android.nfc.Tag
import androidx.annotation.WorkerThread
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.model.PassportInfo

interface NfcReader {
    @WorkerThread
    fun readNfc(tag: Tag, mrzInfo: MrzInfo, timeout: Int = ISO_DEP_TIMEOUT_IN_MILLIS, progressCallback: (Float) -> Unit): PassportInfo?

    companion object {
        private const val ISO_DEP_TIMEOUT_IN_MILLIS = 60000

        @JvmStatic
        fun newInstance(context: Context): NfcReader = NfcReaderImpl(context)
    }
}
