package co.nimblehq.alpine.sample.nfc

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import co.nimblehq.alpine.databinding.ActivityPassportInformationBinding

class PassportInformationActivity : ComponentActivity() {

    private val binding: ActivityPassportInformationBinding by lazy {
        ActivityPassportInformationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        with(binding) {
            passportInfoPassportNumber.editText?.imeOptions = EditorInfo.IME_ACTION_DONE

            // TODO: Update on Integrate ticket.
            val shouldEnabledNextButton = !passportInfoPassportNumber.editText?.text.isNullOrEmpty()
                    && !passportInfoDateOfBirth.editText?.text.isNullOrEmpty()
                    && !passportInfoDateOfExpiry.editText?.text.isNullOrEmpty()
            passportInfoNextButton.isEnabled = shouldEnabledNextButton
        }
    }
}
