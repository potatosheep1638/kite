package com.potatosheep.kite.feature.exception

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class ExceptionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(KiteDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _charmony = MutableStateFlow("")
    val charmony: StateFlow<String> = _charmony

    init {
        viewModelScope.launch {
            val inputStream = context.resources.openRawResource(R.raw.charmony)
            var rawData = ""

            withContext(ioDispatcher) {
                val reader = BufferedReader(InputStreamReader(inputStream))

                while (true) {
                    val line = reader.readLine() ?: break
                    rawData += line
                }
            }

            _charmony.value = rawData
        }
    }
}