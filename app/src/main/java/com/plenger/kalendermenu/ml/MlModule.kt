package com.plenger.kalendermenu.ml

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * MlModule — Hilt DI
 * Provides TfliteHelper dan RecipeSearch sebagai Singleton.
 * Kedua class ini di-share ke SpecificMenuViewModel
 * dan AiRecommendationViewModel.
 */
@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideTfliteHelper(
        @ApplicationContext context: Context
    ): TfliteHelper = TfliteHelper(context)

    @Provides
    @Singleton
    fun provideRecipeSearch(
        @ApplicationContext context: Context
    ): RecipeSearch = RecipeSearch(context)
}
