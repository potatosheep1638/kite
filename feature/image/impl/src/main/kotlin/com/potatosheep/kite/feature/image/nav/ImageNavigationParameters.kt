package com.potatosheep.kite.feature.image.nav

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class ImageParameters(
    val imageLinks: List<String>,
    val captions: List<String?>
) : Parcelable

val ImageParametersType = object : NavType<ImageParameters>(
    isNullableAllowed = false
) {
    override fun put(bundle: Bundle, key: String, value: ImageParameters) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): ImageParameters? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            bundle.getParcelable(key, ImageParameters::class.java)
        else
            bundle.getParcelable(key)
    }

    override fun serializeAsValue(value: ImageParameters): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): ImageParameters {
        return Json.decodeFromString<ImageParameters>(value)
    }
}