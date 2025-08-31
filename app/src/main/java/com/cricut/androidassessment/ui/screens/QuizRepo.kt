package com.cricut.androidassessment.ui.screens

class QuizRepo {
    val trueFalseQuestion = TrueFalseQuestion( id = 1, text = "Android is better than iOS", correctAnswer = "True")
    val multipleChoiceQuestion = MultipleChoiceQuestion( id = 2, text = "Which is the best language for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), "Kotlin")
    val multipleSelectionQuestion = MultipleSelectionQuestion( id = 3, text = "Which are the main languages for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), setOf("Kotlin", "Java"))
    val openEndedQuestion = OpenEndedQuestion( id = 4, text = "Why is Android better than iOS?", "Because")

    val questions = listOf(trueFalseQuestion, multipleChoiceQuestion, multipleSelectionQuestion, openEndedQuestion)
}