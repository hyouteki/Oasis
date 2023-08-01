package com.hyouteki.oasis.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hyouteki.oasis.R
import com.hyouteki.oasis.bottomsheets.ChatUserBottomSheet
import com.hyouteki.oasis.bottomsheets.HistoryBottomSheet
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.CustomFragment
import com.hyouteki.oasis.comms.BottomSheetComms
import com.hyouteki.oasis.comms.FragMainComms
import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.databinding.ActivityMainBinding
import com.hyouteki.oasis.fragments.*
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.utils.Helper
import com.hyouteki.oasis.utils.Saver
import com.hyouteki.oasis.utils.Tags
import com.hyouteki.oasis.viewmodels.OasisViewModel

class MainActivity : AppCompatActivity(), FragMainComms, Communicator, BottomSheetComms {

    private lateinit var binding: ActivityMainBinding
    private val user = FirebaseAuth.getInstance().currentUser!!
    private var fragmentInstance: CustomFragment = MarketplaceFragment()
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var sharedPreferences: SharedPreferences
    private var currentPost: MarketplacePost? = null
    private var currentChatUserId: String? = null

    companion object {
        private const val TAG = "custom"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)

        sharedPreferences = Saver.getPreferences(this)

        binding.navbar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> {
                    appBarVisibility(true)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame, MarketplaceFragment())
                        commit()
                    }
                    true
                }
                R.id.happening -> {
                    appBarVisibility(true)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame, HappeningFragment())
                        commit()
                    }
                    true
                }
                R.id.add -> {
                    appBarVisibility(false)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame, AddFragment())
                        commit()
                    }
                    true
                }
                R.id.chat -> {
                    appBarVisibility(false)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame, ChooseUserFragment())
                        commit()
                    }
                    true
                }
                R.id.more -> {
                    appBarVisibility(false)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame, MoreFragment())
                        commit()
                    }
                    true
                }
                else -> false
            }
        }
        when (sharedPreferences.getString(Saver.DEFAULT_TAB, Tags.MARKETPLACE)) {
            Tags.MARKETPLACE -> binding.navbar.selectedItemId = R.id.marketplace
            Tags.HAPPENING -> binding.navbar.selectedItemId = R.id.happening
        }

//        addButton.setOnClickListener {
//            when (fragmentInstance) {
//                is MarketplaceFragment -> {
//                    launchAddMarketplacePostActivity()
//                }
//                is ChooseUserFragment -> {
//                    AddUserBottomSheet().show(supportFragmentManager, "AddUser#Dialog@Oasis")
//                }
//            }
//        }

//        editButton.setOnClickListener {
//            when (fragmentInstance) {
//                is ProfileFragment -> {
//                    val fragment = (fragmentInstance as ProfileFragment)
//                    when (canEdit) {
//                        true -> {
//                            editButton.setImageResource(R.drawable.ic_edit)
//                            if (fragment.save()) {
//                                canEdit = false
//                            }
//                        }
//                        false -> {
//                            editButton.setImageResource(R.drawable.ic_check)
//                            fragment.enable()
//                            canEdit = true
//                        }
//                    }
//                }
//            }
//        }

//        userImage.setOnClickListener {
//            fragmentInstance = ProfileFragment()
//            launchFragment()
//            drawerLayout.closeDrawer(Gravity.START)
//        }
//
//        refreshButton.setOnClickListener {
//            if (fragmentInstance is HistoryFragment) {
//                fragmentInstance = HistoryFragment()
//                launchFragment()
//            }
//        }
//
//        menuButton.setOnClickListener {
//            drawerLayout.openDrawer(Gravity.START)
//        }

//        OasisViewModel.getUserDocument(user.uid).addOnSuccessListener { documentSnapshot ->
//            documentSnapshot.toObject(User::class.java)?.let {
//                Glide.with(userImage.context).load(it.photoUrl).into(userImage)
//                userName.text = it.displayName
//            }
//        }

