package com.example

import com.example.data.model.LyricLine
import com.example.data.model.Song
import com.example.data.repository.MusicCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicCatalogUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testMusicCatalogSongsLoaded() {
    assertTrue(MusicCatalog.songs.isNotEmpty())
    assertTrue(MusicCatalog.featuredPlaylists.isNotEmpty())
    assertTrue(MusicCatalog.categories.isNotEmpty())

    val song1 = MusicCatalog.getSongById("song_1")
    assertNotNull(song1)
    assertEquals("Midnight Horizon", song1?.title)
    assertEquals("Synthwave", song1?.genre)
  }

  @Test
  fun testSearchSongs() {
    val searchMidnight = MusicCatalog.searchSongs("Midnight")
    assertTrue(searchMidnight.any { it.title.contains("Midnight") })

    val searchArtist = MusicCatalog.searchSongs("Neon Mirage")
    assertTrue(searchArtist.isNotEmpty())

    val searchEmpty = MusicCatalog.searchSongs("")
    assertEquals(MusicCatalog.songs.size, searchEmpty.size)
  }

  @Test
  fun testSongDurationFormatted() {
    val sampleSong = Song(
      id = "test_1",
      title = "Test Song",
      artist = "Test Artist",
      album = "Test Album",
      durationSeconds = 195,
      audioUrl = "https://example.com/audio.mp3",
      artworkUrl = "https://example.com/art.jpg",
      genre = "Electronic",
      lyrics = listOf(LyricLine(0, "Line 1"))
    )
    assertEquals("3:15", sampleSong.durationFormatted)
  }

  @Test
  fun testYouTubeTrendingSongs() {
    val trending = com.example.data.repository.YouTubeMusicRepository.trendingYouTubeSongs
    assertTrue(trending.isNotEmpty())
    val first = trending[0]
    assertTrue(first.isYouTube)
    assertNotNull(first.youtubeVideoId)
    assertTrue(first.audioUrl.startsWith("http"))
    assertTrue(first.audioUrl.endsWith(".mp3"))
  }
}
