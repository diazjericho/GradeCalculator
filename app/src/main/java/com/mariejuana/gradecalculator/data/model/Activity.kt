package com.mariejuana.gradecalculator.data.model

data class Activity(
    val id: String,
    val yearLevel: String,
    val academicYear: String,
    val semesterId: String,
    val semesterName: String,
    val subjectId: String,
    val subjectName: String,
    val subjectCode: String,
    val subjectUnits: Float,
    val categoryId: String,
    val categoryName: String,
    val percentage: Float,
    val activityName: String,
    val score: Float,
    val totalScore: Float
)
