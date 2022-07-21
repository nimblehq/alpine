package co.nimblehq.alpine.sample.nfc

import android.os.Bundle
import androidx.activity.ComponentActivity
import co.nimblehq.alpine.databinding.ActivityNfcScanningBinding

class NfcScanningActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNfcScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
