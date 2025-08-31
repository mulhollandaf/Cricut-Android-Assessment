package com.cricut.androidassessment.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// ViewModel to hold the state
class QuizViewModel : ViewModel() {
    private val _questions = QuizRepo().questions
    val questions: List<Question> = _questions

    private val _answers = MutableStateFlow<Map<Int, Any>>(emptyMap())
    val answers: StateFlow<Map<Int, Any>> = _answers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    val currentQuestion: Question
        get() = questions[currentQuestionIndex.value]

    fun onAnswerChanged(questionId: Int, answer: String) {
        _answers.update { currentAnswers ->
            currentAnswers.toMutableMap().apply {
                this[questionId] = answer
            }
        }
    }

    fun onMultipleAnswersChanged(questionId: Int, option: String) {
        _answers.update { currentAnswers ->
            val currentSelectedOptions = (currentAnswers[questionId] as? Set<*> ?: emptySet<String>()).toMutableSet()
            val newAnswers = currentAnswers.toMutableMap()

            if (currentSelectedOptions.contains(option)) {
                currentSelectedOptions.remove(option)
            } else {
                currentSelectedOptions.add(option)
            }
            newAnswers[questionId] = currentSelectedOptions
            newAnswers
        }
    }

    fun onNextClicked() {
        if (_currentQuestionIndex.value < questions.size - 1) {
            _currentQuestionIndex.update { it + 1 }
        } else {
            // Handle form submission or completion
            println("Form submitted: ${answers.value}")
        }
    }

    fun onPreviousClicked() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.update { it - 1 }
        }
    }
}