package com.example.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val audioUrl: String,
    val artworkUrl: String,
    val genre: String,
    val releaseYear: Int = 2024,
    val playCount: String = "2.4M",
    val lyrics: List<LyricLine> = emptyList(),
    val youtubeVideoId: String? = null,
    val isYouTube: Boolean = false
) {
    val durationFormatted: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val songIds: List<String>,
    val isCustom: Boolean = false,
    val createdBy: String = "SoundWave Editorial"
)

data class GenreCategory(
    val id: String,
    val name: String,
    val gradientStartHex: Long,
    val gradientEndHex: Long,
    val iconName: String
)
