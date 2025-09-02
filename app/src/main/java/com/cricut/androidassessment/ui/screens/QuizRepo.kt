package com.cricut.androidassessment.ui.screens

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepo @Inject constructor(): QuizRepoI {
    private val _questions = MutableStateFlow<List<Question>>(emptyList())

    val trueFalseQuestion = TrueFalseQuestion( id = 1, text = "Android is better than iOS", correctAnswer = "True")
    val multipleChoiceQuestion = MultipleChoiceQuestion( id = 2, text = "Which is the best language for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), "Kotlin")
    val multipleSelectionQuestion = MultipleSelectionQuestion( id = 3, text = "Which are the main languages for Android coding", listOf("Kotlin", "Java", "C++", "Javascript"), setOf("Kotlin", "Java"))
    val openEndedQuestion = OpenEndedQuestion( id = 4, text = "Why is Android better than iOS?", "Because")


    init {
        loadInitialQuestions()
    }

    private fun loadInitialQuestions() {
        _questions.value = listOf(
            trueFalseQuestion,
            multipleChoiceQuestion,
            multipleSelectionQuestion,
            openEndedQuestion
        )
    }

    override fun getQuestions(): StateFlow<List<Question>> = _questions.asStateFlow()
}