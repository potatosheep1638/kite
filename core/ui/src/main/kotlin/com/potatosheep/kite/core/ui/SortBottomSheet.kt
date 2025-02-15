package com.potatosheep.kite.core.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.designsystem.KiteBottomSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostSortBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    sortFunction: (SortOption.Post) -> Unit,
    onOptionClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultOption: SortOption.Post = SortOption.Post.HOT,
) {
    var selectedOption by rememberSaveable { mutableStateOf(defaultOption) }
    val sortOptions = SortOption.Post.entries

    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Text(
            text = "Sort options",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(Modifier.padding(bottom = 12.dp)) {

            sortOptions.forEach { option ->
                val selected = option == selectedOption

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (selectedOption != option) {
                            selectedOption = option

                            sortFunction(option)
                            onOptionClick()
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommentSortBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    sortFunction: (SortOption.Comment) -> Unit,
    onOptionClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultOption: SortOption.Comment = SortOption.Comment.CONFIDENCE,
) {
    val sortOptions = SortOption.Comment.entries
    var selectedOption by rememberSaveable { mutableStateOf(defaultOption) }

    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Text(
            text = "Sort options",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(Modifier.padding(bottom = 12.dp)) {

            sortOptions.forEach { option ->
                val selected = option == selectedOption

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (selectedOption != option) {
                            selectedOption = option

                            sortFunction(option)
                            onOptionClick()
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserSortBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    sortFunction: (SortOption.User) -> Unit,
    onOptionClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultOption: SortOption.User = SortOption.User.HOT,
) {
    val sortOptions = SortOption.User.entries
    var selectedOption by rememberSaveable { mutableStateOf(defaultOption) }

    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Text(
            text = "Sort options",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FlowRow(Modifier.padding(bottom = 12.dp)) {

            sortOptions.forEach { option ->
                val selected = option == selectedOption

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (selectedOption != option) {
                            selectedOption = option

                            sortFunction(option)
                            onOptionClick()
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
        }
    }
}