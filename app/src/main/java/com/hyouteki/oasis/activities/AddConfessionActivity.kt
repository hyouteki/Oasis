package com.hyouteki.oasis.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hyouteki.oasis.R
import com.hyouteki.oasis.databinding.ActivityAddConfessionBinding
import com.hyouteki.oasis.models.Confession
import com.hyouteki.oasis.utils.Helper
import com.hyouteki.oasis.utils.Saver
import com.hyouteki.oasis.viewmodels.OasisViewModel

class AddConfessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddConfessionBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddConfessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.background)

        initialize()
        handleTouches()
    }

    private fun initialize() {
        sharedPreferences = Saver.getPreferences(this)
        editor = Saver.getEditor(this)
        binding.addressedTo.setText(
            sharedPreferences
                .getString(Saver.CONFESSION_ADDRESSED_TO, "")
        )
        binding.confession.setText(
            sharedPreferences
                .getString(Saver.CONFESSION_CONFESSION, "")
        )
        if (sharedPreferences.getString(Saver.CONFESSION_ADDRESSED_TO, "") != "" ||
            sharedPreferences.getString(Saver.CONFESSION_CONFESSION, "") != ""
        ) {
            Helper.makeToast(this, "Loaded from draft")
        }
    }

    private fun handleTouches() {
        binding.save.setOnClickListener {
            editor.apply {
                putString(Saver.CONFESSION_ADDRESSED_TO, binding.addressedTo.text.toString())
                putString(Saver.CONFESSION_CONFESSION, binding.confession.text.toString())
                apply()
            }
            finish()
        }
        binding.cancel.setOnClickListener { finish() }
        binding.submit.setOnClickListener {
            if (binding.addressedTo.text.isEmpty() || binding.confession.text.isEmpty()) {
                Helper.makeToast(this, "Fill all the required details")
            } else {
                OasisViewModel.addConfession(
                    Confession(
                        id = System.currentTimeMillis().toString(),
                        addressedTo = binding.addressedTo.text.toString(),
                        confession = binding.confession.text.toString()
                    )
                )
                editor.apply {
                    putString(Saver.CONFESSION_ADDRESSED_TO, "")
                    putString(Saver.CONFESSION_CONFESSION, "")
                    apply()
                }
                finish()
            }
        }
    }
}