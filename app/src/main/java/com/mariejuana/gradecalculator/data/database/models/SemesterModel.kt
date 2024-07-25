package com.mariejuana.gradecalculator.data.database.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class SemesterModel : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var yearLevel: String = ""
    var semester: String = ""
    var academicYear: String = ""
    var listSubject: RealmList<SubjectModel> = realmListOf()
}

