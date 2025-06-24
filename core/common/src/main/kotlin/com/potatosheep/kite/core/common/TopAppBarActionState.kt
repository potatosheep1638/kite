package com.potatosheep.kite.core.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TopAppBarActionState {
    var showSort: Boolean by mutableStateOf(false)
}
