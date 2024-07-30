package com.mariejuana.gradecalculator.data.adapters.year

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.update.year.UpdateYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class YearLevelAdapter(private var yearLevelList: ArrayList<YearLevel>,
                       private var context: Context,
                       private var yearLevelAdapterCallback: YearLevelAdapterInterface,
                       private val refreshDataInterface: UpdateYearLevelDialog.RefreshDataInterface):
    RecyclerView.Adapter<YearLevelAdapter.YearLevelViewHolder>() {
    private var database = RealmDatabase()
    private var buttonsVisible = false

    interface YearLevelAdapterInterface {
        // Add view etc
    }

    inner class YearLevelViewHolder(private val binding: ContentRvYearBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: YearLevel) {
            buttonsVisible = false
            binding.buttonsYearLevelAction.visibility = View.GONE
            binding.buttonShowSemester.visibility = View.GONE

            with(binding) {
                textYearLevel.text = itemData.yearLevel

                val academicYear = "A.Y. ${itemData.academicYear}"
                textYearAcademic.text = String.format(academicYear)
                textYearGrade.text = "N/A"

                cvYear.setOnClickListener {
                    if (!buttonsVisible) {
                        buttonsYearLevelAction.visibility = View.VISIBLE
                        buttonShowSemester.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        buttonsYearLevelAction.visibility = View.GONE
                        buttonShowSemester.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                buttonEditYear.setOnClickListener {
                    var editYearDialog = UpdateYearLevelDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("updateYearLevelId", itemData.id)
                    bundle.putString("updateYearLevelName", itemData.yearLevel)
                    bundle.putString("updateYearLevelAcademicYear", itemData.academicYear)

                    editYearDialog.refreshDataCallback = refreshDataInterface
                    editYearDialog.arguments = bundle
                    editYearDialog.show(manager, null)
                }

                buttonShowSemester.setOnClickListener {
                    var intent = Intent(context, SemesterScreen::class.java)
                    intent.putExtra("yearLevelId", itemData.id)
                    intent.putExtra("academicYear", academicYear)
                    context.startActivity(intent)
                }

                buttonRemoveYear.setOnClickListener {

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