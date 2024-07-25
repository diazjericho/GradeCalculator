package com.mariejuana.gradecalculator.data.model

data class Activity(
    val id: String,
    val yearLevel: String,
    val semester: String,
    val subject: String,
    val category: String,
    val name: String,
    val score: Float,
    val totalScore: Float
)
