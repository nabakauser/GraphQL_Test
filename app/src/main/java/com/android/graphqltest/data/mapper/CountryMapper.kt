package com.android.graphqltest.data.mapper

import com.android.graphqltest.CountriesQuery
import com.android.graphqltest.CountryQuery
import com.android.graphqltest.domain.model.DetailedCountry
import com.android.graphqltest.domain.model.SimpleCountry

fun  CountriesQuery.Country.toSimpleCountry(): SimpleCountry {
    return SimpleCountry(
        code = code,
        name = name,
        capital = capital ?: "No capital",
        emoji = emoji
    )
}

fun   CountryQuery.Country.toDetailedCountry(): DetailedCountry {
    return DetailedCountry(
        code = code,
        name = name,
        capital = capital ?: "No capital",
        emoji = emoji,
        languages = languages.map { it.name },
        continent = continent.name,
        currency = currency
    )
}