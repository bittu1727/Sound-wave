package com.example.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.data.model.Song
import com.example.data.repository.MusicCatalog
import com.example.data.repository.YouTubeMusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppRepeatMode {
    OFF, ALL, ONE
}

class PlayerManager private constructor(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    private var mediaController: MediaController? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(AppRepeatMode.OFF)
    val repeatMode: StateFlow<AppRepeatMode> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _sleepTimerRemainingSeconds = MutableStateFlow<Int?>(null)
    val sleepTimerRemainingSeconds: StateFlow<Int?> = _sleepTimerRemainingSeconds.asStateFlow()

    init {
        initializeMediaController()
    }

    private fun initializeMediaController(onReady: ((MediaController) -> Unit)? = null) {
        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, MusicPlaybackService::class.java)
        )
        val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                try {
                    val controller = controllerFuture.get()
                    mediaController = controller
                    setupPlayerListener(controller)
                    syncFromController(controller)
                    onReady?.invoke(controller)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    private fun setupPlayerListener(player: Player) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressTicker()
                } else {
                    stopProgressTicker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = player.duration.coerceAtLeast(0L)
                    _isBuffering.value = false
                } else if (playbackState == Player.STATE_ENDED) {
                    skipToNext()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem != null) {
                    val songId = mediaItem.mediaId
                    val allKnownSongs = _queue.value + YouTubeMusicRepository.trendingYouTubeSongs + MusicCatalog.songs
                    val song = allKnownSongs.find { it.id == songId }
                    if (song != null) {
                        _currentSong.value = song
                        _queueIndex.value = player.currentMediaItemIndex
                        _durationMs.value = if (player.duration > 0) player.duration else (song.durationSeconds * 1000L)
                        _currentPositionMs.value = 0L
                    }
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _isShuffleEnabled.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> AppRepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> AppRepeatMode.ALL
                    else -> AppRepeatMode.OFF
                }
            }
        })
    }

    private fun syncFromController(player: Player) {
        _isPlaying.value = player.isPlaying
        _isShuffleEnabled.value = player.shuffleModeEnabled
        _repeatMode.value = when (player.repeatMode) {
            Player.REPEAT_MODE_ONE -> AppRepeatMode.ONE
            Player.REPEAT_MODE_ALL -> AppRepeatMode.ALL
            else -> AppRepeatMode.OFF
        }
        val currentItem = player.currentMediaItem
        if (currentItem != null) {
            val allKnownSongs = _queue.value + YouTubeMusicRepository.trendingYouTubeSongs + MusicCatalog.songs
            _currentSong.value = allKnownSongs.find { it.id == currentItem.mediaId }
            _queueIndex.value = player.currentMediaItemIndex
            _durationMs.value = player.duration.coerceAtLeast(0L)
            _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
        }
        if (player.isPlaying) {
            startProgressTicker()
        }
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    _currentPositionMs.value = controller.currentPosition.coerceAtLeast(0L)
                    if (controller.duration > 0) {
                        _durationMs.value = controller.duration
                    }
                }
                delay(400)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playSongList(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        _queue.value = songs
        val validIndex = startIndex.coerceIn(0, songs.size - 1)
        _queueIndex.value = validIndex
        val targetSong = songs[validIndex]
        _currentSong.value = targetSong
        _currentPositionMs.value = 0L
        _durationMs.value = (targetSong.durationSeconds * 1000L)
        _isBuffering.value = true
        _isPlaying.value = true

        val mediaItems = songs.map { s ->
            MediaItem.Builder()
                .setMediaId(s.id)
                .setUri(s.audioUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist)
                        .setAlbumTitle(s.album)
                        .setArtworkUri(android.net.Uri.parse(s.artworkUrl))
                        .build()
                )
                .build()
        }

        val controller = mediaController
        if (controller != null) {
            controller.setMediaItems(mediaItems, validIndex, 0L)
            controller.prepare()
            controller.play()
            startProgressTicker()
        } else {
            initializeMediaController { c ->
                c.setMediaItems(mediaItems, validIndex, 0L)
                c.prepare()
                c.play()
                startProgressTicker()
            }
        }
    }

    fun playSingleSong(song: Song) {
        val currentQueueList = _queue.value
        val existingIndex = currentQueueList.indexOfFirst { it.id == song.id }
        if (existingIndex >= 0) {
            playSongList(currentQueueList, existingIndex)
        } else {
            val newQueue = currentQueueList.toMutableList().apply { add(song) }
            playSongList(newQueue, newQueue.size - 1)
        }
    }

    fun togglePlayPause() {
        val controller = mediaController
        if (controller != null) {
            if (controller.isPlaying) {
                controller.pause()
                _isPlaying.value = false
            } else {
                if (controller.mediaItemCount > 0) {
                    controller.play()
                    _isPlaying.value = true
                } else if (_currentSong.value != null) {
                    _currentSong.value?.let { playSingleSong(it) }
                } else if (YouTubeMusicRepository.trendingYouTubeSongs.isNotEmpty()) {
                    playSongList(YouTubeMusicRepository.trendingYouTubeSongs, 0)
                }
            }
        } else {
            initializeMediaController { c ->
                if (_currentSong.value != null) {
                    _currentSong.value?.let { playSingleSong(it) }
                } else {
                    playSongList(YouTubeMusicRepository.trendingYouTubeSongs, 0)
                }
            }
        }
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs
        mediaController?.seekTo(positionMs)
    }

    fun skipToNext() {
        if (_queue.value.isNotEmpty()) {
            val nextIndex = (_queueIndex.value + 1) % _queue.value.size
            playSongList(_queue.value, nextIndex)
        } else {
            mediaController?.seekToNextMediaItem()
        }
    }

    fun skipToPrevious() {
        if (_currentPositionMs.value > 3000L) {
            seekTo(0L)
        } else if (_queue.value.isNotEmpty()) {
            val prevIndex = if (_queueIndex.value - 1 < 0) _queue.value.size - 1 else _queueIndex.value - 1
            playSongList(_queue.value, prevIndex)
        } else {
            mediaController?.seekToPreviousMediaItem()
        }
    }

    fun toggleShuffle() {
        val newState = !_isShuffleEnabled.value
        _isShuffleEnabled.value = newState
        mediaController?.shuffleModeEnabled = newState
    }

    fun toggleRepeatMode() {
        val nextMode = when (_repeatMode.value) {
            AppRepeatMode.OFF -> AppRepeatMode.ALL
            AppRepeatMode.ALL -> AppRepeatMode.ONE
            AppRepeatMode.ONE -> AppRepeatMode.OFF
        }
        _repeatMode.value = nextMode
        val playerMode = when (nextMode) {
            AppRepeatMode.ONE -> Player.REPEAT_MODE_ONE
            AppRepeatMode.ALL -> Player.REPEAT_MODE_ALL
            AppRepeatMode.OFF -> Player.REPEAT_MODE_OFF
        }
        mediaController?.repeatMode = playerMode
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        mediaController?.playbackParameters = PlaybackParameters(speed)
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _sleepTimerRemainingSeconds.value = null
            return
        }
        var remainingSeconds = minutes * 60
        _sleepTimerRemainingSeconds.value = remainingSeconds

        sleepTimerJob = scope.launch {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds -= 1
                _sleepTimerRemainingSeconds.value = remainingSeconds
            }
            mediaController?.pause()
            _isPlaying.value = false
            _sleepTimerRemainingSeconds.value = null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PlayerManager? = null

        fun getInstance(context: Context): PlayerManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PlayerManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
