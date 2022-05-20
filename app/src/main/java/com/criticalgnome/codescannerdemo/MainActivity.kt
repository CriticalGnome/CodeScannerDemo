package com.criticalgnome.codescannerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.criticalgnome.codescannerdemo.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.common.Barcode.*
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scanner: GmsBarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(FORMAT_QR_CODE, FORMAT_AZTEC).build()
        val isOptionsRequired = false
        scanner = if (isOptionsRequired) {
            GmsBarcodeScanning.getClient(this, options) // With a configured options
        } else {
            GmsBarcodeScanning.getClient(this) // Without options
        }

        binding.startButton.setOnClickListener {
            scanner.startScan()
                .addOnSuccessListener { barcode: Barcode ->
                    // Task completed successfully
                    Log.d(TAG, "FORMAT: ${barcode.format}")
                    Log.d(TAG, "TYPE: ${barcode.valueType}")
                    when(barcode.valueType) {
                        TYPE_CONTACT_INFO -> {
                            barcode.contactInfo?.let {
                                Log.d(TAG, "NAME: ${it.name?.formattedName}")
                                it.addresses.forEachIndexed { index1, address -> address.addressLines.forEachIndexed { index2, line -> Log.d(TAG, "ADDRESS_$index1 LINE_$index2: $line") } }
                                it.emails.forEachIndexed { index, email -> Log.d(TAG, "EMAIL_$index: ${email.address}") }
                                it.phones.forEachIndexed { index, phone -> Log.d(TAG, "PHONE_$index: ${phone.number}") }
                                Log.d(TAG, "ORGANIZATION: ${it.organization}")
                                Log.d(TAG, "TITLE: ${it.title}")
                                it.urls.forEachIndexed { index, url -> Log.d(TAG, "URL_$index: $url") }
                            }
                        }
                        TYPE_EMAIL -> {
                            Log.d(TAG, "EMAIL ADDRESS: ${barcode.email?.address}")
                            Log.d(TAG, "EMAIL SUBJECT: ${barcode.email?.subject}")
                            Log.d(TAG, "EMAIL BODY: ${barcode.email?.body}")
                        }
                        TYPE_URL -> {
                            Log.d(TAG, "URL: ${barcode.url?.url}")
                        }
                        TYPE_WIFI -> {
                            Log.d(TAG, "WIFI SSID: ${barcode.wifi?.ssid}")
                            Log.d(TAG, "WIFI PASSWORD: ${barcode.wifi?.password}")
                            Log.d(TAG, "WIFI ENCRYPTION TYPE: ${barcode.wifi?.encryptionType}")
                        }
                        else -> Log.d(TAG, "RAW: ${barcode.rawValue}")
                    }
                }
                .addOnFailureListener { e: Throwable ->
                    // Task failed with an exception
                    Log.d(TAG, "ERROR: ${e.message}")
                    AlertDialog.Builder(this).setTitle("Error").setMessage(e.message).setPositiveButton(android.R.string.ok, null).show()
                }
        }
    }
    
    private companion object {
        private const val TAG = "Google_code_scanner"
    }
}
