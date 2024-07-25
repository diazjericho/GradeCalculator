package com.mariejuana.gradecalculator.data.model

data class Category(
    val id: String,
    val yearLevel: String,
    val semester: String,
    val subject: String,
    val name: String,
    val percentage: Float
)
