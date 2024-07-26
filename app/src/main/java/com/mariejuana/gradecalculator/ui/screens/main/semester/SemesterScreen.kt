package com.mariejuana.gradecalculator.ui.screens.main.semester

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.semester.SemesterAdapter
import com.mariejuana.gradecalculator.data.adapters.year.YearLevelAdapter
import com.mariejuana.gradecalculator.data.database.models.SemesterModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ActivitySemesterScreenBinding
import com.mariejuana.gradecalculator.databinding.ActivityYearLevelScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.semester.AddSemesterDialog
import io.realm.kotlin.internal.platform.threadId
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SemesterScreen : AppCompatActivity(), AddSemesterDialog.RefreshDataInterface, SemesterAdapter.SemesterAdapterInterface {
    private lateinit var binding: ActivitySemesterScreenBinding
    private lateinit var semesterList: ArrayList<Semester>
    private lateinit var adapter: SemesterAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySemesterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val yearLevelId = extras?.getString("yearLevelId")
        val academicYear = extras?.getString("academicYear")

        val bundle = Bundle()
        bundle.putString("yearLevelId", yearLevelId.toString())
        bundle.putString("academicYear", academicYear.toString())

        semesterList = arrayListOf()

        adapter = SemesterAdapter(semesterList, this, this)
        getSemester()

        val layoutManager = LinearLayoutManager(this)
        binding.cvSemester.layoutManager = layoutManager
        binding.cvSemester.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val addSemesterDialog = AddSemesterDialog()
            addSemesterDialog.refreshDataCallback = this
            addSemesterDialog.arguments = bundle
            addSemesterDialog.show(supportFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        getSemester()
    }

    override fun refreshData() {
        getSemester()
    }

    private fun mapSemesterDetails(semesterModel: SemesterModel): Semester {
        return Semester(
            id = semesterModel.id.toHexString(),
            yearLevel = semesterModel.yearLevel,
            academicYear = semesterModel.academicYear,
            semester = semesterModel.semester
        )
    }

    private fun getSemester() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllSemester"))
        scope.launch(Dispatchers.IO) {
            val extras = intent.extras
            val yearLevelId = extras?.getString("yearLevelId")

            val semester = yearLevelId?.let { database.getAllSemesterByYear(it) }
            semesterList = arrayListOf()
            if (semester != null) {
                semesterList.addAll(
                    semester.map {
                        mapSemesterDetails(it)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateSemesterList(semesterList)
            }
        }
    }
}