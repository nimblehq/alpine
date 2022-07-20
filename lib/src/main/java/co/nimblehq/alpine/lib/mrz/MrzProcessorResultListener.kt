package co.nimblehq.alpine.lib.mrz

import co.nimblehq.alpine.lib.model.MrzInfo

interface MrzProcessorResultListener {
    fun onSuccess(mrzInfo: MrzInfo)

    fun onError(e: MrzProcessorException)
}
