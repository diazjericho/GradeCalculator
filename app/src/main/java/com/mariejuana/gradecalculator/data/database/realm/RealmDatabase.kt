package com.mariejuana.gradecalculator.data.database.realm

import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.models.SemesterModel
import com.mariejuana.gradecalculator.data.database.models.SubjectModel
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.data.model.YearLevel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.lang.IllegalStateException
import java.time.Year

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
        val year = realm.query<YearLevelModel>("id == $0", ObjectId(id)).first().find()
        return year?.listSemesterModel
    }

    // Get all subject by semester
    fun getAllSubjectBySemester(id: String): List<SubjectModel>? {
        val semester = realm.query<SemesterModel>("id == $0", ObjectId(id)).first().find()
        return semester?.listSubject
    }

    // Get all category by subject
    fun getAllCategoryBySubject(id: String): List<CategoryModel>? {
        val subject = realm.query<SubjectModel>("id == $0", ObjectId(id)).first().find()
        return subject?.listCategory
    }

    // Get all activities by category
    fun getAllActivitiesByCategory(id: String): List<ActivityModel>? {
        val category = realm.query<CategoryModel>("id == $0", ObjectId(id)).first().find()
        return category?.listActivity
    }

    suspend fun addYearLevel(yearLevel: String, academicYear: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelDetails = YearLevelModel().apply {
                    this.yearLevel = yearLevel
                    this.academicYear = academicYear
                }

                copyToRealm(yearLevelDetails)
            }
        }
    }

    suspend fun addSemester(yearLevel: String, semesterName: String, academicYear: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelResult: YearLevelModel? = realm.query<YearLevelModel>("id == $0", ObjectId(yearLevel)).first().find()

                if (yearLevelResult != null) {
                    val semesterDetails = SemesterModel().apply {
                        this.yearLevel = yearLevel
                        this.semester = semesterName
                        this.academicYear = academicYear
                    }

                    val saveSemesterDetails = copyToRealm(semesterDetails)
                    findLatest(yearLevelResult)?.listSemesterModel?.add(saveSemesterDetails)
                }
            }
        }
    }

    suspend fun addSubject(yearLevel: String, semesterName: String, semesterId: String, academicYear: String,
                           subjectName: String, subjectCode: String, subjectUnits: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val semesterResult: SemesterModel? = realm.query<SemesterModel>("id == $0", ObjectId(semesterId)).first().find()

                if (semesterResult != null) {
                    val subjectDetails = SubjectModel().apply {
                        this.yearLevel = yearLevel
                        this.semesterName = semesterName
                        this.semesterId = semesterId
                        this.academicYear = academicYear
                        this.name = subjectName
                        this.code = subjectCode
                        this.units = subjectUnits
                    }

                    val saveSubjectDetails = copyToRealm(subjectDetails)
                    findLatest(semesterResult)?.listSubject?.add(saveSubjectDetails)
                }
            }
        }
    }

    suspend fun addCategory(yearLevel: String, semesterName: String, semesterId: String, academicYear: String,
                           subjectName: String, subjectCode: String, subjectUnits: Float,
                            categoryName: String, percentage: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(semesterName)).first().find()

                if (subjectResult != null) {
                    val categoryDetails = CategoryModel().apply {
                        this.yearLevel = yearLevel
                        this.semesterName = semesterName
                        this.semesterId = semesterId
                        this.academicYear = academicYear
                        this.subjectName = subjectName
                        this.subjectCode = subjectCode
                        this.subjectUnits = subjectUnits
                        this.categoryName = categoryName
                        this.percentage = percentage
                    }

                    val saveCategoryDetails = copyToRealm(categoryDetails)
                    findLatest(subjectResult)?.listCategory?.add(saveCategoryDetails)
                }
            }
        }
    }

    suspend fun addActivity(yearLevel: String, semesterName: String, semesterId: String, academicYear: String,
                            subjectName: String, subjectCode: String, subjectUnits: Float,
                            categoryName: String, percentage: Float,
                            activityName: String, score: Float, totalScore: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(semesterName)).first().find()

                if (categoryResult != null) {
                    val activityDetails = ActivityModel().apply {
                        this.yearLevel = yearLevel
                        this.semesterName = semesterName
                        this.semesterId = semesterId
                        this.academicYear = academicYear
                        this.subjectName = subjectName
                        this.subjectCode = subjectCode
                        this.subjectUnits = subjectUnits
                        this.categoryName = categoryName
                        this.percentage = percentage
                        this.activityName = activityName
                        this.score = score
                        this.totalScore = totalScore
                    }

                    val saveActivityDetails = copyToRealm(activityDetails)
                    findLatest(categoryResult)?.listActivity?.add(saveActivityDetails)
                }
            }
        }
    }
}