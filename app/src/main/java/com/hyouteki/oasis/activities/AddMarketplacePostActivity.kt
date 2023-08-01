package com.hyouteki.oasis.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.R
import com.hyouteki.oasis.bottomsheets.ImageIntentBottomSheet
import com.hyouteki.oasis.comms.BottomSheetComms
import com.hyouteki.oasis.databinding.ActivityAddMarketplacePostBinding
import com.hyouteki.oasis.utils.Helper
import com.hyouteki.oasis.utils.Logger
import de.hdodenhof.circleimageview.CircleImageView

class AddMarketplacePostActivity : AppCompatActivity(), BottomSheetComms {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityAddMarketplacePostBinding
    private var imageUri: Uri? = null
    private val tagList = ArrayList<Chip>()
    private var imageUrl: String? = null
    private var itemNameText: String? = null
    private var itemDescText: String? = null
    private var itemPriceText: String? = null
    private var itemTypeText: Boolean = SELL
    private var pid: String? = null
    private var tag1Text: String? = null
    private var tag2Text: String? = null
    private var tag3Text: String? = null

    companion object {
        const val IMAGE_REQUEST_CODE = 2
        const val PRODUCT = false
        const val EXPERIENCE = true
        const val SELL = false
        const val RENT = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMarketplacePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.background)

        initialize()

        val userImage = findViewById<CircleImageView>(R.id.profile)
        val tagGroup = findViewById<ChipGroup>(R.id.tag_group)
        tagList.add(findViewById(R.id.tag_cloth))
        tagList.add(findViewById(R.id.tag_food))
        tagList.add(findViewById(R.id.tag_electronic))
        tagList.add(findViewById(R.id.tag_furniture))
        tagList.add(findViewById(R.id.tag_stationary))
        tagList.add(findViewById(R.id.tag_utensil))

        initializeArguments()
        setUpArguments()

        Glide.with(userImage.context).load(currentUser.photoUrl).dontAnimate()
            .placeholder(R.drawable.ic_person).into(userImage)

        binding.itemImage.setOnClickListener {
            ImageIntentBottomSheet().show(supportFragmentManager, "ImageIntent#BottomSheet@Oasis")
        }

