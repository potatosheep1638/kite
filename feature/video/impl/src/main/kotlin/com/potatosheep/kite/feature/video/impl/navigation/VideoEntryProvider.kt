package com.potatosheep.kite.feature.video.impl.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.video.api.navigation.VideoNavKey
import com.potatosheep.kite.feature.video.impl.VideoRoute
import com.potatosheep.kite.feature.video.impl.VideoViewModel
import com.potatosheep.kite.feature.video.impl.VideoViewModel.Factory

fun EntryProviderScope<VideoNavKey>.videoEntry(navigator: Navigator) {
    entry<VideoNavKey> { key ->
        VideoRoute(
            onBackClick = { navigator.goBack() },
            viewModel = hiltViewModel<VideoViewModel, Factory> {
                it.create(key.videoLink)
            }
        )
    }
}