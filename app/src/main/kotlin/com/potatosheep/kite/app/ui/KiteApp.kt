package com.potatosheep.kite.app.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.potatosheep.kite.app.navigation.TopLevelScreen
import com.potatosheep.kite.core.common.TopAppBarActionState
import com.potatosheep.kite.core.common.util.LocalSnackbarHostState
import com.potatosheep.kite.core.common.util.LocalTopAppBarActionState
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.navigation.Navigator

@Composable
fun KiteApp(
    appState: KiteAppState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Surface(
        color = LocalBackgroundColor.current,
        modifier = modifier
    ) {
        val topAppBarActionState = remember { TopAppBarActionState() }

        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
            LocalTopAppBarActionState provides topAppBarActionState
        ) {
            TopLevelScreen(
                navigator = navigator,
                snackbarHostState = LocalSnackbarHostState.current,
                topAppBarActionState = LocalTopAppBarActionState.current,
                modifier = modifier
            )
        }
    }
}