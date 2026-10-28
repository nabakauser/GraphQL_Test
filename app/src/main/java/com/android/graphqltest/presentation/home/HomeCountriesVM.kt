package com.android.graphqltest.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.graphqltest.domain.model.SimpleCountry
import com.android.graphqltest.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCountriesVM @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountryUiState())
    val uiState: StateFlow<CountryUiState> = _uiState.asStateFlow()

    init {
        getCountries()
    }

    fun getCountries() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isEmpty = false
                )
            }
            try {
                val countries = repository.getCountries()
                Log.d("HomeCountriesVM", "Fetched countries count: ${countries.size}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        countries = countries,
                        isEmpty = countries.isEmpty(),
                        error = null
                    )
                }
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
}

data class CountryUiState(
    val isLoading: Boolean = false,
    val countries: List<SimpleCountry> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)