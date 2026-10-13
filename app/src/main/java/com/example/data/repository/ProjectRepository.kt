package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.model.AudioTrack
import com.example.data.model.Clip
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectMappers
import com.example.data.model.TemplateItem
import com.example.data.model.TextOverlay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProject(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    suspend fun createNewProject(
        title: String = "Neon Cyberpunk Reel",
        aspectRatio: String = "9:16",
        initialClips: List<Clip> = emptyList()
    ): Long {
        val clips = initialClips.ifEmpty { getDefaultStarterClips() }
        val texts = getDefaultStarterTexts()
        val audio = AudioTrack(
            name = "Synthwave Odyssey",
            artist = "MotionAI Audio Lab",
            durationMs = 12000L,
            presetKey = "synthwave"
        )

        val totalDuration = clips.sumOf { it.activeDurationMs }

        val entity = ProjectEntity(
            title = title,
            aspectRatio = aspectRatio,
            thumbnailUri = clips.firstOrNull()?.uri,
            clipsJson = ProjectMappers.clipsToJson(clips),
            textOverlaysJson = ProjectMappers.textOverlaysToJson(texts),
            audioTrackJson = ProjectMappers.audioTrackToJson(audio),
            totalDurationMs = totalDuration,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            status = "Draft"
        )
        return projectDao.insertProject(entity)
    }

    suspend fun createFromTemplate(template: TemplateItem): Long {
        val sampleClips = listOf(
            Clip(
                title = "Intro Shot",
                type = "IMAGE",
                durationMs = 3000L,
                filterEffect = if (template.category == "Anime") "ANIME" else "CINEMATIC",
                transition = "DISSOLVE"
            ),
            Clip(
                title = "Action Climax",
                type = "IMAGE",
                durationMs = 4000L,
                filterEffect = if (template.category == "Phonk") "GLITCH" else "CYBERPUNK",
                speed = 1.25f,
                transition = "ZOOM"
            ),
            Clip(
                title = "Outro Reveal",
                type = "IMAGE",
                durationMs = 3000L,
                filterEffect = "VINTAGE",
                transition = "FADE"
            )
        )

        val sampleTexts = template.defaultTextOverlays.mapIndexed { idx, txt ->
            TextOverlay(
                text = txt,
                startTimeMs = (idx * 3000L),
                endTimeMs = (idx * 3000L + 2500L),
                yPercent = 0.75f,
                colorHex = "#06B6D4"
            )
        }.ifEmpty {
            listOf(
                TextOverlay(
                    text = template.title,
                    startTimeMs = 0L,
                    endTimeMs = 3000L,
                    yPercent = 0.7f
                )
            )
        }

        val entity = ProjectEntity(
            title = template.title,
            aspectRatio = template.aspectRatio,
            thumbnailUri = null,
            clipsJson = ProjectMappers.clipsToJson(sampleClips),
            textOverlaysJson = ProjectMappers.textOverlaysToJson(sampleTexts),
            audioTrackJson = ProjectMappers.audioTrackToJson(
                AudioTrack(
                    name = template.musicName,
                    artist = "MotionAI Beats",
                    durationMs = (template.durationSec * 1000).toLong()
                )
            ),
            totalDurationMs = (template.durationSec * 1000).toLong(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            status = "Draft"
        )
        return projectDao.insertProject(entity)
    }

    suspend fun saveProjectState(
        id: Long,
        title: String,
        aspectRatio: String,
        clips: List<Clip>,
        textOverlays: List<TextOverlay>,
        audioTrack: AudioTrack?,
        status: String = "Draft"
    ) {
        val totalDuration = clips.sumOf { it.activeDurationMs }
        val updated = ProjectEntity(
            id = id,
            title = title,
            aspectRatio = aspectRatio,
            thumbnailUri = clips.firstOrNull()?.uri,
            clipsJson = ProjectMappers.clipsToJson(clips),
            textOverlaysJson = ProjectMappers.textOverlaysToJson(textOverlays),
            audioTrackJson = ProjectMappers.audioTrackToJson(audioTrack),
            totalDurationMs = totalDuration,
            updatedAt = System.currentTimeMillis(),
            status = status
        )
        projectDao.updateProject(updated)
    }

    suspend fun duplicateProject(id: Long): Long {
        val existing = projectDao.getProjectByIdDirect(id) ?: return -1L
        val copy = existing.copy(
            id = 0,
            title = "${existing.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return projectDao.insertProject(copy)
    }

    suspend fun deleteProject(id: Long) {
        projectDao.deleteProjectById(id)
    }

    suspend fun deleteAll() {
        projectDao.deleteAllProjects()
    }

    suspend fun ensureInitialData() {
        val currentList = allProjects.firstOrNull()
        if (currentList.isNullOrEmpty()) {
            createNewProject(
                title = "Tokyo Neon Night Drift",
                aspectRatio = "9:16",
                initialClips = getDefaultStarterClips()
            )
            createNewProject(
                title = "Cyberpunk AI Action Cut",
                aspectRatio = "16:9",
                initialClips = listOf(
                    Clip(
                        title = "City Drone Pass",
                        durationMs = 5000L,
                        filterEffect = "CYBERPUNK",
                        contrast = 1.2f,
                        saturation = 1.3f
                    ),
                    Clip(
                        title = "Speed Highway",
                        durationMs = 4000L,
                        filterEffect = "GLITCH",
                        speed = 1.5f
                    )
                )
            )
        }
    }

    private fun getDefaultStarterClips(): List<Clip> {
        return listOf(
            Clip(
                title = "Scene 1: Neon Entrance",
                type = "IMAGE",
                durationMs = 3500L,
                filterEffect = "CYBERPUNK",
                brightness = 0.05f,
                contrast = 1.2f,
                saturation = 1.35f,
                temperature = 0.1f,
                transition = "DISSOLVE"
            ),
            Clip(
                title = "Scene 2: Speed Burst",
                type = "IMAGE",
                durationMs = 3000L,
                speed = 1.25f,
                filterEffect = "GLITCH",
                contrast = 1.15f,
                saturation = 1.4f,
                transition = "ZOOM"
            ),
            Clip(
                title = "Scene 3: Horizon Glow",
                type = "IMAGE",
                durationMs = 4000L,
                filterEffect = "SUNSET",
                temperature = 0.3f,
                saturation = 1.2f,
                transition = "FADE"
            )
        )
    }

    private fun getDefaultStarterTexts(): List<TextOverlay> {
        return listOf(
            TextOverlay(
                text = "MOTION AI STUDIO",
                startTimeMs = 200L,
                endTimeMs = 3200L,
                colorHex = "#06B6D4",
                yPercent = 0.25f,
                fontStyle = "NEON"
            ),
            TextOverlay(
                text = "Turn imagination into motion",
                startTimeMs = 3300L,
                endTimeMs = 6500L,
                colorHex = "#F8FAFC",
                yPercent = 0.72f,
                fontStyle = "BOLD"
            )
        )
    }
}
