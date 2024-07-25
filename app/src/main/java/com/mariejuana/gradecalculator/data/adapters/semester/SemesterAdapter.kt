package com.mariejuana.gradecalculator.data.adapters.semester

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class SemesterAdapter(private var semesterList: ArrayList<Semester>, private var context: Context, private var semesterAdapterCallback: SemesterAdapterInterface):
    RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder>() {

    interface SemesterAdapterInterface {
        // Add view etc
    }

    inner class SemesterViewHolder(private val binding: ContentRvSemesterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Semester) {
            with(binding) {
                textSemesterLevel.text = itemData.semester
                textYearAcademic.text = itemData.academicYear
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterViewHolder {
        val binding = ContentRvSemesterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SemesterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SemesterViewHolder, position: Int) {
        val semesterData = semesterList[position]
        holder.bind(semesterData)
    }
    override fun getItemCount(): Int {
        return semesterList.size
    }

    fun updateSemesterList(semesterList: ArrayList<Semester>) {
        this.semesterList = arrayListOf()
        notifyDataSetChanged()
        this.semesterList = semesterList
        this.notifyItemInserted(this.semesterList.size)
    }
}