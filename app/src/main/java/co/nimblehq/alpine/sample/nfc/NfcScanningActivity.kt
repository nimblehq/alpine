package co.nimblehq.alpine.sample.nfc

import android.os.Bundle
import androidx.activity.ComponentActivity
import co.nimblehq.alpine.R

class NfcScanningActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_scanning)
    }
}
