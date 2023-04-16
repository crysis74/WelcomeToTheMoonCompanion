package ru.bepis.mooncompanion.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.systemGestures
import androidx.core.view.updatePadding
import ru.bepis.mooncompanion.R
import ru.bepis.mooncompanion.util.hideSystemUI

class MainActivity : AppCompatActivity(R.layout.ac_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.hideSystemUI()
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets(systemGestures())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
    }
}