package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrack
import com.example.ui.components.TimelineView
import com.example.ui.components.VideoCanvasPreview
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioDivider
import com.example.ui.theme.StudioNeonPink
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

enum class EditorToolTab {
    CLIPS,
    ADJUST,
    FILTERS,
    TEXT,
    AUDIO,
    TRANSITIONS
}

@Composable
fun EditorScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projectTitle by viewModel.projectTitle.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val clips by viewModel.clips.collectAsStateWithLifecycle()
    val textOverlays by viewModel.textOverlays.collectAsStateWithLifecycle()
    val audioTrack by viewModel.audioTrack.collectAsStateWithLifecycle()
    val selectedClipIndex by viewModel.selectedClipIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val canRedo by viewModel.canRedo.collectAsStateWithLifecycle()

    var activeToolTab by remember { mutableStateOf(EditorToolTab.CLIPS) }
    var showRatioDropdown by remember { mutableStateOf(false) }

    val activeClip = clips.getOrNull(selectedClipIndex)
    val totalDurationMs = viewModel.totalDurationMs

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
    ) {
        // Editor Header Navigation Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioDivider)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = StudioTextPrimary
                        )
                    }

                    Text(
                        text = projectTitle,
                        color = StudioTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.width(110.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) StudioTextPrimary else StudioTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) StudioTextPrimary else StudioTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Aspect Ratio Selector Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StudioCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .clickable { showRatioDropdown = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = aspectRatio.label.substringBefore(" "),
                                color = StudioCyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showRatioDropdown,
                            onDismissRequest = { showRatioDropdown = false }
                        ) {
                            AspectRatio.values().forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.label) },
                                    onClick = {
                                        viewModel.updateAspectRatio(r)
                                        showRatioDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { viewModel.saveCurrentProject() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save project",
                            tint = StudioCyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Export Button
                    Button(
                        onClick = { viewModel.openScreen(StudioScreen.EXPORT) },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("editor_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Upper Section: Video Canvas Preview
        VideoCanvasPreview(
            aspectRatio = aspectRatio,
            activeClip = activeClip,
            activeTexts = textOverlays,
            isPlaying = isPlaying,
            currentPositionMs = currentPositionMs,
            totalDurationMs = totalDurationMs,
            isMuted = audioTrack?.isMuted ?: false,
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onToggleMute = { viewModel.toggleAudioMute() },
            onSeekToZero = { viewModel.seekTo(0L) },
            modifier = Modifier.weight(1f)
        )

        androidx.compose.material3.HorizontalDivider(color = StudioDivider, thickness = 1.dp)

        // Middle Section: Multi-track Timeline View
        TimelineView(
            clips = clips,
            textOverlays = textOverlays,
            audioTrack = audioTrack,
            selectedClipIndex = selectedClipIndex,
            currentPositionMs = currentPositionMs,
            totalDurationMs = totalDurationMs,
            onSelectClip = { viewModel.selectClip(it) },
            onSeek = { viewModel.seekTo(it) },
            onSplitClip = { viewModel.splitClipAtPlayhead() },
            onDuplicateClip = { viewModel.duplicateClip(it) },
            onDeleteClip = { viewModel.removeClip(it) },
            onAddClip = { viewModel.addClip() },
            modifier = Modifier.height(180.dp)
        )

        androidx.compose.material3.HorizontalDivider(color = StudioDivider, thickness = 1.dp)

        // Bottom Section: Tool Tabs & Context Control Panels
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioSurface)
        ) {
            // Tool Tabs Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditorTabPill("Clips", Icons.Default.Movie, activeToolTab == EditorToolTab.CLIPS) {
                    activeToolTab = EditorToolTab.CLIPS
                }
                EditorTabPill("Adjust", Icons.Default.Tune, activeToolTab == EditorToolTab.ADJUST) {
                    activeToolTab = EditorToolTab.ADJUST
                }
                EditorTabPill("Filters", Icons.Default.ColorLens, activeToolTab == EditorToolTab.FILTERS) {
                    activeToolTab = EditorToolTab.FILTERS
                }
                EditorTabPill("Text & Captions", Icons.Default.TextFields, activeToolTab == EditorToolTab.TEXT) {
                    activeToolTab = EditorToolTab.TEXT
                }
                EditorTabPill("Audio", Icons.Default.MusicNote, activeToolTab == EditorToolTab.AUDIO) {
                    activeToolTab = EditorToolTab.AUDIO
                }
                EditorTabPill("Transitions", Icons.Default.Transform, activeToolTab == EditorToolTab.TRANSITIONS) {
                    activeToolTab = EditorToolTab.TRANSITIONS
                }
            }

            // Active Tool Content Drawer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(StudioSurfaceElevated)
                    .padding(12.dp)
            ) {
                when (activeToolTab) {
                    EditorToolTab.CLIPS -> ClipsControlPanel(viewModel = viewModel, clip = activeClip)
                    EditorToolTab.ADJUST -> AdjustControlPanel(viewModel = viewModel, clip = activeClip)
                    EditorToolTab.FILTERS -> FiltersControlPanel(viewModel = viewModel, clip = activeClip)
                    EditorToolTab.TEXT -> TextControlPanel(viewModel = viewModel, textOverlays = textOverlays)
                    EditorToolTab.AUDIO -> AudioControlPanel(viewModel = viewModel, audioTrack = audioTrack)
                    EditorToolTab.TRANSITIONS -> TransitionsControlPanel(viewModel = viewModel, clip = activeClip)
                }
            }
        }
    }
}

