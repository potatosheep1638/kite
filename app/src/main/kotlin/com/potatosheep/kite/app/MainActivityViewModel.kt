package com.potatosheep.kite.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _isColdBoot = MutableStateFlow(true)
    val isColdBoot: StateFlow<Boolean> = _isColdBoot

    fun setColdBootState(isColdBoot: Boolean) {
        _isColdBoot.value = isColdBoot
    }
}