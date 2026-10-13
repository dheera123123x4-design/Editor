package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiToolType
import com.example.data.model.AspectRatio
import com.example.data.model.ProjectEntity
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
fun HomeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    var showRatioModal by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }
    val isGeminiReady = remember { GeminiAiService.isApiKeyConfigured() }

    // Android Photo/Video Picker launcher for importing media
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.createNewProject(
                ratio = AspectRatio.PORTRAIT_9_16,
                initialMediaUri = uri.toString()
            )
            viewModel.showToast("Imported media into new project")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Header & AI Status Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MotionAI Studio",
                        color = StudioTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Turn your imagination into motion.",
                        color = StudioCyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // AI Engine Indicator Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isGeminiReady) Color(0xFF0F2E20) else Color(0xFF261C14),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isGeminiReady) StudioSuccessGreen else StudioAmber
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isGeminiReady) StudioSuccessGreen else StudioAmber,
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isGeminiReady) "AI Online" else "Demo Mode",
                            color = if (isGeminiReady) StudioSuccessGreen else StudioAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero "New Project" Card with Gradient
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_new_project_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF6D28D9),
                                    Color(0xFF8B5CF6),
                                    Color(0xFF06B6D4)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Start Video Project",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Multi-track timeline, AI style synthesis, color grading, and auto-caption generator.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Button(
                                onClick = { showRatioModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioDarkBg),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("home_create_project_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = StudioCyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "New Project",
                                    color = StudioTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                modifier = Modifier.testTag("home_import_media_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoFile,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Import Media",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Import Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionPill(
                    title = "Import Video",
                    icon = Icons.Default.Movie,
                    accentColor = StudioCyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                )

                QuickActionPill(
                    title = "Import Photo",
                    icon = Icons.Default.Image,
                    accentColor = StudioNeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }

        // Featured AI Tools Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FEATURED AI CREATIVE SUITE",
                        color = StudioTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "6 Tools Available",
                        color = StudioCyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        AiFeatureCard(
                            title = "Motion Transfer",
                            desc = "Pose & dance diffusion",
                            icon = Icons.Default.Animation,
                            badge = "Diffusion",
                            accentColor = StudioPrimaryViolet,
                            onClick = { viewModel.selectAiTool(AiToolType.MOTION_TRANSFER) }
                        )
                    }
                    item {
                        AiFeatureCard(
                            title = "Anime Stylizer",
                            desc = "Studio Ghibli & Manga",
                            icon = Icons.Default.Palette,
                            badge = "Neural Style",
                            accentColor = StudioNeonPink,
                            onClick = { viewModel.selectAiTool(AiToolType.ANIME_STYLE) }
                        )
                    }
                    item {
                        AiFeatureCard(
                            title = "Photo to Video",
                            desc = "Cinematic camera moves",
                            icon = Icons.Default.Movie,
                            badge = "Veo 3.1",
                            accentColor = StudioCyanAccent,
                            onClick = { viewModel.selectAiTool(AiToolType.PHOTO_TO_VIDEO) }
                        )
                    }
                    item {
                        AiFeatureCard(
                            title = "Background Cut",
                            desc = "Subject isolation",
                            icon = Icons.Default.ContentCut,
                            badge = "Segmentation",
                            accentColor = StudioAmber,
                            onClick = { viewModel.selectAiTool(AiToolType.BACKGROUND_REMOVAL) }
                        )
                    }
                    item {
                        AiFeatureCard(
                            title = "Auto Captions",
                            desc = "AI Speech-to-Subtitle",
                            icon = Icons.Default.Subtitles,
                            badge = "Gemini Audio",
                            accentColor = StudioSuccessGreen,
                            onClick = { viewModel.selectAiTool(AiToolType.AUTO_CAPTIONS) }
                        )
                    }
                }
            }
        }

        // Recent Projects Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT PROJECTS",
                        color = StudioTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "${projects.size} Saved",
                        color = StudioTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (projects.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = StudioSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = StudioTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No projects yet",
                                color = StudioTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Create your first project or pick a template to get started.",
                                color = StudioTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        projects.forEach { proj ->
                            ProjectListItem(
                                project = proj,
                                onOpen = { viewModel.openProject(proj) },
                                onDelete = { projectToDelete = proj }
                            )
                        }
                    }
                }
            }
        }
    }

    // Aspect Ratio Selection Modal Dialog
    if (showRatioModal) {
        AlertDialog(
            onDismissRequest = { showRatioModal = false },
            title = {
                Text(
                    text = "Select Project Canvas Ratio",
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AspectRatio.values().forEach { ratio ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showRatioModal = false
                                    viewModel.createNewProject(ratio)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = StudioCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ratio.label,
                                    color = StudioTextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = StudioPrimaryViolet
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRatioModal = false }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            },
            containerColor = StudioSurface
        )
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = {
                Text("Delete Project?", color = StudioTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete \"${projectToDelete?.title}\"? This cannot be undone.",
                    color = StudioTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it.id) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioNeonPink)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            },
            containerColor = StudioSurface
        )
    }
}

@Composable
private fun QuickActionPill(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_action_${title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = title,
                color = StudioTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AiFeatureCard(
    title: String,
    desc: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = StudioSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x3306B6D4)
                ) {
                    Text(
                        text = badge,
                        color = StudioCyanAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = title,
                color = StudioTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = desc,
                color = StudioTextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun ProjectListItem(
    project: ProjectEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("project_item_${project.id}"),
        shape = RoundedCornerShape(12.dp),
        color = StudioCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Aspect ratio thumbnail box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E2433))
                    .border(1.dp, StudioBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.aspectRatio,
                    color = StudioCyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = project.title,
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                Text(
                    text = "${project.status} • ${(project.totalDurationMs / 1000f).toInt()}s",
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = onOpen,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit project",
                    tint = StudioPrimaryViolet,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete project",
                    tint = StudioTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
