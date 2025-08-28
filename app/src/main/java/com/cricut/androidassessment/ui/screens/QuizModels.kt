package com.cricut.androidassessment.ui.screens

sealed interface Question {
    val id: String
    val text: String
}

data class TrueFalseQuestion(
    override val id: String,
    override val text: String,
    val correctAnswer: Boolean // Though validation is not required, storing it might be useful
) : Question

data class MultipleChoiceQuestion(
    override val id: String,
    override val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int // Though validation is not required
) : Question

data class MultipleSelectionQuestion(
 override val id: String,
 override val text: String,
     val options: List<String>,
     val correctAnswersIndices: Set<Int>
 ) : Question

data class OpenEndedQuestion(
 override val id: String,
 override val text: String
 ) : Question

sealed interface Answer {
    val questionId: String
}

data class TrueFalseAnswer(
    override val questionId: String,
    val selectedAnswer: Boolean? = null
) : Answer

data class MultipleChoiceAnswer(
    override val questionId: String,
    val selectedOptionIndex: Int? = null
) : Answer

 data class MultipleSelectionAnswer(
 override val questionId: String,
     val selectedOptionIndices: Set<Int>? = null
 ) : Answer

 data class OpenEndedAnswer(
 override val questionId: String,
     val responseText: String? = null
 ) : Answer
