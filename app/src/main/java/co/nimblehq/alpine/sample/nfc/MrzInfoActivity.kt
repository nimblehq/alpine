package co.nimblehq.alpine.sample.nfc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import co.nimblehq.alpine.databinding.ActivityMrzInfoBinding

class MrzInfoActivity : ComponentActivity() {

    private val binding: ActivityMrzInfoBinding by lazy {
        ActivityMrzInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        with(binding) {

            // TODO: Update on Integrate ticket.
            val shouldEnabledNextButton = listOf(
                passportNumber,
                dateOfBirth,
                dateOfExpiry
            ).all { !it.editText?.text.isNullOrEmpty() }
            nextButton.isEnabled = shouldEnabledNextButton
        }
    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, MrzInfoActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
