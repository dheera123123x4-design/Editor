package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TemplateItem
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioNeonPink
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun TemplatesScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val templates = remember {
        listOf(
            TemplateItem(
                id = "tmpl_cinematic",
                title = "Cinematic Slow-Mo Cut",
                category = "Cinematic",
                aspectRatio = "16:9",
                durationSec = 12,
                clipSlots = 3,
                musicName = "Synthwave Odyssey",
                description = "Dramatic teal and orange color grading with smooth dissolves and ambient synth soundtrack.",
                tags = listOf("4K Look", "Teal-Orange", "Widescreen"),
                defaultTextOverlays = listOf("THE BEGINNING", "BEYOND THE HORIZON")
            ),
            TemplateItem(
                id = "tmpl_phonk",
                title = "Cyberpunk Phonk Beat",
                category = "Phonk",
                aspectRatio = "9:16",
                durationSec = 15,
                clipSlots = 4,
                musicName = "Phonk Drift Wave",
                description = "High-octane bass drops with glitch effects, chromatic aberration, and synchronized zoom cuts.",
                tags = listOf("Phonk", "Glitch FX", "TikTok/Reels"),
                defaultTextOverlays = listOf("NIGHT DRIFT", "VELOCITY MAX")
            ),
            TemplateItem(
                id = "tmpl_travel",
                title = "Travel Wanderlust Reel",
                category = "Travel",
                aspectRatio = "9:16",
                durationSec = 18,
                clipSlots = 5,
                musicName = "Ambient Sunrise",
                description = "Warm golden hour tone with breezy transitions, kinetic typography, and uplifting melody.",
                tags = listOf("Golden Hour", "Vlog", "Wanderlust"),
                defaultTextOverlays = listOf("EXPLORE MORE", "LOST IN KYOTO")
            ),
            TemplateItem(
                id = "tmpl_anime",
                title = "Anime Epic Opener",
                category = "Anime",
                aspectRatio = "9:16",
                durationSec = 10,
                clipSlots = 3,
                musicName = "Neon Shonen Rock",
                description = "Speed line overlays, vibrant anime cel-shading colors, and dramatic typography.",
                tags = listOf("Anime", "Speed Lines", "Trending"),
                defaultTextOverlays = listOf("AWAKENING", "CHAPTER I")
            ),
            TemplateItem(
                id = "tmpl_montage",
                title = "Photo Montage Memories",
                category = "Montage",
                aspectRatio = "1:1",
                durationSec = 15,
                clipSlots = 6,
                musicName = "Lofi Nostalgia",
                description = "Polaroid style photo frames, vintage soft film grain, and gentle cross-dissolves.",
                tags = listOf("Vintage", "Polaroid", "Memories"),
                defaultTextOverlays = listOf("MEMORIES 2026", "TIME FLIES")
            ),
            TemplateItem(
                id = "tmpl_social",
                title = "Social Product Drop",
                category = "Promo",
                aspectRatio = "4:5",
                durationSec = 8,
                clipSlots = 2,
                musicName = "Modern Pop Beats",
                description = "Punchy kinetic text animations, bold neon colors, and attention-grabbing product reveals.",
                tags = listOf("Clean", "Commercial", "Instagram"),
                defaultTextOverlays = listOf("NEW DROP", "AVAILABLE NOW")
            )
        )
    }

    var selectedCategory by remember { mutableStateOf("All") }
    var previewTemplate by remember { mutableStateOf<TemplateItem?>(null) }

    val filtered = templates.filter {
        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Motion Templates",
                    color = StudioTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Ready-made cuts with synced soundtracks & FX",
                    color = StudioCyanAccent,
                    fontSize = 12.sp
                )
            }
        }

        // Category Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Cinematic", "Phonk", "Travel", "Anime", "Montage", "Promo").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) StudioPrimaryViolet else StudioCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else StudioTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Templates Cards List
        items(filtered, key = { it.id }) { tmpl ->
            TemplateCard(
                template = tmpl,
                onPreview = { previewTemplate = tmpl },
                onApply = { viewModel.createProjectFromTemplate(tmpl) }
            )
        }
    }

    // Template Preview & Apply Modal Dialog
    if (previewTemplate != null) {
        val t = previewTemplate!!
        AlertDialog(
            onDismissRequest = { previewTemplate = null },
            title = {
                Text(t.title, color = StudioTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(t.description, color = StudioTextSecondary, fontSize = 13.sp)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Aspect Ratio: ${t.aspectRatio}", color = StudioCyanAccent, fontSize = 12.sp)
                            Text("• Duration: ${t.durationSec} seconds", color = StudioTextPrimary, fontSize = 12.sp)
                            Text("• Media Slots: ${t.clipSlots} clips required", color = StudioTextPrimary, fontSize = 12.sp)
                            Text("• Soundtrack: ${t.musicName}", color = StudioNeonPink, fontSize = 12.sp)
                        }
                    }

                    Text("Applying will create a fully editable project with starter clips and timing.", color = StudioTextMuted, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createProjectFromTemplate(t)
                        previewTemplate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet)
                ) {
                    Text("Apply Template", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { previewTemplate = null }) {
                    Text("Close", color = StudioTextSecondary)
                }
            },
            containerColor = StudioSurface
        )
    }
}

@Composable
private fun TemplateCard(
    template: TemplateItem,
    onPreview: () -> Unit,
    onApply: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}"),
        shape = RoundedCornerShape(14.dp),
        color = StudioSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E2433)
                ) {
                    Text(
                        text = "${template.aspectRatio} • ${template.durationSec}s",
                        color = StudioCyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = StudioNeonPink, modifier = Modifier.size(14.dp))
                    Text(template.musicName, color = StudioTextMuted, fontSize = 11.sp)
                }
            }

            Text(
                text = template.title,
                color = StudioTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = template.description,
                color = StudioTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${template.clipSlots} Media Slots",
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioTextPrimary)
                    ) {
                        Text("Preview", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StudioPrimaryViolet)
                    ) {
                        Text("Use Template", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