@Composable
private fun EditorTabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) StudioPrimaryViolet else StudioCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) StudioPrimaryViolet else StudioBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else StudioTextMuted,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = if (selected) Color.White else StudioTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

// 1. Clips Panel: Speed, Rotate, Flip, Duration
@Composable
private fun ClipsControlPanel(viewModel: StudioViewModel, clip: com.example.data.model.Clip?) {
    if (clip == null) {
        Text("Select a clip to edit its transform properties", color = StudioTextMuted, fontSize = 12.sp)
        return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rotate & Flip buttons
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Transform", color = StudioTextMuted, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.rotateSelectedClip() },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = StudioCyanAccent)
                    Spacer(Modifier.width(4.dp))
                    Text("${clip.rotation}°", color = StudioTextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.flipHorizontalSelectedClip() },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Flip, contentDescription = "Flip", tint = StudioNeonPink)
                    Spacer(Modifier.width(4.dp))
                    Text("Flip", color = StudioTextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Speed presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Speed (${clip.speed}x)", color = StudioTextMuted, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                    Surface(
                        modifier = Modifier.clickable {
                            viewModel.updateSelectedClip { it.copy(speed = spd) }
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = if (clip.speed == spd) StudioPrimaryViolet else StudioCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Text(
                            text = "${spd}x",
                            color = if (clip.speed == spd) Color.White else StudioTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// 2. Adjust Panel: Real-time Brightness, Contrast, Saturation sliders
@Composable
private fun AdjustControlPanel(viewModel: StudioViewModel, clip: com.example.data.model.Clip?) {
    if (clip == null) return

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color Grading Sliders",
                color = StudioCyanAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Reset All",
                color = StudioNeonPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    viewModel.updateSelectedClip {
                        it.copy(brightness = 0f, contrast = 1f, saturation = 1f, temperature = 0f)
                    }
                }
            )
        }

        // Saturation Slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Saturation (${String.format("%.1f", clip.saturation)})", color = StudioTextSecondary, fontSize = 10.sp, modifier = Modifier.width(100.dp))
            Slider(
                value = clip.saturation,
                onValueChange = { newVal -> viewModel.updateSelectedClip { it.copy(saturation = newVal) } },
                valueRange = 0f..2.0f,
                colors = SliderDefaults.colors(thumbColor = StudioCyanAccent, activeTrackColor = StudioCyanAccent),
                modifier = Modifier.weight(1f)
            )
        }

        // Contrast Slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Contrast (${String.format("%.1f", clip.contrast)})", color = StudioTextSecondary, fontSize = 10.sp, modifier = Modifier.width(100.dp))
            Slider(
                value = clip.contrast,
                onValueChange = { newVal -> viewModel.updateSelectedClip { it.copy(contrast = newVal) } },
                valueRange = 0.5f..1.8f,
                colors = SliderDefaults.colors(thumbColor = StudioPrimaryViolet, activeTrackColor = StudioPrimaryViolet),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// 3. Filters Panel
@Composable
private fun FiltersControlPanel(viewModel: StudioViewModel, clip: com.example.data.model.Clip?) {
    if (clip == null) return

    val filters = listOf(
        "NORMAL" to "Normal",
        "CYBERPUNK" to "Cyberpunk Neon",
        "CINEMATIC" to "Teal & Orange",
        "ANIME" to "Anime Vibrant",
        "SUNSET" to "Golden Hour",
        "NOIR" to "B&W Film",
        "VINTAGE" to "Vintage 90s",
        "GLITCH" to "VHS Glitch"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { (key, label) ->
            val isSelected = clip.filterEffect == key
            Surface(
                modifier = Modifier
                    .clickable { viewModel.applyFilterToSelectedClip(key) }
                    .width(100.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) StudioPrimaryViolet else StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) StudioCyanAccent else StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(if (isSelected) StudioCyanAccent else StudioDarkBg, CircleShape)
                    )
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else StudioTextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// 4. Text & Auto-Captions Panel
@Composable
private fun TextControlPanel(
    viewModel: StudioViewModel,
    textOverlays: List<com.example.data.model.TextOverlay>
) {
    var newTextInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add new text field
        Column(
            modifier = Modifier.width(220.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = newTextInput,
                onValueChange = { newTextInput = it },
                placeholder = { Text("Title / Text...", color = StudioTextMuted, fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = StudioCyanAccent,
                    unfocusedBorderColor = StudioBorder
                ),
                modifier = Modifier.height(52.dp)
            )

            Button(
                onClick = {
                    if (newTextInput.isNotBlank()) {
                        viewModel.addTextOverlay(newTextInput.trim())
                        newTextInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyanAccent),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Text Overlay", color = StudioDarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Auto Captions AI Action
        Surface(
            modifier = Modifier
                .width(180.dp)
                .clickable { viewModel.generateAiCaptions("AI Cinematic video captions with timestamps") },
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E1035),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioPrimaryViolet)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = StudioPrimaryViolet, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Auto Captions", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Generate timestamped speech subtitles using Gemini AI speech models",
                    color = StudioTextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// 5. Audio Control Panel
@Composable
private fun AudioControlPanel(viewModel: StudioViewModel, audioTrack: AudioTrack?) {
    val presets = listOf(
        "Synthwave Odyssey" to "MotionAI Beats",
        "Cyberpunk 2077" to "Neon Syndicate",
        "Cinematic Piano" to "Aura Soundscape",
        "Phonk Drift" to "Bass Wave",
        "Lofi Cafe Vibes" to "Chillhop Lab"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        presets.forEach { (name, artist) ->
            val isSelected = audioTrack?.name == name
            Surface(
                modifier = Modifier
                    .clickable {
                        viewModel.setAudioTrack(
                            AudioTrack(
                                name = name,
                                artist = artist,
                                durationMs = 15000L
                            )
                        )
                    }
                    .width(130.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF0F2E35) else StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) StudioCyanAccent else StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isSelected) StudioCyanAccent else StudioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(artist, color = StudioTextMuted, fontSize = 9.sp, maxLines = 1)
                }
            }
        }
    }
}

// 6. Transitions Panel
@Composable
private fun TransitionsControlPanel(viewModel: StudioViewModel, clip: com.example.data.model.Clip?) {
    if (clip == null) return

    val transitions = listOf("NONE", "FADE", "DISSOLVE", "SLIDE", "ZOOM", "FLASH")

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        transitions.forEach { trans ->
            val isSelected = clip.transition == trans
            Surface(
                modifier = Modifier
                    .clickable { viewModel.applyTransitionToSelectedClip(trans) }
                    .width(90.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) StudioNeonPink else StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) StudioNeonPink else StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Transform,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else StudioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = trans,
                        color = if (isSelected) Color.White else StudioTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
