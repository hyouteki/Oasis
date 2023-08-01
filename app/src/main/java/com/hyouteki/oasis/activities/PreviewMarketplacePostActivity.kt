package com.hyouteki.oasis.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.hyouteki.oasis.R
import com.hyouteki.oasis.utils.Helper
import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.databinding.ActivityPreviewMarketplacePostBinding
import com.hyouteki.oasis.dialogs.ProgressDialog
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewMarketplacePostActivity : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivityPreviewMarketplacePostBinding
    private val storageReference = FirebaseStorage.getInstance().reference
    private var postAdded: Boolean = false
    private val progressDialog: ProgressDialog = ProgressDialog()
    private lateinit var imageUrl: Uri
    private lateinit var pid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewMarketplacePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.background)

        imageUrl = Uri.parse(intent.extras?.getString("imageUrl1", "null"))
        pid = intent.extras?.getString("pid", "null").toString()
        val itemNameText = intent.extras?.getString("itemName", "Item name")
        val itemDescText = intent.extras?.getString("itemDesc", "Item description")
        val itemPriceText = intent.extras?.getString("itemPrice", "00000")
        val lendingTimeText = intent.extras?.getString("lendingTime", "0")
        val postType = intent.extras?.getString("postType", "Sell")
        val itemType = intent.extras?.getBoolean("itemType", AddMarketplacePostActivity.PRODUCT)
        val tag1Text = intent.extras?.getString("tag1", "null")
        val tag2Text = intent.extras?.getString("tag2", "null")
        val tag3Text = intent.extras?.getString("tag3", "null")

        if (imageUrl == null) {
            Logger.info("image url is if null")
        } else if (imageUrl.toString() == null) {
            Logger.info("image url is else if 1 null")
        } else if (imageUrl.toString() == "null") {
            Logger.info("image url is else if 2 null")
        } else {
            Logger.info("image url is else null")
        }

        Glide.with(binding.profile.context)
            .load(currentUser?.photoUrl)
            .into(binding.profile)

        if (intent.extras?.getString("imageUrl1", "null") == "null") {
            binding.imageCard.visibility = View.GONE
            binding.space.visibility = View.VISIBLE
            binding.separator.visibility = View.VISIBLE
            binding.itemType.visibility = View.GONE
            binding.itemType2.visibility = View.VISIBLE
        } else {
            binding.itemImage.setImageURI(imageUrl)
            binding.itemType.visibility = View.VISIBLE
            binding.itemType2.visibility = View.GONE
        }

        binding.itemName.text = itemNameText
        binding.itemDesc.text = itemDescText
        binding.itemPrice.text = itemPriceText

        if (itemType == AddMarketplacePostActivity.PRODUCT) {
            binding.colorStrip1.setBackgroundResource(R.color.midnightPurple)
            binding.colorStrip2.setBackgroundResource(R.color.midnightPurple)
        } else {
            binding.colorStrip1.setBackgroundResource(R.color.spaceRed)
            binding.colorStrip2.setBackgroundResource(R.color.spaceRed)
        }

        when (postType) {
            "Sell" -> {
                binding.itemTypeText.text = postType
                binding.itemType2Text.text = postType
                binding.itemType.setCardBackgroundColor(getColor(R.color.spaceRed))
                binding.itemType2.setCardBackgroundColor(getColor(R.color.spaceRed))
            }
            "Lend" -> {
                binding.itemTypeText.text = "Lending for $lendingTimeText days"
                binding.itemType2Text.text = "Lending for $lendingTimeText days"
                binding.itemType.setCardBackgroundColor(getColor(R.color.OasisMedium))
                binding.itemType2.setCardBackgroundColor(getColor(R.color.OasisMedium))
            }
            "Lost" -> {
                binding.ruppee.visibility = View.GONE
                binding.itemPrice.visibility = View.GONE
                binding.itemTypeText.text = postType
                binding.itemType2Text.text = postType
                binding.itemType.setCardBackgroundColor(getColor(R.color.purple))
                binding.itemType2.setCardBackgroundColor(getColor(R.color.purple))
            }
            "Found" -> {
                binding.ruppee.visibility = View.GONE
                binding.itemPrice.visibility = View.GONE
                binding.itemTypeText.text = postType
                binding.itemType2Text.text = postType
                binding.itemType.setCardBackgroundColor(getColor(R.color.lightGreen))
                binding.itemType2.setCardBackgroundColor(getColor(R.color.lightGreen))
            }
        }

        if (tag1Text == "null" && tag2Text == "null" && tag3Text == "null") {
            binding.tagGroup.visibility = View.GONE
            binding.tagsImage.visibility = View.GONE
        }
        if (tag1Text != "null") {
            binding.tag1.text = tag1Text
            binding.tag1Image.text = tag1Text
        } else {
            binding.tag1.visibility = View.GONE
            binding.tag1Image.visibility = View.GONE
        }
        if (tag2Text != "null") {
            binding.tag2.text = tag2Text
            binding.tag2Image.text = tag2Text
        } else {
            binding.tag2.visibility = View.GONE
            binding.tag2Image.visibility = View.GONE
        }
        if (tag3Text != "null") {
            binding.tag3.text = tag3Text
            binding.tag3Image.text = tag3Text
        } else {
            binding.tag3.visibility = View.GONE
            binding.tag3Image.visibility = View.GONE
        }

        binding.itemDesc.text = itemDescText.toString()

        handleTouches()

    }

    private fun handleTouches() {
        binding.detailsCard.setOnClickListener {
            if (binding.imageCard.visibility == View.VISIBLE) {
                if (binding.details.visibility == View.VISIBLE) {
                    binding.details.visibility = View.GONE
                    binding.tagsImage.visibility = View.VISIBLE
                } else {
                    binding.details.visibility = View.VISIBLE
                    binding.tagsImage.visibility = View.GONE
                }
            }
        }
        binding.previous.setOnClickListener { finish() }
        handleSubmitClick()
    }

    private fun handleSubmitClick() {
        binding.submit.setOnClickListener {
            if (!postAdded) {
                var currentTime = System.currentTimeMillis()
                if (imageUrl.toString() == "null") {
                    val post = MarketplacePost(
                        uid = currentUser?.uid.toString(),
                        itemName = binding.itemName.text.toString(),
                        itemDesc = binding.itemDesc.text.toString(),
                        itemPrice = binding.itemPrice.text.toString(),
                        postType = intent.extras?.getString("postType", "Sell")!!,
                        lendingTime = intent.extras?.getString("lendingTime", "0").toString(),
                        pid = currentTime.toString(),
                        tag1 = intent.extras?.getString("tag1", "null").toString(),
                        tag2 = intent.extras?.getString("tag2", "null").toString(),
                        tag3 = intent.extras?.getString("tag3", "null").toString(),
                        itemType = intent.extras?.getBoolean(
                            "itemType",
                            MarketplacePost.PRODUCT
                        )!!
                    )
                    when (pid) {
                        "null" -> {
                            PostDao().addMarketplacePost(post)
                            Helper.makeToast(
                                this@PreviewMarketplacePostActivity,
                                "Post uploaded successfully!!"
                            )
                        }
                        else -> {
                            post.pid = pid
                            PostDao().updateMarketplacePost(post)
                            Helper.makeToast(
                                this@PreviewMarketplacePostActivity,
                                "Post updated successfully!!"
                            )
                        }
                    }
                    postAdded = true
                    finish()
                    return@setOnClickListener
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        progressDialog.show(supportFragmentManager, "Progress#Dialog@Oasis")
                        withContext(Dispatchers.Main) {
                            handleSubmit()
                        }
                    }
                }
            }
        }
    }


    private suspend fun handleSubmit() {
        val currentTime = System.currentTimeMillis()
        val uploadTask =
            storageReference.child("MarketplacePost/$currentTime.png").putFile(imageUrl)
        uploadTask.addOnSuccessListener {
            val downloadTask =
                storageReference.child("MarketplacePost/$currentTime.png").downloadUrl
            downloadTask.addOnSuccessListener {
                val post = MarketplacePost(
                    uid = currentUser?.uid.toString(),
                    imageUrl1 = "$it",
                    itemName = binding.itemName.text.toString(),
                    itemDesc = binding.itemDesc.text.toString(),
                    itemPrice = binding.itemPrice.text.toString(),
                    itemType = intent.extras?.getBoolean(
                        "itemType",
                        AddMarketplacePostActivity.PRODUCT
                    )!!,
                    postType = intent.extras?.getString("postType", "Sell").toString(),
                    pid = currentTime.toString(),
                    tag1 = intent.extras?.getString("tag1", "null").toString(),
                    tag2 = intent.extras?.getString("tag2", "null").toString(),
                    tag3 = intent.extras?.getString("tag3", "null").toString(),
                    lendingTime = intent.extras?.getString("lendingTime", "0").toString()
                )
                if (pid == "null") {
                    PostDao().addMarketplacePost(post)
                    Toast.makeText(
                        this@PreviewMarketplacePostActivity,
                        "Post uploaded successfully!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    post.pid = pid!!
                    PostDao().updateMarketplacePost(post)
                    Helper.makeToast(
                        this@PreviewMarketplacePostActivity,
                        "Post updated successfully!!"
                    )
                    postAdded = true
                    finish()
                    progressDialog.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@PreviewMarketplacePostActivity,
                    "Image downloading failed!! Try again",
                    Toast.LENGTH_SHORT
                ).show()
                postAdded = false
                progressDialog.dismiss()
            }

        }.addOnFailureListener {
            Helper.makeToast(
                this@PreviewMarketplacePostActivity,
                "Image uploading failed!! Try again"
            )
            Logger.warning(
                "Image uploading failed!! Try again", it
            )
            postAdded = false
            progressDialog.dismiss()
        }
    }
}