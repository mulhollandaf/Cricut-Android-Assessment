package com.cricut.androidassessment.ui.screens

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class QuizViewModelTest {

    // Rule to swap the main dispatcher with a test dispatcher
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: QuizViewModel
    private lateinit var fakeQuizRepo: FakeQuizRepo
    private val repo = QuizRepo()

    @Before
    fun setUp() {
        fakeQuizRepo = FakeQuizRepo()
        // Pass the fake repo to the ViewModel
        viewModel = QuizViewModel(fakeQuizRepo) 
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher
    }

    @Test
    fun `testInitialState - questions loaded, index and score are default`() = runTest {
        val sampleQuestions = listOf(
            TrueFalseQuestion(1, "Type1", "True"),
            TrueFalseQuestion(2, "Type2", "False")
        )
        fakeQuizRepo.setQuestions(sampleQuestions)
        // Re-initialize ViewModel to make it pick up new questions from the fake repo via its init block
        viewModel = QuizViewModel(fakeQuizRepo) 

        // Advance a virtual time to allow coroutines in init to complete
        advanceUntilIdle()


        assertEquals(0, viewModel.currentQuestionIndex.value)
        assertEquals(null, viewModel.score.value)
        assertEquals(sampleQuestions.size, viewModel.numberOfQuestions)
        assertEquals(false, viewModel.questions.value.isEmpty(),"Questions should not be empty after loading")
        assertEquals(sampleQuestions, viewModel.questions.value)
    }

    @Test
    fun `onNextClicked - advances question index normally`() = runTest {
        val sampleQuestions = listOf(
            repo.trueFalseQuestion,
            repo.multipleChoiceQuestion,
            repo.multipleSelectionQuestion
        )
        fakeQuizRepo.setQuestions(sampleQuestions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()


        viewModel.onNextClicked()
        assertEquals(1, viewModel.currentQuestionIndex.value)

        viewModel.onNextClicked()
        assertEquals(2, viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `onNextClicked - does not advance past last question`() = runTest {
         val sampleQuestions = listOf(
            repo.trueFalseQuestion
        )
        fakeQuizRepo.setQuestions(sampleQuestions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()


        viewModel.onNextClicked() 
        assertEquals(0, viewModel.currentQuestionIndex.value, "Should stay at the last question index")
    }


    @Test
    fun `onPreviousClicked - decrements question index normally`() = runTest {
        val sampleQuestions = listOf(
            repo.trueFalseQuestion,
            repo.multipleChoiceQuestion
        )
        fakeQuizRepo.setQuestions(sampleQuestions)
        viewModel = QuizViewModel(fakeQuizRepo) 
        advanceUntilIdle()

        viewModel.onNextClicked() // Go to the second question first
        assertEquals(1, viewModel.currentQuestionIndex.value)

        viewModel.onPreviousClicked()
        assertEquals(0, viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `onPreviousClicked - does not decrement before first question`() = runTest {
        val sampleQuestions = listOf(
            repo.trueFalseQuestion
        )
        fakeQuizRepo.setQuestions(sampleQuestions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.onPreviousClicked()
        assertEquals(0, viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `currentQuestionIndex updates correctly with Turbine`() = runTest {
         val sampleQuestions = listOf(
            repo.trueFalseQuestion,
         repo.multipleChoiceQuestion
         )
        fakeQuizRepo.setQuestions(sampleQuestions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.currentQuestionIndex.test {
            assertEquals(0, awaitItem())
            viewModel.onNextClicked()
            assertEquals(1, awaitItem())
            viewModel.onPreviousClicked()
            assertEquals(0, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `submitAnswers - calculates score correctly for single answer`() = runTest {
        val questions = listOf(
            MultipleChoiceQuestion(id = 1,text = "Q1", options = listOf("A", "B"), correctAnswer = "A"),
        )
        fakeQuizRepo.setQuestions(questions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.onAnswerChanged(1, "A")

        viewModel.submitAnswers()
        assertEquals(1, viewModel.score.value)
    }

    @Test
    fun `submitAnswers - calculates score correctly for single answer wrong`() = runTest {
        val questions = listOf(
            MultipleChoiceQuestion(id = 1,text = "Q1", options = listOf("A", "B"), correctAnswer = "A"),
        )
        fakeQuizRepo.setQuestions(questions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.onAnswerChanged(1, "B")

        viewModel.submitAnswers()
        assertEquals(0, viewModel.score.value)
    }


    @Test
    fun `submitAnswers - calculates score correctly for multiple choice answers`() = runTest {
        val questions = listOf(
            MultipleSelectionQuestion(id = 1, text = "Q1", options = listOf("A", "B", "C"), correctAnswer = setOf("A", "B")),
        )
        fakeQuizRepo.setQuestions(questions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()
        
        viewModel.onMultipleAnswersChanged(1, "A")
        viewModel.onMultipleAnswersChanged(1, "B")

        viewModel.submitAnswers()
        assertEquals(1, viewModel.score.value, "Only Q1 is fully correct")
    }

    @Test
    fun `submitAnswers - calculates score correctly for multiple choice answers wrong`() = runTest {
        val questions = listOf(
            MultipleSelectionQuestion(id = 1, text = "Q1", options = listOf("A", "B", "C"), correctAnswer = setOf("A", "B")),
        )
        fakeQuizRepo.setQuestions(questions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.onMultipleAnswersChanged(1, "A")
        viewModel.onMultipleAnswersChanged(1, "C")

        viewModel.submitAnswers()
        assertEquals(0, viewModel.score.value, "Only Q1 is fully correct")
    }


    @Test
    fun `restartQuiz - resets state`() = runTest {
        val questions = listOf(
            repo.trueFalseQuestion,
            repo.multipleChoiceQuestion
        )
        fakeQuizRepo.setQuestions(questions)
        viewModel = QuizViewModel(fakeQuizRepo)
        advanceUntilIdle()

        viewModel.onAnswerChanged(1, "True")
        viewModel.onNextClicked() 
        viewModel.submitAnswers()
        
        assertNotEquals(0, viewModel.currentQuestionIndex.value)
        assertNotEquals(emptyMap(), viewModel.answers.value)
        assertNotNull(viewModel.score.value)

        viewModel.restartQuiz()

        assertEquals(0, viewModel.currentQuestionIndex.value)
        assertEquals(emptyMap(), viewModel.answers.value)
        assertNull(viewModel.score.value)
    }

}

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class FakeQuizRepo : QuizRepoI { 
    private val questionsFlow = MutableStateFlow<List<Question>>(emptyList())

    fun setQuestions(questions: List<Question>) {
        questionsFlow.value = questions 
    }

    override fun getQuestions(): StateFlow<List<Question>> {
        return questionsFlow.asStateFlow()
    }
}