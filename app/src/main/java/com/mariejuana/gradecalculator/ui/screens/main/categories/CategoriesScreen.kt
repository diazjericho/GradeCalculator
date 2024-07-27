package com.mariejuana.gradecalculator.ui.screens.main.categories

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mariejuana.gradecalculator.R
import com.mariejuana.gradecalculator.data.adapters.category.CategoryAdapter
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.databinding.ActivityCategoriesScreenBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.category.AddCategoryDialog
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
            binding.textPercentage.text = "${String.format("%.2f", acquiredPercentageForSubject)}% / ${String.format("%.2f", totalPercentageForSubject)}%"
        } else {
            println("Subject not found or retrieved successfully.")
        }
    }
}