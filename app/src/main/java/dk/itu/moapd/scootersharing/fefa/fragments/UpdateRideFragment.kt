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
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentUpdateRideBinding
import dk.itu.moapd.scootersharing.fefa.models.Scooter
import java.text.SimpleDateFormat

class UpdateRideFragment : Fragment() {

    // Set up binding variable
    private lateinit var binding: FragmentUpdateRideBinding
    // Set up variable for the Scooter
    private lateinit var scooter: Scooter

    // Initialize the binding and the content view
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get an instance of the RidesDB database
        ridesDB = RidesDB.get(this.requireContext())

        // Get the list of scooters from the MainFragment
        val scooters = MainFragment.ridesDB.getRidesList()

        // Set up the adapter for the RecyclerView
        adapter = CustomAdapter(scooters, parentFragmentManager)
    }

    // Inflate the layout for this fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment using the binding object
        binding = FragmentUpdateRideBinding.inflate(layoutInflater, container, false)

        // Set the name of the current scooter in the EditText
        binding.apply {
            EditTextName.setText(ridesDB.getCurrentScooter().name)
        }

        return binding.root
    }

    // Set up the click listeners for the buttons in the UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Set up the click listener for the UpdateRideButton
            UpdateRideButton.setOnClickListener{
                if (EditTextName.text.isNotEmpty() &&
                    EditLocationName.text.isNotEmpty()){

                    // Create a new Scooter object and update the current scooter in the database
                    scooter = Scooter(trimEditText(EditTextName), trimEditText(EditLocationName), System.currentTimeMillis())
                    ridesDB.updateCurrentScooter(scooter.location)

                    // Show a snackbar message to the user
                    showMessage()

                    // Clear the text in the EditLocationName EditText and set the hint
                    EditLocationName.setText("")
                    EditLocationName.hint = "Enter your location"
                }
            }

            // Set up the click listener for the BackButton
            BackButton.setOnClickListener{
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
     * Function to create and show a snackbar message to the UI
     */
    private fun showMessage() {
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(scooter.timeStamp)

        // Create and show a snackbar with the current scooter's name, location, and timestamp
        Snackbar.make(
            binding.root,
            "Ride started using Scooter: Name: " + scooter.name + ", Location: " + scooter.location + ", Time: " + currentDate + ".",
            5000
        ).show()
    }

    companion object {
        // The RidesDB database instance
        lateinit var ridesDB: RidesDB

        // The adapter for the RecyclerView
        private lateinit var adapter: CustomAdapter
    }
}
