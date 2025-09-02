package com.cricut.androidassessment // You can choose your package structure for modules

import com.cricut.androidassessment.ui.screens.QuizRepo
import com.cricut.androidassessment.ui.screens.QuizRepoI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepo: QuizRepo
    ): QuizRepoI
}