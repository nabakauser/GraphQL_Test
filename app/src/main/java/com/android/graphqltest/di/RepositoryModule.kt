package com.android.graphqltest.di

import com.android.graphqltest.data.repositoryimpl.CountryRepositoryImpl
import com.android.graphqltest.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCountryRepository(
        implementation: CountryRepositoryImpl
    ): CountryRepository
}