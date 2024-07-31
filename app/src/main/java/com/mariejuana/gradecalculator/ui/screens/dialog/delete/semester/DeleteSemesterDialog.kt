package com.mariejuana.gradecalculator.ui.screens.dialog.delete.semester

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogDeleteBinding
import com.mariejuana.gradecalculator.extensions.Extensions
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteSemesterDialog : DialogFragment() {
    private lateinit var binding: DialogDeleteBinding
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
        binding = DialogDeleteBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("deleteYearLevelId").toString()
        val semesterName = bundle!!.getString("deleteSemesterName").toString()
        val semesterId = bundle!!.getString("deleteSemesterId").toString()

        with(binding) {
            textName.text = "Delete ${semesterName}?"
            textDetails.text = "Are you sure you want to delete '${semesterName}'? " +
                    "This cannot be undone."
            buttonDelete.setText("Delete semester")

            buttonDelete.setOnClickListener {
                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("deleteSemesterDetails"))
                scope.launch(Dispatchers.IO) {
                    database.deleteSemester(yearLevelId, semesterId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Semester has been deleted!", Toast.LENGTH_LONG).show()
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