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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.RidesDB
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import java.text.SimpleDateFormat
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.fefa.models.Scooter


class StartRideFragment : Fragment(){

    // Set up binding variable
    private lateinit var binding: FragmentStartRideBinding

    // Set up variable for the Scooter
    private lateinit var scooter : Scooter

    /**
     * Initialize the binding and the content view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)
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
        val scooters = MainFragment.ridesDB.getRidesList()

        // Create a new CustomAdapter and pass the list of scooters and the parentFragmentManager to it
        adapter = CustomAdapter(scooters, parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the onClickListeners for the buttons
        binding.apply {
            StartRideButton.setOnClickListener {
                if (EditTextName.text.isNotEmpty() &&
                    EditLocationName.text.isNotEmpty()
                ) {
                    // Create a new Scooter object using the data from the text fields
                    scooter = Scooter(
                        trimEditText(EditTextName),
                        trimEditText(EditLocationName),
                        System.currentTimeMillis()
                    )

                    // Add the new Scooter to the RidesDB
                    ridesDB.addScooter(scooter)

                    // Show a message using a snackbar
                    showMessage()

                    // Clear the text fields and reset the hints
                    EditTextName.setText("")
                    EditLocationName.setText("")
                    EditTextName.hint = "Enter your name"
                    EditLocationName.hint = "Enter your location"

                    // Replace the current fragment with the MainFragment
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MainFragment()).commit()
                }
            }
            BackButton.setOnClickListener {
                // Replace the current fragment with the MainFragment
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MainFragment()).commit()
            }
        }
    }

    /**
     * Function to convert textFields' content to a string
     */
    private fun trimEditText(text: EditText): String {
        return text.text.toString().trim()
    }

    /**
     * Function to create and show snackBar to the UI
     */
    private fun showMessage() {
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(scooter.timeStamp)
        Snackbar.make(
            binding.root,
            "Ride started using Scooter: Name: " + scooter.name + ", Location: " + scooter.location + ", Time: " + currentDate + ".",
            5000
        ).show()
    }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
    }
}