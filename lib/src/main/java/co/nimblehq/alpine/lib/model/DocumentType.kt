package co.nimblehq.alpine.lib.model

enum class DocumentType(private val documentCode: String?) {
    PASSPORT("P"),
    ID_CARD("I"),
    OTHER(null);

    companion object {
        internal fun from(documentCode: String?): DocumentType {
            return values().find { it.documentCode.equals(documentCode, true) } ?: OTHER
        }
    }
}
