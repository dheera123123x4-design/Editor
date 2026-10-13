package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ExportScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val projectTitle by viewModel.projectTitle.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()
    val exportProgress by viewModel.exportProgress.collectAsStateWithLifecycle()
    val exportResolution by viewModel.exportResolution.collectAsStateWithLifecycle()
    val exportFps by viewModel.exportFps.collectAsStateWithLifecycle()
    val exportedFilePath by viewModel.exportedFilePath.collectAsStateWithLifecycle()

    val totalDurationMs = viewModel.totalDurationMs
    val durationSeconds = (totalDurationMs / 1000f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.openScreen(StudioScreen.EDITOR) },
                    modifier = Modifier.testTag("export_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Editor",
                        tint = StudioTextPrimary
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Export Project",
                        color = StudioTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = projectTitle,
                        color = StudioCyanAccent,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Summary Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = StudioSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("PROJECT SPECIFICATIONS", color = StudioTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Canvas Ratio", color = StudioTextSecondary, fontSize = 13.sp)
                        Text(aspectRatio.label, color = StudioTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration", color = StudioTextSecondary, fontSize = 13.sp)
                        Text("${String.format("%.1f", durationSeconds)}s", color = StudioTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Size", color = StudioTextSecondary, fontSize = 13.sp)
                        val mbEstimate = if (exportResolution.startsWith("1080")) (durationSeconds * 2.2f) else (durationSeconds * 1.2f)
                        Text("~${String.format("%.1f", mbEstimate)} MB", color = StudioCyanAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Resolution & Frame Rate Settings
        if (!isExporting && exportedFilePath == null) {
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
                        Text("OUTPUT PRESETS", color = StudioTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

                        // Resolution options
                        Text("Resolution:", color = StudioTextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("720p HD", "1080p Full HD").forEach { res ->
                                val isSelected = exportResolution == res
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.configureExport(res, exportFps) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) StudioPrimaryViolet else StudioCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(res, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(if (res.startsWith("1080")) "High Bitrate" else "Fast Mobile", color = StudioTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        // Frame Rate options
                        Text("Frame Rate (FPS):", color = StudioTextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(24 to "Cinematic", 30 to "Standard", 60 to "Fluid 60fps").forEach { (fps, desc) ->
                                val isSelected = exportFps == fps
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.configureExport(exportResolution, fps) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) StudioCyanAccent else StudioCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("${fps} fps", color = if (isSelected) StudioDarkBg else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(desc, color = if (isSelected) StudioDarkBg else StudioTextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        // Start Export Action
                        Button(
                            onClick = { viewModel.startExport() },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_export_render_button")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Render & Export Video", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Active Rendering Progress Card
        if (isExporting) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = exportProgress / 100f,
                            color = StudioCyanAccent,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(72.dp)
                        )

                        Text(
                            text = "Encoding Video Stream ($exportProgress%)",
                            color = StudioTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Processing neural transitions, color grades, and audio mixes...",
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )

                        LinearProgressIndicator(
                            progress = exportProgress / 100f,
                            color = StudioCyanAccent,
                            trackColor = StudioCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        OutlinedButton(
                            onClick = { viewModel.cancelExport() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioNeonPink),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioNeonPink),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cancel Export")
                        }
                    }
                }
            }
        }

        // Completed Output Success Card
        if (exportedFilePath != null && !isExporting) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0C241B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioSuccessGreen)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StudioSuccessGreen, modifier = Modifier.size(24.dp))
                            Text("Video Export Successful!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Text(
                            text = "Saved container:\n$exportedFilePath",
                            color = StudioTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "video/mp4"
                                        putExtra(Intent.EXTRA_SUBJECT, projectTitle)
                                        putExtra(Intent.EXTRA_TEXT, "Created with MotionAI Studio")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioSuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Share", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.openScreen(StudioScreen.EDITOR) },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = StudioCyanAccent, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Edit More", color = StudioTextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
