package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiTaskStatus
import com.example.data.model.AiToolType
import com.example.data.remote.GeminiAiService
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioNeonPink
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioSuccessGreen
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AiToolsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTool by viewModel.selectedAiTool.collectAsStateWithLifecycle()
    val activeTask by viewModel.activeAiTask.collectAsStateWithLifecycle()

    var sourceImageUri by remember { mutableStateOf<String?>(null) }
    var motionRefUri by remember { mutableStateOf<String?>(null) }
    var promptInput by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Studio Ghibli Anime") }
    var selectedCameraMove by remember { mutableStateOf("Cinematic Zoom In") }
    var consentChecked by remember { mutableStateOf(true) }
    var showSetupDialog by remember { mutableStateOf(false) }

    val isGeminiConfigured = remember { GeminiAiService.isApiKeyConfigured() }

    // Media pickers
    val sourceImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) sourceImageUri = uri.toString()
    }

    val motionVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) motionRefUri = uri.toString()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Motion Suite",
                        color = StudioTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Generative models & neural visual FX",
                        color = StudioCyanAccent,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { showSetupDialog = true },
                    modifier = Modifier.testTag("ai_setup_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "API Setup Information",
                        tint = StudioTextSecondary
                    )
                }
            }
        }

        // Horizontal Tool Tabs Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiToolType.values().forEach { tool ->
                    val isSelected = selectedTool == tool
                    Surface(
                        modifier = Modifier.clickable { viewModel.selectAiTool(tool) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) StudioPrimaryViolet else StudioCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) StudioCyanAccent else StudioBorder
                        )
                    ) {
                        Text(
                            text = tool.title,
                            color = if (isSelected) Color.White else StudioTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Active Tool Banner Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = StudioSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x338B5CF6)
                        ) {
                            Text(
                                text = selectedTool.modelTag,
                                color = StudioCyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = if (isGeminiConfigured) "Configured Model" else "Honest Demo Mode",
                            color = if (isGeminiConfigured) StudioSuccessGreen else StudioAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = selectedTool.title,
                        color = StudioTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = selectedTool.subtitle,
                        color = StudioTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Tool Inputs / Upload Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = StudioSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "INPUT & PARAMETERS",
                        color = StudioTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Source Image Upload Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clickable {
                                    sourceImagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = StudioCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (sourceImageUri != null) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = if (sourceImageUri != null) StudioSuccessGreen else StudioCyanAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = if (sourceImageUri != null) "Source Loaded" else "Select Source Photo",
                                    color = StudioTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Motion Reference Video (For Motion Transfer)
                        if (selectedTool == AiToolType.MOTION_TRANSFER) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clickable {
                                        motionVideoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = StudioCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (motionRefUri != null) Icons.Default.CheckCircle else Icons.Default.VideoFile,
                                        contentDescription = null,
                                        tint = if (motionRefUri != null) StudioSuccessGreen else StudioNeonPink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (motionRefUri != null) "Motion Video OK" else "Motion Reference Video",
                                        color = StudioTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Prompt Input
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("AI Motion Prompt / Scene Context", color = StudioTextMuted) },
                        placeholder = {
                            Text(
                                when (selectedTool) {
                                    AiToolType.PHOTO_TO_VIDEO -> "e.g., Cinematic slow zoom, neon raindrops, soft breeze"
                                    AiToolType.ANIME_STYLE -> "e.g., Studio Ghibli cel-shaded hand-drawn style"
                                    AiToolType.AUTO_CAPTIONS -> "e.g., Context of dialogue: futuristic cyber city"
                                    else -> "e.g., Dynamic camera sweep, 60fps cinematic fluidity"
                                },
                                color = StudioTextMuted,
                                fontSize = 12.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StudioCyanAccent,
                            unfocusedBorderColor = StudioBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input")
                    )

                    // Tool specific dropdowns/presets
                    if (selectedTool == AiToolType.ANIME_STYLE) {
                        Text("Style Preset:", color = StudioTextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Studio Ghibli Anime", "Cyberpunk Manga", "Makoto Shinkai", "Vibrant 3D Pixar", "Classic Oil Painting").forEach { style ->
                                Surface(
                                    modifier = Modifier.clickable { selectedStyle = style },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedStyle == style) StudioNeonPink else StudioCard
                                ) {
                                    Text(
                                        text = style,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (selectedTool == AiToolType.PHOTO_TO_VIDEO) {
                        Text("Camera Move Direction:", color = StudioTextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Cinematic Zoom In", "Orbit Arc 360", "Smooth Pan Right", "Crane Tilt Up").forEach { move ->
                                Surface(
                                    modifier = Modifier.clickable { selectedCameraMove = move },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedCameraMove == move) StudioCyanAccent else StudioCard
                                ) {
                                    Text(
                                        text = move,
                                        color = if (selectedCameraMove == move) StudioDarkBg else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Ethical Likeness Consent Checkbox for Motion Transfer
                    if (selectedTool == AiToolType.MOTION_TRANSFER) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = consentChecked,
                                onCheckedChange = { consentChecked = it },
                                colors = CheckboxDefaults.colors(checkedColor = StudioCyanAccent)
                            )
                            Text(
                                text = "I verify I have the rights to use and process this portrait likeness.",
                                color = StudioTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Generate Button
                    Button(
                        onClick = {
                            viewModel.runActiveAiTool(
                                sourceUri = sourceImageUri,
                                referenceUri = motionRefUri,
                                prompt = promptInput,
                                stylePreset = selectedStyle,
                                consentGranted = consentChecked
                            )
                        },
                        enabled = activeTask.status != AiTaskStatus.PROCESSING,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_generate_button")
                    ) {
                        if (activeTask.status == AiTaskStatus.PROCESSING) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Processing Model (${activeTask.progressPercent}%)...", color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Generate ${selectedTool.title}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Task Progress, Logs & Results Card
        if (activeTask.status != AiTaskStatus.IDLE) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (activeTask.status) {
                                    AiTaskStatus.PROCESSING -> "Synthesizing Motion..."
                                    AiTaskStatus.SUCCESS -> "Generation Complete!"
                                    AiTaskStatus.ERROR -> "Generation Notice"
                                    else -> "Task Status"
                                },
                                color = when (activeTask.status) {
                                    AiTaskStatus.SUCCESS -> StudioSuccessGreen
                                    AiTaskStatus.ERROR -> StudioNeonPink
                                    else -> StudioCyanAccent
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            if (activeTask.isDemoMode) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x33F59E0B)
                                ) {
                                    Text(
                                        text = "DEMO SIMULATION",
                                        color = StudioAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (activeTask.status == AiTaskStatus.PROCESSING) {
                            LinearProgressIndicator(
                                progress = activeTask.progressPercent / 100f,
                                color = StudioCyanAccent,
                                trackColor = StudioCard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }

                        // Terminal Logs Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A0D14)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                activeTask.logs.forEach { logLine ->
                                    Text(
                                        text = "> $logLine",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Action on Success: Add to Editor
                        if (activeTask.status == AiTaskStatus.SUCCESS) {
                            Button(
                                onClick = { viewModel.applyAiResultToEditor() },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioSuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("apply_ai_to_editor_button")
                            ) {
                                Text("Insert Clip Into Project Timeline", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // AI Provider Configuration Info Dialog
    if (showSetupDialog) {
        AlertDialog(
            onDismissRequest = { showSetupDialog = false },
            title = {
                Text("AI Provider Setup & Credentials", color = StudioTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "MotionAI Studio integrates Gemini 3.5 & Veo models for multimodal video motion synthesis, prompt intelligence, and speech captions.",
                        color = StudioTextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        "• Gemini API Key Status: ${if (isGeminiConfigured) "Connected" else "Not Configured"}",
                        color = if (isGeminiConfigured) StudioSuccessGreen else StudioAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "To configure your own API key: Open AI Studio Secrets panel, and inject GEMINI_API_KEY. For dedicated video diffusion (Runway Gen-3, Kling, or Luma), configure backend webhooks in Profile.",
                        color = StudioTextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSetupDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet)
                ) {
                    Text("Understood", color = Color.White)
                }
            },
            containerColor = StudioSurface
        )
    }
}
