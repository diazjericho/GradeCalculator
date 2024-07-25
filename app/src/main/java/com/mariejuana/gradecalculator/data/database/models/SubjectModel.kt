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
    var semester: String = ""
    var name: String = ""
    var code: String = ""
    var listCategory: RealmList<CategoryModel> = realmListOf()
}

