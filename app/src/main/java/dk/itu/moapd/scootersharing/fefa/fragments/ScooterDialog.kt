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

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentScooterDialogBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

class ScooterDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentScooterDialogBinding
    private lateinit var scooter: String
    private lateinit var auth: FirebaseAuth
    private var paymentListener: ValueEventListener? = null
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
        scooter = arguments?.getString("scooterID")!!
        ridesDB = RidesDB.get(this.requireContext())
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScooterDialogBinding.inflate(layoutInflater, container, false)
        auth = getFirebaseAuthInstance()
        val storage = Firebase.storage("gs://scooter-sharing-a1130.appspot.com")
        val imageRef = storage.getReferenceFromUrl("gs://scooter-sharing-a1130.appspot.com/"+scooter+".jpeg")
        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.root.context)
                .load(it)
                .centerCrop()
                .into(binding.imageView3)
        }
        val currentScooter = ridesDB.getScooter(scooter)

        binding.fieldOne.text = currentScooter.id
        binding.fieldTwo.text = currentScooter.getAddressFromLatLng(this.requireContext())
        binding.fieldThree.text = "Battery: "+currentScooter.battery.toString()
        binding.StartRideButton.setOnClickListener{
            database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").child("isEmpty").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value as Boolean){
                        Toast.makeText(context, "Please add payment before you can ride", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }else{
                        currentScooter.inUse = true
                        val fragment = QRCodeReaderFragment()
                        val bundle = Bundle()
                        bundle.putString("scooterID", scooter)
                        fragment.arguments = bundle
                        parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, fragment).commit()
                        dismiss()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            }).also {
                paymentListener = it
            }

        }
        binding.closeButton.setOnClickListener{
            dismiss()
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        paymentListener?.let { database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").child("isEmpty").removeEventListener(it) }
    }

    private fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        lateinit var ridesDB: RidesDB
    }
}
