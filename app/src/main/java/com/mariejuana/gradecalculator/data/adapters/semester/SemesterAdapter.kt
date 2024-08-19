package com.mariejuana.gradecalculator.data.adapters.semester

import android.app.AlertDialog
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
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.semester.DeleteSemesterDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.year.DeleteYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.semester.UpdateSemesterDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.year.UpdateYearLevelDialog
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen
import com.mariejuana.gradecalculator.ui.screens.main.subjects.SubjectScreen

class SemesterAdapter(private var semesterList: ArrayList<Semester>,
                      private var context: Context,
                      private var semesterAdapterCallback: SemesterAdapterInterface,
                      private val refreshDataInterface: UpdateSemesterDialog.RefreshDataInterface,
                      private val refreshDataInterfaceDelete: DeleteSemesterDialog.RefreshDataInterface  ):
    RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder>() {
    private var database = RealmDatabase()
    private var buttonsVisible = false
    private var sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    interface SemesterAdapterInterface {
        // Add view etc
    }

    inner class SemesterViewHolder(private val binding: ContentRvSemesterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Semester) {
            buttonsVisible = false
            binding.buttonsSemesterAction.visibility = View.GONE
            binding.buttonShowSubject.visibility = View.GONE

            with(binding) {
                val academicYear = "${itemData.academicYear}"
                val totalGradeForSemester = database.getAcquiredPercentageForSemester(itemData.id)

                val disableFinalGrade = sharedPreferences.getBoolean("disableFinalGrade", false)

                if (disableFinalGrade) {
                    textSemesterGrade.visibility = View.GONE
                } else {
                    textSemesterGrade.visibility = View.VISIBLE

                    if (totalGradeForSemester.isNaN()) {
                        textSemesterGrade.text = "0% (R)"
                    } else {
                        when (totalGradeForSemester) {
                            in 100.01 ..totalGradeForSemester.toDouble() -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (4.0)"
                            in 96.00..100.00 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (4.0)"
                            in 90.00..95.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (3.5)"
                            in 84.00..89.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (3.0)"
                            in 78.00..83.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (2.5)"
                            in 72.00..77.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (2.0)"
                            in 66.00..71.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (1.5)"
                            in 60.00..65.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (1.0)"
                            in 0.00..59.99 -> binding.textSemesterGrade.text = "${String.format("%.2f", totalGradeForSemester)}% (R)"
                            else -> binding.textSemesterGrade.text = "N/A"
                        }
                    }
                }

                textSemesterLevel.text = itemData.semester
                textYearAcademic.text = itemData.academicYear

                cvSemester.setOnClickListener {
                    if (!buttonsVisible) {
                        buttonsSemesterAction.visibility = View.VISIBLE
                        buttonShowSubject.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        buttonsSemesterAction.visibility = View.GONE
                        buttonShowSubject.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                buttonEditSemester.setOnClickListener {
                    var editSemesterDialog = UpdateSemesterDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("updateYearLevelId", itemData.yearLevel)
                    bundle.putString("updateSemesterId", itemData.id)
                    bundle.putString("updateSemesterName", itemData.semester)

                    editSemesterDialog.refreshDataCallback = refreshDataInterface
                    editSemesterDialog.arguments = bundle
                    editSemesterDialog.show(manager, null)
                }

                buttonRemoveSemester.setOnClickListener {
                    var editSemesterDialog = DeleteSemesterDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("deleteYearLevelId", itemData.yearLevel)
                    bundle.putString("deleteSemesterId", itemData.id)
                    bundle.putString("deleteSemesterName", itemData.semester)

                    editSemesterDialog.refreshDataCallback = refreshDataInterfaceDelete
                    editSemesterDialog.arguments = bundle
                    editSemesterDialog.show(manager, null)
                }

                buttonShowSubject.setOnClickListener {
                    var intent = Intent(context, SubjectScreen::class.java)
                    intent.putExtra("semesterId", itemData.id)
                    intent.putExtra("semesterName", itemData.semester)
                    intent.putExtra("yearLevelId", itemData.yearLevel)
                    intent.putExtra("academicYear", academicYear)
                    context.startActivity(intent)
                }
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