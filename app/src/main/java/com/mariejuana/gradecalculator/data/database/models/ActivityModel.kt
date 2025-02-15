package com.mariejuana.gradecalculator.data.database.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ActivityModel : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var yearLevel: String = ""
    var semesterId: String = ""
    var semesterName: String = ""
    var academicYear: String = ""
    var subjectId: String = ""
    var subjectName: String = ""
    var subjectCode: String = ""
    var subjectUnits: Float = 0.0F
    var categoryId: String = ""
    var categoryName: String = ""
    var percentage: Float = 0.0F
    var activityName: String = ""
    var score: Float = 0.0F
    var totalScore: Float = 0.0F
}
