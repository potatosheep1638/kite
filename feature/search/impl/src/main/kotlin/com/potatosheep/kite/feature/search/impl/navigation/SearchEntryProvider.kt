package com.potatosheep.kite.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.searchresult.api.navigation.SearchResultNavKey

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchResultNavKey>(metadata = defaultTransitionSpec()) { key ->

    }
}
