package co.nimblehq.alpine.sample

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.nfc.*
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import co.nimblehq.alpine.R
import co.nimblehq.alpine.databinding.ActivityNfcScanningBinding
import co.nimblehq.alpine.lib.model.MrzInfo
import co.nimblehq.alpine.lib.model.PassportInfo
import co.nimblehq.alpine.lib.nfc.NfcReader
import coil.load
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class NfcScanningActivity : ComponentActivity() {

    private val mrzInfo: MrzInfo? by lazy {
        intent.extras?.getParcelable(ARG_MRZ)
    }
    private val adapter: NfcAdapter by lazy {
        (getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter
    }
    private val nfcReader: NfcReader by lazy {
        NfcReader.newInstance(this)
    }
    private val binding: ActivityNfcScanningBinding by lazy {
        ActivityNfcScanningBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        startScanning()
    }

    override fun onPause() {
        super.onPause()
        adapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.takeIf { it.action == NfcAdapter.ACTION_TECH_DISCOVERED }
            ?.extras
            ?.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)
            ?.takeIf { it.techList.contains(NFC_TECH_ISO_DEP) }
            ?.run(::readNfc)
    }

    @SuppressLint("InlinedApi")
    private fun startScanning() {
        val intent = Intent(this, javaClass)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        val techLists = arrayOf(arrayOf(NFC_TECH_ISO_DEP))
        adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
    }

    private fun readNfc(tag: Tag) {
        with(binding) {
            tvInstruction.visibility = GONE
            gPassportDetails.visibility = GONE
            pbLoading.visibility = VISIBLE
        }
        mrzInfo
            ?.run { readDataFromNfcChip(tag, this) }
            ?: run { showError() }
    }

    private fun readDataFromNfcChip(tag: Tag, mrzInfo: MrzInfo) {
        lifecycleScope.launch(Dispatchers.Main) {
            showMessage(getString(R.string.nfc_scanning_reading_data))
            val passportInfo = withContext(Dispatchers.IO) { nfcReader.readNfc(tag, mrzInfo) }
            if (passportInfo == null) {
                showError()
            } else {
                loadPassportInfo(passportInfo)
            }
        }
    }

    private fun showError() {
        binding.gPassportDetails.visibility = GONE
        binding.pbLoading.visibility = GONE
        binding.tvInstruction.visibility = VISIBLE
        showMessage(getString(R.string.error_generic))
    }

    private fun loadPassportInfo(passportInfo: PassportInfo) {
        with(passportInfo) {
            with(binding) {
                gPassportDetails.visibility = VISIBLE
                pbLoading.visibility = GONE
                personDetails?.run {
                    tvName.text = getString(R.string.nfc_scanning_name, name)
                    tvSurname.text = getString(R.string.nfc_scanning_surname, surname)
                    tvPersonalNumber.text = getString(R.string.nfc_scanning_personal_number, personalNumber)
                    tvGender.text = getString(R.string.nfc_scanning_gender, gender)
                    tvBirthDate.text = getString(R.string.nfc_scanning_birth_date, birthDate)
                    tvExpiryDate.text = getString(R.string.nfc_scanning_expiry_date, expiryDate)
                    tvSerialNumber.text = getString(R.string.nfc_scanning_passport_number, documentNumber)
                    tvNationality.text = getString(R.string.nfc_scanning_nationality, nationality)
                    tvIssuerAuthority.text = getString(R.string.nfc_scanning_issuer_authority, issuingState)
                }
                tvDocumentType.text = getString(R.string.nfc_scanning_document_type, documentType?.name)
                biometrics?.run {
                    faceImage?.bitmap?.let { bitmap ->
                        ivPassportImage.load(bitmap)
                        gPassportImage.visibility = VISIBLE
                    } ?: run { gPassportImage.visibility = GONE }
                    signatureImage?.bitmap?.let { bitmap ->
                        ivSignatureImage.load(bitmap)
                        gSignatureImage.visibility = VISIBLE
                    } ?: run { gSignatureImage.visibility = GONE }
                    fingerprints?.firstOrNull()?.let { bitmap ->
                        ivFingerprintImage.load(bitmap)
                        gFingerprintImage.visibility = VISIBLE
                    } ?: run { gFingerprintImage.visibility = GONE }
                }
            }
        }
    }

    private fun showMessage(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    companion object {
        private const val ARG_MRZ = "ARG_MRZ"
        private const val NFC_TECH_ISO_DEP = "android.nfc.tech.IsoDep"

        fun start(context: Context, mrzInfo: MrzInfo) {
            context.startActivity(
                Intent(context, NfcScanningActivity::class.java)
                    .putExtra(ARG_MRZ, mrzInfo)
            )
        }
    }
}
