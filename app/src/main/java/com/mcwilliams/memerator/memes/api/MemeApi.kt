package com.mcwilliams.memerator.memes.api

import androidx.annotation.Keep
import retrofit2.http.GET

@Keep
interface MemeApi {

    @GET("images")
    suspend fun getMemeImages(): List<String>

}