        binding.itemTypeSwitch.setOnCheckedChangeListener { _, _ ->
            handleTypeSwitchAction()
        }
    }

    override fun imageIntentTypeCamera() {
        super.imageIntentTypeCamera()
    }

    override fun imageIntentTypePhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun initialize() {
        handleTouches()
        lendTimeVisibility(false)
        binding.addImage.visibility = View.VISIBLE
    }

    private fun lendTimeVisibility(visibility: Boolean) {
        when (visibility) {
            true -> binding.lendTime.visibility = View.VISIBLE
            false -> binding.lendTime.visibility = View.GONE
        }
    }

    private fun itemPriceVisibility(visibility: Boolean) {
        Logger.debugger("item price visibility $visibility")
        when (visibility) {
            true -> {
                binding.ruppee.visibility = View.VISIBLE
                binding.itemPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.ruppee.visibility = View.GONE
                binding.itemPrice.visibility = View.GONE
            }
        }
    }

    private fun handleTouches() {
        handleDialogs()
        binding.itemType.setOnClickListener {
            binding.itemTypeSwitch.isChecked = !binding.itemTypeSwitch.isChecked
        }
        binding.next.setOnClickListener {
            Logger.info("next button clicked")
            handlePreviewAction()
        }
    }

    private fun handleDialogs() {
        binding.postType.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Select post type")
                val items = arrayOf("Sell", "Lend", "Lost", "Found")
                var checkedItem = when (binding.postTypeDesc.text.toString()) {
                    "Sell" -> 0
                    "Lend" -> 1
                    "Lost" -> 2
                    else -> 3
                }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { dialog, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    binding.postTypeDesc.text = items[checkedItem]
                    when (checkedItem) {
                        0 -> {
                            lendTimeVisibility(false)
                            itemPriceVisibility(true)
                        }
                        1 -> {
                            lendTimeVisibility(true)
                            itemPriceVisibility(true)
                        }
                        2, 3 -> {
                            lendTimeVisibility(false)
                            itemPriceVisibility(false)
                        }
                    }
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
        binding.lendTime.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Enter lend time in days")
                val view: View = layoutInflater.inflate(R.layout.lend_time_picker, null)
                setView(view)
                val lendTime: TextInputEditText = view.findViewById(R.id.lend_time)
                lendTime.setText(binding.lendTimeDesc.text)
                setPositiveButton("Save") { _, _ ->
                    binding.lendTimeDesc.text = lendTime.text
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
    }

    private fun handleTypeSwitchAction() {
        if (binding.itemTypeSwitch.isChecked == PRODUCT) {
            binding.colorStrip1.setBackgroundResource(R.color.midnightPurple)
            binding.colorStrip2.setBackgroundResource(R.color.midnightPurple)
        } else {
            binding.colorStrip1.setBackgroundResource(R.color.spaceRed)
            binding.colorStrip2.setBackgroundResource(R.color.spaceRed)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.itemImage.setImageURI(data?.data)
            this.imageUri = data?.data
            binding.addImage.visibility = View.GONE
        }
    }

    private fun handlePreviewAction() {
        val selectedTagList = ArrayList<String>()
        selectedTagList.add("null")
        selectedTagList.add("null")
        selectedTagList.add("null")
        selectedTagList.add("null")
        selectedTagList.add("null")
        selectedTagList.add("null")
        var i = 0
        for (tag in tagList) {
            if (tag.isChecked) {
                Logger.info(i.toString() + tag.text.toString())
                selectedTagList[i++] = tag.text.toString()
            }
        }
        if (binding.itemName.text.toString() == "") {
            Helper.makeToast(this, "Enter item name")
            return
        }
        if (binding.itemDesc.text.toString() == "") {
            Helper.makeToast(this, "Enter item description")
            return
        }
        if (i > 3) {
            Toast.makeText(
                this@AddMarketplacePostActivity, "Choose at max only 3 tags", Toast.LENGTH_SHORT
            ).show()
        } else {
            if (binding.itemPrice.visibility == View.VISIBLE && binding.itemPrice.text.toString() == "") {
                binding.itemPrice.setText("0")
                Helper.makeToast(this, "Item price has been set to 0")
            }
            val intent = Intent(
                this@AddMarketplacePostActivity, PreviewMarketplacePostActivity::class.java
            )
            val bundle = Bundle()
            val lendingTime = when (binding.postTypeDesc.text.toString()) {
                "Lend" -> binding.lendTimeDesc.text.toString()
                else -> ""
            }
            bundle.putString("imageUrl1", imageUri.toString())
            bundle.putString("imageUrl2", "null")
            bundle.putString("imageUrl3", "null")
            bundle.putString("imageUrl4", "null")
            bundle.putString("imageUrl5", "null")
            bundle.putString("pid", pid)
            bundle.putString("itemName", binding.itemName.text.toString())
            bundle.putString("itemDesc", binding.itemDesc.text.toString())
            bundle.putString("itemPrice", binding.itemPrice.text.toString())
            bundle.putBoolean("itemType", binding.itemTypeSwitch.isChecked)
            bundle.putString("lendingTime", lendingTime)
            bundle.putString("postType", binding.postTypeDesc.text.toString())
            bundle.putString("tag1", selectedTagList[0])
            bundle.putString("tag2", selectedTagList[1])
            bundle.putString("tag3", selectedTagList[2])
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun initializeArguments() {
        intent.getStringExtra("imageUrl")?.let {
            this.imageUrl = it
        }
        intent.getStringExtra("itemName")?.let {
            this.itemNameText = it
        }
        intent.getStringExtra("itemDesc")?.let {
            this.itemDescText = it
        }
        intent.getStringExtra("itemPrice")?.let {
            this.itemPriceText = it
        }
        intent.extras?.getBoolean("itemType").let {
            it?.let {
                this.itemTypeText = it
            }
        }
        intent.extras?.getString("postType").let {
            it?.let {
                binding.postTypeDesc.text = it
                when (it) {
                    "Sell" -> {
                        lendTimeVisibility(false)
                        itemPriceVisibility(true)
                    }
                    "Lend" -> {
                        lendTimeVisibility(true)
                        itemPriceVisibility(true)
                    }
                    "Lost", "Found" -> {
                        lendTimeVisibility(false)
                        itemPriceVisibility(false)
                    }
                }
            }
        }
//        intent.getStringExtra("lendingTime")?.let {
//            if (it != "") {
//                lendingButton.isChecked = true
//                Helper.makeToast(this, it)
//                itemTime.setText(it)
//            } else {
//                lendingButton.isChecked = false
//            }
//        } ?: run {
//            this.lendingButton.isChecked = false
//        }
//        handleLendingButtonAction()
        intent.getStringExtra("pid")?.let {
            this.pid = it
        }
        intent.getStringExtra("tag1")?.let {
            this.tag1Text = it
        }
        intent.getStringExtra("tag2")?.let {
            this.tag2Text = it
        }
        intent.getStringExtra("tag3")?.let {
            this.tag3Text = it
        }
    }

    private fun setUpArguments() {
        this.imageUrl?.let {
            if (it != "null") {
                this.imageUri = Uri.parse(it)
                Glide.with(binding.itemImage.context)
                    .load(it)
                    .into(binding.itemImage)
            }
        }
        binding.itemName.setText(itemNameText)
        binding.itemDesc.setText(itemDescText)
        binding.itemPrice.setText(itemPriceText)
        binding.itemTypeSwitch.isChecked = itemTypeText
        handleTypeSwitchAction()
        for (tag in tagList) {
            this.tag1Text?.let {
                if (tag.text == tag1Text) {
                    tag.isChecked = true
                }
            }
            this.tag2Text?.let {
                if (tag.text == tag2Text) {
                    tag.isChecked = true
                }
            }
            this.tag3Text?.let {
                if (tag.text == tag3Text) {
                    tag.isChecked = true
                }
            }
        }
    }
}