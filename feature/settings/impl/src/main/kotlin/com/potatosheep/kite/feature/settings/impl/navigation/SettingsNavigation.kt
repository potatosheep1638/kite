package com.potatosheep.kite.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.settings.api.navigation.SettingsNavKey
import com.potatosheep.kite.feature.settings.impl.SettingsRoute

fun EntryProviderScope<SettingsNavKey>.settingsEntry(navigator: Navigator) {
    entry<SettingsNavKey> {
        SettingsRoute(
            onBackClick = { navigator.goBack() },
        )
    }
}