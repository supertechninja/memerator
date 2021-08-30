package com.mcwilliams.memerator.di

import android.content.Context
import com.mcwilliams.memerator.ui.MemeratorRepository
import com.mcwilliams.memerator.memes.api.MemeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [MemeNetworkModule::class], )
class AppModule {

    @Provides
    @Singleton
    fun provideDashboardRepository(
        @ApplicationContext context: Context,
        memeApi: MemeApi
    ): MemeratorRepository =
        MemeratorRepository(
            context,
            memeApi
        )
}