package ru.bepis.mooncompanion.util

import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun BottomSheetDialogFragment.requireBottomSheetDialog() = requireDialog() as BottomSheetDialog

fun BottomSheetDialog.requireRootView(): FrameLayout =
    requireNotNull(findViewById(com.google.android.material.R.id.design_bottom_sheet))