//        navigationView.setNavigationItemSelectedListener {
//            var check = true
//            when (it.itemId) {
//                R.id.menu_am_marketplace -> {
//                    fragmentInstance = MarketplaceFragment()
//                }
//                R.id.menu_am_lost_found -> {
//                    fragmentInstance = LostAndFoundFragment()
//                }
//                R.id.menu_am_clubs -> {
//                    fragmentInstance = ClubsFragment()
//                }
//                R.id.menu_am_add -> {
//                    fragmentInstance = AddFragment()
//                }
//                R.id.menu_am_history -> {
//                    fragmentInstance = HistoryFragment()
//                }
//                R.id.menu_am_chat -> {
//                    fragmentInstance = ChooseUserFragment()
//                }
//                R.id.menu_am_contact_support -> {
//                    fragmentInstance = ContactSupportFragment()
//                }
//                R.id.menu_am_about_us -> {
//                    fragmentInstance = AboutUsFragment()
//                }
//                R.id.menu_am_invite -> {
//                    check = true
//                    val shareIntent = Intent()
//                    shareIntent.action = Intent.ACTION_SEND
//                    shareIntent.type = "text/plain"
//                    shareIntent.putExtra(
//                        Intent.EXTRA_TEXT,
//                        "Hey download this cool app called Oasis. From this link https://github.com/Hyouteki/Oasis/raw/main/Oasis.apk"
//                    )
//                    startActivity(Intent.createChooser(shareIntent, "Invite a friend using..."))
//                }
//                R.id.menu_am_settings -> {
//                    fragmentInstance = SettingsFragment()
//                }
//                R.id.menu_am_sign_out -> {
//                    val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
//                    with(dialog) {
//                        setTitle("Confirm sign-out")
//                        setMessage("Are you sure you want to sign-out?")
//                        setPositiveButton("Yes") { _, _ ->
//                            val gso =
//                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                    .requestIdToken(getString(R.string.default_web_client_id))
//                                    .requestEmail().build()
//                            val gsc = GoogleSignIn.getClient(this@MainActivity, gso)
//                            val auth = Firebase.auth
//                            FirebaseAuth.getInstance().signOut()
//                            auth.signOut()
//                            gsc.signOut()
//                            Log.d("onReceive", "Logout in progress")
//                            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
//                            finish()
//                        }
//                        setNegativeButton("No") { _, _ ->
//
//                        }
//                        show()
//                    }
//                    check = false
//                }
//            }
//            if (check) {
//                launchFragment()
//            }
//            drawerLayout.closeDrawer(Gravity.START)
//            true
//        }
    }

//    private fun handleActionVisibility() {
//        when (action) {
//            CustomFragment.NO_ACTION -> {
//                addButton.visibility = View.GONE
//                searchButton.visibility = View.GONE
//                refreshButton.visibility = View.GONE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.ADD_ACTION -> {
//                addButton.visibility = View.VISIBLE
//                searchButton.visibility = View.GONE
//                refreshButton.visibility = View.GONE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.SEARCH_ACTION -> {
//                addButton.visibility = View.GONE
//                searchButton.visibility = View.VISIBLE
//                refreshButton.visibility = View.GONE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.ADD_SEARCH_ACTION -> {
//                addButton.visibility = View.VISIBLE
//                searchButton.visibility = View.VISIBLE
//                refreshButton.visibility = View.GONE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.REFRESH_ACTION -> {
//                addButton.visibility = View.GONE
//                searchButton.visibility = View.GONE
//                refreshButton.visibility = View.VISIBLE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.REFRESH_SEARCH_ACTION -> {
//                addButton.visibility = View.GONE
//                searchButton.visibility = View.VISIBLE
//                refreshButton.visibility = View.VISIBLE
//                editButton.visibility = View.GONE
//            }
//            CustomFragment.EDIT_ACTION -> {
//                addButton.visibility = View.GONE
//                searchButton.visibility = View.GONE
//                refreshButton.visibility = View.GONE
//                editButton.visibility = View.VISIBLE
//            }
//        }
//    }

//    private fun launchFragment() {
//        titleText.text = fragmentInstance.getTitle()
//        action = fragmentInstance.getAction()
//        handleActionVisibility()
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fl_am_frame, fragmentInstance)
//            commit()
//        }
//    }

    override fun switchToAddFragment() {
        binding.navbar.selectedItemId = R.id.add
    }

    override fun launchAddMarketplacePostActivity() {
        startActivity(Intent(this@MainActivity, AddMarketplacePostActivity::class.java))
    }

//    override fun onResume() {
//        if (fragmentInstance is ChooseUserFragment) {
//            fragmentInstance = ChooseUserFragment()
//            launchFragment()
//        }
//        super.onResume()
//    }

    private fun launchAddMarketplacePostActivity(post: MarketplacePost) {
        val intent = Intent(this@MainActivity, AddMarketplacePostActivity::class.java)
        val bundle = Bundle()
        bundle.putString("imageUrl", post.imageUrl1)
        bundle.putString("itemName", post.itemName)
        bundle.putString("itemDesc", post.itemDesc)
        bundle.putString("itemPrice", post.itemPrice)
        bundle.putBoolean("itemType", post.itemType)
        bundle.putString("lendingTime", post.lendingTime)
        bundle.putString("pid", post.pid)
        bundle.putString("tag1", post.tag1)
        bundle.putString("tag2", post.tag2)
        bundle.putString("tag3", post.tag3)
        intent.putExtras(bundle)
        startActivity(intent)
    }

