package dk.itu.moapd.scootersharing.fefa.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.fefa.R
import java.io.File

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    val storage = Firebase.storage("gs://scooter-sharing-a1130.appspot.com")
    val imageRef = storage.getReferenceFromUrl("gs://scooter-sharing-a1130.appspot.com")
    private lateinit var scooter: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        scooter = arguments?.getString("scooterID")!!
        binding.apply {
            cameraCaptureButton.setOnClickListener{
                takePhoto()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(){
        // Create output file to save captured image
        val outputDirectory = requireContext().cacheDir
        val outputFile = File(outputDirectory, "image.jpg")

        // Set up image capture use case to capture the photo
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        // Set up camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the image capture use case with the camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // Take the photo
            imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    uploadImageToBucket(output.savedUri!!, imageRef.child(scooter))
                }
            })
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun uploadImageToBucket(uri: Uri, image: StorageReference) {
        image.putFile(uri).addOnSuccessListener { imageUrl ->
            imageUrl.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                image.putFile(uri)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, MapFragment()).commit()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
