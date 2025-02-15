package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingRow(
    text: String,
    description: String,
    onClick: () -> Unit,
) {
    Box (
        Modifier
        .fillMaxWidth()
        .clickable(
            onClick = onClick
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(bottom = 2.dp),
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}