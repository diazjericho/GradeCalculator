package com.mariejuana.gradecalculator.ui.screens.main.yearlevel

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.year.YearLevelAdapter
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ActivityYearLevelScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.add.year.AddYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.year.DeleteYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.year.UpdateYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.main.settings.SettingsScreen
import com.mariejuana.gradecalculator.ui.theme.GradeCalculator.Companion.context
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YearLevelScreen : AppCompatActivity(),
    AddYearLevelDialog.RefreshDataInterface,
    UpdateYearLevelDialog.RefreshDataInterface,
    DeleteYearLevelDialog.RefreshDataInterface,
    YearLevelAdapter.YearLevelAdapterInterface {
    private lateinit var binding: ActivityYearLevelScreenBinding
    private lateinit var yearLevelList: ArrayList<YearLevel>
    private lateinit var adapter: YearLevelAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYearLevelScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        yearLevelList = arrayListOf()

        adapter = YearLevelAdapter(yearLevelList, this, this, this, this)
        getYearLevel()

        val layoutManager = LinearLayoutManager(this)
        binding.cvYear.layoutManager = layoutManager
        binding.cvYear.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val addYearLevelDialog = AddYearLevelDialog()
            addYearLevelDialog.refreshDataCallback = this
            addYearLevelDialog.show(supportFragmentManager, null)
        }

        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsScreen::class.java)
            this.startActivity(intent)
        }

        val settingIcon = binding.buttonSettings
        updateSettingImageForTheme(settingIcon)
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

                if (yearLevelList.isEmpty()) {
                    binding.cvYear.visibility = View.GONE
                    binding.noItemsFound.visibility = View.VISIBLE
                } else {
                    binding.cvYear.visibility = View.VISIBLE
                    binding.noItemsFound.visibility = View.GONE
                }
            }
        }
    }

    private fun updateSettingImageForTheme(imgOwner: ImageView) {
        val isDarkMode = when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        if (isDarkMode) {
            imgOwner.setImageResource(R.drawable.ic_settings_white)
        } else {
            imgOwner.setImageResource(R.drawable.ic_settings_black)
        }
    }
}