package com.mariejuana.gradecalculator.data.database.realm

import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.models.SemesterModel
import com.mariejuana.gradecalculator.data.database.models.SubjectModel
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.data.model.Category
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.lang.IllegalStateException

class RealmDatabase {
    // Setup the database
    private val realm : Realm by lazy {
        val config = RealmConfiguration
            .Builder(schema =  setOf(YearLevelModel::class, SemesterModel::class, SubjectModel::class, CategoryModel::class, ActivityModel::class))
            .schemaVersion(1)
            .build()
        Realm.open(config)
    }

    // Get all year level
    fun getAllYears(): List<YearLevelModel> {
        return realm.query<YearLevelModel>().find()
    }

    // Get all semester by year
    fun getAllSemesterByYear(id: String): List<SemesterModel>? {
        val year = realm.query<YearLevelModel>("id == $0", id).first().find()
        return year?.listSemesterModel
    }

    // Get all subject by semester
    fun getAllSubjectBySemester(id: String): List<SubjectModel>? {
        val semester = realm.query<SemesterModel>("id == $0", id).first().find()
        return semester?.listSubject
    }

    // Get all category by subject
    fun getAllCategoryBySubject(id: String): List<CategoryModel>? {
        val subject = realm.query<SubjectModel>("id == $0", id).first().find()
        return subject?.listCategory
    }

    // Get all activities by category
    fun getAllActivitiesByCategory(id: String): List<ActivityModel>? {
        val category = realm.query<CategoryModel>("id == $0", id).first().find()
        return category?.listActivity
    }
}