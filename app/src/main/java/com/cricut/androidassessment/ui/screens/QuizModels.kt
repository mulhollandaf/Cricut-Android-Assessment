package com.cricut.androidassessment.ui.screens

sealed interface Question {
    val id: Int
    val text: String

    val correctAnswer: Any
}

data class TrueFalseQuestion(
    override val id: Int,
    override val text: String,
    override val correctAnswer: String,
    val options: List<String> = listOf("True", "False"),
) : Question

data class MultipleChoiceQuestion(
    override val id: Int,
    override val text: String,
    val options: List<String>,
    override val correctAnswer: String
) : Question

data class MultipleSelectionQuestion(
    override val id: Int,
    override val text: String,
    val options: List<String>,
    override val correctAnswer: Set<String>
) : Question

data class OpenEndedQuestion(
    override val id: Int,
    override val text: String,
    override val correctAnswer: String
) : Question
