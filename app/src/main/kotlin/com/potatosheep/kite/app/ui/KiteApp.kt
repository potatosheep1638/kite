package com.potatosheep.kite.app.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.potatosheep.kite.app.navigation.topLevelEntry
import com.potatosheep.kite.core.common.TopAppBarActionState
import com.potatosheep.kite.core.common.util.LocalSnackbarHostState
import com.potatosheep.kite.core.common.util.LocalTopAppBarActionState
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.core.navigation.toEntries
import com.potatosheep.kite.feature.about.impl.navigation.aboutEntry
import com.potatosheep.kite.feature.bookmark.impl.navigation.bookmarkEntry
import com.potatosheep.kite.feature.image.navigation.imageEntry
import com.potatosheep.kite.feature.onboarding.impl.navigation.onboardingEntry
import com.potatosheep.kite.feature.post.impl.navigation.postEntry
import com.potatosheep.kite.feature.search.impl.navigation.searchEntry
import com.potatosheep.kite.feature.settings.impl.navigation.settingsEntry
import com.potatosheep.kite.feature.subreddit.impl.navigation.subredditEntry
import com.potatosheep.kite.feature.user.impl.navigation.userEntry
import com.potatosheep.kite.feature.video.impl.navigation.videoEntry

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
            NavDisplay(
                entries = appState.navigationState.toEntries(entryProvider {
                    topLevelEntry(navigator)
                    aboutEntry(navigator, "3.4.0")
                    bookmarkEntry(navigator)
                    imageEntry(navigator)
                    onboardingEntry(navigator, { })
                    postEntry(navigator)
                    searchEntry(navigator)
                    settingsEntry(navigator)
                    subredditEntry(navigator)
                    userEntry(navigator)
                    videoEntry(navigator)
                }),
                onBack = { navigator.goBack() }
            )
        }
    }
}