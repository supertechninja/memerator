package com.mcwilliams.memerator.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemeratorViewModel @Inject constructor(
    private val memeratorRepository: MemeratorRepository,
) : ViewModel() {

    private val _images: MutableLiveData<List<String>> = MutableLiveData()
    val images : LiveData<List<String>> = _images

    init {
        viewModelScope.launch {
            memeratorRepository.getMemeImages().collect {
                Log.d("TAG", ": $it")
                _images.postValue(it)
            }
        }

    }

}