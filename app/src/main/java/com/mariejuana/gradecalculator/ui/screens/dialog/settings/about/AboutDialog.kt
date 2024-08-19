package com.mariejuana.gradecalculator.ui.screens.dialog.settings.about

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddCategoryBinding
import com.mariejuana.gradecalculator.databinding.DialogAddSemesterBinding
import com.mariejuana.gradecalculator.databinding.DialogAddSubjectBinding
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import com.mariejuana.gradecalculator.databinding.DialogSettingsAboutBinding
import com.mariejuana.gradecalculator.databinding.DialogSettingsLetterBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutDialog : DialogFragment() {
    private lateinit var binding: DialogSettingsAboutBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSettingsAboutBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val aboutContent = "This project was made in Android Studio and showcases the complexity of Realm database in Kotlin. " +
                    "Made in XML, not in Jetpack Compose. It calculates all of the grades inputted with their raw grades, " +
                    "and is applied for a specific school for the conversion for final grade since this is for my girlfriend. \uD83D\uDC96\n" +
                    "\n" +
                    "Made by Jericho Diaz!"
            aboutTextContent.text = aboutContent.toString()
        }
    }
}