package dk.itu.moapd.scootersharing.fefa.fragments

import android.os.Bundle
import android.text.Editable
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
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentPaymentBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB

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

    open fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}