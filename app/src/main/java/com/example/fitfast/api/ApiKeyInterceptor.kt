package com.example.fitfast.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val request: Request = original.newBuilder()
            .header("x-rapidapi-key", "afc878610cmsh0afc8c0de6ddf9cp1118e3jsn80ba32950d0f")
            .header("x-rapidapi-host", "exercisedb.p.rapidapi.com")
            .build()
        return chain.proceed(request)
    }
}