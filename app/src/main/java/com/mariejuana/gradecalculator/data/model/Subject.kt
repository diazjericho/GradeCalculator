package com.mariejuana.gradecalculator.data.model

data class Subject(
    val id: String,
    val yearLevel: String,
    val semesterId: String,
    val semesterName: String,
    val academicYear: String,
    val subjectName: String,
    val subjectCode: String,
    val subjectUnits: Float
)
