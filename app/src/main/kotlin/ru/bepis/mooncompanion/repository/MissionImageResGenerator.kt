package ru.bepis.mooncompanion.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.bepis.mooncompanion.repository.model.MissionResp

class MissionImageResGenerator(private val context: Context) {

    @SuppressLint("DiscouragedApi", "SupportAnnotationUsage")
    @DrawableRes
    suspend fun generate(
        fieldNumber: Int,
        mission: MissionResp.MissionRespExtra
    ): Int = withContext(Dispatchers.IO) {
        val postfix =
            "${fieldNumber}_${mission.type.name.lowercase()}_${mission.imageOrdinal}"
        val name = "$MISSION_IMAGE_PREFIX$postfix"
        context.resources.getIdentifier(name, DRAWABLE_DEF_TYPE, context.packageName)
            .takeIf { it != 0 } ?: throw Exception("DrawableRes not found. Query: $name")
    }

    companion object {
        private const val DRAWABLE_DEF_TYPE = "drawable"
        private const val MISSION_IMAGE_PREFIX = "ic_mission_"
    }
}