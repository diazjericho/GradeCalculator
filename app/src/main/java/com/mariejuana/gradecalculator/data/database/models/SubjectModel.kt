package com.mariejuana.gradecalculator.data.database.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class SubjectModel : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var yearLevel: String = ""
    var semesterId: String = ""
    var semesterName: String = ""
    var academicYear: String = ""
    var name: String = ""
    var code: String = ""
    var units: Float = 0.0F
    var listCategory: RealmList<CategoryModel> = realmListOf()
}

