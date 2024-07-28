package com.mariejuana.gradecalculator.data.adapters.subject

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
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.data.model.YearLevel
import com.mariejuana.gradecalculator.databinding.ContentRvSemesterBinding
import com.mariejuana.gradecalculator.databinding.ContentRvSubjectBinding
import com.mariejuana.gradecalculator.databinding.ContentRvYearBinding
import com.mariejuana.gradecalculator.ui.screens.dialog.update.subject.UpdateSubjectDialog
import com.mariejuana.gradecalculator.ui.screens.main.categories.CategoriesScreen
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen
import com.mariejuana.gradecalculator.ui.screens.main.subjects.SubjectScreen

class SubjectAdapter(private var subjectList: ArrayList<Subject>, private var context: Context, private var subjectAdapterCallback: SubjectAdapterInterface,
                     private val refreshDataInterface: UpdateSubjectDialog.RefreshDataInterface):
    RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    private var database = RealmDatabase()
    private var buttonsVisible = false

    interface SubjectAdapterInterface {
        // Add view etc
    }

    inner class SubjectViewHolder(private val binding: ContentRvSubjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Subject) {
            buttonsVisible = false

            with(binding) {
                buttonsSubjectAction.visibility = View.GONE
                buttonShowCategory.visibility = View.GONE

                textSubjectName.text = itemData.subjectName

                if (itemData.subjectUnits > 1) {
                    textSubjectCode.text = "${itemData.subjectCode} | ${itemData.subjectUnits} units"
                } else {
                    textSubjectCode.text = "${itemData.subjectCode} | ${itemData.subjectUnits} unit"
                }

                cvSubject.setOnClickListener {
                    if (!buttonsVisible) {
                        buttonsSubjectAction.visibility = View.VISIBLE
                        buttonShowCategory.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        buttonsSubjectAction.visibility = View.GONE
                        buttonShowCategory.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                val acquiredPercentageForSubject = database.getAcquiredPercentageForSubject(itemData.id)
                if (acquiredPercentageForSubject.isNaN()) {
                    textSubjectGrade.text = "0% (R)"
                } else {
                    when (acquiredPercentageForSubject) {
                        in 100.01 ..acquiredPercentageForSubject.toDouble() -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (4.0)"
                        in 96.00..100.00 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (4.0)"
                        in 90.00..95.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (3.5)"
                        in 84.00..89.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (3.0)"
                        in 78.00..83.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (2.5)"
                        in 72.00..77.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (2.0)"
                        in 66.00..71.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (1.5)"
                        in 60.00..65.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (1.0)"
                        in 0.00..59.99 -> binding.textSubjectGrade.text = "${String.format("%.2f", acquiredPercentageForSubject)}% (R)"
                        else -> binding.textSubjectGrade.text = "N/A"
                    }
                }

                buttonEditSubject.setOnClickListener {
                    var editSubjectDialog = UpdateSubjectDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("updateSubjectId", itemData.id)
                    bundle.putString("updateSubjectName", itemData.subjectName)
                    bundle.putString("updateSubjectCode", itemData.subjectCode)
                    bundle.putFloat("updateSubjectUnits", itemData.subjectUnits)

                    editSubjectDialog.refreshDataCallback = refreshDataInterface
                    editSubjectDialog.arguments = bundle
                    editSubjectDialog.show(manager, null)
                }

                buttonShowCategory.setOnClickListener {
                    var intent = Intent(context, CategoriesScreen::class.java)
                    intent.putExtra("semesterId", itemData.semesterId)
                    intent.putExtra("semesterName", itemData.semesterName)
                    intent.putExtra("yearLevelId", itemData.yearLevel)
                    intent.putExtra("academicYear", itemData.academicYear)
                    intent.putExtra("subjectId", itemData.id)
                    intent.putExtra("subjectName", itemData.subjectName)
                    intent.putExtra("subjectCode", itemData.subjectCode)
                    intent.putExtra("subjectUnits", itemData.subjectUnits)
                    context.startActivity(intent)
                }
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