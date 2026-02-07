package com.potatosheep.kite.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.translation.R.string as Translation

@Composable
fun SortChip(
    onClick: () -> Unit,
    currentSortOption: SortOption.Search,
    currentSortTimeframe: SortOption.Timeframe,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        FilterChip(
            onClick = onClick,
            label = {
                Text(
                    text = "${stringResource(currentSortOption.label)} ${
                        if (currentSortOption != SortOption.Search.NEW)
                            " •  ${stringResource(currentSortTimeframe.label)}"
                        else
                            ""
                    }",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            selected = true,
            leadingIcon = {
                Icon(
                    imageVector = KiteIcons.Check,
                    contentDescription = stringResource(currentSortOption.label),
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = KiteIcons.DropdownAlt,
                    contentDescription = stringResource(Translation.content_desc_sort)
                )
            },
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}