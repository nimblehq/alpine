package co.nimblehq.alpine.lib.mrz

interface MrzProcessor {
    fun processImageFile(filePath: String, mrzProcessorResultListener: MrzProcessorResultListener)

    companion object {
        @JvmStatic
        fun newInstance(): MrzProcessor = MrzProcessorImpl()
    }
}
