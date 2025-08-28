package com.cricut.androidassessment.ui.screens

class QuizRepo {
    val trueFalseQuestion = TrueFalseQuestion( id = "1", text = "Android is better than iOS", true)
    val multipleChoiceQuestion = MultipleChoiceQuestion( id = "2", text = "Which is the best language for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), 0)
    val multipleSelectionQuestion = MultipleSelectionQuestion( id = "3", text = "Which are the main languages for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), setOf(0, 1))
    val openEndedQuestion = OpenEndedQuestion( id = "4", text = "Why is Android better than iOS?")
}