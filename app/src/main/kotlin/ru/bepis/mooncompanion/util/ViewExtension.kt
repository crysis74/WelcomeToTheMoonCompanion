package ru.bepis.mooncompanion.util

import android.widget.RadioButton
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun BottomAppBar.setEnabledMenuItem(@IdRes idRes: Int, isEnabled: Boolean) {
    val item = menu.findItem(idRes)
    val alpha = if (isEnabled) 255 else 50
    checkNotNull(item.icon).alpha = alpha
    item.isEnabled = isEnabled
}

fun BottomAppBar.setIconMenuItem(@IdRes idRes: Int, @DrawableRes iconRes: Int) {
    menu.findItem(idRes).setIcon(iconRes)
}

fun isCheckedChangeFlow(button: RadioButton) = callbackFlow {
    button.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) trySend(button.id)
    }
    awaitClose {
        button.isChecked = false
        button.setOnCheckedChangeListener(null)
    }
}