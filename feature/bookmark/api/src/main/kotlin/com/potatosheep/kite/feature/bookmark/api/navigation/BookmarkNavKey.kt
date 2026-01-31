package com.potatosheep.kite.feature.bookmark.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object BookmarkNavKey : NavKey

fun Navigator.navigateToBookmark() {
    navigate(BookmarkNavKey)
}