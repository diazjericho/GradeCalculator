package com.mariejuana.gradecalculator.data.adapters.year

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.year.DeleteYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.year.UpdateYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class YearLevelAdapter(private var yearLevelList: ArrayList<YearLevel>,
                       private var context: Context,
                       private var yearLevelAdapterCallback: YearLevelAdapterInterface,
                       private val refreshDataInterface: UpdateYearLevelDialog.RefreshDataInterface,
                       private val refreshDataInterfaceDelete: DeleteYearLevelDialog.RefreshDataInterface):
    RecyclerView.Adapter<YearLevelAdapter.YearLevelViewHolder>() {
    private var database = RealmDatabase()
    private var buttonsVisible = false
    private var sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    interface YearLevelAdapterInterface {
        // Add view etc
    }

    inner class YearLevelViewHolder(private val binding: ContentRvYearBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: YearLevel) {
            buttonsVisible = false
            binding.buttonsYearLevelAction.visibility = View.GONE
            binding.buttonShowSemester.visibility = View.GONE

            with(binding) {
                val academicYear = "A.Y. ${itemData.academicYear}"
                val totalGradeForYearLevel = database.getAcquiredPercentageForYearLevel(itemData.id)

                textYearLevel.text = itemData.yearLevel
                textYearAcademic.text = String.format(academicYear)

                // Show or hide the grades
                val disableFinalGrade = sharedPreferences.getBoolean("disableFinalGrade", false)

                if (disableFinalGrade) {
                    textYearGrade.visibility = View.GONE
                } else {
                    textYearGrade.visibility = View.VISIBLE

                    if (totalGradeForYearLevel.isNaN()) {
                        textYearGrade.text = "0% (R)"
                    } else {
                        when (totalGradeForYearLevel) {
                            in 100.01 ..totalGradeForYearLevel.toDouble() -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (4.0)"
                            in 96.00..100.00 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (4.0)"
                            in 90.00..95.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (3.5)"
                            in 84.00..89.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (3.0)"
                            in 78.00..83.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (2.5)"
                            in 72.00..77.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (2.0)"
                            in 66.00..71.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (1.5)"
                            in 60.00..65.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (1.0)"
                            in 0.00..59.99 -> binding.textYearGrade.text = "${String.format("%.2f", totalGradeForYearLevel)}% (R)"
                            else -> binding.textYearGrade.text = "N/A"
                        }
                    }
                }

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
                    intent.putExtra("yearLevelName", itemData.yearLevel)
                    intent.putExtra("academicYear", academicYear)
                    context.startActivity(intent)
                }

                buttonRemoveYear.setOnClickListener {
                    val deleteYearDialog = DeleteYearLevelDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("deleteYearLevelId", itemData.id)
                    bundle.putString("deleteYearLevelName", itemData.yearLevel)
                    bundle.putString("deleteYearLevelAcademicYear", itemData.academicYear)

                    deleteYearDialog.refreshDataCallback = refreshDataInterfaceDelete
                    deleteYearDialog.arguments = bundle
                    deleteYearDialog.show(manager, null)
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