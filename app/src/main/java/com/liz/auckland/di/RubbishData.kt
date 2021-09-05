package com.liz.auckland.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Town city hien tai
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentTownCity

/**
 * Suburb locality hien tai
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentSuburbLocality

/**
 * Road name hien tai
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentRoadName

/**
 * So nha hien tai
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentAddressNumber

/**
 * Dia chi lay rubbish
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RubbishAn

@InstallIn(SingletonComponent::class)
@Module
object RubbishData {
    @CurrentTownCity
    @Singleton
    @Provides
    fun provideCurrentTownCity(): MutableLiveData<String?> = MutableLiveData(null)

    @CurrentSuburbLocality
    @Singleton
    @Provides
    fun provideCurrentSuburbLocality(): MutableLiveData<String?> = MutableLiveData(null)

    @CurrentRoadName
    @Singleton
    @Provides
    fun provideCurrentRoadName(): MutableLiveData<String?> = MutableLiveData(null)

    @CurrentAddressNumber
    @Singleton
    @Provides
    fun provideCurrentAddressNumber(): MutableLiveData<String?> = MutableLiveData(null)

    @RubbishAn
    @Singleton
    @Provides
    fun provideRubbishAn(
        @CurrentSuburbLocality currentSuburbLocality: MutableLiveData<String?>,
        @CurrentRoadName currentRoadName: MutableLiveData<String?>,
        @CurrentAddressNumber currentAddressNumber: MutableLiveData<String?>
    ): LiveData<String>? = Transformations.switchMap(currentSuburbLocality) { locality ->
        Transformations.switchMap(currentRoadName) { road ->
            Transformations.map(currentAddressNumber) { address ->
                "$address $road, $locality"
            }
        }
    }
}