package com.potatosheep.kite.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.app.ui.TopLevelScreen
import com.potatosheep.kite.core.common.util.LocalSnackbarHostState
import com.potatosheep.kite.core.common.util.LocalTopAppBarActionState
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object TopLevelNavKey : NavKey

fun Navigator.navigateToTopLevel() {
    navigate(TopLevelNavKey)
}

fun EntryProviderScope<NavKey>.topLevelEntry(navigator: Navigator, isTopLevel: Boolean) {
    entry<TopLevelNavKey> {
        TopLevelScreen(
            navigator = navigator,
            isTopLevel = isTopLevel,
            snackbarHostState = LocalSnackbarHostState.current,
            topAppBarActionState = LocalTopAppBarActionState.current,
        )
    }
}
