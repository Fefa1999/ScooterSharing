package dk.itu.moapd.scootersharing.fefa.fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.fefa.R


class LoginFragment : Fragment() {
    private val signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result -> onSignInResult(result) }
    var database = Firebase.database("https://scooter-sharing-a1130-default-rtdb.europe-west1.firebasedatabase.app/").reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult
    ) {
        if (result.resultCode == RESULT_OK)
            startMainFragment()
    }

    fun startMainFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container_main, MainFragment()).commit()
    }
}