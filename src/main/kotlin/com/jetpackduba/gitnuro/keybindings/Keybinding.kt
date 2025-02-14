@file:OptIn(ExperimentalComposeUiApi::class)

package com.jetpackduba.gitnuro.keybindings

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import com.jetpackduba.gitnuro.system.OS
import com.jetpackduba.gitnuro.system.getCurrentOs

data class Keybinding(
    val alt: Boolean = false,
    val control: Boolean = false,
    val meta: Boolean = false,
    val shift: Boolean = false,
    val key: Key
)

enum class KeybindingOption {
    REFRESH,

    /**
     * Used mostly for dialogs with a single input field
     */
    SIMPLE_ACCEPT,

    /**
     * Used to accept multi-line text field like the commit message
     */
    TEXT_ACCEPT,

    /**
     * Used to close dialogs or components
     */
    EXIT,

    /**
     * Used to go up in lists
     */
    UP,

    /**
     * Used to go down in lists
     */
    DOWN,
}


@OptIn(ExperimentalComposeUiApi::class)
private fun baseKeybindings() = mapOf(
    KeybindingOption.REFRESH to listOf(
        Keybinding(key = Key.F5),
        Keybinding(control = true, key = Key.R),
    ),
    KeybindingOption.SIMPLE_ACCEPT to listOf(
        Keybinding(key = Key.Enter),
    ),
    KeybindingOption.TEXT_ACCEPT to listOf(
        Keybinding(control = true, key = Key.Enter),
    ),
    KeybindingOption.EXIT to listOf(
        Keybinding(key = Key.Escape),
    ),
    KeybindingOption.UP to listOf(
        Keybinding(key = Key.DirectionUp),
    ),
    KeybindingOption.DOWN to listOf(
        Keybinding(key = Key.DirectionDown),
    ),
)

private fun linuxKeybindings(): Map<KeybindingOption, List<Keybinding>> = baseKeybindings()
private fun windowsKeybindings(): Map<KeybindingOption, List<Keybinding>> = baseKeybindings()

private fun macKeybindings(): Map<KeybindingOption, List<Keybinding>> {
    val macBindings = baseKeybindings().toMutableMap()

    macBindings.apply {
        this[KeybindingOption.REFRESH] = listOf(
            Keybinding(key = Key.F5),
            Keybinding(meta = true, key = Key.R),
        )
    }

    return macBindings
}

val keybindings by lazy {
    return@lazy when (getCurrentOs()) {
        OS.LINUX -> linuxKeybindings()
        OS.WINDOWS -> windowsKeybindings()
        OS.MAC -> macKeybindings()
        OS.UNKNOWN -> baseKeybindings()
    }
}

fun KeyEvent.matchesBinding(keybindingOption: KeybindingOption): Boolean {
    val keybindings = keybindings

    val matchingKeybindingsList = keybindings[keybindingOption] ?: return false

    return matchingKeybindingsList.any { keybinding ->
        keybinding.alt == this.isAltPressed &&
                keybinding.control == this.isCtrlPressed &&
                keybinding.meta == this.isMetaPressed &&
                keybinding.shift == this.isShiftPressed &&
                keybinding.key == this.key
    } && this.type == KeyEventType.KeyDown
}