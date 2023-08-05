package com.hyouteki.oasis.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyouteki.oasis.R
import com.hyouteki.oasis.databinding.ActivitySignInBinding
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var gsc: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG: String = "SIGN_IN_ACTIVITY"
        private const val SIGN_IN_REQUEST_CODE: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        binding.signInWithGoogle.setOnClickListener { signIn() }
    }

    private fun signIn() {
        startActivityForResult(gsc.signInIntent, SIGN_IN_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i(TAG, "request-code=$requestCode; request-result=$resultCode")
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle=${account.id}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed-code=${e.statusCode}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.i(TAG, "firebaseAuthWithGoogle:idToken=$idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.signInWithGoogle.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        Log.i(TAG, "firebaseUser=${firebaseUser.toString()}")
        if (firebaseUser != null) {
            val user =
                User(firebaseUser.uid, firebaseUser.displayName!!, firebaseUser.photoUrl.toString())
            OasisViewModel.addUserIfNotPresent(user)
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
            binding.signInWithGoogle.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}