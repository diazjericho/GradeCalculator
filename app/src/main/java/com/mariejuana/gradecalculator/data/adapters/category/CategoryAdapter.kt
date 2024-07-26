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
                textCategoryName.text = itemData.categoryName

                val percentage = itemData.percentage.toString()
                textCategoryGrade.text = percentage

                val activity: List<ActivityModel>? = getActivityDetails(itemData.id)
                val firstActivity: ActivityModel? = activity?.firstOrNull()

                val subjectName: String = firstActivity?.subjectName ?: "N/A"
                val score: Float = firstActivity?.score ?: 0.0F
                val totalScore: Float = firstActivity?.totalScore ?: 0.0F

                textSubcategoryName.text = subjectName
                textCategoryGrade.text = "$score / $totalScore"
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