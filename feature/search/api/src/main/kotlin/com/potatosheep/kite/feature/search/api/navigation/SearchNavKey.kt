package com.potatosheep.kite.feature.search.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(
    val subredditScope: String?,
    val sort: SortOption.Search,
    val timeframe: SortOption.Timeframe,
    val query: String
) : NavKey

fun Navigator.navigateToSearch(
    sort: SortOption.Search = SortOption.Search.RELEVANCE,
    timeframe: SortOption.Timeframe = SortOption.Timeframe.ALL,
    subredditScope: String? = null,
    query: String = "",
) {
    navigate(SearchNavKey(subredditScope, sort, timeframe, query))
}