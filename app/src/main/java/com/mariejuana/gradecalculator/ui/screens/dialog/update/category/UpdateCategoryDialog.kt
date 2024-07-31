package com.mariejuana.gradecalculator.ui.screens.dialog.update.category

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
import com.mariejuana.gradecalculator.databinding.DialogUpdateCategoryBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateCategoryDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateCategoryBinding
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
        binding = DialogUpdateCategoryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("updateYearLevelId").toString()
        val semesterId = bundle!!.getString("updateSemesterId").toString()
        val subjectId = bundle!!.getString("updateSubjectId").toString()
        val categoryId = bundle!!.getString("updateCategoryId").toString()
        val categoryName = bundle!!.getString("updateCategoryName").toString()
        val categoryPercentage = bundle!!.getFloat("updateCategoryPercentage").toString()

        with(binding) {
            textInputCategoryName.setText(categoryName)
            textInputCategoryPercentage.setText(categoryPercentage)

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

                    database.updateCategory(subjectId, categoryId, categoryName, categoryPercentage)
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