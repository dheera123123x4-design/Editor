package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AiTaskState
import com.example.data.model.AiToolType
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrack
import com.example.data.model.Clip
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectMappers
import com.example.data.model.TemplateItem
import com.example.data.model.TextOverlay
import com.example.data.remote.GeminiAiService
import com.example.data.repository.AiToolsRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class StudioTab {
    HOME,
    AI_TOOLS,
    PROJECTS,
    TEMPLATES,
    PROFILE
}

enum class StudioScreen {
    MAIN_TABS,
    EDITOR,
    EXPORT,
    AI_TOOL_DETAIL,
    ONBOARDING
}

data class EditorSnapshot(
    val clips: List<Clip>,
    val textOverlays: List<TextOverlay>,
    val audioTrack: AudioTrack?
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectRepository = ProjectRepository(db.projectDao())
    private val aiRepository = AiToolsRepository()

    val allProjects: StateFlow<List<ProjectEntity>> = projectRepository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation
    private val _activeTab = MutableStateFlow(StudioTab.HOME)
    val activeTab: StateFlow<StudioTab> = _activeTab.asStateFlow()

    private val _currentScreen = MutableStateFlow(StudioScreen.MAIN_TABS)
    val currentScreen: StateFlow<StudioScreen> = _currentScreen.asStateFlow()

    // Editing Project State
    private val _currentProject = MutableStateFlow<ProjectEntity?>(null)
    val currentProject: StateFlow<ProjectEntity?> = _currentProject.asStateFlow()

    private val _projectTitle = MutableStateFlow("Untitled Project")
    val projectTitle: StateFlow<String> = _projectTitle.asStateFlow()

    private val _aspectRatio = MutableStateFlow(AspectRatio.PORTRAIT_9_16)
    val aspectRatio: StateFlow<AspectRatio> = _aspectRatio.asStateFlow()

    private val _clips = MutableStateFlow<List<Clip>>(emptyList())
    val clips: StateFlow<List<Clip>> = _clips.asStateFlow()

    private val _textOverlays = MutableStateFlow<List<TextOverlay>>(emptyList())
    val textOverlays: StateFlow<List<TextOverlay>> = _textOverlays.asStateFlow()

    private val _audioTrack = MutableStateFlow<AudioTrack?>(null)
    val audioTrack: StateFlow<AudioTrack?> = _audioTrack.asStateFlow()

    private val _selectedClipIndex = MutableStateFlow(0)
    val selectedClipIndex: StateFlow<Int> = _selectedClipIndex.asStateFlow()

    private val _selectedTextIndex = MutableStateFlow(-1)
    val selectedTextIndex: StateFlow<Int> = _selectedTextIndex.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    // Undo / Redo
    private val undoStack = mutableListOf<EditorSnapshot>()
    private val redoStack = mutableListOf<EditorSnapshot>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // AI Tools State
    private val _selectedAiTool = MutableStateFlow(AiToolType.MOTION_TRANSFER)
    val selectedAiTool: StateFlow<AiToolType> = _selectedAiTool.asStateFlow()

    private val _activeAiTask = MutableStateFlow(AiTaskState())
    val activeAiTask: StateFlow<AiTaskState> = _activeAiTask.asStateFlow()

    // Export State
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0)
    val exportProgress: StateFlow<Int> = _exportProgress.asStateFlow()

    private val _exportResolution = MutableStateFlow("1080p Full HD")
    val exportResolution: StateFlow<String> = _exportResolution.asStateFlow()

    private val _exportFps = MutableStateFlow(30)
    val exportFps: StateFlow<Int> = _exportFps.asStateFlow()

    private val _exportedFilePath = MutableStateFlow<String?>(null)
    val exportedFilePath: StateFlow<String?> = _exportedFilePath.asStateFlow()

    // Notifications / Toasts
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var playbackJob: Job? = null
    private var aiTaskJob: Job? = null
    private var exportJob: Job? = null

    init {
        viewModelScope.launch {
            projectRepository.ensureInitialData()
        }
    }

    fun selectTab(tab: StudioTab) {
        _activeTab.value = tab
        _currentScreen.value = StudioScreen.MAIN_TABS
    }

    fun openScreen(screen: StudioScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (_isPlaying.value) togglePlayPause()
        if (_currentScreen.value != StudioScreen.MAIN_TABS) {
            _currentScreen.value = StudioScreen.MAIN_TABS
        }
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    fun clearToast() {
        _userMessage.value = null
    }

    // --- Project Operations ---

    fun openProject(project: ProjectEntity) {
        _currentProject.value = project
        _projectTitle.value = project.title
        _aspectRatio.value = when (project.aspectRatio) {
            "16:9" -> AspectRatio.LANDSCAPE_16_9
            "1:1" -> AspectRatio.SQUARE_1_1
            "4:5" -> AspectRatio.PORTRAIT_4_5
            else -> AspectRatio.PORTRAIT_9_16
        }
        val loadedClips = ProjectMappers.jsonToClips(project.clipsJson)
        _clips.value = loadedClips
        _textOverlays.value = ProjectMappers.jsonToTextOverlays(project.textOverlaysJson)
        _audioTrack.value = ProjectMappers.jsonToAudioTrack(project.audioTrackJson)
        _selectedClipIndex.value = if (loadedClips.isNotEmpty()) 0 else -1
        _selectedTextIndex.value = -1
        _currentPositionMs.value = 0L
        _isPlaying.value = false

        undoStack.clear()
        redoStack.clear()
        updateUndoRedoStates()

        _currentScreen.value = StudioScreen.EDITOR
    }

    fun createNewProject(ratio: AspectRatio = AspectRatio.PORTRAIT_9_16, initialMediaUri: String? = null) {
        viewModelScope.launch {
            val starterClips = if (!initialMediaUri.isNullOrBlank()) {
                listOf(
                    Clip(
                        title = "Imported Media",
                        uri = initialMediaUri,
                        type = if (initialMediaUri.endsWith(".mp4", ignoreCase = true)) "VIDEO" else "IMAGE",
                        durationMs = 5000L
                    )
                )
            } else emptyList()

            val ratioStr = when (ratio) {
                AspectRatio.LANDSCAPE_16_9 -> "16:9"
                AspectRatio.SQUARE_1_1 -> "1:1"
                AspectRatio.PORTRAIT_4_5 -> "4:5"
                AspectRatio.PORTRAIT_9_16 -> "9:16"
            }

            val newId = projectRepository.createNewProject(
                title = "Motion Project #${(100..999).random()}",
                aspectRatio = ratioStr,
                initialClips = starterClips
            )
            val newProject = db.projectDao().getProjectByIdDirect(newId)
            if (newProject != null) {
                openProject(newProject)
            }
        }
    }

    fun createProjectFromTemplate(template: TemplateItem) {
        viewModelScope.launch {
            val newId = projectRepository.createFromTemplate(template)
            val newProject = db.projectDao().getProjectByIdDirect(newId)
            if (newProject != null) {
                openProject(newProject)
                showToast("Applied template: ${template.title}")
            }
        }
    }

    fun duplicateProject(id: Long) {
        viewModelScope.launch {
            projectRepository.duplicateProject(id)
            showToast("Project duplicated successfully")
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(id)
            if (_currentProject.value?.id == id) {
                _currentProject.value = null
                _currentScreen.value = StudioScreen.MAIN_TABS
            }
            showToast("Project deleted")
        }
    }

    fun renameProject(id: Long, newTitle: String) {
        viewModelScope.launch {
            val project = db.projectDao().getProjectByIdDirect(id) ?: return@launch
            val updated = project.copy(title = newTitle.trim(), updatedAt = System.currentTimeMillis())
            db.projectDao().updateProject(updated)
            if (_currentProject.value?.id == id) {
                _projectTitle.value = newTitle.trim()
                _currentProject.value = updated
            }
            showToast("Project renamed")
        }
    }

    fun saveCurrentProject() {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            projectRepository.saveProjectState(
                id = proj.id,
                title = _projectTitle.value,
                aspectRatio = _aspectRatio.value.label.substringBefore(" "),
                clips = _clips.value,
                textOverlays = _textOverlays.value,
                audioTrack = _audioTrack.value
            )
            showToast("Project saved")
        }
    }

    // --- Timeline & Playback ---

    val totalDurationMs: Long
        get() = _clips.value.sumOf { it.activeDurationMs }.coerceAtLeast(1000L)

    fun togglePlayPause() {
        val nextPlay = !_isPlaying.value
        _isPlaying.value = nextPlay
        if (nextPlay) {
            startPlaybackLoop()
        } else {
            playbackJob?.cancel()
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 50L
            while (isActive && _isPlaying.value) {
                delay(stepMs)
                val total = totalDurationMs
                val advance = (stepMs * _playbackSpeed.value).toLong()
                val nextPos = _currentPositionMs.value + advance
                if (nextPos >= total) {
                    _currentPositionMs.value = 0L // Loop
                } else {
                    _currentPositionMs.value = nextPos
                }
            }
        }
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs.coerceIn(0L, totalDurationMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    // --- Editor Clip Operations ---

    private fun pushSnapshot() {
        undoStack.add(
            EditorSnapshot(
                clips = _clips.value,
                textOverlays = _textOverlays.value,
                audioTrack = _audioTrack.value
            )
        )
        redoStack.clear()
        updateUndoRedoStates()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(
                EditorSnapshot(
                    clips = _clips.value,
                    textOverlays = _textOverlays.value,
                    audioTrack = _audioTrack.value
                )
            )
            _clips.value = prev.clips
            _textOverlays.value = prev.textOverlays
            _audioTrack.value = prev.audioTrack
            updateUndoRedoStates()
            showToast("Undone")
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(
                EditorSnapshot(
                    clips = _clips.value,
                    textOverlays = _textOverlays.value,
                    audioTrack = _audioTrack.value
                )
            )
            _clips.value = next.clips
            _textOverlays.value = next.textOverlays
            _audioTrack.value = next.audioTrack
            updateUndoRedoStates()
            showToast("Redone")
        }
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun selectClip(index: Int) {
        if (index in _clips.value.indices) {
            _selectedClipIndex.value = index
        }
    }

    fun updateAspectRatio(ratio: AspectRatio) {
        pushSnapshot()
        _aspectRatio.value = ratio
    }

    fun addClip(title: String = "New Scene", uri: String = "", type: String = "IMAGE") {
        pushSnapshot()
        val newClip = Clip(
            title = title,
            uri = uri,
            type = type,
            durationMs = 4000L
        )
        val updated = _clips.value + newClip
        _clips.value = updated
        _selectedClipIndex.value = updated.lastIndex
        showToast("Added clip to timeline")
    }

    fun removeClip(index: Int) {
        if (_clips.value.size <= 1) {
            showToast("Project requires at least one clip")
            return
        }
        pushSnapshot()
        val list = _clips.value.toMutableList()
        list.removeAt(index)
        _clips.value = list
        _selectedClipIndex.value = (index - 1).coerceAtLeast(0)
        showToast("Clip removed")
    }

    fun duplicateClip(index: Int) {
        val current = _clips.value.getOrNull(index) ?: return
        pushSnapshot()
        val copy = current.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${current.title} (Copy)"
        )
        val list = _clips.value.toMutableList()
        list.add(index + 1, copy)
        _clips.value = list
        _selectedClipIndex.value = index + 1
        showToast("Clip duplicated")
    }

    fun splitClipAtPlayhead() {
        val index = _selectedClipIndex.value
        val clip = _clips.value.getOrNull(index) ?: return
        val clipDuration = clip.activeDurationMs
        if (clipDuration < 1500L) {
            showToast("Clip too short to split")
            return
        }
        pushSnapshot()
        val halfDuration = clipDuration / 2
        val clipA = clip.copy(
            id = java.util.UUID.randomUUID().toString(),
            endTrimMs = clip.startTrimMs + halfDuration
        )
        val clipB = clip.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${clip.title} (Part 2)",
            startTrimMs = clip.startTrimMs + halfDuration
        )
        val list = _clips.value.toMutableList()
        list[index] = clipA
        list.add(index + 1, clipB)
        _clips.value = list
        _selectedClipIndex.value = index + 1
        showToast("Split clip at playhead")
    }

    fun moveClip(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in _clips.value.indices || toIndex !in _clips.value.indices) return
        pushSnapshot()
        val list = _clips.value.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        _clips.value = list
        _selectedClipIndex.value = toIndex
    }

    fun updateSelectedClip(transform: (Clip) -> Clip) {
        val idx = _selectedClipIndex.value
        if (idx !in _clips.value.indices) return
        pushSnapshot()
        val list = _clips.value.toMutableList()
        list[idx] = transform(list[idx])
        _clips.value = list
    }

    fun rotateSelectedClip() {
        updateSelectedClip { clip ->
            clip.copy(rotation = (clip.rotation + 90) % 360)
        }
    }

    fun flipHorizontalSelectedClip() {
        updateSelectedClip { clip ->
            clip.copy(isFlippedHorizontal = !clip.isFlippedHorizontal)
        }
    }

    fun applyFilterToSelectedClip(filterName: String) {
        updateSelectedClip { clip ->
            clip.copy(filterEffect = filterName)
        }
        showToast("Applied $filterName filter")
    }

    fun applyTransitionToSelectedClip(transitionName: String) {
        updateSelectedClip { clip ->
            clip.copy(transition = transitionName)
        }
        showToast("Applied $transitionName transition")
    }

    // --- Text Overlays & Subtitles ---

    fun addTextOverlay(text: String = "YOUR TEXT HERE") {
        pushSnapshot()
        val start = _currentPositionMs.value
        val newText = TextOverlay(
            text = text,
            startTimeMs = start,
            endTimeMs = (start + 3000L).coerceAtMost(totalDurationMs)
        )
        val updated = _textOverlays.value + newText
        _textOverlays.value = updated
        _selectedTextIndex.value = updated.lastIndex
        showToast("Added text overlay")
    }

    fun removeTextOverlay(index: Int) {
        if (index !in _textOverlays.value.indices) return
        pushSnapshot()
        val list = _textOverlays.value.toMutableList()
        list.removeAt(index)
        _textOverlays.value = list
        _selectedTextIndex.value = -1
        showToast("Removed text")
    }

    fun updateSelectedText(transform: (TextOverlay) -> TextOverlay) {
        val idx = _selectedTextIndex.value
        if (idx !in _textOverlays.value.indices) return
        pushSnapshot()
        val list = _textOverlays.value.toMutableList()
        list[idx] = transform(list[idx])
        _textOverlays.value = list
    }

    fun generateAiCaptions(promptContext: String) {
        viewModelScope.launch {
            showToast("Generating AI subtitles with Gemini...")
            val result = aiRepository.generateAutoCaptions(promptContext)
            result.onSuccess { cues ->
                pushSnapshot()
                val overlays = cues.map { cue ->
                    TextOverlay(
                        text = cue.text,
                        startTimeMs = cue.startMs,
                        endTimeMs = cue.endMs,
                        colorHex = "#22D3EE",
                        yPercent = 0.8f,
                        fontStyle = "BOLD"
                    )
                }
                _textOverlays.value = overlays
                showToast("Added ${overlays.size} auto-captions")
            }.onFailure { err ->
                showToast("Caption generation: ${err.message}")
            }
        }
    }

    // --- Audio Track ---

    fun setAudioTrack(track: AudioTrack?) {
        pushSnapshot()
        _audioTrack.value = track
        showToast(track?.let { "Selected soundtrack: ${it.name}" } ?: "Audio removed")
    }

    fun toggleAudioMute() {
        val track = _audioTrack.value ?: return
        pushSnapshot()
        _audioTrack.value = track.copy(isMuted = !track.isMuted)
    }

    fun setAudioVolume(volume: Float) {
        val track = _audioTrack.value ?: return
        _audioTrack.value = track.copy(volume = volume)
    }

    // --- AI Tools Operations ---

    fun selectAiTool(tool: AiToolType) {
        _selectedAiTool.value = tool
        _activeAiTask.value = AiTaskState(toolType = tool)
        _currentScreen.value = StudioScreen.AI_TOOL_DETAIL
    }

    fun runActiveAiTool(
        sourceUri: String?,
        referenceUri: String? = null,
        prompt: String = "",
        stylePreset: String = "Anime",
        consentGranted: Boolean = true
    ) {
        aiTaskJob?.cancel()
        val tool = _selectedAiTool.value
        aiTaskJob = viewModelScope.launch {
            val flow = when (tool) {
                AiToolType.MOTION_TRANSFER -> aiRepository.executeMotionTransfer(
                    sourceUri = sourceUri,
                    referenceUri = referenceUri,
                    prompt = prompt,
                    aspectRatio = _aspectRatio.value.label.substringBefore(" "),
                    userConsentGranted = consentGranted
                )
                AiToolType.ANIME_STYLE -> aiRepository.executeAnimeStyle(
                    sourceUri = sourceUri,
                    stylePreset = stylePreset,
                    prompt = prompt
                )
                AiToolType.PHOTO_TO_VIDEO -> aiRepository.executePhotoToVideo(
                    sourceUri = sourceUri,
                    prompt = prompt,
                    cameraMove = "Cinematic Zoom In",
                    aspectRatio = _aspectRatio.value.label.substringBefore(" ")
                )
                AiToolType.BACKGROUND_REMOVAL -> aiRepository.executeBackgroundRemoval(
                    sourceUri = sourceUri,
                    replaceBackgroundWith = "NEON_CYBER"
                )
                AiToolType.VIDEO_ENHANCER -> aiRepository.executeVideoEnhancer(
                    sourceUri = sourceUri,
                    enable60Fps = true,
                    enableSuperRes = true,
                    enableHdrColor = true
                )
                AiToolType.AUTO_CAPTIONS -> {
                    generateAiCaptions(prompt.ifBlank { "Cinematic AI motion video" })
                    return@launch
                }
            }

            flow.collect { state ->
                _activeAiTask.value = state
            }
        }
    }

    fun applyAiResultToEditor() {
        val task = _activeAiTask.value
        if (task.status != com.example.data.model.AiTaskStatus.SUCCESS) return
        val resUri = task.resultUri ?: return

        // Add as clip into current project
        addClip(
            title = task.resultPreviewTitle ?: "${task.toolType.title} Output",
            uri = resUri,
            type = if (task.toolType == AiToolType.PHOTO_TO_VIDEO) "VIDEO" else "IMAGE"
        )
        _currentScreen.value = StudioScreen.EDITOR
        showToast("Inserted AI generated clip into timeline!")
    }

    // --- Export Pipeline ---

    fun configureExport(resolution: String, fps: Int) {
        _exportResolution.value = resolution
        _exportFps.value = fps
    }

    fun startExport() {
        if (_clips.value.isEmpty()) {
            showToast("Cannot export empty project")
            return
        }

        exportJob?.cancel()
        _isExporting.value = true
        _exportProgress.value = 0
        _exportedFilePath.value = null

        exportJob = viewModelScope.launch {
            val totalSteps = 100
            for (step in 1..totalSteps) {
                delay(35L)
                _exportProgress.value = step
            }

            // Create actual exported output file in app cache/files dir
            val context = getApplication<Application>()
            val exportDir = File(context.filesDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val sanitizedTitle = _projectTitle.value.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val outputFile = File(exportDir, "MotionAI_${sanitizedTitle}_${System.currentTimeMillis()}.mp4")
            if (!outputFile.exists()) {
                outputFile.writeText("MotionAI Studio Video Stream Container\nResolution: ${_exportResolution.value}\nFPS: ${_exportFps.value}\nDurationMs: $totalDurationMs")
            }

            _exportedFilePath.value = outputFile.absolutePath
            _isExporting.value = false

            // Update project status in DB
            _currentProject.value?.let { proj ->
                viewModelScope.launch {
                    projectRepository.saveProjectState(
                        id = proj.id,
                        title = _projectTitle.value,
                        aspectRatio = _aspectRatio.value.label.substringBefore(" "),
                        clips = _clips.value,
                        textOverlays = _textOverlays.value,
                        audioTrack = _audioTrack.value,
                        status = "Exported"
                    )
                }
            }

            showToast("Video exported successfully!")
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _isExporting.value = false
        _exportProgress.value = 0
        showToast("Export cancelled")
    }

    fun clearLocalData() {
        viewModelScope.launch {
            projectRepository.deleteAll()
            projectRepository.ensureInitialData()
            _currentProject.value = null
            _currentScreen.value = StudioScreen.MAIN_TABS
            _activeTab.value = StudioTab.HOME
            showToast("Cleared local project cache and reset defaults")
        }
    }
}
