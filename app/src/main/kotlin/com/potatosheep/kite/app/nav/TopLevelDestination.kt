package com.potatosheep.kite.app.nav

import androidx.compose.ui.graphics.vector.ImageVector
import com.potatosheep.kite.core.designsystem.KiteIcons

// TODO: Change hardcoded label to use string.xml
enum class TopLevelDestination(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
) {
    HOME(
        icon = KiteIcons.Home,
        selectedIcon = KiteIcons.HomeSelected,
        label = "Home"
    ),
    LIBRARY(
        icon = KiteIcons.Library,
        selectedIcon = KiteIcons.LibrarySelected,
        label = "Library"
    )
}