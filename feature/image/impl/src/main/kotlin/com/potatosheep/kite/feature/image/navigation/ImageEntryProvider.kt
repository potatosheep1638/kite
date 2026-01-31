package com.potatosheep.kite.feature.image.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.ImageRoute
import com.potatosheep.kite.feature.image.ImageViewModel
import com.potatosheep.kite.feature.image.ImageViewModel.Factory
import com.potatosheep.kite.feature.image.api.navigation.ImageNavKey

fun EntryProviderScope<NavKey>.imageEntry(navigator: Navigator) {
    entry<ImageNavKey>(metadata = defaultTransitionSpec()) { key ->
        ImageRoute(
            onBackClick = { navigator.goBack() },
            viewModel = hiltViewModel<ImageViewModel, Factory> {
                it.create(key.imageLinks, key.captions)
            }
        )
    }
}