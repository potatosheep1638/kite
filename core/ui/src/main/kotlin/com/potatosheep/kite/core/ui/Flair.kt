package com.potatosheep.kite.core.ui

import com.potatosheep.kite.core.model.FlairComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.model.FlairComponentType
import com.potatosheep.kite.core.designsystem.KiteTheme

@Composable
fun Flair(
    flairComponents: List<FlairComponent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            flairComponents.forEachIndexed { index, component ->
                if (component.type == FlairComponentType.EMOJI) {
                    AsyncImage(
                        modifier = Modifier
                            .padding(
                                start =
                                if (index == 0)
                                    6.dp
                                else
                                    0.dp,
                                end =
                                if (index == flairComponents.size - 1)
                                    6.dp
                                else
                                    0.dp
                            )
                            .size(20.dp),
                        model = component.value,
                        contentDescription = "Emoji",
                        placeholder = null,
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = component.value,
                        modifier = Modifier
                            .padding(
                                start =
                                if (index == 0)
                                    6.dp
                                else
                                    0.dp,
                                end =
                                if (index == flairComponents.size - 1)
                                    6.dp
                                else
                                    0.dp
                            ),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FlairPreview() {
    KiteTheme {
        val flairComponents = listOf(
            FlairComponent(
                value = "Test",
                type = FlairComponentType.TEXT
            )
        )

        Surface {
            Flair(
                flairComponents = flairComponents,
                onClick = {},
                modifier = Modifier
            )
        }
    }
}