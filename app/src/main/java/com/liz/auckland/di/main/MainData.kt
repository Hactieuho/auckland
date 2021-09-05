package com.liz.auckland.di.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.di.CurrentTownCity
import com.liz.auckland.resource.Resource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

/**
 * An hien suburb locality
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SuburbLocalityVisibility

/**
 * An hien road name
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoadNameVisibility

/**
 * An hien address number
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AddressNumberVisibility

/**
 * An hien rubbish info
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RubbishInfoVisibility

/**
 * An hien dia chi lay rubbish
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RubbishVisibility

/**
 * An hien loading
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoadingVisibility

/**
 * An hien alarms
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AlarmEnabled

@InstallIn(ViewModelComponent::class)
@Module
object MainData {

    @SuburbLocalityVisibility
    @ViewModelScoped
    @Provides
    fun provideSuburbLocalityVisibility(
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
        Transformations.switchMap(rubbishRepository.currentTownCity) { city ->
            Transformations.map(rubbishRepository.getSuburbLocalitiesResult) { localities ->
                if (cities is Resource.Success && !city.isNullOrEmpty() && localities is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    @RoadNameVisibility
    @ViewModelScoped
    @Provides
    fun provideRoadNameVisibility(
        @SuburbLocalityVisibility suburbLocalityVisibility: LiveData<Int>,
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(suburbLocalityVisibility) { localityVisible ->
        Transformations.switchMap(rubbishRepository.currentSuburbLocality) { locality ->
            Transformations.map(rubbishRepository.getRoadNamesResult) { roads ->
                if (localityVisible == View.VISIBLE && !locality.isNullOrEmpty() && roads is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    @AddressNumberVisibility
    @ViewModelScoped
    @Provides
    fun provideAddressNumberVisibility(
        @RoadNameVisibility roadNameVisibility: LiveData<Int>,
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(roadNameVisibility) { roadVisible ->
        Transformations.switchMap(rubbishRepository.currentRoadName) { road ->
            Transformations.map(rubbishRepository.getAddressNumbersResult) { addresses ->
                if (roadVisible == View.VISIBLE && !road.isNullOrEmpty() && addresses is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    @RubbishInfoVisibility
    @ViewModelScoped
    @Provides
    fun provideRubbishInfoVisibility(
        @AddressNumberVisibility addressNumberVisibility: LiveData<Int>,
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(rubbishRepository.getRubbishInfoResult) { rubbish ->
            Transformations.map(rubbishRepository.currentAddressNumber) { address ->
                if (addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    @RubbishVisibility
    @ViewModelScoped
    @Provides
    fun provideRubbishVisibility(
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(rubbishRepository.currentTownCity) { city ->
        Transformations.switchMap(rubbishRepository.currentSuburbLocality) { locality ->
            Transformations.switchMap(rubbishRepository.currentRoadName) { road ->
                Transformations.map(rubbishRepository.currentAddressNumber) { address ->
                    if (city.isNullOrEmpty() || locality.isNullOrEmpty() || road.isNullOrEmpty() || address.isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
    }

    @LoadingVisibility
    @ViewModelScoped
    @Provides
    fun provideLoadingVisibility(
        rubbishRepository: RubbishRepository
    ): LiveData<Int> = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
        Transformations.switchMap(rubbishRepository.getSuburbLocalitiesResult) { localities ->
            Transformations.switchMap(rubbishRepository.getRoadNamesResult) { roads ->
                Transformations.switchMap(rubbishRepository.getAddressNumbersResult) { addresses ->
                    Transformations.map(rubbishRepository.getRubbishInfoResult) { rubish ->
                        if (cities.isLoading() || localities.isLoading() || roads.isLoading() || addresses.isLoading() || rubish.isLoading()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
            }
        }
    }

    @AlarmEnabled
    @ViewModelScoped
    @Provides
    fun provideAlarmEnabled(
        @AddressNumberVisibility addressNumberVisibility: LiveData<Int>,
        rubbishRepository: RubbishRepository
    ): LiveData<Boolean> = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(rubbishRepository.getRubbishInfoResult) { rubbish ->
            Transformations.map(rubbishRepository.currentAddressNumber) { address ->
                addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()
            }
        }
    }

}