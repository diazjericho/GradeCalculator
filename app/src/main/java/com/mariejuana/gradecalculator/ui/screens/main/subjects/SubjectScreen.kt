package com.mariejuana.gradecalculator.ui.screens.main.subjects

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.semester.SemesterAdapter
import com.mariejuana.gradecalculator.data.adapters.subject.SubjectAdapter
import com.mariejuana.gradecalculator.data.database.models.SubjectModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.databinding.ActivitySemesterScreenBinding
import com.mariejuana.gradecalculator.databinding.ActivitySubjectScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.add.subject.AddSubjectDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.subject.DeleteSubjectDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.subject.UpdateSubjectDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectScreen : AppCompatActivity(),
    AddSubjectDialog.RefreshDataInterface,
    UpdateSubjectDialog.RefreshDataInterface,
    DeleteSubjectDialog.RefreshDataInterface,
    SubjectAdapter.SubjectAdapterInterface {
    private lateinit var binding: ActivitySubjectScreenBinding
    private lateinit var subjectList: ArrayList<Subject>
    private lateinit var adapter: SubjectAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val yearLevelId = extras?.getString("yearLevelId")
        val academicYear = extras?.getString("academicYear")
        val semesterId = extras?.getString("semesterId")
        val semesterName = extras?.getString("semesterName")

        val bundle = Bundle()
        bundle.putString("yearLevelId", yearLevelId.toString())
        bundle.putString("academicYear", academicYear.toString())
        bundle.putString("semesterId", semesterId.toString())
        bundle.putString("semesterName", semesterName.toString())

        subjectList = arrayListOf()

        adapter = SubjectAdapter(subjectList, this, this, this, this)
        getSubject()

        val layoutManager = LinearLayoutManager(this)
        binding.cvSubject.layoutManager = layoutManager
        binding.cvSubject.adapter = adapter

        with(binding) {
            textListSubjects.text = "Subjects for ${semesterName} | ${academicYear}"
        }

        binding.searchSubjectDetails.addTextChangedListener((object : TextWatcher,
            TextInputLayout.OnEditTextAttachedListener {
            override fun afterTextChanged(s: Editable?) {
                val searchSubjectDetails = s.toString().lowercase()

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("SearchSubjects"))
                scope.launch(Dispatchers.IO) {
                    val result = semesterId?.let { database.searchSubject(it, searchSubjectDetails) }

                    subjectList = arrayListOf()
                    if (result != null) {
                        subjectList.addAll(
                            result.map {
                                mapSubjectDetails(it)
                            }
                        )
                    }
                    withContext(Dispatchers.Main) {
                        adapter.updateSubjectList(subjectList)

                        if (subjectList.isEmpty()) {
                            binding.cvSubject.visibility = View.GONE
                            binding.noItemsFound.visibility = View.VISIBLE
                        } else {
                            binding.cvSubject.visibility = View.VISIBLE
                            binding.noItemsFound.visibility = View.GONE
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nothing to do
            }

            override fun onEditTextAttached(textInputLayout: TextInputLayout) {
                // Nothing to do
            }
        }))

        binding.fabAdd.setOnClickListener {
            val addSubjectDialog = AddSubjectDialog()
            addSubjectDialog.refreshDataCallback = this
            addSubjectDialog.arguments = bundle
            addSubjectDialog.show(supportFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        getSubject()
    }

    override fun refreshData() {
        getSubject()
    }

    private fun mapSubjectDetails(subjectModel: SubjectModel): Subject {
        return Subject(
            id = subjectModel.id.toHexString(),
            yearLevel = subjectModel.yearLevel,
            academicYear = subjectModel.academicYear,
            semesterName = subjectModel.semesterName,
            semesterId = subjectModel.semesterId,
            subjectName = subjectModel.name,
            subjectCode = subjectModel.code,
            subjectUnits = subjectModel.units
        )
    }

    private fun getSubject() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllSubject"))
        scope.launch(Dispatchers.IO) {
            val extras = intent.extras
            val semesterId = extras?.getString("semesterId")

            val subject = semesterId?.let { database.getAllSubjectBySemester(it) }
            subjectList = arrayListOf()
            if (subject != null) {
                subjectList.addAll(
                    subject.map {
                        mapSubjectDetails(it)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateSubjectList(subjectList)

                if (subjectList.isEmpty()) {
                    binding.cvSubject.visibility = View.GONE
                    binding.noItemsFound.visibility = View.VISIBLE
                } else {
                    binding.cvSubject.visibility = View.VISIBLE
                    binding.noItemsFound.visibility = View.GONE
                }
            }
        }
    }
}