package com.potatosheep.kite.core.designsystem

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

fun defaultTransitionSpec(): Map<String, Any> {
    return NavDisplay.transitionSpec {
        fadeIn() + slideIntoContainer(SlideDirection.Start) togetherWith
                fadeOut() + scaleOut(targetScale = 0.6f) + slideOutOfContainer(SlideDirection.Start)
    } + NavDisplay.popTransitionSpec {
        fadeIn() + slideIntoContainer(SlideDirection.End, initialOffset = { it / 4 }) togetherWith
                fadeOut() +
                slideOutOfContainer(towards = SlideDirection.End, targetOffset = { it / 12 })
    } + NavDisplay.predictivePopTransitionSpec {
        fadeIn() + scaleIn(initialScale = 0.6f) +
                slideIntoContainer(SlideDirection.End, initialOffset = { it / 4 }) togetherWith
                fadeOut() +
                scaleOut( targetScale = 0.8f) +
                slideOutOfContainer(towards = SlideDirection.End, targetOffset = { it / 12 })
    }
}