package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.data.model.Clip
import com.example.data.model.TextOverlay
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyanAccent
import com.example.ui.theme.StudioDivider
import com.example.ui.theme.StudioNeonPink
import com.example.ui.theme.StudioPlayhead
import com.example.ui.theme.StudioPrimaryViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTrackBg

@Composable
fun TimelineView(
    clips: List<Clip>,
    textOverlays: List<TextOverlay>,
    audioTrack: AudioTrack?,
    selectedClipIndex: Int,
    currentPositionMs: Long,
    totalDurationMs: Long,
    onSelectClip: (Int) -> Unit,
    onSeek: (Long) -> Unit,
    onSplitClip: () -> Unit,
    onDuplicateClip: (Int) -> Unit,
    onDeleteClip: (Int) -> Unit,
    onAddClip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val pxPerSecond = 70.dp
    val totalSeconds = (totalDurationMs / 1000f).coerceAtLeast(1f)
    val timelineWidth = (totalSeconds * 70).dp.coerceAtLeast(360.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface),
        color = StudioSurface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Timeline Action Bar (Split, Duplicate, Delete, Add)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIMELINE (${clips.size} CLIPS)",
                    color = StudioTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onSplitClip,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("timeline_split_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallSplit,
                            contentDescription = "Split clip",
                            tint = StudioCyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDuplicateClip(selectedClipIndex) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("timeline_duplicate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate clip",
                            tint = StudioTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDeleteClip(selectedClipIndex) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("timeline_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete clip",
                            tint = StudioNeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onAddClip,
                        modifier = Modifier
                            .size(32.dp)
                            .background(StudioPrimaryViolet, RoundedCornerShape(6.dp))
                            .testTag("timeline_add_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add clip",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            androidx.compose.material3.HorizontalDivider(color = StudioDivider, thickness = 1.dp)

            // Timeline Tracks Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(StudioTrackBg)
            ) {
                // Horizontally Scrollable Tracks
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .width(timelineWidth)
                        .padding(bottom = 8.dp)
                ) {
                    // Time Ruler Track
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color(0xFF0D111A))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val numMarkers = (totalSeconds).toInt() + 2
                        for (sec in 0..numMarkers) {
                            Box(
                                modifier = Modifier
                                    .width(pxPerSecond)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "${sec}s",
                                    color = StudioTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Main Video Clips Track
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        clips.forEachIndexed { index, clip ->
                            val clipSeconds = clip.activeDurationMs / 1000f
                            val clipWidth = (clipSeconds * 70).dp.coerceAtLeast(64.dp)
                            val isSelected = index == selectedClipIndex

                            Box(
                                modifier = Modifier
                                    .width(clipWidth)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF2E1A47) else StudioCard)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) StudioPrimaryViolet else StudioBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectClip(index) }
                                    .padding(6.dp)
                                    .testTag("clip_item_$index")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = clip.title,
                                        color = if (isSelected) StudioCyanAccent else StudioTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${String.format("%.1f", clipSeconds)}s",
                                            color = StudioTextMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (clip.filterEffect != "NORMAL") {
                                            Text(
                                                text = clip.filterEffect.take(4),
                                                color = StudioNeonPink,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Audio Track
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F2930))
                                .border(1.dp, Color(0xFF155E75), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Audio track",
                                    tint = StudioCyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = audioTrack?.name ?: "Synthwave Odyssey (Soundtrack)",
                                    color = StudioCyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Text Overlays Track
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .padding(vertical = 2.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (textOverlays.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "+ Add text overlays or auto-captions",
                                    color = StudioTextMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        } else {
                            textOverlays.forEach { textItem ->
                                val textDurationSec = ((textItem.endTimeMs - textItem.startTimeMs) / 1000f).coerceAtLeast(1f)
                                val textWidth = (textDurationSec * 70).dp.coerceAtLeast(60.dp)

                                Box(
                                    modifier = Modifier
                                        .width(textWidth)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF33200D))
                                        .border(1.dp, Color(0xFFB45309), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TextFields,
                                            contentDescription = "Text overlay",
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = textItem.text,
                                            color = Color(0xFFFDE68A),
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Vertical Playhead Bar with Scrub Handle
                val progressFraction = if (totalDurationMs > 0) currentPositionMs.toFloat() / totalDurationMs else 0f
                val playheadOffset = (progressFraction * (timelineWidth.value)).dp

                Box(
                    modifier = Modifier
                        .offset(x = playheadOffset)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(StudioPlayhead)
                ) {
                    // Playhead Diamond/Circle Handle at top
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(12.dp)
                            .offset(y = (-2).dp)
                            .background(StudioPlayhead, CircleShape)
                    )
                }
            }
        }
    }
}
