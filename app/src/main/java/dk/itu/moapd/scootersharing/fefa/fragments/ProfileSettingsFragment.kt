package dk.itu.moapd.scootersharing.fefa.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dk.itu.moapd.scootersharing.fefa.R
import dk.itu.moapd.scootersharing.fefa.RidesDB
import dk.itu.moapd.scootersharing.fefa.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentProfileSettingsBinding


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
        val scooters = MainFragment.ridesDB.getRidesList()

        // Create a new CustomAdapter and pass the list of scooters and the parentFragmentManager to it
        adapter = CustomAdapter(scooters, parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the onClickListeners for the buttons
        binding.apply {
            BackButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MainFragment()).commit()
            }
            LogoutButton.setOnClickListener{
                auth.signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(requireContext().getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut()
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, LoginFragment()).commit()
            }
            RideHistoryButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, ListRidesFragment()).commit()
            }
            profileName.setText(auth.currentUser?.displayName.toString())
            Picasso.with(context).load(auth.currentUser?.photoUrl).into(ProfileImage);
        }
    }

    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
    }
}