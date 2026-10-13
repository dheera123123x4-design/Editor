package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "Untitled Edit",
    val aspectRatio: String = "9:16",
    val thumbnailUri: String? = null,
    val clipsJson: String = "[]",
    val textOverlaysJson: String = "[]",
    val audioTrackJson: String? = null,
    val totalDurationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: String = "Draft" // "Draft", "Exported", "Rendering"
)
