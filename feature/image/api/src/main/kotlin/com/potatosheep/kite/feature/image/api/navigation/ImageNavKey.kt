package com.potatosheep.kite.feature.image.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ImageNavKey(
    val imageLinks: List<String>,
    val captions: List<String?>
) : NavKey

fun Navigator.navigateToImage(
    imageLinks: List<String>,
    captions: List<String?>
) {
    navigate(ImageNavKey(imageLinks, captions))
}