package com.mariejuana.gradecalculator.ui.screens.dialog.add.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddActivityBinding
import com.mariejuana.gradecalculator.databinding.DialogAddCategoryBinding
import com.mariejuana.gradecalculator.databinding.DialogAddSemesterBinding
import com.mariejuana.gradecalculator.databinding.DialogAddSubjectBinding
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddActivityDialog : DialogFragment() {
    private lateinit var binding: DialogAddActivityBinding
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
        binding = DialogAddActivityBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("yearLevelId")
        val academicYear = bundle!!.getString("academicYear")
        val semesterId = bundle!!.getString("semesterId")
        val semesterName = bundle!!.getString("semesterName")
        val subjectId = bundle!!.getString("subjectId")
        val subjectName = bundle!!.getString("subjectName")
        val subjectCode = bundle!!.getString("subjectCode")
        val subjectUnits = bundle!!.getFloat("subjectUnits")
        val categoryId = bundle!!.getString("categoryId")
        val categoryName = bundle!!.getString("categoryName")
        val categoryPercentage = bundle!!.getFloat("categoryPercentage")

        with(binding) {
            buttonAdd.setOnClickListener {
                if (textActivityName.text.isNullOrEmpty()) {
                    textActivityName.error = "Required"
                    return@setOnClickListener
                }
                if (textInputScore.text.isNullOrEmpty()) {
                    textInputScore.error = "Required"
                    return@setOnClickListener
                }
                if (textInputTotalScore.text.isNullOrEmpty()) {
                    textInputTotalScore.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("addActivityDetails"))
                scope.launch(Dispatchers.IO) {
                    val activityName = textActivityName.text.toString()
                    val activityScore = textInputScore.text.toString()
                    val activityTotalScore = textInputTotalScore.text.toString()

                    if (yearLevelId != null) {
                        if (semesterName != null) {
                            if (semesterId != null) {
                                if (academicYear != null) {
                                    if (subjectId != null) {
                                        if (subjectName != null) {
                                            if (subjectCode != null) {
                                                if (categoryId != null) {
                                                    if (categoryName != null) {
                                                        activityTotalScore.toFloatOrNull()
                                                            ?.let { it1 ->
                                                                database.addActivity(yearLevelId, semesterName, semesterId, academicYear,
                                                                    subjectId, subjectName, subjectCode, subjectUnits,
                                                                    categoryId, categoryName, categoryPercentage,
                                                                    activityName, activityScore.toFloat(),
                                                                    it1
                                                                )
                                                            }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Activity has been added!", Toast.LENGTH_LONG).show()
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