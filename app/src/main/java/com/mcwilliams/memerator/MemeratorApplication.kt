package com.mcwilliams.memerator

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MemeratorApplication() : Application() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        var isUserLoggedIn = false
    }

}