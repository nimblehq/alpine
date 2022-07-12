package co.nimblehq.alpine.lib.model.nfc

data class AdditionalPersonDetails(
    var custodyInformation: String? = null,
    var fullDateOfBirth: String? = null,
    var nameOfHolder: String? = null,
    var otherNames: List<String>? = null,
    var otherValidTDNumbers: List<String>? = null,
    var permanentAddressConstituents: List<String>? = null,
    var personalNumber: String? = null,
    var personalSummary: String? = null,
    var placeOfBirthConstituents: List<String>? = null,
    var profession: String? = null,
    var proofOfCitizenship: ByteArray? = null,
    var tag: Int = 0,
    var tagPresenceList: List<Int>? = null,
    var telephone: String? = null,
    var title: String? = null
)
