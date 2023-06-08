package com.example.quizapp.models

data class Quiz(
    val id: String = "",
    val title: String = "",
    val quizDescription: String = "",
    val questions: MutableMap<String, Question> = mutableMapOf()
)
