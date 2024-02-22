package androidx.compose.ui.platform

import androidx.compose.runtime.Composable

@Suppress(names = ["unused"])
object LocalSoftwareKeyboardController {
    val current
        @Composable
        get() = LocalSoftwareKeyboardController.current
}