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
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.models.RidesDB
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentProfileSettingsBinding


@Suppress("DEPRECATION")
class ProfileSettingsFragment : Fragment() {
    // Set up binding variable
    private lateinit var binding: FragmentProfileSettingsBinding
    private lateinit var auth: FirebaseAuth

    /**
     * Initialize the binding and the content view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        auth = FirebaseAuth.getInstance()
        binding = FragmentProfileSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /**
     * Function to initialize the data
     */
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        // Get the RidesDB instance using the requireContext() method of the fragment
        ridesDB = RidesDB.get(this.requireContext())

        // Get the list of scooters from the RidesDB
        val scooters = ridesDB.getRidesList()

        // Create a new CustomAdapter and pass the list of scooters and the parentFragmentManager to it
        adapter = CustomAdapter(scooters, parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the onClickListeners for the buttons
        binding.apply {
            mapsButton.setOnClickListener {
                if(context?.let {
                        PermissionChecker.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    } == PackageManager.PERMISSION_GRANTED){
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MapFragment()).commit()
                }else{
                    Toast.makeText(context, "Please allow app to use location before you can access the map", Toast.LENGTH_SHORT).show()
                }
            }
            ridesButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, ListRidesFragment()).commit()
            }
            homeButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, MainFragment()).commit()
            }
            LogoutButton.setOnClickListener {
                val builder = AlertDialog.Builder(this.getRoot().context)
                builder.setTitle("Log Out")
                builder.setMessage("Are you sure you want to log out?")
                builder.setPositiveButton(android.R.string.yes) { _, _ ->
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(requireContext().getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                    googleSignInClient.signOut()
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Handle successful sign-out
                                Log.e("Logout", "Google Sign-Out succeeded", task.exception)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_main, LoginFragment()).commit()
                            } else {
                                // Handle sign-out failure
                                Log.e("Logout", "Google Sign-Out failed", task.exception)
                            }
                        }
                    auth.signOut()
                }
                builder.setNegativeButton(android.R.string.no) { _, _ -> }
                builder.show()
            }

            PaymentButton.setOnClickListener{
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, PaymentFragment()).commit()
            }
            profileName.setText(auth.currentUser?.displayName.toString())
            if(resources.configuration.orientation == 1){
                Picasso.with(context).load(auth.currentUser?.photoUrl).into(ProfileImage)
            }
            Rides.setOnClickListener{
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, ListOldRidesFragment()).commit()
            }
            }
        }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
    }
}