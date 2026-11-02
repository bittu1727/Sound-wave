package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.MusicViewModel
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyLightGray

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MusicViewModel) {
    val context = LocalContext.current

    // Request notification permission on Android 13+ for background playback media controls notification
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val currentSong by viewModel.playerManager.currentSong.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val isBuffering by viewModel.playerManager.isBuffering.collectAsState()
    val currentPositionMs by viewModel.playerManager.currentPositionMs.collectAsState()
    val durationMs by viewModel.playerManager.durationMs.collectAsState()
    val likedIds by viewModel.likedSongIds.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()

    val showCreatePlaylist by viewModel.showCreatePlaylistDialog.collectAsState()
    val songForAddToPlaylist by viewModel.songForAddToPlaylist.collectAsState()
    val customPlaylists by viewModel.customPlaylists.collectAsState()
    val showSleepTimer by viewModel.showSleepTimerDialog.collectAsState()
    val sleepTimerSeconds by viewModel.playerManager.sleepTimerRemainingSeconds.collectAsState()

    val isLiked = currentSong != null && likedIds.contains(currentSong?.id)

    val effectiveDuration = if (durationMs > 0) durationMs else ((currentSong?.durationSeconds ?: 1) * 1000L)
    val progress = (currentPositionMs.toFloat() / effectiveDuration.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = SpotifyBlack,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Floating Mini Player
                    MiniPlayerBar(
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        isBuffering = isBuffering,
                        isLiked = isLiked,
                        progress = progress,
                        onMiniPlayerClick = { viewModel.setNowPlayingExpanded(true) },
                        onPlayPauseClick = { viewModel.playerManager.togglePlayPause() },
                        onNextClick = { viewModel.playerManager.skipToNext() },
                        onLikeClick = { currentSong?.let { viewModel.toggleLikeSong(it.id) } }
                    )

                    // Spotify 3-Tab Bottom Navigation Bar
                    NavigationBar(
                        containerColor = Color(0xFF101010),
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0 && selectedPlaylist == null,
                            onClick = {
                                selectedTab = 0
                                viewModel.selectPlaylist(null)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 0 && selectedPlaylist == null) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = SpotifyGreen.copy(alpha = 0.2f),
                                unselectedIconColor = SpotifyLightGray,
                                unselectedTextColor = SpotifyLightGray
                            ),
                            modifier = Modifier.testTag("bottom_nav_home")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 1 && selectedPlaylist == null,
                            onClick = {
                                selectedTab = 1
                                viewModel.selectPlaylist(null)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 1 && selectedPlaylist == null) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            },
                            label = { Text("Search") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = SpotifyGreen.copy(alpha = 0.2f),
                                unselectedIconColor = SpotifyLightGray,
                                unselectedTextColor = SpotifyLightGray
                            ),
                            modifier = Modifier.testTag("bottom_nav_search")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 2 && selectedPlaylist == null,
                            onClick = {
                                selectedTab = 2
                                viewModel.selectPlaylist(null)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 2 && selectedPlaylist == null) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                    contentDescription = "Your Library"
                                )
                            },
                            label = { Text("Your Library") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = SpotifyGreen.copy(alpha = 0.2f),
                                unselectedIconColor = SpotifyLightGray,
                                unselectedTextColor = SpotifyLightGray
                            ),
                            modifier = Modifier.testTag("bottom_nav_library")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (selectedPlaylist != null) {
                    PlaylistDetailScreen(
                        playlist = selectedPlaylist!!,
                        viewModel = viewModel,
                        onBack = { viewModel.selectPlaylist(null) }
                    )
                } else {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onPlaylistClick = { playlist -> viewModel.selectPlaylist(playlist) }
                        )
                        1 -> SearchScreen(viewModel = viewModel)
                        2 -> LibraryScreen(
                            viewModel = viewModel,
                            onPlaylistClick = { playlist -> viewModel.selectPlaylist(playlist) }
                        )
                    }
                }
            }
        }

        // Full-screen Now Playing Overlay
        AnimatedVisibility(
            visible = isNowPlayingExpanded && currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            NowPlayingScreen(
                viewModel = viewModel,
                onCollapse = { viewModel.setNowPlayingExpanded(false) }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylist) {
            CreatePlaylistDialog(
                onDismiss = { viewModel.setShowCreatePlaylist(false) },
                onCreate = { title, desc -> viewModel.createPlaylist(title, desc) }
            )
        }

        // Add to Playlist Dialog
        songForAddToPlaylist?.let { song ->
            AddToPlaylistDialog(
                song = song,
                playlists = customPlaylists,
                onDismiss = { viewModel.setSongForAddToPlaylist(null) },
                onPlaylistSelected = { playlistId ->
                    viewModel.addSongToPlaylist(playlistId, song.id)
                },
                onCreateNewClick = {
                    viewModel.setShowCreatePlaylist(true)
                }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimer) {
            SleepTimerDialog(
                activeRemainingSeconds = sleepTimerSeconds,
                onDismiss = { viewModel.setShowSleepTimer(false) },
                onSetTimer = { minutes ->
                    viewModel.playerManager.setSleepTimer(minutes)
                }
            )
        }
    }
}
