package com.mariejuana.gradecalculator.data.adapters.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.model.Activity
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvActivityBinding
import com.mariejuana.gradecalculator.databinding.ContentRvCategoryBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSubjectBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class ActivityAdapter(private var activityList: ArrayList<Activity>, private var context: Context, private var activityAdapterCallback: ActivityAdapterInterface):
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    interface ActivityAdapterInterface {
        // Add view etc
    }

    inner class ActivityViewHolder(private val binding: ContentRvActivityBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Activity) {
            with(binding) {
                textSubcategoryName.text = itemData.activityName

                val totalScore = "${itemData.score} / ${itemData.totalScore}"
                textSubcategoryGrade.text = totalScore
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ContentRvActivityBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activityData = activityList[position]
        holder.bind(activityData)
    }
    override fun getItemCount(): Int {
        return activityList.size
    }

    fun updateActivityList(activityList: ArrayList<Activity>) {
        this.activityList = arrayListOf()
        notifyDataSetChanged()
        this.activityList = activityList
        this.notifyItemInserted(this.activityList.size)
    }
}