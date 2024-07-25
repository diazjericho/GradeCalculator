package com.mariejuana.gradecalculator.data.adapters.year

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class YearLevelAdapter(private var yearLevelList: ArrayList<YearLevel>, private var context: Context, private var yearLevelAdapterCallback: YearLevelAdapterInterface):
    RecyclerView.Adapter<YearLevelAdapter.YearLevelViewHolder>() {
    interface YearLevelAdapterInterface {
        // Add view etc
    }

    inner class YearLevelViewHolder(private val binding: ContentRvYearBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: YearLevel) {
            with(binding) {
                textYearLevel.text = itemData.yearLevel

                val academicYear = "A.Y. ${itemData.academicYear}"
                textYearAcademic.text = String.format(academicYear)
                textYearGrade.text = "N/A"

                cvYear.setOnClickListener {
                    var intent = Intent(context, SemesterScreen::class.java)
                    intent.putExtra("yearLevelId", itemData.id)
                    intent.putExtra("academicYear", academicYear)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearLevelViewHolder {
        val binding = ContentRvYearBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return YearLevelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YearLevelViewHolder, position: Int) {
        val yearLevelData = yearLevelList[position]
        holder.bind(yearLevelData)
    }
    override fun getItemCount(): Int {
        return yearLevelList.size
    }

    fun updateYearLevelList(yearLevelList: ArrayList<YearLevel>) {
        this.yearLevelList = arrayListOf()
        notifyDataSetChanged()
        this.yearLevelList = yearLevelList
        this.notifyItemInserted(this.yearLevelList.size)
    }
}