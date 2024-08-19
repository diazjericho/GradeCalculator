package com.mariejuana.gradecalculator.ui.screens.main.settings

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.databinding.ActivitySettingsScreenBinding
import com.mariejuana.gradecalculator.databinding.ActivityYearLevelScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.settings.about.AboutDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.settings.letter.SpecialLetterDialog

class SettingsScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchDisableFinalGrade = binding.switchDisableFinalGrade

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val isDisabledFinalGrade = sharedPreferences.getBoolean("disableFinalGrade", false)
        switchDisableFinalGrade.isChecked = isDisabledFinalGrade

        switchDisableFinalGrade.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("disableFinalGrade", switchDisableFinalGrade.isChecked)
            editor.apply()
        }

        binding.cvLetter.setOnClickListener {
            val letterDialog = SpecialLetterDialog()
            letterDialog.show(supportFragmentManager, null)
        }

        binding.cvShowAbout.setOnClickListener {
            val aboutDialog = AboutDialog()
            aboutDialog.show(supportFragmentManager, null)
        }
    }
}