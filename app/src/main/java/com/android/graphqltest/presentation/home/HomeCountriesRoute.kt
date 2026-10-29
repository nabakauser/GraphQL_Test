package com.android.graphqltest.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeCountriesRoute(
    viewModel: HomeCountriesVM = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeCountriesScreen(
        countryList = uiState.countries,
        isLoading = uiState.isLoading,
        error = uiState.error,
        favoriteCountryCodes = uiState.favoriteCountryCodes,
        visitedCountryCodes = uiState.visitedCountryCodes,
        onFavoriteClick = viewModel::toggleFavorite,
        onVisitedClick = viewModel::toggleVisited,
        onRetry = viewModel::getCountries,
        onCountryClick = {}
    )
}