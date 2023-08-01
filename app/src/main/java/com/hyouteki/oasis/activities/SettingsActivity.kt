package com.hyouteki.oasis.activities

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hyouteki.oasis.R
import com.hyouteki.oasis.databinding.ActivitySettingsBinding
import com.hyouteki.oasis.utils.Saver
import com.hyouteki.oasis.utils.Tags

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)

        sharedPreferences = Saver.getPreferences(this)
        editor = sharedPreferences.edit()

        initialize()
        handleTouches()
        handleDialogs()
    }

    private fun handleDialogs() {
        binding.defaultTab.setOnClickListener {
            with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                setTitle("Select default tab")
                val items = arrayOf(Tags.MARKETPLACE, Tags.HAPPENING)
                var checkedItem = when (binding.defaultTabDesc.text.toString()) {
                    Tags.MARKETPLACE -> 0
                    else -> 1
                }
                setSingleChoiceItems(items, checkedItem,
                    DialogInterface.OnClickListener { _, which ->
                        checkedItem = which
                    })
                setPositiveButton("Save") { _, _ ->
                    binding.defaultTabDesc.text = items[checkedItem]
                    editor.apply {
                        putString(Saver.DEFAULT_TAB, items[checkedItem])
                        apply()
                    }
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
    }

    private fun handleTouches() {
        binding.previous.setOnClickListener {
            finish()
        }

        binding.confirmDelete.setOnClickListener {
            binding.confirmDeleteSwitch.isChecked = !binding.confirmDeleteSwitch.isChecked
            editor.apply {
                putBoolean(Saver.CONFIRM_DELETE, binding.confirmDeleteSwitch.isChecked)
                apply()
            }
        }

        binding.confirmDeleteSwitch.setOnCheckedChangeListener { _, _ ->
            editor.apply {
                putBoolean(Saver.CONFIRM_DELETE, binding.confirmDeleteSwitch.isChecked)
                apply()
            }
        }
    }

    private fun initialize() {
        binding.confirmDeleteSwitch.isChecked =
            sharedPreferences.getBoolean(Saver.CONFIRM_DELETE, false)
        binding.defaultTabDesc.text =
            sharedPreferences.getString(Saver.DEFAULT_TAB, Tags.MARKETPLACE)
    }
}