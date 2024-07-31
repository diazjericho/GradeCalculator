package com.mariejuana.gradecalculator.data.adapters.activity

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
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.activity.DeleteActivityDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.delete.category.DeleteCategoryDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.activity.UpdateActivityDialog
import com.mariejuana.gradecalculator.ui.screens.dialog.update.category.UpdateCategoryDialog
import com.mariejuana.gradecalculator.ui.screens.main.semester.SemesterScreen

class ActivityAdapter(private var activityList: ArrayList<Activity>,
                      private var context: Context,
                      private var activityAdapterCallback: ActivityAdapterInterface,
                      private val refreshDataInterface: UpdateActivityDialog.RefreshDataInterface,
                      private val refreshDataInterfaceDelete: DeleteActivityDialog.RefreshDataInterface):
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {
    private var database = RealmDatabase()
    private var buttonsVisible = false

    interface ActivityAdapterInterface {
        // Add view etc
    }

    inner class ActivityViewHolder(private val binding: ContentRvActivityBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(itemData: Activity) {
            buttonsVisible = false

            with(binding) {
                buttonsActivityAction.visibility = View.GONE

                textSubcategoryName.text = itemData.activityName

                val totalScore = "${itemData.score} / ${itemData.totalScore}"
                textSubcategoryGrade.text = totalScore

                cvActivity.setOnClickListener {
                    if (!buttonsVisible) {
                        buttonsActivityAction.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        buttonsActivityAction.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                buttonEditActivity.setOnClickListener {
                    var editActivityDialog = UpdateActivityDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("updateCategoryId", itemData.categoryId)
                    bundle.putString("updateActivityId", itemData.id)
                    bundle.putString("updateActivityName", itemData.activityName)
                    bundle.putFloat("updateActivityScore", itemData.score)
                    bundle.putFloat("updateActivityTotalScore", itemData.totalScore)

                    editActivityDialog.refreshDataCallback = refreshDataInterface
                    editActivityDialog.arguments = bundle
                    editActivityDialog.show(manager, null)
                }

                buttonRemoveActivity.setOnClickListener {
                    var deleteActivityDialog = DeleteActivityDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("deleteCategoryId", itemData.categoryId)
                    bundle.putString("deleteActivityId", itemData.id)
                    bundle.putString("deleteActivityName", itemData.activityName)

                    deleteActivityDialog.refreshDataCallback = refreshDataInterfaceDelete
                    deleteActivityDialog.arguments = bundle
                    deleteActivityDialog.show(manager, null)
                }
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