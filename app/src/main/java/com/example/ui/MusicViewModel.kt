package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CustomPlaylistEntity
import com.example.data.local.LikedSongEntity
import com.example.data.local.MusicDatabase
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.repository.MusicCatalog
import com.example.data.repository.YouTubeMusicRepository
import com.example.playback.PlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    val playerManager = PlayerManager.getInstance(application)
    private val musicDao = MusicDatabase.getInstance(application).musicDao()

    val likedSongIds: StateFlow<Set<String>> = musicDao.getAllLikedSongIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val customPlaylists: StateFlow<List<Playlist>> = musicDao.getAllCustomPlaylists()
        .map { entities ->
            entities.map { entity ->
                val songIds = if (entity.songIdsJson.isBlank()) {
                    emptyList()
                } else {
                    entity.songIdsJson.split(",").filter { it.isNotBlank() }
                }
                Playlist(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    coverUrl = entity.coverUrl.ifBlank {
                        "https://i.ytimg.com/vi/vL14wk2cj6U/hqdefault.jpg"
                    },
                    songIds = songIds,
                    isCustom = true,
                    createdBy = "You"
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<Playlist>> = combine(customPlaylists) { custom ->
        val youtubePlaylists = listOf(
            Playlist(
                id = "pl_yt_trending",
                title = "YouTube Top Hits",
                description = "Hottest trending songs streamed ad-free directly from YouTube.",
                coverUrl = "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
                songIds = YouTubeMusicRepository.trendingYouTubeSongs.map { it.id }
            ),
            Playlist(
                id = "pl_yt_desi",
                title = "Bollywood & Desi Vibe",
                description = "Top Indian & Bollywood hits ad-free from YouTube.",
                coverUrl = "https://i.ytimg.com/vi/vL14wk2cj6U/hqdefault.jpg",
                songIds = listOf("yt_vL14wk2cj6U", "yt_B0p-iL_WbcA")
            )
        )
        youtubePlaylists + MusicCatalog.featuredPlaylists + custom[0]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MusicCatalog.featuredPlaylists)

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Song>>(YouTubeMusicRepository.trendingYouTubeSongs)
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(listOf("Arijit Singh", "Coldplay", "The Weeknd", "Lo-Fi Beats", "Taylor Swift", "Bollywood Hits"))
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _showLyricsSheet = MutableStateFlow(false)
    val showLyricsSheet: StateFlow<Boolean> = _showLyricsSheet.asStateFlow()

    private val _showQueueSheet = MutableStateFlow(false)
    val showQueueSheet: StateFlow<Boolean> = _showQueueSheet.asStateFlow()

    private val _showSleepTimerDialog = MutableStateFlow(false)
    val showSleepTimerDialog: StateFlow<Boolean> = _showSleepTimerDialog.asStateFlow()

    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _songForAddToPlaylist = MutableStateFlow<Song?>(null)
    val songForAddToPlaylist: StateFlow<Song?> = _songForAddToPlaylist.asStateFlow()

    init {
        // Initial load of trending YouTube songs
        _searchResults.value = YouTubeMusicRepository.trendingYouTubeSongs
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = YouTubeMusicRepository.trendingYouTubeSongs
                _suggestions.value = listOf("Arijit Singh", "Coldplay", "The Weeknd", "Lo-Fi Beats", "Taylor Swift", "Bollywood Hits")
                _isSearching.value = false
                return@launch
            }

            _isSearching.value = true

            // Fetch live search suggestions
            launch {
                val sug = YouTubeMusicRepository.getSearchSuggestions(query)
                if (sug.isNotEmpty()) {
                    _suggestions.value = sug
                }
            }

            // Debounce for 350ms before querying YouTube Music
            delay(350)
            val results = YouTubeMusicRepository.searchYouTubeMusic(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun executeSearchNow(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            val results = YouTubeMusicRepository.searchYouTubeMusic(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = categoryName
        if (categoryName != null) {
            _searchQuery.value = ""
            viewModelScope.launch {
                _isSearching.value = true
                val results = YouTubeMusicRepository.searchYouTubeMusic(categoryName)
                _searchResults.value = results
                _isSearching.value = false
            }
        }
    }

    fun selectPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun setShowLyrics(show: Boolean) {
        _showLyricsSheet.value = show
    }

    fun setShowQueue(show: Boolean) {
        _showQueueSheet.value = show
    }

    fun setShowSleepTimer(show: Boolean) {
        _showSleepTimerDialog.value = show
    }

    fun setShowCreatePlaylist(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun setSongForAddToPlaylist(song: Song?) {
        _songForAddToPlaylist.value = song
    }

    fun toggleLikeSong(songId: String) {
        viewModelScope.launch {
            val isLiked = likedSongIds.value.contains(songId)
            if (isLiked) {
                musicDao.unlikeSong(songId)
            } else {
                musicDao.likeSong(LikedSongEntity(songId = songId))
            }
        }
    }

    fun createPlaylist(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val id = "custom_pl_${UUID.randomUUID().toString().take(8)}"
            val entity = CustomPlaylistEntity(
                id = id,
                title = title.trim(),
                description = description.trim().ifBlank { "Personal playlist" },
                coverUrl = "https://i.ytimg.com/vi/vL14wk2cj6U/hqdefault.jpg",
                songIdsJson = ""
            )
            musicDao.insertOrUpdatePlaylist(entity)
            _showCreatePlaylistDialog.value = false
        }
    }

    fun addSongToPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            val playlist = customPlaylists.value.find { it.id == playlistId } ?: return@launch
            val currentSongs = playlist.songIds.toMutableList()
            if (!currentSongs.contains(songId)) {
                currentSongs.add(songId)
                val updated = CustomPlaylistEntity(
                    id = playlist.id,
                    title = playlist.title,
                    description = playlist.description,
                    coverUrl = playlist.coverUrl,
                    songIdsJson = currentSongs.joinToString(",")
                )
                musicDao.insertOrUpdatePlaylist(updated)
            }
            _songForAddToPlaylist.value = null
        }
    }

    fun removeSongFromPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            val playlist = customPlaylists.value.find { it.id == playlistId } ?: return@launch
            val currentSongs = playlist.songIds.filter { it != songId }
            val updated = CustomPlaylistEntity(
                id = playlist.id,
                title = playlist.title,
                description = playlist.description,
                coverUrl = playlist.coverUrl,
                songIdsJson = currentSongs.joinToString(",")
            )
            musicDao.insertOrUpdatePlaylist(updated)
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = playlist.copy(songIds = currentSongs)
            }
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            musicDao.deletePlaylist(playlistId)
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = null
            }
        }
    }

    fun playSongFromList(song: Song, list: List<Song>) {
        val index = list.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playerManager.playSongList(list, index)
    }

    fun playPlaylist(playlist: Playlist, shuffle: Boolean = false) {
        val allAvailableSongs = YouTubeMusicRepository.trendingYouTubeSongs + MusicCatalog.songs
        val songsInPlaylist = playlist.songIds.mapNotNull { id ->
            allAvailableSongs.find { it.id == id }
        }
        if (songsInPlaylist.isEmpty()) return
        val queue = if (shuffle) songsInPlaylist.shuffled() else songsInPlaylist
        playerManager.playSongList(queue, 0)
    }
}
