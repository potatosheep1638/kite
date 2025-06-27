package com.potatosheep.kite.feature.library.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.potatosheep.kite.feature.library.HomeRoute

@Composable
fun HomeScreen(
    onBackClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeRoute(
        onBackClick = onBackClick,
        onSubredditClick = onSubredditClick,
        onSavedClick = onSavedClick,
        modifier = modifier
    )
}