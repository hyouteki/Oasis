package com.hyouteki.oasis.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyouteki.oasis.R
import com.hyouteki.oasis.bottomsheets.MarketplaceBottomSheet
import com.hyouteki.oasis.bottomsheets.MoreBottomSheet
import com.hyouteki.oasis.communicators.MarketplaceCommunicator
import com.hyouteki.oasis.communicators.MoreCommunicator
import com.hyouteki.oasis.databinding.ActivityMainBinding
import com.hyouteki.oasis.fragments.AddFragment
import com.hyouteki.oasis.fragments.ChooseUserFragment
import com.hyouteki.oasis.fragments.HappeningFragment
import com.hyouteki.oasis.fragments.MarketplaceFragment
import com.hyouteki.oasis.fragments.MoreFragment


class MainActivity : AppCompatActivity(), MarketplaceCommunicator, MoreCommunicator {
    private lateinit var binding: ActivityMainBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val fragments = arrayListOf(
        MarketplaceFragment(),
        HappeningFragment(),
        AddFragment(),
        ChooseUserFragment(),
        MoreFragment(),
    )
    private var currentFragmentID = 0

    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleBottomNavigationBar()
        initializeUIComponents()
    }

    private fun initializeUIComponents() {
        loadFragment()
    }

    private fun handleBottomNavigationBar() {
        binding.navbar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> currentFragmentID = 0
                R.id.happening -> currentFragmentID = 1
                R.id.add -> currentFragmentID = 2
                R.id.chat -> currentFragmentID = 3
                R.id.more -> currentFragmentID = 4
            }
            loadFragment()
            true
        }
        binding.navbar.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> {
                    MarketplaceBottomSheet(this).show(
                        supportFragmentManager, MarketplaceBottomSheet.TAG
                    )
                }

                R.id.happening -> {
                }

                R.id.add -> {
                }

                R.id.chat -> {

                }

                R.id.more -> {
                    MoreBottomSheet(this).show(supportFragmentManager, MarketplaceBottomSheet.TAG)
                }
            }
            true
        }
    }

    private fun loadFragment() {
        binding.navbar.selectedItemId = currentFragmentID
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragments[currentFragmentID])
            commit()
        }
    }

    override fun handleMarketplacePostAddAction() {
        TODO("Not yet implemented")
    }

    override fun handleMarketplacePostSortAction() {
        TODO("Not yet implemented")
    }

    override fun handleMarketplacePostSearchAction() {
        TODO("Not yet implemented")
    }

    override fun handlePostAddAction() {
        currentFragmentID = 2
        binding.navbar.findViewById<View>(R.id.add).performClick()
        loadFragment()
    }

    override fun handleUserIDCopyAction() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UserID@Oasis", currentUser.uid)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "User ID copied", Toast.LENGTH_SHORT).show()
    }

    override fun handleProfileEditAction() {
        TODO("Not yet implemented")
    }

    override fun handleSavedAction() {
        TODO("Not yet implemented")
    }

    override fun handleInviteAction() {
        TODO("Not yet implemented")
    }

    override fun handleSettingsAction() {
        TODO("Not yet implemented")
    }

    override fun handleSignOutAction() {
        with(MaterialAlertDialogBuilder(this)) {
            setTitle("Confirm sign-out")
            setMessage("Are you sure you want to sign-out?")
            setPositiveButton("Yes") { _, _ -> handleSignOut() }
            setNegativeButton("No") { _, _ -> }
            show()
        }
    }

    override fun handleCopyrightAction() {
        val url = "https://github.com/Hyouteki/Oasis/blob/main/LICENSE.md"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun handleSignOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val gsc = GoogleSignIn.getClient(this@MainActivity, gso)
        val auth = Firebase.auth
        FirebaseAuth.getInstance().signOut()
        auth.signOut()
        gsc.signOut()
        Log.d(TAG, "Signing out")
        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        finish()
    }
}