package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Clip(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "Clip",
    val type: String = "VIDEO", // "VIDEO" or "IMAGE"
    val uri: String = "",
    val durationMs: Long = 4000L,
    val startTrimMs: Long = 0L,
    val endTrimMs: Long = 4000L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val rotation: Int = 0, // 0, 90, 180, 270
    val isFlippedHorizontal: Boolean = false,
    val isFlippedVertical: Boolean = false,
    val brightness: Float = 0f, // -0.5f to 0.5f
    val contrast: Float = 1.0f, // 0.5f to 1.5f
    val saturation: Float = 1.0f, // 0f to 2.0f
    val temperature: Float = 0f, // -0.5f to 0.5f
    val sharpness: Float = 1.0f,
    val filterEffect: String = "NORMAL", // NORMAL, CINEMATIC, CYBERPUNK, VINTAGE, NOIR, SUNSET, ANIME, GLITCH
    val transition: String = "NONE" // NONE, FADE, DISSOLVE, SLIDE, ZOOM, FLASH
) {
    val activeDurationMs: Long
        get() = ((endTrimMs - startTrimMs).coerceAtLeast(500L) / speed).toLong()
}

@JsonClass(generateAdapter = true)
data class TextOverlay(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "Title",
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 3000L,
    val colorHex: String = "#FFFFFF",
    val bgHex: String? = "#80000000",
    val fontSizeSp: Float = 22f,
    val xPercent: Float = 0.5f, // 0f..1f (center is 0.5)
    val yPercent: Float = 0.5f,
    val fontStyle: String = "BOLD" // DEFAULT, BOLD, SERIF, MONOSPACE, NEON
)

@JsonClass(generateAdapter = true)
data class AudioTrack(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Background Track",
    val artist: String = "MotionAI Sound",
    val durationMs: Long = 15000L,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val presetKey: String? = "ambient_synth"
)

enum class AspectRatio(val label: String, val ratioWidth: Int, val ratioHeight: Int, val floatRatio: Float) {
    PORTRAIT_9_16("9:16 (Reels/Shorts)", 9, 16, 9f / 16f),
    LANDSCAPE_16_9("16:9 (YouTube/Cinema)", 16, 9, 16f / 9f),
    SQUARE_1_1("1:1 (Square Feed)", 1, 1, 1f),
    PORTRAIT_4_5("4:5 (Instagram)", 4, 5, 4f / 5f)
}
