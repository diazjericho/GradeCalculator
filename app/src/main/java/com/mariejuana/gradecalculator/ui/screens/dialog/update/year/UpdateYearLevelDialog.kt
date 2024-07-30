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
import com.mariejuana.gradecalculator.extensions.Extensions
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
    private var extensions = Extensions()

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

        val bundle = arguments
        val yearLevelId = bundle!!.getString("updateYearLevelId").toString()
        val yearLevelName = bundle!!.getString("updateYearLevelName").toString()
        val yearLevelAcademicYear = bundle!!.getString("updateYearLevelAcademicYear").toString()

        val yearLevelExtractedAcademicYear = extensions.extractYears(yearLevelAcademicYear)
        val yearLevelStartAcademicYear = yearLevelExtractedAcademicYear?.first
        val yearLevelEndAcademicYear = yearLevelExtractedAcademicYear?.second

        with(binding) {
            textYearLevel.setText(yearLevelName)
            if (yearLevelStartAcademicYear != null) {
                textInputYearStart.setText(yearLevelStartAcademicYear.toString())
            }
            if (yearLevelEndAcademicYear != null) {
                textInputYearEnd.setText(yearLevelEndAcademicYear.toString())
            }

            buttonUpdate.setOnClickListener {
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

                    database.updateYear(yearLevelId, yearLevel, academicYear)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Year level has been updated!", Toast.LENGTH_LONG).show()
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