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
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.adapters.CustomRidesAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentListRidesBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

class ListOldRidesFragment : Fragment() {
    private lateinit var binding: FragmentListRidesBinding
    private lateinit var auth: FirebaseAuth
    val listOfHashmaps = mutableListOf<HashMap<String, Any?>>()
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        createRidesList()
        adapter = CustomRidesAdapter(listOfHashmaps, parentFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListRidesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mapsButton.setOnClickListener {
                if(context?.let {
                        PermissionChecker.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    } == PackageManager.PERMISSION_GRANTED){
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MapFragment()).commit()
                }else{
                    Toast.makeText(context, "Please allow app to use location before you can access the map", Toast.LENGTH_SHORT).show()
                }            }
            homeButton.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MainFragment()).commit()
            }
            settingsButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, ProfileSettingsFragment()).commit()
            }
            Log.e("test", listOfHashmaps.size.toString())
            scooterList.adapter = adapter
            scooterList.layoutManager = LinearLayoutManager(this.root.context)
        }
    }

    private fun createRidesList(){
        database.child("Rides").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate over the children of the "scooters" node
                for (dataSnapShot in dataSnapshot.children) {
                    if(dataSnapShot.child("UserId").value==auth.currentUser?.uid) {
                        var ride = hashMapOf(
                            "rideID" to dataSnapShot.child("rideId").value,
                            "ScooterId" to dataSnapShot.child("ScooterId").value,
                            "length" to dataSnapShot.child("lengthInMinutes").value,
                            "price" to dataSnapShot.child("price").value,
                        )
                        if (!listOfHashmaps.contains(ride)) {
                            listOfHashmaps.add(ride)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomRidesAdapter
    }
}