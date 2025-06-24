package com.potatosheep.kite.core.common.util

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.potatosheep.kite.core.common.TopAppBarActionState

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

val LocalTopAppBarActionState = compositionLocalOf<TopAppBarActionState> { error("No TopAppBarState provided") }