package com.mcwilliams.memerator.ui

import android.content.Context
import android.content.SharedPreferences
import com.mcwilliams.memerator.R
import com.mcwilliams.memerator.memes.api.MemeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemeratorRepository @Inject constructor(
    val context: Context,
    private val memeApi: MemeApi
) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    fun getMemeImages(): Flow<List<String>> =
        flow {
            emit(memeApi.getMemeImages())
        }

}