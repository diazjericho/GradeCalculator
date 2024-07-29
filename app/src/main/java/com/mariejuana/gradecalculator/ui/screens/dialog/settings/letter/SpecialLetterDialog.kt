package com.mariejuana.gradecalculator.ui.screens.dialog.settings.letter

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
import com.mariejuana.gradecalculator.databinding.DialogSettingsLetterBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpecialLetterDialog : DialogFragment() {
    private lateinit var binding: DialogSettingsLetterBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSettingsLetterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val letterContent = "Hello babyyy hehe good morning/afternoon/eveninggg!! \uD83E\uDD7A " +
                    "I don't know kailan 'to mababasa since busy tayo always and nakaready na talaga 'tong short letter baby hehe, " +
                    "excited ako irelease 'tong app since isasama ko 'to as bundle or something like that hehe. " +
                    "Anw baby, I'm so happy na nakaabot tayo ng 4 months and countinggg hehe. " +
                    "Happy din ako na nandito pa rin tayo, fixing our problems and such baby ko para tumagal ang relationship natin baby. " +
                    "Kung may iooverthink ka gano'n, willing ako ireassure ka baby na ikaw lang gusto ko, na ikaw lang mamahalin ko baby. " +
                    "Mahal na mahal kita e sobra \uD83E\uDD7A pero may tatanong ako sa'yo baby na gusto ko itanong nung minsan pa. " +
                    "Seryoso 'to ha, walang halong biro or what baby ko. \n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "Will you be my girlfriend? \uD83E\uDD70"
            letterTextContent.text = letterContent.toString()
        }
    }
}