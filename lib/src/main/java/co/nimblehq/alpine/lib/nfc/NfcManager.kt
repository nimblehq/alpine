@file:JvmName("NfcManager")

package co.nimblehq.alpine.lib.nfc

import android.content.Context
import android.graphics.Bitmap
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.annotation.WorkerThread
import co.nimblehq.alpine.lib.mrz.MrzInfo
import co.nimblehq.alpine.lib.model.nfc.*
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.*
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.lds.iso19794.FingerImageInfo

interface NfcReader {
    @WorkerThread
    fun readNfc(tag: Tag, mrzInfo: MrzInfo, timeout: Int = ISO_DEP_TIMEOUT_IN_MILLIS): PassportInfo?

    companion object {
        @JvmStatic
        fun newInstance(context: Context): NfcReader = NfcReaderImpl(context)
    }
}

private const val ISO_DEP_TIMEOUT_IN_MILLIS = 60000

private class NfcReaderImpl(private val context: Context) : NfcReader {

    @Suppress("ComplexMethod", "TooGenericExceptionCaught")
    override fun readNfc(tag: Tag, mrzInfo: MrzInfo, timeout: Int): PassportInfo? {
        try {
            val bacKey = mrzInfo.run {
                BACKey(documentNumber, dateOfBirth, dateOfExpiry)
            }
            val isoDep = IsoDep.get(tag)
            isoDep?.timeout = timeout
            val cardService = CardService.getInstance(isoDep)
            cardService.open()
            val passportService = PassportService(
                cardService,
                PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                PassportService.DEFAULT_MAX_BLOCKSIZE,
                true,
                false
            )
            passportService.open()
            var paceSucceeded = false
            try {
                val cardSecurityFile =
                    CardSecurityFile(passportService.getInputStream(PassportService.EF_CARD_SECURITY))
                val securityInfoCollection = cardSecurityFile.securityInfos
                for (securityInfo in securityInfoCollection) {
                    if (securityInfo is PACEInfo) {
                        passportService.doPACE(
                            bacKey,
                            securityInfo.objectIdentifier,
                            PACEInfo.toParameterSpec(securityInfo.parameterId)
                        )
                        paceSucceeded = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            passportService.sendSelectApplet(paceSucceeded)
            if (!paceSucceeded) {
                try {
                    passportService.getInputStream(PassportService.EF_COM).read()
                } catch (e: Exception) {
                    passportService.doBAC(bacKey)
                }
            }

            val passportInfo = PassportInfo()

            // -- Personal Details -- //
            val personDetails = PersonDetails()
            val dg1InputStream = passportService.getInputStream(PassportService.EF_DG1)
            val dg1File = DG1File(dg1InputStream)
            val dg1MrzInfo = dg1File.mrzInfo
            personDetails.apply {
                name = dg1MrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
                surname = dg1MrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' }
                personalNumber = dg1MrzInfo.personalNumber
                gender = dg1MrzInfo.gender.toString()
                birthDate = dg1MrzInfo.dateOfBirth
                expiryDate = dg1MrzInfo.dateOfExpiry
                documentNumber = dg1MrzInfo.documentNumber
                nationality = dg1MrzInfo.nationality
                issuingState = dg1MrzInfo.issuingState
            }

            passportInfo.documentType = DocumentType.from(dg1MrzInfo.documentCode)

            // -- Face Image -- //
            val dg2InputStream = passportService.getInputStream(PassportService.EF_DG2)
            val dg2File = DG2File(dg2InputStream)
            val faceInfos = dg2File.faceInfos
            val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
            for (faceInfo in faceInfos) {
                allFaceImageInfos.addAll(faceInfo.faceImageInfos)
            }
            if (allFaceImageInfos.isNotEmpty()) {
                val faceImageInfo = allFaceImageInfos.iterator().next()
                val image = ImageUtil.getImage(context, faceImageInfo)
                personDetails.faceImage = image
            }

            // -- Fingerprint (if exist)-- //
            try {
                val dg3InputStream = passportService.getInputStream(PassportService.EF_DG3)
                val dg3File = DG3File(dg3InputStream)
                val fingerInfos = dg3File.fingerInfos
                val allFingerImageInfos: MutableList<FingerImageInfo> = ArrayList()
                for (fingerInfo in fingerInfos) {
                    allFingerImageInfos.addAll(fingerInfo.fingerImageInfos)
                }
                val fingerprintsImage: MutableList<Bitmap> = ArrayList()
                if (allFingerImageInfos.isNotEmpty()) {
                    for (fingerImageInfo in allFingerImageInfos) {
                        val image = ImageUtil.getImage(context, fingerImageInfo)
                        fingerprintsImage.add(image)
                    }
                    personDetails.fingerprints = fingerprintsImage
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // -- Portrait Picture -- //
            try {
                val dg5InputStream = passportService.getInputStream(PassportService.EF_DG5)
                val dg5File = DG5File(dg5InputStream)
                val displayedImageInfos = dg5File.images
                if (displayedImageInfos.isNotEmpty()) {
                    val displayedImageInfo = displayedImageInfos.iterator().next()
                    val image = ImageUtil.getImage(context, displayedImageInfo)
                    personDetails.portraitImage = image
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // -- Signature (if exist) -- //
            try {
                val dg7InputStream = passportService.getInputStream(PassportService.EF_DG7)
                val dg7File = DG7File(dg7InputStream)
                val signatureImageInfos = dg7File.images
                if (signatureImageInfos.isNotEmpty()) {
                    val displayedImageInfo = signatureImageInfos.iterator().next()
                    val image = ImageUtil.getImage(context, displayedImageInfo)
                    personDetails.signature = image
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // -- Additional Details (if exist) -- //
            val additionalPersonDetails = AdditionalPersonDetails()
            try {
                val dg11InputStream = passportService.getInputStream(PassportService.EF_DG11)
                val dg11File = DG11File(dg11InputStream)
                if (dg11File.length > 0) {
                    additionalPersonDetails.apply {
                        custodyInformation = dg11File.custodyInformation
                        nameOfHolder = dg11File.nameOfHolder
                        fullDateOfBirth = dg11File.fullDateOfBirth
                        otherNames = dg11File.otherNames
                        otherValidTDNumbers = dg11File.otherValidTDNumbers
                        permanentAddressConstituents = dg11File.permanentAddress
                        personalNumber = dg11File.personalNumber
                        personalSummary = dg11File.personalSummary
                        placeOfBirthConstituents = dg11File.placeOfBirth
                        profession = dg11File.profession
                        proofOfCitizenship = dg11File.proofOfCitizenship
                        this.tag = dg11File.tag
                        tagPresenceList = dg11File.tagPresenceList
                        telephone = dg11File.telephone
                        title = dg11File.title
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // -- Additional Document Details (if exist) -- //
            val additionalDocumentDetails = AdditionalDocumentDetails()
            try {
                val dg12InputStream = passportService.getInputStream(PassportService.EF_DG12)
                val dg12File = DG12File(dg12InputStream)
                if (dg12File.length > 0) {
                    additionalDocumentDetails.apply {
                        dateOfIssue = dg12File.dateOfIssue
                        issuingAuthority = dg12File.issuingAuthority
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // -- Document Public Key -- //
            try {
                val dg15InputStream = passportService.getInputStream(PassportService.EF_DG15)
                val dg15File = DG15File(dg15InputStream)
                val publicKey = dg15File.publicKey
                passportInfo.documentPublicKey = publicKey
            } catch (e: Exception) {
                e.printStackTrace()
            }
            passportInfo.personDetails = personDetails
            passportInfo.additionalPersonDetails = additionalPersonDetails
            passportInfo.additionalDocumentDetails = additionalDocumentDetails

            return passportInfo
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
