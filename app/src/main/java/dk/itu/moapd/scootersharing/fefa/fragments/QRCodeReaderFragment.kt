import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.itu.moapd.scootersharing.fefa.R
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import dk.itu.moapd.scootersharing.fefa.fragments.*
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

class QRCodeReaderFragment : Fragment() {
    private lateinit var scooterID: String
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        lateinit var ridesDB: RidesDB
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        scooterID = arguments?.getString("scooterID")!!
        ridesDB = RidesDB.get(this.requireContext())
        return inflater.inflate(R.layout.fragment_qr_code_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION)
            } else {
                startScanner()
            }
        }

    private fun startScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanner()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

