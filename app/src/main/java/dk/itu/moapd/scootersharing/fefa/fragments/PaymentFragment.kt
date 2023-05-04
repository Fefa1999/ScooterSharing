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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = getFirebaseAuthInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").addValueEventListener(object : ValueEventListener  {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("isEmpty").value == false){
                        etCvv.setText(snapshot.child("cvv").getValue(String::class.java))
                        etNameOnCard.setText(snapshot.child("name").getValue(String::class.java))
                        etExpiryDate.setText(snapshot.child("expiry").getValue(String::class.java))
                        etCardNumber.setText(snapshot.child("cardNumber").getValue(String::class.java))
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            btnPayment.setOnClickListener{
                if(etCvv.text.isNullOrEmpty()||etCardNumber.text.isNullOrEmpty()||etExpiryDate.text.isNullOrEmpty()||etNameOnCard.text.isNullOrEmpty()){
                    Toast.makeText(context, "Please fill out every field", Toast.LENGTH_SHORT).show()
                }else if(etCvv.text.toString().contains(Regex("[a-zA-Z]+"))||etCvv.text.toString().length!=3){
                    Toast.makeText(context, "Make sure cvv only contains 3 numbers", Toast.LENGTH_SHORT).show()
                }else if(!etExpiryDate.text.toString().contains(Regex("[0-9/]+"))||etExpiryDate.text.toString().length!=5){
                    Toast.makeText(context, "Make sure ExpiryDate is in the format 12/12", Toast.LENGTH_SHORT).show()
                }else if(etCardNumber.text.toString().contains(Regex("[a-zA-Z]+"))||etCardNumber.text.toString().length!=16){
                    Toast.makeText(context, "Make sure CardNumber is 16 numbers", Toast.LENGTH_SHORT).show()
                }else{
                    val card = hashMapOf(
                        "cardNumber" to etCardNumber.text.toString(),
                        "cvv" to etCvv.text.toString(),
                        "expiry" to etExpiryDate.text.toString(),
                        "name" to etNameOnCard.text.toString(),
                        "isEmpty" to false,
                    )
                    database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").setValue(card)
                }
            }
            btnBack.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, ProfileSettingsFragment()).commit()
            }
            btnPaymentRemove.setOnClickListener{
                database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").removeValue()
                database.child("Users").child(auth.currentUser?.uid.toString()).child("Payment").child("isEmpty").setValue(true)
            }
        }
    }

    private fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}