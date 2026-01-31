package com.potatosheep.kite.feature.user.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class UserNavKey(val user: String) : NavKey

fun Navigator.navigateToUser(user: String) {
    navigate(UserNavKey(user))
}
