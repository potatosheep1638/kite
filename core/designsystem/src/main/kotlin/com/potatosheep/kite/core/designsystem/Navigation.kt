package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KiteNavigationSuiteScaffold(
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo()),
    containerColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    NavigationSuiteScaffold(
        navigationSuiteItems = navigationSuiteItems,
        layoutType = layoutType,
        containerColor = containerColor,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun KiteNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    navigationBarItems: @Composable (RowScope.() -> Unit)
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        content = navigationBarItems
    )
}

@Preview
@Composable
private fun KiteNavigationSuitePreview() {
    val items = listOf("Home", "Subscriptions", "Feed")
    val icons = listOf(
        KiteIcons.Home,
        KiteIcons.Subscription,
        KiteIcons.Feed
    )
    val selectedIcons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Notifications,
        Icons.Rounded.Favorite
    )
    val selectedItemIndex = 0

    KiteTheme {
        KiteNavigationSuiteScaffold(
            navigationSuiteItems = {
                items.forEachIndexed { index, itemDesc ->
                    item(
                        selected = itemDesc == items[selectedItemIndex],
                        icon = {
                            Icon(
                                imageVector =
                                if (itemDesc == items[selectedItemIndex])
                                    selectedIcons[index]
                                else
                                    icons[index],
                                contentDescription = itemDesc,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        label = { Text(itemDesc) },
                        onClick = {}
                    )
                }
            }
        ) {}
    }
}

@Preview
@Composable
private fun KiteNavigationBarPreview() {
    val items = listOf("Home", "Subscriptions", "Feed")
    val icons = listOf(
        KiteIcons.Home,
        KiteIcons.Subscription,
        KiteIcons.Feed
    )
    val selectedIcons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Notifications,
        Icons.Rounded.Favorite
    )
    val selectedItemIndex = 0

    KiteTheme {
        KiteNavigationBar {
            items.forEachIndexed { index, itemDesc ->
                NavigationBarItem(
                    selected = itemDesc == items[selectedItemIndex],
                    icon = {
                        Icon(
                            imageVector =
                            if (itemDesc == items[selectedItemIndex])
                                selectedIcons[index]
                            else
                                icons[index],
                            contentDescription = itemDesc,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    label = { Text(itemDesc) },
                    onClick = {}
                )
            }
        }
    }
}