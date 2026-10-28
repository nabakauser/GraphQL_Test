package com.android.graphqltest.domain.repository

import com.android.graphqltest.domain.model.DetailedCountry
import com.android.graphqltest.domain.model.SimpleCountry

interface CountryRepository {
    suspend fun getCountries(): List<SimpleCountry>
    suspend fun getCountry(code: String): DetailedCountry?
}