package com.hyouteki.oasis.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.R
import com.hyouteki.oasis.abstractions.MarketplaceTags
import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.databinding.ActivityAddMarketplacePostBinding
import com.hyouteki.oasis.databinding.LendTimePickerBinding
import com.hyouteki.oasis.databinding.MarketplaceTagsBinding
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel

class AddMarketplacePostActivity : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityAddMarketplacePostBinding
    private var postID: String = ""
    private val categoryTags = arrayListOf<String>()
    private var sellTag: String = "Sell"
    private var conditionTag: String? = null
    private var lendTime: String = "1"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    companion object {
        const val TAG: String = "ADD_MARKETPLACE_POST_ACTIVITY"
        const val ITEM_ID: String = "ITEM_ID"
        const val ITEM_NAME: String = "ITEM_NAME"
        const val ITEM_DESC: String = "ITEM_DESC"
        const val ITEM_PRICE: String = "ITEM_PRICE"
        const val CATEGORY_TAGS: String = "CATEGORY_TAGS"
        const val SELL_TAG: String = "SELL_TAG"
        const val CONDITION_TAG: String = "CONDITION_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMarketplacePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSharedPreferences()
        initializeUIComponents()
        initializeArguments()
        if (postID.isEmpty()) initializeDraft()
        handleTouches()
    }

    private fun initializeDraft() {
        sharedPreferences.getString(ITEM_ID, "")?.let {
            this.postID = it
        }
        sharedPreferences.getString(ITEM_NAME, "")?.let {
            binding.itemName.setText(it)
        }
        sharedPreferences.getString(ITEM_DESC, "")?.let {
            binding.itemDesc.setText(it)
        }
        sharedPreferences.getString(ITEM_PRICE, "")?.let {
            binding.itemPrice.setText(it)
        }
        sharedPreferences.getStringSet(CATEGORY_TAGS, mutableSetOf())?.let {
            for (out in it) {
                if (out.isNotEmpty()) {
                    categoryTags.add(out)
                }
            }
        }
        sharedPreferences.getString(SELL_TAG, "Sell")?.let {
            this.sellTag = it
        }
        sharedPreferences.getString(CONDITION_TAG, "")?.let {
            if (it.isNotEmpty()) {
                this.conditionTag = it
            }
        }
        handleTags()
    }

    private fun initializeSharedPreferences() {
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUIComponents() {
        OasisViewModel.getUserDocument(currentUser.uid).addOnSuccessListener {
            it.toObject(User::class.java)?.let { me ->
                binding.userName.text = "@${me.name}"
            }
        }
    }

    private fun handleTouches() {
        binding.chooseTagsOption.setOnClickListener {
            handleChooseTagsOption()
        }
        binding.lendTimeOption.setOnClickListener {
            with(MaterialAlertDialogBuilder(this)) {
                setTitle("Enter lending time in days")
                val lendTimePickerBinding = LendTimePickerBinding.inflate(layoutInflater)
                setView(lendTimePickerBinding.root)
                lendTimePickerBinding.lendTimeOption.setText(lendTime)
                setPositiveButton("Save") { _, _ ->
                    sellTag = "Lending for ${lendTimePickerBinding.lendTimeOption.text} days"
                    lendTime = lendTimePickerBinding.lendTimeOption.text.toString()
                    handleTags()
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
        binding.saveAsDraft.setOnClickListener {
            Log.i(TAG, "clicked on save-as-draft")
            handleSaveAsDraft()
        }
        binding.submit.setOnClickListener {
            Log.i(TAG, "clicked on submit")
            handleSubmitAction()
        }
    }

    private fun handleChooseTagsOption() {
        with(MaterialAlertDialogBuilder(this)) {
            setTitle("Choose suitable tags")
            val binding = MarketplaceTagsBinding.inflate(layoutInflater)
            setView(binding.root)
            val categoryChips = MarketplaceTags.getCategoryChips(binding)
            val sellChips = MarketplaceTags.getSellChips(binding)
            val conditionChips = MarketplaceTags.getConditionChips(binding)
            for (chip in categoryChips) {
                for (tag in categoryTags) {
                    chip.isChecked = chip.isChecked || chip.text.toString() == tag
                }
            }
            if (sellTag.subSequence(0, 4) == "Lend") {
                MarketplaceTags.getSellChipLend(binding).isChecked = true
            } else {
                for (chip in sellChips) {
                    chip.isChecked = chip.text.toString() == sellTag
                }
            }
            for (chip in conditionChips) {
                chip.isChecked = chip.text.toString() == conditionTag
            }
            setPositiveButton("Save") { _, _ ->
                categoryTags.clear()
                for (chip in categoryChips) {
                    if (chip.isChecked) {
                        categoryTags.add(chip.text.toString())
                        if (categoryTags.size == 3) {
                            break
                        }
                    }
                }
                for (chip in sellChips) {
                    if (chip.isChecked) {
                        sellTag = if (chip.text.toString() == "Lend") {
                            "Lending for $lendTime days"
                        } else {
                            chip.text.toString()
                        }
                        break
                    }
                }
                conditionTag = null
                for (chip in conditionChips) {
                    if (chip.isChecked) {
                        conditionTag = chip.text.toString()
                        break
                    }
                }
                if (MarketplaceTags.getCategoryTagGroup(binding).checkedChipIds.size > 3) {
                    Toast.makeText(
                        this@AddMarketplacePostActivity,
                        "Select only three category tags",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                handleTags()
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    private fun handleSaveAsDraft() {
        sharedPreferencesEditor.apply {
            putString(ITEM_ID, postID)
            putString(ITEM_NAME, binding.itemName.text.toString())
            putString(ITEM_DESC, binding.itemDesc.text.toString())
            putString(ITEM_PRICE, binding.itemPrice.text.toString())
            putStringSet(CATEGORY_TAGS, categoryTags.toMutableSet())
            putString(SELL_TAG, sellTag)
            putString(CONDITION_TAG, conditionTag)
            commit()
        }
    }

    private fun handleSubmitAction() {
        if (binding.itemName.text.isEmpty()) {
            Toast.makeText(this, "Enter item name", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.itemDesc.text.isEmpty()) {
            Toast.makeText(this, "Enter item description", Toast.LENGTH_SHORT).show()
            return
        }
        val currentTime = System.currentTimeMillis()
        val post = MarketplacePost(
            userID = currentUser.uid,
            postID = currentTime.toString(),
            itemName = binding.itemName.text.toString(),
            itemDesc = binding.itemDesc.text.toString(),
            itemPrice = binding.itemPrice.text.toString(),
            categoryTags = categoryTags,
            sellTag = sellTag,
            conditionTag = conditionTag.orEmpty()
        )
        when (postID) {
            "" -> {
                PostDao().addMarketplacePost(post)
                Toast.makeText(this, "Post submitted successfully", Toast.LENGTH_SHORT).show()
            }

            else -> {
                post.postID = postID
                PostDao().updateMarketplacePost(post)
                PostDao().addMarketplacePost(post)
                Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    private fun initializeArguments() {
        intent.getStringExtra(ITEM_ID)?.let {
            this.postID = it
        }
        intent.getStringExtra(ITEM_NAME)?.let {
            binding.itemName.setText(it)
        }
        intent.getStringExtra(ITEM_DESC)?.let {
            binding.itemDesc.setText(it)
        }
        intent.getStringExtra(ITEM_PRICE)?.let {
            binding.itemPrice.setText(it)
        }
        intent.getStringArrayExtra(CATEGORY_TAGS)?.let {
            for (out in it) {
                if (out.isNotEmpty()) {
                    categoryTags.add(out)
                }
            }
        }
        intent.getStringExtra(SELL_TAG)?.let {
            this.sellTag = it
        }
        intent.getStringExtra(CONDITION_TAG)?.let {
            if (it.isNotEmpty()) {
                this.conditionTag = it
            }
        }
        handleTags()
    }

    private fun handleTags() {
        var chooseTagsDesc = ""
        val tags = arrayListOf(binding.categoryTag1, binding.categoryTag2, binding.categoryTag3)
        for (tag in tags) {
            tag.visibility = View.GONE
        }
        for ((i, categoryTag) in categoryTags.withIndex()) {
            chooseTagsDesc += "$categoryTag, "
            tags[i].text = categoryTag
            tags[i].visibility = View.VISIBLE
        }
        binding.sellTag.text = sellTag
        chooseTagsDesc += sellTag
        if (sellTag.subSequence(0, 4) == "Lend") {
            binding.lendTimeDesc.text = this.lendTime
            binding.lendTimeOption.visibility = View.VISIBLE
        } else {
            binding.lendTimeOption.visibility = View.GONE
        }
        binding.conditionTag.visibility = View.GONE
        if (conditionTag != null) {
            binding.conditionTag.text = conditionTag
            binding.conditionTag.visibility = View.VISIBLE
            chooseTagsDesc += ", $conditionTag"
        }
        binding.chooseTagsDesc.text = chooseTagsDesc
    }
}