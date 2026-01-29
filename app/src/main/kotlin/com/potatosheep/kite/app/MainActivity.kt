package com.potatosheep.kite.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.potatosheep.kite.app.ui.KiteApp
import com.potatosheep.kite.app.ui.rememberAppState
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.feature.exception.ExceptionRoute
import com.potatosheep.kite.feature.image.nav.navigateToImage
import com.potatosheep.kite.feature.post.impl.nav.navigateToPost
import com.potatosheep.kite.feature.subreddit.nav.navigateToSubreddit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }

        init()
    }

    // App screen
    private fun init() {
        val splashScreen = installSplashScreen()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            setContent {
                enableEdgeToEdge()

                DisposableEffect(isSystemInDarkTheme()) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    )
                    onDispose {}
                }

                KiteTheme {
                    Surface {
                        ExceptionRoute(
                            throwable = throwable,
                            onBackClick = { finish() }
                        )
                    }
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.shouldKeepSplashScreen()
        }

        setContent {
            enableEdgeToEdge()

            DisposableEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                )
                onDispose {}
            }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            if (!uiState.shouldKeepSplashScreen()) {
                val appState = rememberAppState(
                    shouldShowOnboarding = uiState.shouldShowOnboarding
                )

                KiteTheme {
                    KiteApp(appState = appState)
                }

                if (uiState.isColdBoot) {
                    intentResolver(appState.navController, intent)
                    viewModel.isBooted()
                }

                // Allow intents to work when the app is running.
                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { intent ->
                        intentResolver(appState.navController, intent)
                    }

                    addOnNewIntentListener(listener)

                    onDispose { removeOnNewIntentListener(listener) }
                }
            }
        }
    }

    private fun intentResolver(navController: NavController, intent: Intent? = null) {

        val uri = intent?.data
        val uriString = intent?.data.toString()

        if (uri != null) {
            when {
                uriString.contains("/r/\\w+/comments/\\w+/comment".toRegex()) -> {
                    navController.navigateToPost(
                        subreddit = uri.pathSegments[1],
                        postId = uri.pathSegments[3],
                        commentId = uri.pathSegments[5]
                    )
                }

                uriString.contains("/r/\\w+/comments/\\w+".toRegex()) -> {
                    navController.navigateToPost(
                        subreddit = uri.pathSegments[1],
                        postId = uri.pathSegments[3],
                        commentId = null
                    )
                }

                uriString.contains("/r/\\w+/s/\\w+".toRegex()) -> {
                    navController.navigateToPost(
                        subreddit = uri.pathSegments[1],
                        postId = uri.pathSegments[3],
                        commentId = null,
                        isShareLink = true
                    )
                }

                uriString.contains("/r/\\w+".toRegex()) -> {
                    navController.navigateToSubreddit(
                        subreddit = uri.pathSegments.last()
                    )
                }

                uriString.contains("\\.jpg|\\.jpeg|\\.png|\\.gif".toRegex()) -> {
                    navController.navigateToImage(
                        imageLinks = listOf(uri.query!!.substringAfter("imageLinks=")),
                        captions = listOf(null)
                    )
                }

                else -> Unit
            }
        }
    }

    // Handles and displays uncaught exceptions.
    @Composable
    private fun UncaughtExceptionScreen(e: Throwable) {
        val scrollState = rememberScrollState()

        KiteTheme {
            Surface(
                color = if (isSystemInDarkTheme())
                    MaterialTheme.colorScheme.surfaceContainerHigh
                else
                    MaterialTheme.colorScheme.surface
            ) {

                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .consumeWindowInsets(padding)
                            .padding(padding)
                    ) {
                        Row(Modifier.horizontalScroll(scrollState)) {
                            Text(
                                text = e.stackTraceToString(),
                                modifier = Modifier,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text("-- END --")
                    }
                }
            }
        }
    }
}