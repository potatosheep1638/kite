package com.potatosheep.kite.core.model

data class User(
    val userName: String,
    val description: String,
    val karma: Int,
    val iconLink: String
)