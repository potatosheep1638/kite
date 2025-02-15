package com.potatosheep.kite.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

@Composable
fun KiteTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    MaterialTheme(
        colorScheme = when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
                if (useDarkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            }

            useDarkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }
    ) {
        val backgroundColor = MaterialTheme.colorScheme.surface

        CompositionLocalProvider(
            LocalBackgroundColor provides backgroundColor
        ) {
            content()
        }
    }
}

object KiteTheme {
    val typography: KiteTypography
        @Composable @ReadOnlyComposable get() = KiteTypography()
}