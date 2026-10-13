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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProjectEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    var projectToRename by remember { mutableStateOf<ProjectEntity?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    val filteredProjects = allProjects.filter { proj ->
        val matchesQuery = proj.title.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "9:16" -> proj.aspectRatio == "9:16"
            "16:9" -> proj.aspectRatio == "16:9"
            "1:1" -> proj.aspectRatio == "1:1"
            "Exported" -> proj.status == "Exported"
            else -> true
        }
        matchesQuery && matchesFilter
    }

    Box(modifier = modifier.fillMaxSize().background(StudioDarkBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saved Projects",
                            color = StudioTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${allProjects.size} edits saved in local database",
                            color = StudioCyanAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search projects by name...", color = StudioTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = StudioTextMuted)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StudioCyanAccent,
                        unfocusedBorderColor = StudioBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("projects_search_bar")
                )
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "9:16", "16:9", "1:1", "Exported").forEach { chip ->
                        val isSelected = selectedFilter == chip
                        Surface(
                            modifier = Modifier.clickable { selectedFilter = chip },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) StudioPrimaryViolet else StudioCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                        ) {
                            Text(
                                text = chip,
                                color = if (isSelected) Color.White else StudioTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Projects List
            if (filteredProjects.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = StudioSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = StudioTextMuted, modifier = Modifier.size(36.dp))
                            Text("No matching projects found", color = StudioTextPrimary, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your search query or create a new project.", color = StudioTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { proj ->
                    ProjectCard(
                        project = proj,
                        onOpen = { viewModel.openProject(proj) },
                        onRename = {
                            projectToRename = proj
                            renameInput = proj.title
                        },
                        onDuplicate = { viewModel.duplicateProject(proj.id) },
                        onDelete = { projectToDelete = proj }
                    )
                }
            }
        }

        // Floating Action Button to create new project
        FloatingActionButton(
            onClick = { viewModel.createNewProject() },
            containerColor = StudioPrimaryViolet,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("projects_fab_add")
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Project")
        }
    }

    // Rename Dialog
    if (projectToRename != null) {
        AlertDialog(
            onDismissRequest = { projectToRename = null },
            title = { Text("Rename Project", color = StudioTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToRename?.let { viewModel.renameProject(it.id, renameInput) }
                        projectToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyanAccent)
                ) {
                    Text("Rename", color = StudioDarkBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToRename = null }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            },
            containerColor = StudioSurface
        )
    }

    // Delete Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Project?", color = StudioTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete \"${projectToDelete?.title}\"? This action cannot be reversed.", color = StudioTextSecondary)
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
private fun ProjectCard(
    project: ProjectEntity,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.updatedAt))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(14.dp),
        color = StudioCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2433)
                    ) {
                        Text(
                            text = project.aspectRatio,
                            color = StudioCyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = project.status,
                        color = if (project.status == "Exported") StudioNeonPink else StudioTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${(project.totalDurationMs / 1000f).toInt()}s Duration",
                    color = StudioTextSecondary,
                    fontSize = 11.sp
                )
            }

            Text(
                text = project.title,
                color = StudioTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modified $dateStr",
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onRename, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = StudioCyanAccent, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = StudioTextPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StudioNeonPink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
