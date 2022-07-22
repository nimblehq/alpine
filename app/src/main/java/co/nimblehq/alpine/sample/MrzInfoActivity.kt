package co.nimblehq.alpine.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import co.nimblehq.alpine.databinding.ActivityMrzInfoBinding
import co.nimblehq.alpine.lib.model.MrzInfo

class MrzInfoActivity : ComponentActivity() {

    private val binding: ActivityMrzInfoBinding by lazy {
        ActivityMrzInfoBinding.inflate(layoutInflater)
    }

    private var passportNumber: String = ""
    private var dateOfBirth: String = ""
    private var dateOfExpiry: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        bindViewEvent()
    }

    private fun setupView() {
        enabledButtonNext()
    }

    private fun bindViewEvent() {
        with(binding) {
            tilPassportNumber.editText?.doAfterTextChanged {
                passportNumber = it.toString()
                enabledButtonNext()
            }

            tilDateOfBirth.editText?.doAfterTextChanged {
                dateOfBirth = it.toString()
                enabledButtonNext()
            }

            tilDateOfExpiry.editText?.doAfterTextChanged {
                dateOfExpiry = it.toString()
                enabledButtonNext()
            }

            btNext.setOnClickListener {
                val mrzInfo = MrzInfo(
                    documentNumber = this@MrzInfoActivity.passportNumber,
                    dateOfBirth = this@MrzInfoActivity.dateOfBirth,
                    dateOfExpiry = this@MrzInfoActivity.dateOfExpiry
                )
                navigateToNfcScanningScreen(mrzInfo)
            }
        }
    }

    private fun enabledButtonNext() {
        val shouldEnabledNextButton = listOf(
            passportNumber,
            dateOfBirth,
            dateOfExpiry
        ).all { it.isNotEmpty() }
        binding.btNext.isEnabled = shouldEnabledNextButton
    }

    private fun navigateToNfcScanningScreen(mrzInfo: MrzInfo) {
        NfcScanningActivity.start(this, mrzInfo)
    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, MrzInfoActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
