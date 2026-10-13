package com.example.data.model

enum class AiToolType(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val modelTag: String
) {
    MOTION_TRANSFER(
        title = "AI Motion Transfer",
        subtitle = "Transfer dynamic camera or dance motion to any still portrait photo",
        iconName = "motion",
        modelTag = "Motion Diffusion v2"
    ),
    ANIME_STYLE(
        title = "Anime & Art Transformation",
        subtitle = "Transform videos or photos into Studio Ghibli, Cyberpunk, or Manga",
        iconName = "palette",
        modelTag = "Gemini / Neural Stylizer"
    ),
    PHOTO_TO_VIDEO(
        title = "Photo-to-Video Generator",
        subtitle = "Bring still photos to life with cinematic camera moves and animated physics",
        iconName = "videocam",
        modelTag = "Veo 3.1 / Gen-Video"
    ),
    BACKGROUND_REMOVAL(
        title = "Smart Background Removal",
        subtitle = "Segment subjects with pixel accuracy and replace background with video/colors",
        iconName = "cut",
        modelTag = "Segmentation Engine"
    ),
    VIDEO_ENHANCER(
        title = "AI Video Enhancer",
        subtitle = "Upscale clarity, smooth to 60 FPS, and enhance dynamic color contrast",
        iconName = "auto_awesome",
        modelTag = "Neural SuperRes"
    ),
    AUTO_CAPTIONS(
        title = "Smart Auto Captions",
        subtitle = "Extract speech and generate animated styled captions with timestamps",
        iconName = "subtitles",
        modelTag = "Gemini Audio Speech"
    )
}

enum class AiTaskStatus {
    IDLE,
    CONFIGURING,
    PROCESSING,
    SUCCESS,
    ERROR
}

data class AiTaskState(
    val taskId: String = java.util.UUID.randomUUID().toString(),
    val toolType: AiToolType = AiToolType.MOTION_TRANSFER,
    val status: AiTaskStatus = AiTaskStatus.IDLE,
    val progressPercent: Int = 0,
    val sourceUri: String? = null,
    val referenceUri: String? = null,
    val prompt: String = "",
    val stylePreset: String = "Cinematic Anime",
    val resultUri: String? = null,
    val resultPreviewTitle: String? = null,
    val errorMessage: String? = null,
    val logs: List<String> = emptyList(),
    val estimatedCost: String = "Free Preview / Configured API",
    val isDemoMode: Boolean = false
)

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val aspectRatio: String,
    val durationSec: Int,
    val clipSlots: Int,
    val musicName: String,
    val description: String,
    val tags: List<String>,
    val defaultTextOverlays: List<String> = emptyList()
)
