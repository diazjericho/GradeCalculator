package com.mariejuana.gradecalculator.ui.screens.dialog.add.semester

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddSemesterBinding
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSemesterDialog : DialogFragment() {
    private lateinit var binding: DialogAddSemesterBinding
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
        binding = DialogAddSemesterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("yearLevelId")
        val academicYear = bundle!!.getString("academicYear")

        with(binding) {
            buttonAdd.setOnClickListener {
                if (textSemesterLevel.text.isNullOrEmpty()) {
                    textSemesterLevel.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("addSemesterDetails"))
                scope.launch(Dispatchers.IO) {
                    val semesterYear = textSemesterLevel.text.toString()

                    if (yearLevelId != null) {
                        database.addSemester(yearLevelId.toString(), semesterYear, academicYear.toString())
                        Log.d("tag", "ID: ${yearLevelId} ${semesterYear}")
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Semester has been added!", Toast.LENGTH_LONG).show()
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