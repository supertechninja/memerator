package com.mcwilliams.memerator.memes.api

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("x-rapidapi-key", "3824e2db05msh1063a546c9e7640p1a0547jsn45c69032191c")
                .addHeader("x-rapidapi-host", "ronreiter-meme-generator.p.rapidapi.com")
                .build()
        )
    }
}