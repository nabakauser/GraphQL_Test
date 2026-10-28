package com.android.graphqltest.data.repositoryimpl

import com.android.graphqltest.CountriesQuery
import com.android.graphqltest.CountryQuery
import com.android.graphqltest.data.mapper.toDetailedCountry
import com.android.graphqltest.data.mapper.toSimpleCountry
import com.android.graphqltest.domain.model.DetailedCountry
import com.android.graphqltest.domain.model.SimpleCountry
import com.android.graphqltest.domain.repository.CountryRepository
import com.apollographql.apollo.ApolloClient
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
): CountryRepository {

    override suspend fun getCountries(): List<SimpleCountry> {
        val response = apolloClient
            .query(CountriesQuery())
            .execute()
        return response.data?.countries?.map {
            it.toSimpleCountry()
        } ?: emptyList()
    }

    override suspend fun getCountry(code: String): DetailedCountry? {
        val response = apolloClient
            .query(CountryQuery(code))
            .execute()
        return response.data?.country?.toDetailedCountry()
    }
}