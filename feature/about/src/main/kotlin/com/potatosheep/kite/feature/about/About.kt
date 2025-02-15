package com.potatosheep.kite.feature.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SettingRow
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.common.R.string as commonStrings

@Composable
fun AboutRoute(
    version: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AboutScreen(
        version = version,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen(
    version: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = LocalBackgroundColor.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                backIconContentDescription = stringResource(commonStrings.back),
                onBackClick = onBackClick,
                title = stringResource(commonStrings.about),
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
            )
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
            SettingRow(
                text = "${stringResource(commonStrings.about_version)}",
                description = version
            ) { }

            SettingRow(
                text = stringResource(commonStrings.about_source_code),
                description = stringResource(commonStrings.about_source_code_desc)
            ) { }


            Text(
                text = stringResource(commonStrings.about_license),
                modifier = Modifier.padding(
                    24.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    KiteTheme {
        AboutScreen(
            version = "0.0.1",
            onBackClick = {}
        )
    }
}