package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_songs")
data class LikedSongEntity(
    @PrimaryKey
    val songId: String,
    val likedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_playlists")
data class CustomPlaylistEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val songIdsJson: String, // Comma separated or json array of songIds
    val createdAt: Long = System.currentTimeMillis()
)
