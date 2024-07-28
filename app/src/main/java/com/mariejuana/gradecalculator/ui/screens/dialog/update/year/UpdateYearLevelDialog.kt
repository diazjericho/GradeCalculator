package com.mariejuana.gradecalculator.ui.screens.dialog.update.year

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import com.mariejuana.gradecalculator.databinding.DialogUpdateYearBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateYearLevelDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateYearBinding
    lateinit var refreshDataCallback: RefreshDataInterface
    private var database = RealmDatabase()

    interface RefreshDataInterface {
        fun refreshData()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateYearBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonAdd.setOnClickListener {
                if (textYearLevel.text.isNullOrEmpty()) {
                    textYearLevel.error = "Required"
                    return@setOnClickListener
                }

                if (textInputYearStart.text.isNullOrEmpty()) {
                    textInputYearStart.error = "Required"
                    return@setOnClickListener
                }

                if (textInputYearEnd.text.isNullOrEmpty()) {
                    textInputYearEnd.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("addYearLevelDetails"))
                scope.launch(Dispatchers.IO) {
                    val yearLevel = textYearLevel.text.toString()
                    val academicYearStart = textInputYearStart.text.toString()
                    val academicYearEnd = textInputYearEnd.text.toString()
                    val academicYear = "${academicYearStart} - ${academicYearEnd}"

                    database.addYearLevel(yearLevel, academicYear)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Year level has been added!", Toast.LENGTH_LONG).show()
                        refreshDataCallback.refreshData()
                        dialog?.dismiss()
                    }
                }
            }

            buttonCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }
}