package com.cricut.androidassessment.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class QuizViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    var numberOfQuestions: Int = 0

    private val _answers = MutableStateFlow<Map<Int, Any>>(emptyMap())
    val answers: StateFlow<Map<Int, Any>> = _answers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    init {
        QuizRepo().getQuestions().onEach { _questions.value = it }.launchIn(viewModelScope)
        numberOfQuestions = questions.value.size
    }

    private val _score = MutableStateFlow<Int?>(null)
    val score: StateFlow<Int?> = _score.asStateFlow()

    val currentQuestion: Question
        get() = questions.value[currentQuestionIndex.value]

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
        if (_currentQuestionIndex.value < numberOfQuestions - 1) {
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

    fun submitAnswers() {
        // Check to see if the answers submitted are the same as the answers to the questions
        val correctAnswers = _questions.value.associate { it.id to it.correctAnswer }
        var score = 0
        _answers.value.forEach { (questionId, submittedAnswer) ->
            val correctAnswer = correctAnswers[questionId]
            if (correctAnswer != null) {
                if (correctAnswer is Set<*> && submittedAnswer is Set<*>) {
                    if (correctAnswer.containsAll(submittedAnswer) && submittedAnswer.containsAll(correctAnswer)) {
                        score++
                    }
                } else if (correctAnswer == submittedAnswer) {
                    score++
                }
            }
        }
        _score.value = score
    }

    fun restartQuiz() {
        _answers.value = emptyMap()
        _currentQuestionIndex.value = 0
        _score.value = null
    }
}