package com.android.graphqltest.presentation.home

import com.android.graphqltest.domain.model.DetailedCountry
import com.android.graphqltest.domain.model.SimpleCountry
import com.android.graphqltest.domain.repository.CountryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
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
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeCountriesVMTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeCountryRepository()

    private val sampleCountries = listOf(
        SimpleCountry(code = "US", name = "United States", capital = "Washington, D.C.", emoji = "🇺🇸"),
        SimpleCountry(code = "CA", name = "Canada", capital = "Ottawa", emoji = "🇨🇦")
    )

    @Test
    fun initialState_beforeCoroutineExecutes_hasDefaultUiState() {
        val viewModel = HomeCountriesVM(fakeRepository)

        val initialState = viewModel.uiState.value

        assertFalse(initialState.isLoading)
        assertTrue(initialState.countries.isEmpty())
        assertNull(initialState.error)
        assertFalse(initialState.isEmpty)
    }

    @Test
    fun stateTransitions_loadingThenSuccessState() = runTest {
        val deferred = CompletableDeferred<List<SimpleCountry>>()
        fakeRepository.getCountriesDeferred = deferred

        val viewModel = HomeCountriesVM(fakeRepository)

        runCurrent()
        val loadingState = viewModel.uiState.value
        assertTrue("Expected isLoading to be true during fetch", loadingState.isLoading)
        assertNull(loadingState.error)
        assertFalse(loadingState.isEmpty)

        deferred.complete(sampleCountries)
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertFalse("Expected isLoading to be false after completion", finalState.isLoading)
        assertEquals(sampleCountries, finalState.countries)
        assertFalse(finalState.isEmpty)
        assertNull(finalState.error)
    }

    @Test
    fun successfulCountryLoading_updatesUiStateWithCountriesAndFinishesLoading() = runTest {
        fakeRepository.countriesResult = Result.success(sampleCountries)

        val viewModel = HomeCountriesVM(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(sampleCountries, state.countries)
        assertFalse(state.isEmpty)
        assertNull(state.error)
    }

    @Test
    fun emptyResult_handlesEmptyCountryListAndSetsIsEmptyTrue() = runTest {
        fakeRepository.countriesResult = Result.success(emptyList())

        val viewModel = HomeCountriesVM(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.countries.isEmpty())
        assertTrue(state.isEmpty)
        assertNull(state.error)
    }

    @Test
    fun repositoryFailure_updatesErrorStateAndFinishesLoading() = runTest {
        val errorMessage = "Failed to fetch countries from network"
        fakeRepository.countriesResult = Result.failure(RuntimeException(errorMessage))

        val viewModel = HomeCountriesVM(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertTrue(state.countries.isEmpty())
        assertFalse(state.isEmpty)
    }

    @Test
    fun repositoryFailure_fallbackDefaultErrorMessageWhenExceptionMessageIsNull() = runTest {
        fakeRepository.countriesResult = Result.failure(RuntimeException())

        val viewModel = HomeCountriesVM(fakeRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Something went wrong", state.error)
        assertTrue(state.countries.isEmpty())
        assertFalse(state.isEmpty)
    }
}

class FakeCountryRepository : CountryRepository {
    var countriesResult: Result<List<SimpleCountry>> = Result.success(emptyList())
    var getCountriesDeferred: CompletableDeferred<List<SimpleCountry>>? = null

    override suspend fun getCountries(): List<SimpleCountry> {
        getCountriesDeferred?.let {
            return it.await()
        }
        return countriesResult.getOrThrow()
    }

    override suspend fun getCountry(code: String): DetailedCountry? {
        throw NotImplementedError("getCountry is not used in HomeCountriesVM")
    }
}