//    override fun launchAddLostAndFoundPostActivity() {
//        fragmentInstance = LostAndFoundFragment()
//        launchFragment()
//    }
//
//    override fun launchAddClubEventPostActivity() {
//        fragmentInstance = ClubsFragment()
//        launchFragment()
//    }

    override fun copyUserID() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MyUid@Oasis", user.uid)
        clipboard.setPrimaryClip(clip)
        Helper.makeToast(this, "Copied to clipboard")
    }

    private fun appBarVisibility(visibility: Boolean) {
        when (visibility) {
            true -> binding.toolbar.visibility = View.VISIBLE
            false -> binding.toolbar.visibility = View.GONE
        }
    }

    override fun handleContact(post: MarketplacePost) {
        if (post.uid == user.uid) {
            Helper.makeToast(this, "Cannot contact self")
        } else {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", post.uid)
            intent.putExtra(
                "message",
                "Hey I am interested in your post \"${post.itemName}\". And I would like to talk about that..."
            )
            OasisViewModel.addUsersIfNotPresent(user.uid, post.uid)
            startActivity(intent)
        }
    }

    override fun handlePostAction(post: MarketplacePost) {
        this.currentPost = post
        HistoryBottomSheet().show(supportFragmentManager, "bottomSheet")
    }

    override fun updatePost() {
        currentPost?.let {
            launchAddMarketplacePostActivity(it)
        }
    }

    override fun deletePost() {
        if (currentPost != null) {
            val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
            with(dialog) {
                setTitle("Confirm delete")
                setMessage("Are you sure you want to delete?")
                setPositiveButton("Yes") { _, _ ->
                    deletePostUtil()
                }
                setNegativeButton("No") { _, _ ->

                }
                show()
            }
        }
    }

    private fun deletePostUtil() {
        PostDao().marketplacePostCollection.document(currentPost!!.pid).delete()
            .addOnSuccessListener {
                Helper.makeToast(this@MainActivity, "Post deleted")
//                fragmentInstance = HistoryFragment()
//                launchFragment()
            }.addOnFailureListener { e ->
                Log.w(
                    "MyPostActivity", "Error deleting post", e
                )
            }
        currentPost?.let {
            if (it.imageUrl1 != "null") {
                val photoReference = storageReference.child("MarketplacePost/${it.pid}.png")
                photoReference.delete()
                    .addOnSuccessListener(OnSuccessListener<Void?> { // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file")
                    }).addOnFailureListener(OnFailureListener { // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file")
                    })
            }
        }
    }

    override fun launchChatActivity(uid: String) {
        OasisViewModel.userCollection.document(uid).get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { dummy ->
                if (user.uid == dummy.uid) {
                    Helper.makeToast(this, "Cannot add self")
                } else {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("uid", uid)
                    intent.putExtra("message", "")
                    OasisViewModel.addUsersIfNotPresent(user.uid, uid)
                    startActivity(intent)
                }
            } ?: run {
                Helper.makeToast(this, "Wrong User ID")
            }
        }
    }

    override fun onUserClick(uid: String) {
        OasisViewModel.userCollection.document(uid).get().addOnSuccessListener {
            it.toObject(User::class.java)?.let { dummy ->
                if (user.uid == dummy.uid) {
                    Helper.makeToast(this, "Cannot add self")
                } else {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("uid", uid)
                    intent.putExtra("message", "")
                    startActivity(intent)
                }
            } ?: run {
                Helper.makeToast(this, "Wrong User ID")
            }
        }
    }

    override fun openChatUserOptions(chatUserId: String) {
        this.currentChatUserId = chatUserId
        ChatUserBottomSheet().show(supportFragmentManager, "ChatUser#Dialog@Oasis")
    }

    override fun copyUserId() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UserID@Oasis", currentChatUserId)
        clipboard.setPrimaryClip(clip)
        Helper.makeToast(this, "Copied to clipboard")
    }

    override fun removeUser() {
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
        with(dialog) {
            setTitle("Confirm remove")
            setMessage("Are you sure you want to remove?")
            setPositiveButton("Yes") { _, _ ->
                currentChatUserId?.let { chatUserId ->
                    OasisViewModel.getUserDocument(user.uid).addOnSuccessListener {
                        it.toObject(User::class.java)?.let { temp ->
                            temp.users.remove(chatUserId)
                            OasisViewModel.userCollection.document(user.uid).set(temp)
                        }
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.frame, ChooseUserFragment()).commit()
                        }
                    }
                }
            }
            setNegativeButton("No") { _, _ ->

            }
            show()
        }
    }

    override fun signOut() {
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)
        with(dialog) {
            setTitle("Confirm sign-out")
            setMessage("Are you sure you want to sign-out?")
            setPositiveButton("Yes") { _, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()
                val gsc = GoogleSignIn.getClient(this@MainActivity, gso)
                val auth = Firebase.auth
                FirebaseAuth.getInstance().signOut()
                auth.signOut()
                gsc.signOut()
                Log.d("onReceive", "Logout in progress")
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
            }
            setNegativeButton("No") { _, _ ->

            }
            show()
        }
    }
}