package com.android.graphqltest.presentation.home

import com.android.graphqltest.domain.model.DetailedCountry
import com.android.graphqltest.domain.model.SimpleCountry
import com.android.graphqltest.domain.repository.CountryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class HomeCountriesVMTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_isLoadingWhileInitRequestIsPending() = runTest {
        val result = CompletableDeferred<List<SimpleCountry>>()
        val viewModel = HomeCountriesVM(FakeCountryRepository { result.await() })

        assertEquals(CountryUiState(isLoading = true), viewModel.uiState.value)

        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)

        result.complete(emptyList())
        advanceUntilIdle()
    }

    @Test
    fun getCountries_success_updatesCountriesAndFinishesLoading() = runTest {
        val countries = listOf(
            SimpleCountry("IN", "India", "Delhi", "🇮🇳"),
            SimpleCountry("JP", "Japan", "Tokyo", "🇯🇵")
        )
        val result = CompletableDeferred<List<SimpleCountry>>()
        val viewModel = HomeCountriesVM(FakeCountryRepository { result.await() })

        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        result.complete(countries)
        advanceUntilIdle()

        assertEquals(countries, viewModel.uiState.value.countries)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun getCountries_emptyResult_finishesLoadingWithEmptyList() = runTest {
        val viewModel = HomeCountriesVM(FakeCountryRepository { emptyList() })

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.countries.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun getCountries_repositoryFailure_setsErrorAndFinishesLoading() = runTest {
        val viewModel = HomeCountriesVM(
            FakeCountryRepository { throw IllegalStateException("Network unavailable") }
        )

        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.countries.isEmpty())
    }

    @Test
    fun getCountries_transitionsFromLoadingToSuccess() = runTest {
        val country = SimpleCountry("CA", "Canada", "Ottawa", "🇨🇦")
        val result = CompletableDeferred<List<SimpleCountry>>()
        val viewModel = HomeCountriesVM(FakeCountryRepository { result.await() })

        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.countries.isEmpty())

        result.complete(listOf(country))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(listOf(country), viewModel.uiState.value.countries)
        assertNull(viewModel.uiState.value.error)
    }

    private class FakeCountryRepository(
        private val countriesProvider: suspend () -> List<SimpleCountry>
    ) : CountryRepository {
        override suspend fun getCountries(): List<SimpleCountry> = countriesProvider()

        override suspend fun getCountry(code: String): DetailedCountry? = null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    private val dispatcher = StandardTestDispatcher()

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
