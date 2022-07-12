package co.nimblehq.alpine.lib.model.nfc

private const val DOCUMENT_CODE_PASSPORT = "P"
private const val DOCUMENT_CODE_ID_CARD = "I"

enum class DocumentType(val value: String) {
    // TODO: Use resource ID instead of hardcoded strings
    PASSPORT("Passport"),
    ID_CARD("ID card"),
    OTHER("Other");

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
