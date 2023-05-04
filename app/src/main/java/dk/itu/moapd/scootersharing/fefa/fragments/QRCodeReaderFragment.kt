/*
*MIT License
*
*Copyright (c) [2023] [Felix Jeppe Fatum]
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*/
package dk.itu.moapd.scootersharing.fefa.fragments

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dk.itu.moapd.scootersharing.fefa.R
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dk.itu.moapd.scootersharing.fefa.fragments.*
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

@Suppress("DEPRECATION")
class QRCodeReaderFragment : Fragment() {
    private lateinit var scooterID: String

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        scooterID = arguments?.getString("scooterID")!!
        ridesDB = RidesDB.get(this.requireContext())
        return inflater.inflate(R.layout.fragment_qr_code_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                startScanner()
        }

    private fun startScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setBeepEnabled(false)
        integrator.setTimeout(20000)
        integrator.initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(context, "Not scanned in time, try again", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                val fragment = MapFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, fragment).commit()
            }, 3000)
        }
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scannedText = result.contents
            if(scannedText == scooterID) {
                val fragment = StartRideFragment()
                val bundle = Bundle()
                bundle.putString("scooterID", scooterID)
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, fragment).commit()
            }else{
                ridesDB.getScooter(scooterID).inUse = false
                Toast.makeText(context, "Wrong QR code scanned", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val fragment = MapFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_main, fragment).commit()
                }, 3000)
            }
        }
    }
}

