package com.potatosheep.kite.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState){
    fun navigate(route: NavKey, navOption: NavOption = NavOption.DEFAULT){
        if (route in state.backStacks.keys){
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            when (navOption) {
                NavOption.SINGLE_TOP -> {
                    val iterator = state.backStacks[state.topLevelRoute]?.listIterator()

                    if (iterator != null) {
                        while (iterator.hasNext()) {
                            val value = iterator.next()

                            if (value::class == route::class) {
                                iterator.remove()
                            }
                        }
                    }

                    state.backStacks[state.topLevelRoute]?.add(route)
                }

                NavOption.DESTROY_PREV_NAV -> {
                    val last = state.backStacks[state.topLevelRoute]?.last()
                    state.backStacks[state.topLevelRoute]?.add(route)
                    state.backStacks[state.topLevelRoute]?.remove(last)
                }

                else -> state.backStacks[state.topLevelRoute]?.add(route)
            }
        }
    }

    fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}

enum class NavOption {
    /**
     * Navigate to this route as normal.
     */
    DEFAULT,

    /**
     * Before navigating to this route, any previous instance of this route in the back stack will
     * be removed.
     */
    SINGLE_TOP,

    /**
     * After navigating to this route, remove the previous route from the back stack.
     */
    DESTROY_PREV_NAV
}