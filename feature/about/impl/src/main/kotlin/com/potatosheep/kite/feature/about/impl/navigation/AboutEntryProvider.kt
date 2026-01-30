package com.potatosheep.kite.feature.about.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.about.api.navigation.AboutNavKey
import com.potatosheep.kite.feature.about.impl.AboutRoute

fun EntryProviderScope<NavKey>.aboutEntry(
    navigator: Navigator,
    versionName: String,
) {
    entry<AboutNavKey> {
        AboutRoute(
            version = versionName,
            onBackClick = { navigator.goBack() },
        )
    }
}