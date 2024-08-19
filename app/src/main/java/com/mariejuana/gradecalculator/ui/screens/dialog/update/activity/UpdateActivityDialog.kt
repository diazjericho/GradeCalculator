package com.mariejuana.gradecalculator.ui.screens.dialog.update.activity

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
import com.mariejuana.gradecalculator.databinding.DialogUpdateActivityBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateActivityDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateActivityBinding
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
        binding = DialogUpdateActivityBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("updateYearLevelId").toString()
        val semesterId = bundle!!.getString("updateSemesterId").toString()
        val subjectId = bundle!!.getString("updateSubjectId").toString()
        val categoryId = bundle!!.getString("updateCategoryId").toString()
        val activityId = bundle!!.getString("updateActivityId").toString()
        val activityName = bundle!!.getString("updateActivityName").toString()
        val activityScore = bundle!!.getFloat("updateActivityScore").toString()
        val activityTotalScore = bundle!!.getFloat("updateActivityTotalScore").toString()

        with(binding) {
            textActivityName.setText(activityName)
            textInputScore.setText(activityScore)
            textInputTotalScore.setText(activityTotalScore)

            buttonUpdate.setOnClickListener {
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
                val scope = CoroutineScope(coroutineContext + CoroutineName("updateActivityDetails"))
                scope.launch(Dispatchers.IO) {
                    val activityName = textActivityName.text.toString()
                    val activityScore = textInputScore.text.toString().toFloat()
                    val activityTotalScore = textInputTotalScore.text.toString().toFloat()

                    database.updateActivity(categoryId, activityId, activityName, activityScore, activityTotalScore)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Activity has been updated!", Toast.LENGTH_LONG).show()
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