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
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.LocationService
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB
import dk.itu.moapd.scootersharing.fefa.models.Scooter

open class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var auth: FirebaseAuth
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * Initializes the RidesDB database and the CustomAdapter for the scooter list
     */
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this.requireContext())
        val scooters = ridesDB.getRidesList()
        adapter = CustomAdapter(scooters, parentFragmentManager)
        auth = getFirebaseAuthInstance()
    }

    /**
     * Inflates the fragment layout and returns the root view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /**
     * Sets up the UI elements and their click listeners using the binding object
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database.child("Users").child(auth.currentUser?.uid.toString()).child("userInfo").setValue(hashMapOf(
            "id" to auth.currentUser?.uid,
            "email" to auth.currentUser?.email,
            "name" to auth.currentUser?.displayName,
        ))
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_main, LoginFragment()).commit()

        requestUserPermissions()
        if(!areScootersAdded) {
            createScooters()
            areScootersAdded = true
        }

        binding.apply {
            mapsButton.setOnClickListener {
                if(context?.let {
                        PermissionChecker.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    } == PackageManager.PERMISSION_GRANTED){
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MapFragment()).commit()
                }else{
                    Toast.makeText(context, "Please allow app to use location before you can access the map", Toast.LENGTH_SHORT).show()
                    requestUserPermissions()
                }
            }
            ridesButton.setOnClickListener {
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, ListRidesFragment()).commit()
            }
            settingsButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, ProfileSettingsFragment()).commit()
            }
        }
    }
    private fun requestUserPermissions() {
        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.size > 0)
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_RESULT
            )
    }
    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (context?.let { PermissionChecker.checkSelfPermission(it, permission) } !=
                PackageManager.PERMISSION_GRANTED)
                result.add(permission)
        return result
    }
    open fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @SuppressLint("MissingPermission")
    private fun createScooters(){
        database.child("Scooters").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate over the children of the "scooters" node
                for (scooterSnapshot in dataSnapshot.children) {
                       var scooter = Scooter(scooterSnapshot.child("ScooterDetails").child("id").value.toString(), scooterSnapshot.child("ScooterDetails").child("lat").value as Double, scooterSnapshot.child("ScooterDetails").child("lon").value as Double, false, 100 )
                        if(!ridesDB.containsScooter(scooterSnapshot.child("ScooterDetails").child("id").value.toString())) {
                            ridesDB.addScooter(scooter)
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
        private var areScootersAdded = false
        private const val ALL_PERMISSIONS_RESULT = 1011
    }
}
