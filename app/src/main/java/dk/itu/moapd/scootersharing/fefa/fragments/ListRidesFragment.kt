package dk.itu.moapd.scootersharing.fefa.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.models.RidesDB
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentListRidesBinding


class ListRidesFragment : Fragment() {
    private lateinit var binding: FragmentListRidesBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this.requireContext())
        val scooters = ridesDB.getRidesList()
        adapter = CustomAdapter(scooters, parentFragmentManager)
        auth = FirebaseAuth.getInstance()
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

            scooterList.adapter = adapter
            scooterList.layoutManager = LinearLayoutManager(this.root.context)
        }
    }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
    }
}