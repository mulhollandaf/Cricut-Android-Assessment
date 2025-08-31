package com.cricut.androidassessment.ui.screens

import kotlinx.coroutines.flow.StateFlow

interface QuizRepoI {
    fun getQuestions(): StateFlow<List<Question>>
}