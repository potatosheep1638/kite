package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KiteDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun KiteDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = PaddingValues(12.dp),
    interactionSource: MutableInteractionSource? = null
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun DropdownMenuPreview() {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val menuItems = listOf(
        "Filter",
        "Settings",
        "About"
    )

    KiteTheme {
        Surface {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        backIcon = KiteIcons.Back,
                        backIconContentDescription = "",
                        onBackClick = {},
                        actions = {
                            IconButton(onClick = { isExpanded = true }) {
                                Icon(
                                    imageVector = KiteIcons.MoreOptions,
                                    contentDescription = null
                                )
                            }

                            KiteDropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = { isExpanded = false }
                            ) {
                                menuItems.forEach { item ->
                                    KiteDropdownMenuItem(
                                        text = item,
                                        onClick = {}
                                    )
                                }
                            }
                        },
                        title = "Test"
                    )
                }
            ) {
                Column(
                    Modifier
                        .padding(it)
                        .consumeWindowInsets(it)) {

                    Text(
                        text = "Random print statements go!",
                    )
                }
            }

        }
    }
}