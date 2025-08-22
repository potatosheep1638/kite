package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.common.R.string as commonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiteTopAppBar(
    title: String,
    searchIcon: ImageVector,
    searchIconContentDescription: String,
    optionsIcon: ImageVector,
    optionsContentDescription: String,
    onSearchClick: () -> Unit,
    onOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
            ) {
                Icon(
                    imageVector = searchIcon,
                    contentDescription = searchIconContentDescription
                )
            }
        },
        actions = {
            IconButton(
                onClick = onOptionsClick,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
            ) {
                Icon(
                    imageVector = optionsIcon,
                    contentDescription = optionsContentDescription
                )
            }
        },
        modifier = modifier,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    backIcon: ImageVector,
    onBackClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit) = {},
    backIconContentDescription: String = stringResource(commonStrings.back),
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Left
                    )
                )
            ) {
                Icon(
                    imageVector = backIcon,
                    contentDescription = backIconContentDescription
                )
            }
        },
        actions = actions,
        modifier = modifier,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiteSearchBar(
    query: String,
    backIcon: ImageVector,
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingComposable: (@Composable () -> Unit)? = null,
    onClear: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    backIconContentDescription: String = stringResource(commonStrings.back),
    inputFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    colors: SearchBarColors = SearchBarDefaults.colors(),
    content: @Composable () -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }

    // Recompose when initialQuery changes
    LaunchedEffect(query) {
        inputQuery = query
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = inputQuery,
                onQueryChange = {
                    inputQuery = it
                    onQueryChange(it)
                },
                onSearch = {
                    onSearch(it)
                },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Row {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Left
                                )
                            )
                        ) {
                            Icon(
                                imageVector = backIcon,
                                contentDescription = backIconContentDescription
                            )
                        }

                        if (leadingComposable != null) {
                            Box(Modifier.height(48.dp)) {
                                leadingComposable()
                            }
                        }
                    }
                },
                trailingIcon = {
                    if (inputQuery.isNotBlank()) {
                        IconButton(
                            onClick = {
                                inputQuery = ""
                                onClear()
                            },
                            modifier = Modifier.windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Right
                                )
                            )
                        ) {
                            Icon(
                                imageVector = KiteIcons.Clear,
                                contentDescription = null
                            )
                        }
                    }
                },
                colors = inputFieldColors
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        shape = RectangleShape,
        colors = colors,
        modifier = modifier
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaTopAppBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
        navigationIconContentColor =
        TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
        titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
        actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            ElevatedCard(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Left
                        )
                    ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = CircleShape,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = KiteIcons.Back,
                        contentDescription = stringResource(commonStrings.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            ElevatedCard(
                onClick = onDownloadClick,
                modifier = modifier.size(40.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = CircleShape,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = KiteIcons.Download,
                        contentDescription = "Download",
                        modifier = Modifier
                            .padding(6.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            ElevatedCard(
                onClick = onShareClick,
                modifier = Modifier
                    .size(40.dp)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Right
                        )
                    ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                shape = CircleShape,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = KiteIcons.Share,
                        contentDescription = "Share",
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        modifier = modifier,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun KiteTopAppBarPreview() {
    KiteTheme {
        KiteTopAppBar(
            title = "kite",
            searchIcon = Icons.Rounded.Search,
            searchIconContentDescription = "Search",
            optionsIcon = Icons.Rounded.MoreVert,
            optionsContentDescription = "Options",
            onSearchClick = {},
            onOptionsClick = {},
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun TopAppBarPreview() {
    KiteTheme {
        SmallTopAppBar(
            backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
            backIconContentDescription = "Back",
            onBackClick = {},
            title = "Post",
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SearchTopAppBarPreview() {
    KiteTheme {
        KiteSearchBar(
            query = "",
            backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
            backIconContentDescription = "Back",
            onBackClick = {},
            onSearch = {},
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.padding(horizontal = 6.dp),
            leadingComposable = {
                InputChip(
                    selected = false,
                    onClick = {},
                    label = {
                        Text(
                            text = "test",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.padding(end = 12.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = KiteIcons.Clear,
                                contentDescription = "Remove scope",
                            )
                        }
                    }
                )
            }
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun MediaTopAppBarPreview() {
    KiteTheme {
        Surface {
            MediaTopAppBar(
                onBackClick = {},
                onShareClick = {},
                onDownloadClick = {},
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}