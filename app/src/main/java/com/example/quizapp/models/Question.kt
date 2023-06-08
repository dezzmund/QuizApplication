package com.example.quizapp.models

data class Question(
    val questionId: String = "Not Set",
    val quesDescription: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val correctAns: String = "",
    var userAnswer: String = ""
)
