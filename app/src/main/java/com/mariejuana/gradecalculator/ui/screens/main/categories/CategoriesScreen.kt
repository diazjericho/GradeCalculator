package com.mariejuana.gradecalculator.ui.screens.main.categories

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.category.CategoryAdapter
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.databinding.ActivityCategoriesScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.add.category.AddCategoryDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesScreen : AppCompatActivity(), AddCategoryDialog.RefreshDataInterface, CategoryAdapter.CategoryAdapterInterface {
    private lateinit var binding: ActivityCategoriesScreenBinding
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var adapter: CategoryAdapter
    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesScreenBinding.inflate(layoutInflater)
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

        updateScorePercentage()

        categoryList = arrayListOf()

        adapter = CategoryAdapter(categoryList, this, this)
        getCategory()

        val layoutManager = LinearLayoutManager(this)
        binding.cvSubject.layoutManager = layoutManager
        binding.cvSubject.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val addCategoryDialog = AddCategoryDialog()
            addCategoryDialog.refreshDataCallback = this
            addCategoryDialog.arguments = bundle
            addCategoryDialog.show(supportFragmentManager, null)
        }

        binding.textSubject.text = "${subjectName} (${subjectCode})"
    }

    override fun onResume() {
        super.onResume()
        getCategory()
        updateScorePercentage()
    }

    override fun refreshData() {
        getCategory()
        updateScorePercentage()
    }

    private fun mapCategoryDetails(categoryModel: CategoryModel): Category {
        return Category(
            id = categoryModel.id.toHexString(),
            yearLevel = categoryModel.yearLevel,
            academicYear = categoryModel.academicYear,
            semesterName = categoryModel.semesterName,
            semesterId = categoryModel.semesterId,
            subjectId = categoryModel.subjectId,
            subjectName = categoryModel.subjectName,
            subjectCode = categoryModel.subjectCode,
            subjectUnits = categoryModel.subjectUnits,
            categoryName = categoryModel.categoryName,
            percentage = categoryModel.percentage
        )
    }

    private fun getCategory() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllCategory"))
        scope.launch(Dispatchers.IO) {
            val extras = intent.extras
            val subjectId = extras?.getString("subjectId")

            val category = subjectId?.let { database.getAllCategoryBySubject(it) }
            categoryList = arrayListOf()
            if (category != null) {
                categoryList.addAll(
                    category.map {
                        mapCategoryDetails(it)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateCategoryList(categoryList)

                if (categoryList.isEmpty()) {
                    binding.cvSubject.visibility = View.GONE
                    binding.noItemsFound.visibility = View.VISIBLE
                } else {
                    binding.cvSubject.visibility = View.VISIBLE
                    binding.noItemsFound.visibility = View.GONE
                }
            }
        }
    }

    private fun updateScorePercentage() {
        val extras = intent.extras
        val subjectId = extras?.getString("subjectId")

        if (subjectId != null) {
            val acquiredScoreForSubject = database.getAcquiredScoreForSubject(subjectId)
            val totalScoreForSubject = database.getTotalScoreForSubject(subjectId)
            val acquiredPercentageForSubject = database.getAcquiredPercentageForSubject(subjectId)
            val totalPercentageForSubject = database.getTotalPercentageForSubject(subjectId)

            binding.textScore.text = "${String.format("%.2f", acquiredScoreForSubject)} / ${String.format("%.2f", totalScoreForSubject)}"
            if (acquiredPercentageForSubject.isNaN()) {
                binding.textPercentage.text = "0% / ${String.format("%.2f", totalPercentageForSubject)}%"
                binding.textGrade.text = "N/A"
            } else {
                binding.textPercentage.text = "${String.format("%.2f", acquiredPercentageForSubject)}% / ${String.format("%.2f", totalPercentageForSubject)}%"

                when (acquiredPercentageForSubject) {
                    in 100.01 ..acquiredPercentageForSubject.toDouble() -> binding.textGrade.text = "4.0"
                    in 96.00..100.00 -> binding.textGrade.text = "4.0"
                    in 90.00..95.99 -> binding.textGrade.text = "3.5"
                    in 84.00..89.99 -> binding.textGrade.text = "3.0"
                    in 78.00..83.99 -> binding.textGrade.text = "2.5"
                    in 72.00..77.99 -> binding.textGrade.text = "2.0"
                    in 66.00..71.99 -> binding.textGrade.text = "1.5"
                    in 60.00..65.99 -> binding.textGrade.text = "1.0"
                    in 0.00..59.99 -> binding.textGrade.text = "R"
                    else -> binding.textGrade.text = "N/A"
                }
            }

        } else {
            println("Subject not found or retrieved successfully.")
        }
    }
}