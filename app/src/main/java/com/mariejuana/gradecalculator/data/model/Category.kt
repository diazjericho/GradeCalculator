package com.mariejuana.gradecalculator.data.model

data class Category(
    val id: String,
    val yearLevel: String,
    val semesterId: String,
    val semesterName: String,
    val academicYear: String,
    val subjectId: String,
    val subjectName: String,
    val subjectCode: String,
    val subjectUnits: Float,
    val categoryName: String,
    val percentage: Float
)
