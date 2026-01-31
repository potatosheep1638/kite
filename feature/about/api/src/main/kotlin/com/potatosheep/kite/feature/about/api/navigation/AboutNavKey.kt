package com.potatosheep.kite.feature.about.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object AboutNavKey : NavKey

fun Navigator.navigateToAbout() {
    navigate(AboutNavKey)
}