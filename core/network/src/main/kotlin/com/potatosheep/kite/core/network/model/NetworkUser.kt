package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.User

data class NetworkUser(
    val userName: String,
    val description: String,
    val karma: Int,
    val iconLink: String
)

fun NetworkUser.toExternalModel(): User = User(
    userName = userName,
    description = description,
    karma = karma,
    iconLink = iconLink
)