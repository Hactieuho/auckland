package com.liz.auckland.ui.main.di

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.resource.Resource
import javax.inject.Inject

class MainData @Inject constructor(
    private val rubbishRepository: RubbishRepository
) {
    val suburbLocalityVisibility: LiveData<Int> = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
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

    val roadNameVisibility: LiveData<Int> = Transformations.switchMap(suburbLocalityVisibility) { localityVisible ->
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

    val addressNumberVisibility: LiveData<Int> = Transformations.switchMap(roadNameVisibility) { roadVisible ->
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

    val rubbishInfoVisibility: LiveData<Int> = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
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

    val rubbishVisibility: LiveData<Int> = Transformations.switchMap(rubbishRepository.currentTownCity) { city ->
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

    val loadingVisibility: LiveData<Int> = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
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

    val alarmEnabled: LiveData<Boolean> = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(rubbishRepository.getRubbishInfoResult) { rubbish ->
            Transformations.map(rubbishRepository.currentAddressNumber) { address ->
                addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()
            }
        }
    }
}