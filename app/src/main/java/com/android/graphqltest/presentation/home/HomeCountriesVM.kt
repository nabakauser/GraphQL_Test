package com.android.graphqltest.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.graphqltest.domain.model.SimpleCountry
import com.android.graphqltest.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCountriesVM @Inject constructor(
    private val repository: CountryRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(CountryUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        getCountries()
    }

    fun getCountries() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val countries = repository.getCountries()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        countries = countries,
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Something went wrong"
                    )
                }
            }
        }
    }

    fun toggleFavorite(countryCode: String) {
        _uiState.update { state ->
            val favoriteCodes = state.favoriteCountryCodes
            state.copy(
                favoriteCountryCodes = if (countryCode in favoriteCodes) {
                    favoriteCodes - countryCode
                } else {
                    favoriteCodes + countryCode
                }
            )
        }
    }

    fun toggleVisited(countryCode: String) {
        _uiState.update { state ->
            val visitedCodes = state.visitedCountryCodes
            state.copy(
                visitedCountryCodes = if (countryCode in visitedCodes) {
                    visitedCodes - countryCode
                } else {
                    visitedCodes + countryCode
                }
            )
        }
    }
}

data class CountryUiState(
    val isLoading: Boolean = false,
    val countries: List<SimpleCountry> = emptyList(),
    val error: String? = null,
    val favoriteCountryCodes: Set<String> = emptySet(),
    val visitedCountryCodes: Set<String> = emptySet()
)