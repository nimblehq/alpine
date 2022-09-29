package co.nimblehq.alpine.lib

import co.nimblehq.alpine.lib.model.DocumentType
import co.nimblehq.alpine.lib.model.DocumentType.*
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentTypeTest {

    @Test
    fun `When calling the factory method, it returns the correct enum`() {
        assertEquals(PASSPORT, DocumentType.from("P"))
        assertEquals(ID_CARD, DocumentType.from("I"))
        assertEquals(OTHER, DocumentType.from(""))
        assertEquals(OTHER, DocumentType.from(null))
    }
}
