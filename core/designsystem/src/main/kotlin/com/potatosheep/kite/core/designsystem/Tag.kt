package com.potatosheep.kite.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NsfwTag(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Red,
    textColor: Color = Color.White
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
    ) {
        Text(
            text = "NSFW",
            modifier = Modifier
                .padding(
                    horizontal = 6.dp,
                    vertical = 4.dp
                ),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun SpoilerTag(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
    ) {
        Text(
            text = "Spoiler",
            modifier = Modifier
                .padding(
                    horizontal = 6.dp,
                    vertical = 4.dp
                ),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun NsfwTagPreview() {
    KiteTheme {
        Surface {
            NsfwTag()
        }
    }
}

@Preview
@Composable
private fun SpoilerTagPreview() {
    KiteTheme {
        Surface {
            SpoilerTag()
        }
    }
}