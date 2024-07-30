package com.mariejuana.gradecalculator.data.database.realm

import com.mariejuana.gradecalculator.data.database.models.ActivityModel
import com.mariejuana.gradecalculator.data.database.models.CategoryModel
import com.mariejuana.gradecalculator.data.database.models.SemesterModel
import com.mariejuana.gradecalculator.data.database.models.SubjectModel
import com.mariejuana.gradecalculator.data.database.models.YearLevelModel
import com.mariejuana.gradecalculator.data.model.Activity
import com.mariejuana.gradecalculator.data.model.Category
import com.mariejuana.gradecalculator.data.model.Semester
import com.mariejuana.gradecalculator.data.model.Subject
import com.mariejuana.gradecalculator.data.model.YearLevel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId.Companion.invoke
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

    fun getAcquiredScoreForSubject(subjectId: String): Float {
        return getAllCategoryBySubject(subjectId)?.sumByDouble { category ->
            category.listActivity.sumByDouble { it.score.toDouble() }
        }?.toFloat() ?: 0.0F
    }


    fun getTotalScoreForSubject(subjectId: String): Float {
        return getAllCategoryBySubject(subjectId)?.sumByDouble { category ->
            category.listActivity.sumByDouble { it.totalScore.toDouble() }
        }?.toFloat() ?: 0.0F
    }

    fun getTotalScoreForCategory(category: CategoryModel): Float {
        return category.listActivity.sumByDouble { it.totalScore.toDouble() }.toFloat()
    }

    fun getAcquiredPercentageForSubject(subjectId: String): Float {
        val acquiredScore = getAcquiredScoreForSubject(subjectId)
        val totalScore = getTotalScoreForSubject(subjectId)
        return (acquiredScore / totalScore) * 100.0F
    }

    fun getTotalPercentageForSubject(subjectId: String): Float {
        return getAllCategoryBySubject(subjectId)?.sumByDouble { it.percentage.toDouble() }?.toFloat()
            ?: 0.0F
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
                           subjectId: String, subjectName: String, subjectCode: String, subjectUnits: Float,
                            categoryName: String, percentage: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(subjectId)).first().find()

                if (subjectResult != null) {
                    val categoryDetails = CategoryModel().apply {
                        this.yearLevel = yearLevel
                        this.semesterName = semesterName
                        this.semesterId = semesterId
                        this.academicYear = academicYear
                        this.subjectId = subjectId
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
                            subjectId: String, subjectName: String, subjectCode: String, subjectUnits: Float,
                            categoryId: String, categoryName: String, percentage: Float,
                            activityName: String, score: Float, totalScore: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(categoryId)).first().find()

                if (categoryResult != null) {
                    val activityDetails = ActivityModel().apply {
                        this.yearLevel = yearLevel
                        this.semesterName = semesterName
                        this.semesterId = semesterId
                        this.academicYear = academicYear
                        this.subjectId = subjectId
                        this.subjectName = subjectName
                        this.subjectCode = subjectCode
                        this.subjectUnits = subjectUnits
                        this.categoryId = categoryId
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

    suspend fun updateYear(yearLevelId: String, yearLevelName: String, academicYear: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelResult: YearLevelModel? = realm.query<YearLevelModel>("id == $0", ObjectId(yearLevelId)).first().find()

                if (yearLevelResult != null) {
                    val yearLevelExisting = findLatest(yearLevelResult)

                    yearLevelExisting?.apply {
                        this.yearLevel = yearLevelName
                        this.academicYear = academicYear
                    }
                }
            }
        }
    }

    suspend fun updateSemester(yearLevelId: String,
                               semesterId: String,
                               semesterName: String,
                               academicYear: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelResult: YearLevelModel? = realm.query<YearLevelModel>("id == $0", ObjectId(yearLevelId)).first().find()

                if (yearLevelResult != null) {
                    val semesterResult: SemesterModel? = realm.query<SemesterModel>("id == $0", ObjectId(semesterId)).first().find()

                    if (semesterResult != null) {
                        val semesterExisting = findLatest(semesterResult)

                        semesterExisting?.apply {
                            this.semester = semesterName
                            this.academicYear = academicYear
                        }
                    }
                }
            }
        }
    }

    suspend fun updateSubject(semesterId: String,
                              subjectId: String,
                              subjectName: String,
                              subjectCode: String,
                              subjectUnits: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val semesterResult: SemesterModel? = realm.query<SemesterModel>("id == $0", ObjectId(semesterId)).first().find()

                if (semesterResult != null) {
                    val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(subjectId)).first().find()

                    if (subjectResult != null) {
                        val subjectExisting = findLatest(subjectResult)

                        subjectExisting?.apply {
                            this.name = subjectName
                            this.code = subjectCode
                            this.units = subjectUnits
                        }
                    }
                }
            }
        }
    }

    suspend fun updateCategory(subjectId: String,
                               categoryId: String,
                               categoryName: String,
                               percentage: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(subjectId)).first().find()

                if (subjectResult != null) {
                    val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(categoryId)).first().find()

                    if (categoryResult != null) {
                        val categoryExisting = findLatest(categoryResult)

                        categoryExisting?.apply {
                            this.categoryName = categoryName
                            this.percentage = percentage
                        }
                    }
                }
            }
        }
    }

    suspend fun updateActivity(categoryId: String,
                               activityId: String,
                               activityName: String,
                               activityScore: Float,
                               activityTotalScore: Float) {
        withContext(Dispatchers.IO) {
            realm.write {
                val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(categoryId)).first().find()

                if (categoryResult != null) {
                    val activityResult: ActivityModel? = realm.query<ActivityModel>("id == $0", ObjectId(activityId)).first().find()

                    if (activityResult != null) {
                        val activityExisting = findLatest(activityResult)

                        activityExisting?.apply {
                            this.activityName = activityName
                            this.score = activityScore
                            this.totalScore = activityTotalScore
                        }
                    }
                }
            }
        }
    }

    suspend fun deleteYear(year: YearLevel) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelResult: YearLevelModel? = realm.query<YearLevelModel>("id == $0", ObjectId(year.id)).first().find()

                if (yearLevelResult != null) {
                    query<CategoryModel>("id == $0", ObjectId(year.id))
                        .first()
                        .find()
                        ?.let { delete(it) }
                        ?: throw IllegalStateException("Year not found")
                }
            }
        }
    }

    suspend fun deleteSemester(yearLevelId: String, semester: Semester) {
        withContext(Dispatchers.IO) {
            realm.write {
                val yearLevelResult: YearLevelModel? = realm.query<YearLevelModel>("id == $0", ObjectId(yearLevelId)).first().find()

                if (yearLevelResult != null) {
                    val semesterResult: SemesterModel? = realm.query<SemesterModel>("id == $0", ObjectId(semester.id)).first().find()

                    if (semesterResult != null) {
                        val semesterExisting = findLatest(semesterResult)
                        findLatest(yearLevelResult)?.listSemesterModel?.remove(semesterExisting!!)

                        query<CategoryModel>("id == $0", ObjectId(semester.id))
                            .first()
                            .find()
                            ?.let { delete(it) }
                            ?: throw IllegalStateException("Semester not found")
                    }
                }
            }
        }
    }

    suspend fun deleteSubject(semesterId: String, subject: Subject) {
        withContext(Dispatchers.IO) {
            realm.write {
                val semesterResult: SemesterModel? = realm.query<SemesterModel>("id == $0", ObjectId(semesterId)).first().find()

                if (semesterResult != null) {
                    val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(subject.id)).first().find()

                    if (subjectResult != null) {
                        val subjectExisting = findLatest(subjectResult)
                        findLatest(semesterResult)?.listSubject?.remove(subjectExisting!!)

                        query<CategoryModel>("id == $0", ObjectId(subject.id))
                            .first()
                            .find()
                            ?.let { delete(it) }
                            ?: throw IllegalStateException("Subject not found")
                    }
                }
            }
        }
    }

    suspend fun deleteCategory(subjectId: String, category: Category) {
        withContext(Dispatchers.IO) {
            realm.write {
                val subjectResult: SubjectModel? = realm.query<SubjectModel>("id == $0", ObjectId(subjectId)).first().find()

                if (subjectResult != null) {
                    val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(category.id)).first().find()

                    if (categoryResult != null) {
                        val categoryExisting = findLatest(categoryResult)
                        findLatest(subjectResult)?.listCategory?.remove(categoryExisting!!)

                        query<CategoryModel>("id == $0", ObjectId(category.id))
                            .first()
                            .find()
                            ?.let { delete(it) }
                            ?: throw IllegalStateException("Category not found")
                    }
                }
            }
        }
    }

    suspend fun deleteActivity(categoryId: String, activity: Activity) {
        withContext(Dispatchers.IO) {
            realm.write {
                val categoryResult: CategoryModel? = realm.query<CategoryModel>("id == $0", ObjectId(categoryId)).first().find()

                if (categoryResult != null) {
                    val activityResult: ActivityModel? = realm.query<ActivityModel>("id == $0", ObjectId(activity.id)).first().find()

                    if (activityResult != null) {
                        val activityExisting = findLatest(activityResult)
                        findLatest(categoryResult)?.listActivity?.remove(activityExisting!!)

                        query<ActivityModel>("id == $0", ObjectId(activity.id))
                            .first()
                            .find()
                            ?.let { delete(it) }
                            ?: throw IllegalStateException("Activity not found")
                    }
                }
            }
        }
    }
}