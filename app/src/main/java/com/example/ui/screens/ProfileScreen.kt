package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ProfileScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isGeminiReady = remember { GeminiAiService.isApiKeyConfigured() }
    var showAiGuidanceModal by remember { mutableStateOf(false) }
    var showClearConfirmModal by remember { mutableStateOf(false) }
    var showAboutModal by remember { mutableStateOf(false) }
    var darkThemeActive by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = StudioSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(StudioPrimaryViolet, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Motion Creator",
                            color = StudioTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Local Studio Mode (No account required)",
                            color = StudioCyanAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // AI Provider Section
        item {
            ProfileSectionCard(title = "AI ENGINE & MODELS") {
                ProfileClickableRow(
                    title = "Google Gemini Intelligence",
                    subtitle = if (isGeminiReady) "Connected (Server-side key loaded)" else "Not Configured (Demo Mode)",
                    statusColor = if (isGeminiReady) StudioSuccessGreen else StudioAmber,
                    icon = Icons.Default.AutoAwesome,
                    onClick = { showAiGuidanceModal = true }
                )

                ProfileClickableRow(
                    title = "Veo 3.1 & Diffusion Engines",
                    subtitle = "Configure external diffusion video acceleration",
                    statusColor = StudioCyanAccent,
                    icon = Icons.Default.Movie,
                    onClick = { showAiGuidanceModal = true }
                )
            }
        }

        // Editor & Studio Preferences
        item {
            ProfileSectionCard(title = "STUDIO PREFERENCES") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = StudioPrimaryViolet, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Cinematic Dark Theme", color = StudioTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Default high-contrast studio mode", color = StudioTextMuted, fontSize = 11.sp)
                        }
                    }

                    Switch(
                        checked = darkThemeActive,
                        onCheckedChange = { darkThemeActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = StudioPrimaryViolet)
                    )
                }
            }
        }

        // Data Storage & Privacy Section
        item {
            ProfileSectionCard(title = "DATA & PRIVACY") {
                ProfileClickableRow(
                    title = "Local Privacy Policy",
                    subtitle = "Zero cloud tracking • Photos/Videos stay on your device",
                    icon = Icons.Default.Lock,
                    onClick = { showAboutModal = true }
                )

                ProfileClickableRow(
                    title = "Reset Projects & Storage Cache",
                    subtitle = "Clear Room database and temporary exported videos",
                    statusColor = StudioNeonPink,
                    icon = Icons.Default.Delete,
                    onClick = { showClearConfirmModal = true }
                )
            }
        }

        // About & Version
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAboutModal = true },
                shape = RoundedCornerShape(12.dp),
                color = StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("MotionAI Studio v1.0", color = StudioTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Android Jetpack Compose • Kotlin • AGP 9.1", color = StudioTextMuted, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.Info, contentDescription = null, tint = StudioCyanAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    // AI Provider Guidance Modal
    if (showAiGuidanceModal) {
        AlertDialog(
            onDismissRequest = { showAiGuidanceModal = false },
            title = {
                Text("AI Provider Credentials", color = StudioTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "MotionAI Studio respects your privacy and does not expose API secrets in UI code.",
                        color = StudioTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        "• Gemini Status: ${if (isGeminiReady) "Active" else "Needs GEMINI_API_KEY in Secrets panel"}",
                        color = if (isGeminiReady) StudioSuccessGreen else StudioAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "• Features using AI: Auto Captions, Photo-to-Video prompt optimization, and Motion Diffusion.",
                        color = StudioTextPrimary,
                        fontSize = 12.sp
                    )
                    Text(
                        "• When unconfigured, MotionAI Studio runs in an honest demo mode without fake promises.",
                        color = StudioTextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAiGuidanceModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            containerColor = StudioSurface
        )
    }

    // Reset Confirmation Modal
    if (showClearConfirmModal) {
        AlertDialog(
            onDismissRequest = { showClearConfirmModal = false },
            title = { Text("Reset Local Data?", color = StudioTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will delete all saved edits from Room database and restore the default starter sample projects.",
                    color = StudioTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearLocalData()
                        showClearConfirmModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioNeonPink)
                ) {
                    Text("Reset Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmModal = false }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            },
            containerColor = StudioSurface
        )
    }

    // About Dialog
    if (showAboutModal) {
        AlertDialog(
            onDismissRequest = { showAboutModal = false },
            title = { Text("About MotionAI Studio", color = StudioTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MotionAI Studio — Turn your imagination into motion.", color = StudioCyanAccent, fontWeight = FontWeight.Bold)
                    Text("Designed with Material 3 dark aesthetics, multi-track timeline rendering, real-time color matrix transforms, and generative AI features.", color = StudioTextSecondary, fontSize = 12.sp)
                    Text("Privacy Commitment: Imported photos and videos are stored in private app sandbox storage and never shared without explicit consent.", color = StudioTextMuted, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutModal = false }, colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet)) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = StudioSurface
        )
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = StudioTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = StudioSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileClickableRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    statusColor: Color = StudioTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = StudioPrimaryViolet, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, color = StudioTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = statusColor, fontSize = 11.sp, maxLines = 1)
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = StudioTextMuted, modifier = Modifier.size(18.dp))
    }
}
