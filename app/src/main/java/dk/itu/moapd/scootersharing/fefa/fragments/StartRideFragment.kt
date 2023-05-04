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

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.LocationService
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB
import dk.itu.moapd.scootersharing.fefa.models.Scooter
import java.util.*


@Suppress("DEPRECATION")
class StartRideFragment : Fragment() {

    // Set up binding variable
    private lateinit var binding: FragmentStartRideBinding
    private lateinit var clockTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var scooter: String
    private lateinit var currentScooter: Scooter
    private lateinit var auth: FirebaseAuth
    private lateinit var RideId : String

    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private var seconds = 0
    private var minutes = 0
    private var hours = 0
    private var timeInMinutes = (hours*60)+(minutes)+(seconds/60)
    private var amountToPay = (timeInMinutes*1.5)+10

    /**
     * Initialize the binding and the content view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)
        ridesDB = RidesDB.get(this.requireContext())
        scooter = arguments?.getString("scooterID")!!
        currentScooter = ridesDB.getScooter(scooter)
        auth = getFirebaseAuthInstance()
        RideId = UUID.randomUUID().toString()


        handler.post(object : Runnable {
            override fun run() {
                updateTime()
                handler.postDelayed(this, 1000)
            }
        })

        binding.apply {
            scooterName.text = currentScooter.id
            scooterBattery.text = currentScooter.battery.toString()
            scooterAddress.text = currentScooter.getAddressFromLatLng(requireContext())
            stopButton.setOnClickListener{
                val builder = AlertDialog.Builder(this.getRoot().context)
                builder.setTitle("Pay")
                builder.setMessage("Please confirm that you will end the ride, the price is: "+amountToPay+"DKK")
                builder.setPositiveButton(android.R.string.yes) { _, _ ->
                    createRide()
                    currentScooter.inUse = false
                    currentScooter.lat = LocationService.getLat().toDouble()
                    currentScooter.lon = LocationService.getLon().toDouble()
                    ridesDB.updateScooterInDatabase(currentScooter.id)
                    handler.removeCallbacksAndMessages(null)
                    val fragment = CameraFragment()
                    val bundle = Bundle()
                    bundle.putString("scooterID",  scooter)
                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, fragment).commit()
                }
                builder.setNegativeButton(android.R.string.no) { _, _ -> }
                builder.show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clockTextView = binding.clockTextview
    }

    private fun updateTime() {
        seconds++
        if (seconds == 60) {
            seconds = 0
            minutes++
            if(minutes==30){
                currentScooter.battery = currentScooter.battery - 1
            }
            if (minutes == 60) {
                currentScooter.battery = currentScooter.battery - 1
                minutes = 0
                hours++
            }
        }
        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        clockTextView.text = timeString
    }

    private fun createRide(){
        val ride = hashMapOf(
            "RideId" to RideId,
            "UserId" to auth.currentUser?.uid,
            "ScooterId" to currentScooter.id,
            "lengthInMinutes" to timeInMinutes,
            "price" to amountToPay
        )
        database.child("Rides").child(RideId).setValue(ride)
    }

    private fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    companion object {
        lateinit var ridesDB: RidesDB
    }
}

