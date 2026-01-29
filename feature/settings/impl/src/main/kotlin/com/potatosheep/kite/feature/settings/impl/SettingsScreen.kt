package com.potatosheep.kite.feature.settings.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SettingRow
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.UserConfig
import com.potatosheep.kite.core.translation.R.string as Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val instancesUiState by viewModel.instancesUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = uiState,
        instancesUiState = instancesUiState,
        onBackClick = onBackClick,
        setInstance = viewModel::setInstance,
        setBlurNsfw = viewModel::setBlurNsfw,
        setShowNsfw = viewModel::setShowNsfw,
        setBlurSpoiler = viewModel::setBlurSpoiler,
        setUseCustomInstance = viewModel::setUseCustomInstance,
        setCustomInstance = viewModel::setCustomInstance,
        exportSavedPosts = viewModel::exportBookmarks,
        importSavedPosts = viewModel::importBookmarks,
        writeFileIntent = viewModel::writeDocumentIntent,
        readFileIntent = viewModel::readDocumentIntent,
        loadInstances = viewModel::loadInstances,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    settingsUiState: SettingsUiState,
    instancesUiState: InstancesUiState,
    onBackClick: () -> Unit,
    setInstance: (String) -> Unit,
    setBlurNsfw: (Boolean) -> Unit,
    setShowNsfw: (Boolean) -> Unit,
    setBlurSpoiler: (Boolean) -> Unit,
    setUseCustomInstance: (Boolean) -> Unit,
    setCustomInstance: (String) -> Unit,
    exportSavedPosts: (Uri, Context) -> Unit,
    importSavedPosts: (Uri, Context) -> Unit,
    writeFileIntent: () -> Intent,
    readFileIntent: () -> Intent,
    loadInstances: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val fileWriterLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uri = data?.data

        uri?.let {
            exportSavedPosts(it, context)
        }
    }

    val fileReaderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uri = data?.data

        uri?.let {
            importSavedPosts(it, context)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = stringResource(Translation.settings_top_app_bar_title),
                backIcon = KiteIcons.Back,
                backIconContentDescription = "Back",
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
        },
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            Modifier
                .consumeWindowInsets(padding)
                .padding(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
                .verticalScroll(scrollState)
        ) {
            when (settingsUiState) {
                SettingsUiState.Loading -> Unit
                is SettingsUiState.Success -> {
                    var showInstanceDialog by remember { mutableStateOf(false) }
                    var showCustomInstanceDialog by remember { mutableStateOf(false) }

                    val configuration = LocalConfiguration.current

                    SettingsGroupLabel(
                        text = stringResource(Translation.settings_network),
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    SettingRow(
                        text = stringResource(Translation.instance),
                        description = stringResource(Translation.settings_instance_desc)
                    ) {
                        showInstanceDialog = true
                    }

                    HorizontalDivider(Modifier.padding(vertical = 6.dp))

                    SwitchRow(
                        text = stringResource(Translation.settings_custom_instance),
                        description = stringResource(Translation.settings_custom_instance_desc),
                        selected = settingsUiState.userConfig.shouldUseCustomInstance
                    ) {
                        setUseCustomInstance(!settingsUiState.userConfig.shouldUseCustomInstance)
                    }

                    AnimatedVisibility(
                        visible = settingsUiState.userConfig.shouldUseCustomInstance
                    ) {
                        SettingRow(
                            text = stringResource(Translation.settings_choose_custom_instance),
                            description = settingsUiState.userConfig.customInstance
                        ) {
                            showCustomInstanceDialog = true
                        }
                    }

                    Spacer(Modifier.padding(vertical = 6.dp))

                    SettingsGroupLabel(stringResource(Translation.posts))

                    SwitchRow(
                        text = stringResource(Translation.settings_disable_nsfw),
                        description = stringResource(Translation.settings_disable_nsfw_desc),
                        selected = !settingsUiState.userConfig.showNsfw
                    ) {
                        setShowNsfw(!settingsUiState.userConfig.showNsfw)
                    }

                    SwitchRow(
                        text = stringResource(Translation.settings_blur_nsfw),
                        description = stringResource(Translation.settings_blur_nsfw_desc),
                        selected = settingsUiState.userConfig.blurNsfw
                    ) {
                        setBlurNsfw(!settingsUiState.userConfig.blurNsfw)
                    }

                    SwitchRow(
                        text = stringResource(Translation.settings_blur_spoiler),
                        description = stringResource(Translation.settings_blur_spoiler_desc),
                        selected = settingsUiState.userConfig.blurSpoiler
                    ) {
                        setBlurSpoiler(!settingsUiState.userConfig.blurSpoiler)
                    }

                    Spacer(Modifier.padding(vertical = 6.dp))

                    SettingsGroupLabel(stringResource(Translation.settings_data))

                    SettingRow(
                        text = stringResource(Translation.settings_export_saved_posts),
                        description = stringResource(Translation.settings_export_saved_posts_desc)
                    ) {
                        fileWriterLauncher.launch(writeFileIntent())
                    }

                    SettingRow(
                        text = stringResource(Translation.settings_import_saved_posts),
                        description = stringResource(Translation.settings_import_saved_posts_desc)
                    ) {
                        fileReaderLauncher.launch(readFileIntent())
                    }

                    if (showInstanceDialog) {
                        loadInstances()
                        InstanceDialog(
                            settingsUiState = settingsUiState,
                            instancesUiState = instancesUiState,
                            onDismissRequest = { showInstanceDialog = false },
                            onConfirmation = {
                                if (settingsUiState.userConfig.instance != it) {
                                    setInstance(it)
                                }

                                showInstanceDialog = false
                            },
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp)
                        )
                    }

                    if (showCustomInstanceDialog) {
                        CustomInstanceDialog(
                            settingsUiState = settingsUiState,
                            onDismissRequest = { showCustomInstanceDialog = false },
                            onConfirmation = {
                                if (settingsUiState.userConfig.customInstance != it) {
                                    setCustomInstance(it)
                                }

                                showCustomInstanceDialog = false
                            },
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroupLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 6.dp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun InstanceDialog(
    settingsUiState: SettingsUiState.Success,
    instancesUiState: InstancesUiState,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    val scrollState = rememberScrollState()
    var currentSelectedInstance by rememberSaveable { mutableStateOf(settingsUiState.userConfig.instance) }

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = stringResource(Translation.settings_choose_instance),
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .selectableGroup()
            ) {
                when (instancesUiState) {
                    InstancesUiState.Loading -> Unit
                    is InstancesUiState.Success -> {
                        instancesUiState.instances.forEach { instance ->
                            ChooserRow(
                                text = instance,
                                selected = currentSelectedInstance == instance,
                            ) {
                                currentSelectedInstance = instance
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(
                text = stringResource(Translation.confirm),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onConfirmation(currentSelectedInstance) },
            )
        },
        dismissButton = null
    )
}

@Composable
private fun CustomInstanceDialog(
    settingsUiState: SettingsUiState.Success,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    var currentCustomInstance by rememberSaveable { mutableStateOf(settingsUiState.userConfig.customInstance) }

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = stringResource(Translation.settings_choose_custom_instance),
                fontSize = 19.sp,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            TextField(
                value = currentCustomInstance,
                onValueChange = {
                    currentCustomInstance = it
                },
                placeholder = {
                    Text(
                        text = "https://example.instance.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(
                text = "Confirm",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onConfirmation(currentCustomInstance) },
            )
        },
        dismissButton = null
    )
}

@Composable
private fun SwitchRow(
    text: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box (Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
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

                Spacer(Modifier.fillMaxWidth())
            }

            Switch(
                checked = selected,
                onCheckedChange = {
                    onClick()
                }
            )
        }
    }
}

@Composable
private fun ChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SettingsScreenPreview() {
    val userConfig = UserConfig(
        instance = "https://redlib.catsarch.com",
        shouldHideOnboarding = true,
        showNsfw = true,
        blurNsfw = true,
        shouldUseCustomInstance = false,
        customInstance = "",
        blurSpoiler = true
    )

    val instances = listOf(
        "https://l.opnxng.com",
        "https://libreddit.projectsegfau.lt",
        "https://libreddit.bus-hit.me",
        "https://redlib.catsarch.com",
    )

    KiteTheme {
        Surface {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(userConfig),
                instancesUiState = InstancesUiState.Success(instances),
                onBackClick = {},
                setInstance = {},
                setBlurNsfw = {},
                setUseCustomInstance = {},
                setCustomInstance = {},
                setShowNsfw = {},
                setBlurSpoiler = {},
                exportSavedPosts = { _, _ -> },
                importSavedPosts = { _, _ -> },
                writeFileIntent = { Intent() },
                readFileIntent = { Intent() },
                loadInstances = {},
                modifier = Modifier,
                scrollBehavior = null,
            )
        }
    }
}