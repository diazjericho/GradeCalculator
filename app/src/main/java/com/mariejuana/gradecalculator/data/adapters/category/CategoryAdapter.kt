package com.mariejuana.gradecalculator.data.adapters.category

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.Activity
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvCategoryBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSubjectBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.main.categories.CategoriesScreen
import com.mariejuana.gradecalculator.ui.screens.main.categories.activities.ActivitiesScreen
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class CategoryAdapter(private var categoryList: ArrayList<Category>, private var context: Context, private var categoryAdapterCallback: CategoryAdapterInterface):
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
        private val database = RealmDatabase()

    interface CategoryAdapterInterface {
        // Add view etc
    }

    inner class CategoryViewHolder(private val binding: ContentRvCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Category) {
            with(binding) {
                val activity: List<ActivityModel>? = getActivityDetails(itemData.id)

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

                textCategoryName.text = itemData.categoryName
                textSubcategoryName.text = activityNameBuilder.toString()
                textSubcategoryGrade.text = activityScoreBuilder.toString()

                val totalPercentage = itemData.percentage
                val percentage = (achievedScoreSum / totalScoreSum) * totalPercentage
                if (percentage.isNaN()) {
                    textCategoryGrade.text = "0% / ${String.format("%.2f", totalPercentage)}%"
                } else {
                    textCategoryGrade.text = "${String.format("%.2f", percentage)}% / ${String.format("%.2f", totalPercentage)}%"
                }


                cvCategory.setOnClickListener {
                    var intent = Intent(context, ActivitiesScreen::class.java)
                    intent.putExtra("semesterId", itemData.semesterId)
                    intent.putExtra("semesterName", itemData.semesterName)
                    intent.putExtra("yearLevelId", itemData.yearLevel)
                    intent.putExtra("academicYear", itemData.academicYear)
                    intent.putExtra("subjectId", itemData.subjectId)
                    intent.putExtra("subjectName", itemData.subjectName)
                    intent.putExtra("subjectCode", itemData.subjectCode)
                    intent.putExtra("subjectUnits", itemData.subjectUnits)
                    intent.putExtra("categoryId", itemData.id)
                    intent.putExtra("categoryName", itemData.categoryName)
                    intent.putExtra("categoryPercentage", itemData.percentage)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ContentRvCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryData = categoryList[position]
        holder.bind(categoryData)
    }
    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun updateCategoryList(categoryList: ArrayList<Category>) {
        this.categoryList = arrayListOf()
        notifyDataSetChanged()
        this.categoryList = categoryList
        this.notifyItemInserted(this.categoryList.size)
    }

    private fun getActivityDetails(categoryId: String): List<ActivityModel>? {
        return database.getAllActivitiesByCategory(categoryId)
    }
}