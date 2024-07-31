package com.mariejuana.gradecalculator.ui.screens.main.categories.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.activity.ActivityAdapter
import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Activity
import com.mariejuana.gradecalculator.databinding.ActivityActivitiesScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.add.activity.AddActivityDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.activity.DeleteActivityDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.activity.UpdateActivityDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivitiesScreen : AppCompatActivity(),
    AddActivityDialog.RefreshDataInterface,
    UpdateActivityDialog.RefreshDataInterface,
    DeleteActivityDialog.RefreshDataInterface,
    ActivityAdapter.ActivityAdapterInterface {
    private lateinit var binding: ActivityActivitiesScreenBinding
    private lateinit var activityList: ArrayList<Activity>
    private lateinit var adapter: ActivityAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitiesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val yearLevelId = extras?.getString("yearLevelId")
        val academicYear = extras?.getString("academicYear")
        val semesterId = extras?.getString("semesterId")
        val semesterName = extras?.getString("semesterName")
        val subjectId = extras?.getString("subjectId")
        val subjectName = extras?.getString("subjectName")
        val subjectCode = extras?.getString("subjectCode")
        val subjectUnits = extras?.getFloat("subjectUnits")
        val categoryId = extras?.getString("categoryId")
        val categoryName = extras?.getString("categoryName")
        val categoryPercentage = extras?.getFloat("categoryPercentage")

        val bundle = Bundle()
        bundle.putString("yearLevelId", yearLevelId.toString())
        bundle.putString("academicYear", academicYear.toString())
        bundle.putString("semesterId", semesterId.toString())
        bundle.putString("semesterName", semesterName.toString())
        bundle.putString("subjectId", subjectId.toString())
        bundle.putString("subjectName", subjectName.toString())
        bundle.putString("subjectCode", subjectCode.toString())
        if (subjectUnits != null) {
            bundle.putFloat("subjectUnits", subjectUnits)
        }
        bundle.putString("categoryId", categoryId.toString())
        bundle.putString("categoryName", categoryName.toString())
        if (categoryPercentage != null) {
            bundle.putFloat("categoryPercentage", categoryPercentage)
        }

        binding.textCategoryDetails.text = "${subjectName} (${subjectCode}) | ${categoryName}"

        updateScorePercentage()

        activityList = arrayListOf()

        adapter = ActivityAdapter(activityList, this, this, this, this)
        getActivities()

        val layoutManager = LinearLayoutManager(this)
        binding.cvActivities.layoutManager = layoutManager
        binding.cvActivities.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val addActivityDialog = AddActivityDialog()
            addActivityDialog.refreshDataCallback = this
            addActivityDialog.arguments = bundle
            addActivityDialog.show(supportFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        getActivities()
        updateScorePercentage()
    }

    override fun refreshData() {
        getActivities()
        updateScorePercentage()
    }

    private fun mapActivityDetails(activityModel: ActivityModel): Activity {
        return Activity(
            id = activityModel.id.toHexString(),
            yearLevel = activityModel.yearLevel,
            academicYear = activityModel.academicYear,
            semesterName = activityModel.semesterName,
            semesterId = activityModel.semesterId,
            subjectId = activityModel.subjectId,
            subjectName = activityModel.subjectName,
            subjectCode = activityModel.subjectCode,
            subjectUnits = activityModel.subjectUnits,
            categoryId = activityModel.categoryId,
            categoryName = activityModel.categoryName,
            percentage = activityModel.percentage,
            activityName = activityModel.activityName,
            score = activityModel.score,
            totalScore = activityModel.totalScore
        )
    }

    private fun getActivities() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllActivities"))
        scope.launch(Dispatchers.IO) {
            val extras = intent.extras
            val categoryId = extras?.getString("categoryId")

            val activity = categoryId?.let { database.getAllActivitiesByCategory(it) }
            activityList = arrayListOf()
            if (activity != null) {
                activityList.addAll(
                    activity.map {
                        mapActivityDetails(it)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateActivityList(activityList)

                if (activityList.isEmpty()) {
                    binding.cvActivities.visibility = View.GONE
                    binding.noItemsFound.visibility = View.VISIBLE
                } else {
                    binding.cvActivities.visibility = View.VISIBLE
                    binding.noItemsFound.visibility = View.GONE
                }
            }
        }
    }

    private fun getActivityDetails(categoryId: String): List<ActivityModel>? {
        return database.getAllActivitiesByCategory(categoryId)
    }

    private fun updateScorePercentage() {
        val extras = intent.extras
        val categoryId = extras?.getString("categoryId")
        val categoryPercentage = extras?.getFloat("categoryPercentage")

        val activity: List<ActivityModel>? = categoryId?.let { getActivityDetails(it) }

        var totalScoreSum = 0.0F
        var achievedScoreSum = 0.0F

        val activityNameBuilder = StringBuilder()
        val activityScoreBuilder = StringBuilder()

        if (activity.isNullOrEmpty()) {
            activityNameBuilder.append("N/A")
            activityScoreBuilder.append("N/A")
        }

        activity?.forEachIndexed { index, activityItem ->
            activityNameBuilder.append("${activityItem.activityName}")
            activityScoreBuilder.append("${String.format("%.2f", activityItem.score)} / ${String.format("%.2f", activityItem.totalScore)}")
            totalScoreSum += activityItem.totalScore
            achievedScoreSum += activityItem.score

            if (index < activity.size - 1) {
                activityNameBuilder.append("\n")
                activityScoreBuilder.append("\n")
            }
        }

        binding.textScore.text = "${String.format("%.2f", achievedScoreSum)} / ${String.format("%.2f", totalScoreSum)}"

        val totalPercentage = categoryPercentage
        val percentage = (achievedScoreSum / totalScoreSum) * totalPercentage!!
        if (percentage.isNaN()) {
            binding.textPercentage.text = "0% / ${String.format("%.2f", totalPercentage)}%"
        } else {
            binding.textPercentage.text = "${String.format("%.2f", percentage)}% / ${String.format("%.2f", totalPercentage)}%"
        }
    }
}