package com.mariejuana.gradecalculator.ui.screens.main.yearlevel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.year.YearLevelAdapter
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ActivityYearLevelScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.year.AddYearLevelDialog
import io.realm.kotlin.internal.REALM_FILE_EXTENSION
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Year

class YearLevelScreen : AppCompatActivity(), AddYearLevelDialog.RefreshDataInterface, YearLevelAdapter.YearLevelAdapterInterface {
    private lateinit var binding: ActivityYearLevelScreenBinding
    private lateinit var yearLevelList: ArrayList<YearLevel>
    private lateinit var adapter: YearLevelAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYearLevelScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        yearLevelList = arrayListOf()

        adapter = YearLevelAdapter(yearLevelList, this, this)
        getYearLevel()

        val layoutManager = LinearLayoutManager(this)
        binding.cvYear.layoutManager = layoutManager
        binding.cvYear.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val addYearLevelDialog = AddYearLevelDialog()
            addYearLevelDialog.refreshDataCallback = this
            addYearLevelDialog.show(supportFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        getYearLevel()
    }

    override fun refreshData() {
        getYearLevel()
    }

    private fun mapYearLevelDetails(yearLevelModel: YearLevelModel): YearLevel {
        return YearLevel(
            id = yearLevelModel.id.toHexString(),
            yearLevel = yearLevelModel.yearLevel,
            academicYear = yearLevelModel.academicYear
        )
    }

    private fun getYearLevel() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllYears"))
        scope.launch(Dispatchers.IO) {
            val years = database.getAllYears()
            yearLevelList = arrayListOf()
            yearLevelList.addAll(
                years.map {
                    mapYearLevelDetails(it)
                }
            )
            withContext(Dispatchers.Main) {
                adapter.updateYearLevelList(yearLevelList)
//                if (petList.isEmpty()) {
//                    binding.rvPets.visibility = View.GONE
//                    binding.txtNoPetsAvailable.visibility = View.VISIBLE
//                } else {
//                    binding.txtNoPetsAvailable.visibility = View.GONE
//                    binding.rvPets.visibility = View.VISIBLE
//                }
            }
        }
    }
}