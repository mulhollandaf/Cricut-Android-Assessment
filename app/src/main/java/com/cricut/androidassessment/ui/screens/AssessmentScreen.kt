package com.cricut.androidassessment.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

@Composable
fun AssessmentScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel()
) {
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val questions = viewModel.questions
    val currentQuestion = viewModel.currentQuestion
    val allAnswers by viewModel.answers.collectAsState() // This holds the state
    val score by viewModel.score.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (score != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display Score
                Text(
                    text = "Your Score: $score / ${viewModel.numberOfQuestions}",
                    style = MaterialTheme.typography.headlineMedium
                )
                // Restart Button
                Button(
                    onClick = { viewModel.restartQuiz() }
                ) {
                    Text("Restart Quiz")
                }
            }
        } else {
            // Question Area
            currentQuestion?.let { question ->
                when (question) {
                    is TrueFalseQuestion -> {
                        val currentAnswer = allAnswers[question.id] as? String ?: ""
                        SingleSelectOptionQuestionContent(
                            questionText = question.text,
                            options = question.options.toSet(),
                            currentAnswer = currentAnswer,
                            onAnswerChange = { viewModel.onAnswerChanged(question.id, it) }
                        )
                    }
                    is MultipleChoiceQuestion -> {
                        val currentAnswer = allAnswers[question.id] as? String ?: ""
                        SingleSelectOptionQuestionContent(
                            questionText = question.text,
                            options = question.options.toSet(),
                            currentAnswer = currentAnswer,
                            onAnswerChange = { viewModel.onAnswerChanged(question.id, it) }
                        )
                    }
                    is MultipleSelectionQuestion -> {
                        val currentAnswer = allAnswers[question.id] as? Set<String> ?: emptySet()
                        QuestionContent(
                            question = question,
                            currentSelectedAnswers = currentAnswer,
                            onAnswerSelected = { viewModel.onMultipleAnswersChanged(question.id, it) }
                        )
                    }
                    is OpenEndedQuestion -> {
                        val currentAnswer = allAnswers[question.id] as? String ?: ""
                        QuestionContent(
                            question = question,
                            currentAnswer = currentAnswer,
                            onAnswerChange = { viewModel.onAnswerChanged(question.id, it) }
                        )
                    }
                }
            }
        }
        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.onPreviousClicked() },
                enabled = currentQuestionIndex > 0 && score == null // Disable if score is shown

            ) {
                Text("Previous")
            }
            Button(
                onClick = {
                    if (currentQuestionIndex < questions.value.size - 1) {
                        viewModel.onNextClicked()
                    } else {
                        viewModel.submitAnswers()
                    }
                },
                enabled = score == null // Disable if score is shown
            ) {
                Text(if (currentQuestionIndex < viewModel.numberOfQuestions - 1) "Next" else "Submit")
            }
        }
    }
}

@Composable
private fun getSelectedButtonColors(isSelected: Boolean): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun SingleSelectOptionQuestionContent(
    questionText: String,
    options: Set<String>,
    currentAnswer: String,
    onAnswerChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        options.forEach { option ->
            val isSelected = currentAnswer == option
            Button(
                onClick = { onAnswerChange(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = getSelectedButtonColors(isSelected = isSelected)
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: MultipleSelectionQuestion,
    currentSelectedAnswers: Set<String>,
    onAnswerSelected: (option: String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        question.options.forEach { option ->
            val isSelected = currentSelectedAnswers.contains(option)
            Button(
                onClick = { onAnswerSelected(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = getSelectedButtonColors(isSelected = isSelected)
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: OpenEndedQuestion,
    currentAnswer: String,
    onAnswerChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        TextField(
            value = currentAnswer,
            onValueChange = onAnswerChange,
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTrueFalse() {
    MaterialTheme {
        SingleSelectOptionQuestionContent(
            questionText = QuizRepo().trueFalseQuestion.text,
            options = QuizRepo().trueFalseQuestion.options.toSet(),
            currentAnswer = "True",
        ) {}
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewMultipleChoice() {
    MaterialTheme {
        SingleSelectOptionQuestionContent(
            questionText = QuizRepo().multipleChoiceQuestion.text,
            options = QuizRepo().multipleChoiceQuestion.options.toSet(),
            currentAnswer = "Java",
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMultipleSelect() {
    MaterialTheme {
        QuestionContent(
            QuizRepo().multipleSelectionQuestion,
            currentSelectedAnswers = setOf("Kotlin", "Java"),
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOpenEnded() {
    MaterialTheme {
        QuestionContent(
            QuizRepo().openEndedQuestion,
            currentAnswer = "Because",
        ) {}
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    AndroidAssessmentTheme {
        // Manually create QuizRepo and QuizViewModel for the preview
        val quizRepo = QuizRepo()
        val quizViewModel = QuizViewModel(quizRepo)
        AssessmentScreen(viewModel = quizViewModel)
    }
}
