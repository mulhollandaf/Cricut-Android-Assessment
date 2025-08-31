package com.cricut.androidassessment.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Question Area
        when (currentQuestion) { // Use 'val currentQ' for smart casting
            is TrueFalseQuestion -> {
                val currentAnswer = allAnswers[currentQuestion.id] as? String ?: ""
                QuestionContent(
                    question = currentQuestion,
                    currentAnswer = currentAnswer,
                    onAnswerChange = { viewModel.onAnswerChanged(currentQuestion.id, it) })
            }
            is MultipleChoiceQuestion -> {
                val currentAnswer = allAnswers[currentQuestion.id] as? String ?: ""
                QuestionContent(
                    question = currentQuestion,
                    currentAnswer = currentAnswer,
                    onAnswerChange = { viewModel.onAnswerChanged(currentQuestion.id, it) }
                )
            }
            is MultipleSelectionQuestion -> {
                // Ensure you are using the correct QuestionContent overload if you have multiple
                val currentSelectedAnswers = allAnswers[currentQuestion.id] as? Set<String> ?: emptySet()
                QuestionContent(
                    question = currentQuestion,
                    currentSelectedAnswers = currentSelectedAnswers, // This is passed
                    onAnswerSelected = { option ->
                        viewModel.onMultipleAnswersChanged(currentQuestion.id, option)
                    }
                )
            }
            is OpenEndedQuestion -> {
                val currentAnswer = allAnswers[currentQuestion.id] as? String ?: ""
                QuestionContent(
                    question = currentQuestion,
                    currentAnswer = currentAnswer,
                    onAnswerChange = { viewModel.onAnswerChanged(currentQuestion.id, it) }
                )
            }
        }
        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.onPreviousClicked() },
                enabled = currentQuestionIndex > 0
            ) {
                Text("Previous")
            }
            Button(
                onClick = { viewModel.onNextClicked() }
            ) {
                Text(if (currentQuestionIndex < questions.size - 1) "Next" else "Submit")
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: TrueFalseQuestion,
    currentAnswer: String, // This holds the selected option string or ""
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
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )
        question.options.forEach { option ->
            val isSelected = currentAnswer == option
            Button(
                onClick = { onAnswerChange(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: MultipleChoiceQuestion,
    currentAnswer: String, // This holds the selected option string or ""
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
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )
        question.options.forEach { option ->
            val isSelected = currentAnswer == option
            Button(
                onClick = { onAnswerChange(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: MultipleSelectionQuestion,
    currentSelectedAnswers: Set<String>, // Set of selected option strings
    onAnswerSelected: (option: String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start, // Align checkboxes to the start
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )

        question.options.forEach { option ->
            val isSelected = currentSelectedAnswers.contains(option)
            Button(
                onClick = { onAnswerSelected(option)},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: OpenEndedQuestion,
    currentAnswer: String, // This holds the selected answer ("TRUE" or "FALSE" or "")
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
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
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
        QuestionContent(
            question = QuizRepo().trueFalseQuestion,
            currentAnswer = "true",
            {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewMultipleChoice() {
    MaterialTheme { // Ensure you have a MaterialTheme wrapper
        QuestionContent(
            question = QuizRepo().multipleChoiceQuestion,
            currentAnswer = "C++",
            {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMultipleSelect() {
    MaterialTheme {
        QuestionContent(
            QuizRepo().multipleSelectionQuestion,
            currentSelectedAnswers = setOf("Kotlin", "Java"),
            onAnswerSelected = {},
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOpenEnded() {
    MaterialTheme {
        QuestionContent(
            QuizRepo().openEndedQuestion,
            currentAnswer = "Because",
            onAnswerChange = {},
        )

    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    AndroidAssessmentTheme {
        AssessmentScreen()
    }
}
