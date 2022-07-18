package co.nimblehq.alpine.lib.model

private const val DOCUMENT_CODE_PASSPORT = "P"
private const val DOCUMENT_CODE_ID_CARD = "I"

enum class DocumentType {
    PASSPORT,
    ID_CARD,
    OTHER;

    companion object {
        fun from(documentCode: String): DocumentType {
            return when (documentCode) {
                DOCUMENT_CODE_PASSPORT -> PASSPORT
                DOCUMENT_CODE_ID_CARD -> ID_CARD
                else -> OTHER
            }
        }
    }
}
