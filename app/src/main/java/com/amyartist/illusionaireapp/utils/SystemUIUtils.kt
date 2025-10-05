package com.amyartist.illusionaireapp.utils

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Hides the system bars (status bar and navigation bar) for a more immersive experience.
 *
 * @param window The current Activity's Window.
 * @param view The root view of your Activity's layout, or any view within the window.
 *             This is used by WindowInsetsControllerCompat.
 */
fun hideSystemUI(window: Window, view: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, view)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

/**
 * Shows the system bars (status bar and navigation bar).
 *
 * @param window The current Activity's Window.
 * @param view The root view of your Activity's layout, or any view within the window.
 */
fun showSystemUI(window: Window, view: View) {
    WindowCompat.setDecorFitsSystemWindows(window, true) // Let the decor view fit system windows again
    val controller = WindowInsetsControllerCompat(window, view)
    controller.show(WindowInsetsCompat.Type.systemBars())
}
