package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiteBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier
        ) {
            Column(Modifier.padding(horizontal = 12.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Preview
private fun SortOrderSheetPreview() {
    val showBottomSheet = true
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    val options = listOf("Option 1", "Option 2", "Option 3")
    val currentOption = options[0]

    KiteBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        onDismissRequest = {},
        content = {
            Text(
                text = "Options",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FlowRow(Modifier.padding(bottom = 12.dp)) {
                options.forEach { option ->
                    val selected = option == currentOption

                    FilterChip(
                        selected = selected,
                        onClick = {},
                        label = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }
    )
}