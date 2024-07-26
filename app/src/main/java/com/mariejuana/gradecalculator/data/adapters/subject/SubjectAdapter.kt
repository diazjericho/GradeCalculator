package com.mariejuana.gradecalculator.data.adapters.subject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSubjectBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class SubjectAdapter(private var subjectList: ArrayList<Subject>, private var context: Context, private var subjectAdapterCallback: SubjectAdapterInterface):
    RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    interface SubjectAdapterInterface {
        // Add view etc
    }

    inner class SubjectViewHolder(private val binding: ContentRvSubjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Subject) {
            with(binding) {
                textSubjectName.text = itemData.subjectName
                textSubjectCode.text = "${itemData.subjectCode} / ${itemData.subjectUnits}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ContentRvSubjectBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subjectData = subjectList[position]
        holder.bind(subjectData)
    }
    override fun getItemCount(): Int {
        return subjectList.size
    }

    fun updateSubjectList(subjectList: ArrayList<Subject>) {
        this.subjectList = arrayListOf()
        notifyDataSetChanged()
        this.subjectList = subjectList
        this.notifyItemInserted(this.subjectList.size)
    }
}