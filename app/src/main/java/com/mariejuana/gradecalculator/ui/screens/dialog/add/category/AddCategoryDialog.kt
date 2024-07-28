package com.mariejuana.gradecalculator.ui.screens.dialog.add.category

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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCategoryDialog : DialogFragment() {
    private lateinit var binding: DialogAddCategoryBinding
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
        binding = DialogAddCategoryBinding.inflate(layoutInflater,container,false)
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

        with(binding) {
            buttonAdd.setOnClickListener {
                if (textInputCategoryName.text.isNullOrEmpty()) {
                    textInputCategoryName.error = "Required"
                    return@setOnClickListener
                }
                if (textInputCategoryPercentage.text.isNullOrEmpty()) {
                    textInputCategoryPercentage.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("addCategoryDetails"))
                scope.launch(Dispatchers.IO) {
                    val categoryName = textInputCategoryName.text.toString()
                    val categoryPercentage = textInputCategoryPercentage.text.toString().toFloat()

                    if (yearLevelId != null) {
                        if (semesterName != null) {
                            if (semesterId != null) {
                                if (academicYear != null) {
                                    if (subjectId != null) {
                                        if (subjectName != null) {
                                            if (subjectUnits != null) {
                                                if (subjectCode != null) {
                                                    database.addCategory(yearLevelId, semesterName, semesterId, academicYear,
                                                        subjectId, subjectName, subjectCode, subjectUnits.toFloat(),
                                                        categoryName, categoryPercentage)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Category has been added!", Toast.LENGTH_LONG).show()
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