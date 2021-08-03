package com.mcwilliams.memerator.di

import com.mcwilliams.memerator.BuildConfig
import com.mcwilliams.memerator.memes.api.HeaderInterceptor
import com.mcwilliams.memerator.memes.api.MemeApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
@Suppress("unused")
object MemeNetworkModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideMemes(retrofit: Retrofit): MemeApi {
        return retrofit.create(MemeApi::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideMemeApi(
        okHttpClient: OkHttpClient.Builder,
    ): Retrofit {
        okHttpClient.addInterceptor(HeaderInterceptor())

        return Retrofit.Builder()
            .baseUrl("https://ronreiter-meme-generator.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideOkHttp(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(logging)
        }
        return okHttpClient
    }
}