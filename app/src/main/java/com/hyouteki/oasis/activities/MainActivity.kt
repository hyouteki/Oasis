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
import com.hyouteki.oasis.bottomsheets.ModalBottomSheet
import com.hyouteki.oasis.communicators.MainCommunicator
import com.hyouteki.oasis.databinding.ActivityMainBinding
import com.hyouteki.oasis.fragments.AddFragment
import com.hyouteki.oasis.fragments.ChooseUserFragment
import com.hyouteki.oasis.fragments.HappeningFragment
import com.hyouteki.oasis.fragments.MarketplaceFragment
import com.hyouteki.oasis.fragments.ModalFragment
import com.hyouteki.oasis.fragments.MoreFragment
import com.hyouteki.oasis.models.MarketplacePost


class MainActivity : AppCompatActivity(), MainCommunicator {
    private lateinit var binding: ActivityMainBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val fragments = arrayListOf<ModalFragment>(
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
                    class MarketplaceBottomSheet : ModalBottomSheet(
                        arrayListOf(
                            "Add post", "Sort", "Search"
                        ), arrayListOf(
                            R.drawable.add_box_outlined, R.drawable.sort, R.drawable.search
                        )
                    ) {
                        override fun handleAction(position: Int) {
                            when (position) {
                                0 -> handleMarketplacePostAddAction()
                                1 -> handleMarketplacePostSortAction()
                                2 -> handleMarketplacePostSearchAction()
                            }
                        }

                        private fun handleMarketplacePostSortAction() {
                            (fragments[currentFragmentID]).handleAction(MarketplaceFragment.SORT_ACTION_ID)
                        }

                        private fun handleMarketplacePostSearchAction() {
                            TODO("Not yet implemented")
                        }
                    }
                    MarketplaceBottomSheet().show(
                        supportFragmentManager, "MARKETPLACE_BOTTOM_SHEET"
                    )
                }

                R.id.happening -> {
                    class HappeningBottomSheet : ModalBottomSheet(
                        arrayListOf(
                            "Add confession", "Add event", "Sort", "Search"
                        ), arrayListOf(
                            R.drawable.note, R.drawable.event, R.drawable.sort, R.drawable.search
                        )
                    ) {
                        override fun handleAction(position: Int) {
                            when (position) {
                                0 -> handleConfessionAddAction()
                                1 -> handleEventAddAction()
                                2 -> handleHappeningSortAction()
                                3 -> handleHappeningSearchAction()
                            }
                        }

                        private fun handleHappeningSearchAction() {
                            TODO("Not yet implemented")
                        }

                        private fun handleHappeningSortAction() {
                            TODO("Not yet implemented")
                        }

                        private fun handleEventAddAction() {
                            TODO("Not yet implemented")
                        }

                        private fun handleConfessionAddAction() {
                            TODO("Not yet implemented")
                        }
                    }
                    HappeningBottomSheet().show(supportFragmentManager, "MARKETPLACE_BOTTOM_SHEET")
                }

                R.id.chat -> {
                    class ChooseUserBottomSheet : ModalBottomSheet(
                        arrayListOf(
                            "Add user", "Search", "Blocked users"
                        ), arrayListOf(
                            R.drawable.person_add, R.drawable.search, R.drawable.block
                        )
                    ) {
                        override fun handleAction(position: Int) {
                            when (position) {
                                0 -> handleUserAddAction()
                                1 -> handleUserSearchAction()
                                2 -> handleUserBlockedAction()
                            }
                        }

                        private fun handleUserAddAction() {
                            TODO("Not yet implemented")
                        }

                        private fun handleUserSearchAction() {
                            TODO("Not yet implemented")
                        }

                        private fun handleUserBlockedAction() {
                            TODO("Not yet implemented")
                        }
                    }
                    ChooseUserBottomSheet().show(supportFragmentManager, "CHOOSE_USER_BOTTOM_SHEET")
                }

                R.id.more -> {
                    class MoreBottomSheet : ModalBottomSheet(
                        arrayListOf(
                            "Add post",
                            "Copy user ID",
                            "Edit",
                            "Saved",
                            "Invite",
                            "Settings",
                            "Sign out",
                            "2023 Hyouteki"
                        ), arrayListOf(
                            R.drawable.add_box_outlined,
                            R.drawable.copy,
                            R.drawable.edit,
                            R.drawable.saved,
                            R.drawable.invite,
                            R.drawable.settings,
                            R.drawable.log_out,
                            R.drawable.copyright
                        )
                    ) {
                        override fun handleAction(position: Int) {
                            when (position) {
                                0 -> handlePostAddAction()
                                1 -> handleUserIDCopyAction()
                                2 -> handleProfileEditAction()
                                3 -> handleSavedAction()
                                4 -> handleInviteAction()
                                5 -> handleSettingsAction()
                                6 -> handleSignOutAction()
                                7 -> handleCopyrightAction()
                            }
                        }
                    }
                    MoreBottomSheet().show(supportFragmentManager, "MORE_BOTTOM_SHEET")
                }
            }
        }
    }

    private fun loadFragment() {
        binding.navbar.selectedItemId = currentFragmentID
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragments[currentFragmentID])
            commit()
        }
    }

    fun handlePostAddAction() {
        currentFragmentID = 2
        binding.navbar.findViewById<View>(R.id.add).performClick()
        loadFragment()
    }

    fun handleUserIDCopyAction() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UserID@Oasis", currentUser.uid)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "User ID copied", Toast.LENGTH_SHORT).show()
    }

    fun handleProfileEditAction() {
        TODO("Not yet implemented")
    }

    fun handleSavedAction() {
        TODO("Not yet implemented")
    }

    fun handleInviteAction() {
        TODO("Not yet implemented")
    }

    fun handleSettingsAction() {
        TODO("Not yet implemented")
    }

    fun handleSignOutAction() {
        with(MaterialAlertDialogBuilder(this)) {
            setTitle("Confirm sign-out")
            setMessage("Are you sure you want to sign-out?")
            setPositiveButton("Yes") { _, _ -> handleSignOut() }
            setNegativeButton("No") { _, _ -> }
            show()
        }
    }

    fun handleCopyrightAction() {
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

    override fun handleMarketplacePostAddAction() {
        val intent = Intent(this, AddMarketplacePostActivity::class.java)
        startActivity(intent)
    }

    override fun handleMarketplacePostContactAction(marketplacePost: MarketplacePost) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        loadFragment()
        super.onResume()
    }
}