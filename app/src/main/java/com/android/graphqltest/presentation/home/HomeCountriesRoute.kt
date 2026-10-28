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
        countryList = uiState.countries
    )
}