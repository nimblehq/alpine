package co.nimblehq.alpine.lib.nfc

import android.content.Context
import android.graphics.Bitmap
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.annotation.WorkerThread
import co.nimblehq.alpine.lib.model.*
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.*
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.lds.iso19794.FingerImageInfo
import java.security.PublicKey

internal class NfcReaderImpl(private val context: Context) : NfcReader {

    override fun readNfc(tag: Tag, mrzInfo: MrzInfo, timeout: Int): PassportInfo? {
        return try {
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

            val dg1InputStream = passportService.getInputStream(PassportService.EF_DG1)
            val dg1File = DG1File(dg1InputStream)
            val dg1MrzInfo = dg1File.mrzInfo

            PassportInfo(
                personDetails = getPersonDetails(dg1MrzInfo),
                biometrics = getBiometrics(passportService),
                additionalPersonDetails = getAdditionalPersonDetails(passportService),
                additionalDocumentDetails = getAdditionalDocumentDetails(passportService),
                documentType = DocumentType.from(dg1MrzInfo.documentCode),
                documentPublicKey = getDocumentPublicKey(passportService)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getPersonDetails(dg1MrzInfo: MRZInfo): PersonDetails {
        return PersonDetails(
            name = dg1MrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' },
            surname = dg1MrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' },
            personalNumber = dg1MrzInfo.personalNumber,
            gender = dg1MrzInfo.gender.toString(),
            birthDate = dg1MrzInfo.dateOfBirth,
            expiryDate = dg1MrzInfo.dateOfExpiry,
            documentNumber = dg1MrzInfo.documentNumber,
            nationality = dg1MrzInfo.nationality,
            issuingState = dg1MrzInfo.issuingState
        )
    }

    private fun getBiometrics(passportService: PassportService): Biometrics {
        val faceImage = getFaceImage(passportService)
        val portraitImage = getPortraitImage(passportService)
        val signature = getSignature(passportService)
        return Biometrics(
            faceImageBitmap = faceImage?.bitmap,
            faceImageBase64 = faceImage?.base64,
            fingerprints = getFingerprints(passportService),
            portraitImageBitmap = portraitImage?.bitmap,
            portraitImageBase64 = portraitImage?.base64,
            signatureBitmap = signature?.bitmap,
            signatureBase64 = signature?.base64
        )
    }

    private fun getFaceImage(passportService: PassportService): Image? {
        val dg2InputStream = passportService.getInputStream(PassportService.EF_DG2)
        val dg2File = DG2File(dg2InputStream)
        val faceInfos = dg2File.faceInfos
        val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
        for (faceInfo in faceInfos) {
            allFaceImageInfos.addAll(faceInfo.faceImageInfos)
        }
        if (allFaceImageInfos.isNotEmpty()) {
            val faceImageInfo = allFaceImageInfos.iterator().next()
            return ImageUtil.getImage(context, faceImageInfo)
        }
        return null
    }

    private fun getFingerprints(passportService: PassportService): List<Bitmap>? {
        return try {
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
                    val image = ImageUtil.getImage(context, fingerImageInfo).bitmap
                    image?.let(fingerprintsImage::add)
                }
                fingerprintsImage
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getPortraitImage(passportService: PassportService): Image? {
        return try {
            val dg5InputStream = passportService.getInputStream(PassportService.EF_DG5)
            val dg5File = DG5File(dg5InputStream)
            val displayedImageInfos = dg5File.images
            if (displayedImageInfos.isNotEmpty()) {
                val displayedImageInfo = displayedImageInfos.iterator().next()
                ImageUtil.getImage(context, displayedImageInfo)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getSignature(passportService: PassportService): Image? {
        return try {
            val dg7InputStream = passportService.getInputStream(PassportService.EF_DG7)
            val dg7File = DG7File(dg7InputStream)
            val signatureImageInfos = dg7File.images
            if (signatureImageInfos.isNotEmpty()) {
                val displayedImageInfo = signatureImageInfos.iterator().next()
                ImageUtil.getImage(context, displayedImageInfo)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getAdditionalPersonDetails(passportService: PassportService): AdditionalPersonDetails? {
        return try {
            val dg11InputStream = passportService.getInputStream(PassportService.EF_DG11)
            val dg11File = DG11File(dg11InputStream)
            if (dg11File.length > 0) {
                AdditionalPersonDetails(
                    custodyInformation = dg11File.custodyInformation,
                    nameOfHolder = dg11File.nameOfHolder,
                    fullDateOfBirth = dg11File.fullDateOfBirth,
                    otherNames = dg11File.otherNames,
                    otherValidTDNumbers = dg11File.otherValidTDNumbers,
                    permanentAddressConstituents = dg11File.permanentAddress,
                    personalNumber = dg11File.personalNumber,
                    personalSummary = dg11File.personalSummary,
                    placeOfBirthConstituents = dg11File.placeOfBirth,
                    profession = dg11File.profession,
                    proofOfCitizenship = dg11File.proofOfCitizenship,
                    tag = dg11File.tag,
                    tagPresenceList = dg11File.tagPresenceList,
                    telephone = dg11File.telephone,
                    title = dg11File.title
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getAdditionalDocumentDetails(passportService: PassportService): AdditionalDocumentDetails? {
        return try {
            val dg12InputStream = passportService.getInputStream(PassportService.EF_DG12)
            val dg12File = DG12File(dg12InputStream)
            if (dg12File.length > 0) {
                AdditionalDocumentDetails(
                    dateOfIssue = dg12File.dateOfIssue,
                    issuingAuthority = dg12File.issuingAuthority
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getDocumentPublicKey(passportService: PassportService): PublicKey? {
        return try {
            val dg15InputStream = passportService.getInputStream(PassportService.EF_DG15)
            val dg15File = DG15File(dg15InputStream)
            dg15File.publicKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
