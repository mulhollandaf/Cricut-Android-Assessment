package com.cricut.androidassessment.ui.screens

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class QuizRepo {
    val trueFalseQuestion = TrueFalseQuestion( id = 1, text = "Android is better than iOS", correctAnswer = "True")
    val multipleChoiceQuestion = MultipleChoiceQuestion( id = 2, text = "Which is the best language for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), "Kotlin")
    val multipleSelectionQuestion = MultipleSelectionQuestion( id = 3, text = "Which are the main languages for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), setOf("Kotlin", "Java"))
    val openEndedQuestion = OpenEndedQuestion( id = 4, text = "Why is Android better than iOS?", "Because")

    fun getQuestions(): Flow<List<Question>> {
        return flowOf(listOf(trueFalseQuestion, multipleChoiceQuestion, multipleSelectionQuestion, openEndedQuestion))
    }
}