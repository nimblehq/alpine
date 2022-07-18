package co.nimblehq.alpine.lib.mrz

sealed class MrzProcessorException : Exception() {
    object DefaultMrzProcessorException : MrzProcessorException()
    object InvalidMrzInfoMrzProcessorException : MrzProcessorException()
    object MrzInfoNotFoundMrzProcessorException : MrzProcessorException()
    object FileNotFoundMrzProcessorException : MrzProcessorException()
    object TextNotFoundMrzProcessorException : MrzProcessorException()
    object TextNotRecognizedMrzProcessorException : MrzProcessorException()
}
