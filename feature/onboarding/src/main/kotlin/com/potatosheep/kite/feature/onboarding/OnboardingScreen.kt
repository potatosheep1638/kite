package com.potatosheep.kite.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.model.UserConfig
import com.potatosheep.kite.core.common.R.string as commonStrings

@Composable
fun OnboardingRoute(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    val instancesUiState by viewModel.instancesUiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        onboardingUiState = onboardingUiState,
        instancesUiState = instancesUiState,
        onBackClick = onBackClick,
        onNextClick = onNextClick,
        getInstances = viewModel::getInstances,
        setInstance = viewModel::setInstance,
        setCustomInstance = viewModel::setCustomInstance,
        setUseCustomInstance = viewModel::setUseCustomInstance,
        setShouldOnboard = viewModel::setShouldOnboard,
        validateUrl = viewModel::validateUrl,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OnboardingScreen(
    onboardingUiState: OnboardingUiState,
    instancesUiState: InstancesUiState,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    getInstances: () -> Unit,
    setInstance: (String) -> Unit,
    setCustomInstance: (String) -> Unit,
    setUseCustomInstance: (Boolean) -> Unit,
    setShouldOnboard: (Boolean) -> Unit,
    validateUrl: (String) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = LocalBackgroundColor.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val configuration = LocalConfiguration.current

    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = KiteIcons.Back,
                onBackClick = onBackClick,
                title = "",
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
            Text(
                text = stringResource(commonStrings.onboarding_headline),
                modifier = Modifier
                    .padding(
                        top = 48.dp,
                        start = 12.dp,
                        end = 12.dp,
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = stringResource(commonStrings.onboarding_subtitle),
                modifier = Modifier
                    .padding(
                        top = 6.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 48.dp
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            when (onboardingUiState) {
                OnboardingUiState.Loading -> Unit
                is OnboardingUiState.Success -> {
                    val transitionState = remember {
                        MutableTransitionState(true).apply {
                            targetState = true
                        }
                    }

                    var currentCustomInstance by rememberSaveable { mutableStateOf(onboardingUiState.userConfig.customInstance) }
                    var currentSelectedOption by rememberSaveable { mutableStateOf(OnboardingOption.PUBLIC) }
                    var showInstanceDialog by remember { mutableStateOf(false) }

                    var isUrlValid by rememberSaveable { mutableStateOf(validateUrl(currentCustomInstance)) }

                    AnimatedVisibility(
                        visibleState = transitionState,
                        enter = fadeIn(),
                        exit = ExitTransition.None
                    ) {
                        Column {
                            OptionRow(
                                text = stringResource(commonStrings.settings_choose_instance),
                                description = stringResource(commonStrings.settings_instance_desc),
                                selected = currentSelectedOption == OnboardingOption.PUBLIC,
                                onClick = {
                                    currentSelectedOption = OnboardingOption.PUBLIC
                                    setUseCustomInstance(false)
                                }
                            )

                            if (currentSelectedOption == OnboardingOption.PUBLIC) {
                                Text(
                                    text = onboardingUiState.userConfig.instance.ifEmpty {
                                        stringResource(commonStrings.onboarding_no_instance)
                                    },
                                    modifier = Modifier
                                        .padding(
                                            top = 12.dp,
                                            start = 12.dp,
                                            end = 12.dp,
                                        )
                                        .fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Button(
                                    onClick = { showInstanceDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(
                                            start = 24.dp,
                                            top = 12.dp,
                                            bottom = 12.dp,
                                            end = 24.dp
                                        ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(
                                        text = stringResource(commonStrings.settings_choose_instance),
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            OptionRow(
                                text = stringResource(commonStrings.settings_choose_custom_instance),
                                description = stringResource(commonStrings.settings_custom_instance_desc),
                                selected = currentSelectedOption == OnboardingOption.CUSTOM,
                                onClick = {
                                    currentSelectedOption = OnboardingOption.CUSTOM
                                    setUseCustomInstance(true)
                                }
                            )

                            if (currentSelectedOption == OnboardingOption.CUSTOM) {
                                TextField(
                                    value = currentCustomInstance,
                                    onValueChange = {
                                        currentCustomInstance = it

                                        if (validateUrl(it)) {
                                            isUrlValid = true
                                            setCustomInstance(it)
                                        } else {
                                            isUrlValid = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 24.dp,
                                            top = 12.dp,
                                            bottom = 12.dp,
                                            end = 24.dp
                                        ),
                                    placeholder = {
                                        Text(
                                            text = "https://example.instance.com",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )

                                if (!isUrlValid) {
                                    Text(
                                        text = stringResource(commonStrings.invalid_url),
                                        modifier = Modifier
                                            .padding(
                                                start = 24.dp,
                                                end = 12.dp,
                                            )
                                            .fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            )

                            Button(
                                onClick = {
                                    setShouldOnboard(false)
                                    onNextClick()
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(
                                        bottom = 12.dp,
                                        end = 12.dp
                                    ),
                                enabled = when (currentSelectedOption) {
                                    OnboardingOption.PUBLIC -> {
                                        onboardingUiState.userConfig.instance.isNotEmpty()
                                    }

                                    OnboardingOption.CUSTOM -> {
                                        isUrlValid
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = stringResource(commonStrings.next),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    if (showInstanceDialog) {
                        getInstances()
                        InstanceDialog(
                            onboardingUiState = onboardingUiState,
                            instancesUiState = instancesUiState,
                            onDismissRequest = { showInstanceDialog = false },
                            onConfirmation = {
                                setInstance(it)
                                showInstanceDialog = false
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
private fun OptionRow(
    text: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
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
            RadioButton(
                selected = selected,
                modifier = Modifier.padding(end = 12.dp),
                onClick = null,
            )

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


@Composable
private fun InstanceDialog(
    onboardingUiState: OnboardingUiState.Success,
    instancesUiState: InstancesUiState,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    val scrollState = rememberScrollState()
    var currentSelectedInstance by rememberSaveable { mutableStateOf(onboardingUiState.userConfig.instance) }

    AlertDialog(
        properties = properties,
        modifier = modifier,
        icon = null,
        title = {
            Text(
                text = stringResource(commonStrings.settings_choose_instance),
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
                text = stringResource(commonStrings.confirm),
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

private enum class OnboardingOption {
    PUBLIC,
    CUSTOM
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    val instances = listOf(
        "https://l.opnxng.com",
        "https://libreddit.projectsegfau.lt",
        "https://libreddit.bus-hit.me",
        "https://redlib.catsarch.com",
    )

    val userConfig = UserConfig(
        instance = "",
        shouldHideOnboarding = true,
        showNsfw = true,
        blurNsfw = true,
        shouldUseCustomInstance = false,
        customInstance = ""
    )

    KiteTheme {
        OnboardingScreen(
            onBackClick = {},
            onNextClick = {},
            onboardingUiState = OnboardingUiState.Success(userConfig),
            instancesUiState = InstancesUiState.Success(instances),
            getInstances = {},
            setInstance = {},
            setCustomInstance = {},
            setUseCustomInstance = {},
            setShouldOnboard = {},
            validateUrl = { _ -> false }
        )
    }
}