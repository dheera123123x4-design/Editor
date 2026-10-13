package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatio
import com.example.data.model.Clip
import com.example.data.model.TextOverlay
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioNeonPink
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioTextPrimary

@Composable
fun VideoCanvasPreview(
    aspectRatio: AspectRatio,
    activeClip: Clip?,
    activeTexts: List<TextOverlay>,
    isPlaying: Boolean,
    currentPositionMs: Long,
    totalDurationMs: Long,
    isMuted: Boolean,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onSeekToZero: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(aspectRatio.floatRatio, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, StudioBorder, RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F131C))
                        .clickable { onTogglePlayPause() }
                        .testTag("video_canvas_preview")
                ) {
                    if (activeClip != null) {
                        // Render simulated or actual video frame with real-time color matrix
                        CanvasMediaLayer(clip = activeClip)
                    } else {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No clips in timeline",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Render active text overlays at their proportional coordinates
                    activeTexts.forEach { overlay ->
                        if (currentPositionMs in overlay.startTimeMs..overlay.endTimeMs) {
                            TextOverlayBadge(
                                overlay = overlay,
                                modifier = Modifier.align(
                                    when {
                                        overlay.yPercent < 0.35f -> Alignment.TopCenter
                                        overlay.yPercent > 0.65f -> Alignment.BottomCenter
                                        else -> Alignment.Center
                                    }
                                )
                            )
                        }
                    }

                    // Aspect Ratio pill & Filter pill tag at top
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x99000000)
                        ) {
                            Text(
                                text = aspectRatio.label.substringBefore(" "),
                                color = StudioTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (activeClip != null && activeClip.filterEffect != "NORMAL") {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xCC7C3AED)
                            ) {
                                Text(
                                    text = activeClip.filterEffect,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Play/Pause center overlay indicator on pause
                    if (!isPlaying) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(56.dp)
                                .background(Color(0x99000000), CircleShape)
                                .border(1.5.dp, StudioPrimaryViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Preview",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Quick Canvas Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Timecode pill: 00:02.4 / 00:12.0
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = StudioCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Text(
                        text = "${formatTimecode(currentPositionMs)} / ${formatTimecode(totalDurationMs)}",
                        color = StudioCyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Control action buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onSeekToZero,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("canvas_rewind_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart to beginning",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("canvas_mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) StudioNeonPink else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(40.dp)
                            .background(StudioPrimaryViolet, CircleShape)
                            .testTag("canvas_play_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CanvasMediaLayer(clip: Clip) {
    // Generate dynamic color matrix representing brightness, contrast, saturation, and filters
    val colorMatrix = ColorMatrix().apply {
        // Apply saturation
        setToSaturation(clip.saturation)
    }

    // Gradient stylization based on active filter
    val backgroundBrush = when (clip.filterEffect) {
        "CYBERPUNK" -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF3B0764), Color(0xFF06B6D4)))
        "CINEMATIC" -> Brush.verticalGradient(listOf(Color(0xFF022C22), Color(0xFF1E293B), Color(0xFF451A03)))
        "ANIME" -> Brush.verticalGradient(listOf(Color(0xFF4C1D95), Color(0xFFDB2777), Color(0xFFFDE047)))
        "SUNSET" -> Brush.verticalGradient(listOf(Color(0xFF7C2D12), Color(0xFFBE123C), Color(0xFFF59E0B)))
        "NOIR" -> Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF262626), Color(0xFF525252)))
        "VINTAGE" -> Brush.verticalGradient(listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFFD97706)))
        "GLITCH" -> Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFFEC4899), Color(0xFF8B5CF6)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E2433), Color(0xFF131824)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .rotate(clip.rotation.toFloat())
            .scale(
                scaleX = if (clip.isFlippedHorizontal) -1f else 1f,
                scaleY = if (clip.isFlippedVertical) -1f else 1f
            )
            .background(backgroundBrush)
    ) {
        // Visual stylized scene graphics
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = clip.title,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${clip.type} • ${clip.speed}x • ${clip.filterEffect}",
                color = StudioCyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TextOverlayBadge(overlay: TextOverlay, modifier: Modifier = Modifier) {
    val textColor = try {
        Color(android.graphics.Color.parseColor(overlay.colorHex))
    } catch (e: Exception) {
        Color.White
    }

    val bgColor = try {
        overlay.bgHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Transparent
    } catch (e: Exception) {
        Color(0x99000000)
    }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = overlay.text,
            color = textColor,
            fontSize = overlay.fontSizeSp.sp,
            fontWeight = if (overlay.fontStyle == "BOLD") FontWeight.Bold else FontWeight.Normal,
            fontFamily = when (overlay.fontStyle) {
                "MONOSPACE" -> FontFamily.Monospace
                "SERIF" -> FontFamily.Serif
                else -> FontFamily.Default
            },
            textAlign = TextAlign.Center
        )
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val decimal = (ms % 1000) / 100
    return String.format("%02d:%02d.%d", minutes, seconds, decimal)
}
