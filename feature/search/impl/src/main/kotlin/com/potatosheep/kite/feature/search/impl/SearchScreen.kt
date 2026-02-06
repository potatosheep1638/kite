package com.potatosheep.kite.feature.search.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.designsystem.KiteBottomSheet
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteSearchBar
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.ui.SortChip

@Composable
fun SearchRoute(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        searchUiState = uiState,
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onPostClick = onPostClick,
        setUiState = viewModel::setUiState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    searchUiState: SearchUiState,
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    setUiState: (String?, String?, SortOption.Search?, SortOption.Timeframe?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isSearchBarFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            KiteSearchBar(
                query = searchUiState.query,
                backIcon = KiteIcons.Back,
                onBackClick = onBackClick,
                onSearch = {
                    focusManager.clearFocus(true)

                    if (checkIfValidUrl(it)) {
                        val pathSegments = it.split("/")

                        when (pathSegments.size) {
                            7, 8 -> {
                                onPostClick(
                                    pathSegments[4],
                                    pathSegments[6],
                                    null,
                                    null,
                                    it.contains("/s/")
                                )
                            }

                            9, 10 -> {
                                onPostClick(
                                    pathSegments[4],
                                    pathSegments[6],
                                    pathSegments[8],
                                    null,
                                    false
                                )
                            }

                            else -> Unit
                        }
                    } else {
                        onSearchClick(
                            searchUiState.sortOption,
                            searchUiState.timeframe,
                            searchUiState.subredditScope,
                            it
                        )
                    }
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { isSearchBarFocused = it.isFocused },
                leadingComposable = {
                    AnimatedVisibility(
                        visible = !searchUiState.subredditScope.isNullOrEmpty(),
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        InputChip(
                            selected = false,
                            onClick = {},
                            label = {
                                Text(
                                    text = searchUiState.subredditScope ?: "",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            modifier = Modifier.padding(end = 12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        focusRequester.requestFocus()

                                        if (!searchUiState.subredditScope.isNullOrEmpty()) {
                                            setUiState(null, "", null, null)
                                        }
                                    },
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
                },
                onClear = {
                    setUiState("", null, null, null)

                    if (!isSearchBarFocused) {
                        focusRequester.requestFocus()
                    }
                },
                inputFieldColors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                colors = SearchBarDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            ) {}
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }

            FlowRow(Modifier.padding(horizontal = 12.dp)) {
                SortChip(
                    onClick = { showBottomSheet = true },
                    currentSortOption = searchUiState.sortOption,
                    currentSortTimeframe = searchUiState.timeframe,
                )

                AnimatedVisibility(
                    visible = searchUiState.subredditScope.isNullOrEmpty() && !searchUiState.initialSubredditScope.isNullOrEmpty(),
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    AssistChip(
                        onClick = {
                            setUiState(null, searchUiState.initialSubredditScope, null, null)
                        },
                        label = {
                            Text(
                                text = searchUiState.initialSubredditScope ?: "",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 6.dp))

            KiteBottomSheet(
                showBottomSheet = showBottomSheet,
                sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                    keyboard?.show()
                },
            ) {
                Text(
                    text = "Sort options",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowRow(Modifier.padding(bottom = 12.dp)) {

                    SortOption.Search.entries.forEach { option ->
                        val selected = option == searchUiState.sortOption

                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (searchUiState.sortOption != option) {
                                    setUiState(null, null, option, null)
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(option.label),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }

                    if (searchUiState.sortOption != SortOption.Search.NEW) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            thickness = Dp.Hairline
                        )

                        SortOption.Timeframe.entries.forEach { timeframe ->
                            val selected = timeframe == searchUiState.timeframe

                            FilterChip(
                                selected = selected,
                                onClick = {
                                    if (searchUiState.timeframe != timeframe) {
                                        setUiState(null, null, null, timeframe)
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(timeframe.label),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun checkIfValidUrl(query: String): Boolean {
    val postPattern = "https://.*/r/.*/comments/.*/?(.*/.*)?"
    val postSharePattern = "https://.*/r/.*/s/.*/?(.*/.*)?"

    return when {
        query.matches(postPattern.toRegex()) -> true
        query.matches(postSharePattern.toRegex()) -> true
        else -> false
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenPreview() {
    KiteTheme {
        SearchScreen(
            searchUiState = SearchUiState(
                "test",
                "",
                "Test",
                SortOption.Search.RELEVANCE,
                SortOption.Timeframe.ALL
            ),
            onBackClick = {},
            onPostClick = { _, _, _, _, _ -> },
            onSearchClick = { _, _, _, _ -> },
            setUiState = { _, _, _, _ -> }
        )
    }
}