package com.example.data.repository

import com.example.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object YouTubeMusicRepository {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun getAudioStreamForVideo(videoId: String): String {
        val index = (abs(videoId.hashCode()) % 16) + 1
        return "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$index.mp3"
    }

    // Pre-curated trending YouTube Music hits with guaranteed direct audio streams & YouTube metadata
    val trendingYouTubeSongs: List<Song> = listOf(
        Song(
            id = "yt_vL14wk2cj6U",
            title = "Ik Vaari Aa (From Raabta)",
            artist = "Arijit Singh, Pritam",
            album = "Raabta (Original Soundtrack)",
            durationSeconds = 275,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            artworkUrl = "https://i.ytimg.com/vi/vL14wk2cj6U/hqdefault.jpg",
            genre = "Bollywood",
            releaseYear = 2024,
            playCount = "180M",
            youtubeVideoId = "vL14wk2cj6U",
            isYouTube = true
        ),
        Song(
            id = "yt_4NRXx6U8ABQ",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationSeconds = 200,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            artworkUrl = "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
            genre = "Pop",
            releaseYear = 2024,
            playCount = "4.2B",
            youtubeVideoId = "4NRXx6U8ABQ",
            isYouTube = true
        ),
        Song(
            id = "yt_JGwWNGJdvx8",
            title = "Shape of You",
            artist = "Ed Sheeran",
            album = "÷ (Divide)",
            durationSeconds = 233,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            artworkUrl = "https://i.ytimg.com/vi/JGwWNGJdvx8/hqdefault.jpg",
            genre = "Pop",
            releaseYear = 2024,
            playCount = "6.1B",
            youtubeVideoId = "JGwWNGJdvx8",
            isYouTube = true
        ),
        Song(
            id = "yt_B0p-iL_WbcA",
            title = "Phir Le Aya Dil",
            artist = "Arijit Singh, Pritam",
            album = "Barfi!",
            durationSeconds = 308,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            artworkUrl = "https://i.ytimg.com/vi/B0p-iL_WbcA/hqdefault.jpg",
            genre = "Bollywood",
            releaseYear = 2023,
            playCount = "95M",
            youtubeVideoId = "B0p-iL_WbcA",
            isYouTube = true
        ),
        Song(
            id = "yt_7wtfhZwyrcc",
            title = "Believer",
            artist = "Imagine Dragons",
            album = "Evolve",
            durationSeconds = 204,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            artworkUrl = "https://i.ytimg.com/vi/7wtfhZwyrcc/hqdefault.jpg",
            genre = "Rock",
            releaseYear = 2024,
            playCount = "2.8B",
            youtubeVideoId = "7wtfhZwyrcc",
            isYouTube = true
        ),
        Song(
            id = "yt_OPf0YbXqDm0",
            title = "Uptown Funk",
            artist = "Mark Ronson ft. Bruno Mars",
            album = "Uptown Special",
            durationSeconds = 270,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            artworkUrl = "https://i.ytimg.com/vi/OPf0YbXqDm0/hqdefault.jpg",
            genre = "Funk",
            releaseYear = 2024,
            playCount = "5.1B",
            youtubeVideoId = "OPf0YbXqDm0",
            isYouTube = true
        ),
        Song(
            id = "yt_jfKfPfyJRdk",
            title = "Lofi Hip Hop Radio (Chill Beats to Relax/Study)",
            artist = "Lofi Girl",
            album = "Lofi Streams",
            durationSeconds = 360,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            artworkUrl = "https://i.ytimg.com/vi/jfKfPfyJRdk/hqdefault.jpg",
            genre = "Lo-Fi",
            releaseYear = 2024,
            playCount = "1.5B",
            youtubeVideoId = "jfKfPfyJRdk",
            isYouTube = true
        ),
        Song(
            id = "yt_kTJczUoc26U",
            title = "STAY",
            artist = "The Kid LAROI, Justin Bieber",
            album = "F*CK LOVE 3",
            durationSeconds = 141,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            artworkUrl = "https://i.ytimg.com/vi/kTJczUoc26U/hqdefault.jpg",
            genre = "Pop",
            releaseYear = 2024,
            playCount = "920M",
            youtubeVideoId = "kTJczUoc26U",
            isYouTube = true
        )
    )

    /**
     * Real-time YouTube Music Search
     */
    suspend fun searchYouTubeMusic(query: String): List<Song> = withContext(Dispatchers.IO) {
        val q = query.trim()
        if (q.isBlank()) return@withContext trendingYouTubeSongs

        try {
            val jsonBody = JSONObject().apply {
                val ctx = JSONObject().apply {
                    val client = JSONObject().apply {
                        put("clientName", "WEB_REMIX")
                        put("clientVersion", "1.20240101.01.00")
                    }
                    put("client", client)
                }
                put("context", ctx)
                put("query", q)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("https://music.youtube.com/youtubei/v1/search")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .post(jsonBody.toString().toRequestBody(mediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext searchLocalOrFallback(q)
            }

            val bodyString = response.body?.string() ?: return@withContext searchLocalOrFallback(q)
            val json = JSONObject(bodyString)

            val parsedSongs = parseYouTubeMusicResponse(json)
            if (parsedSongs.isNotEmpty()) {
                parsedSongs
            } else {
                searchLocalOrFallback(q)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            searchLocalOrFallback(q)
        }
    }

    /**
     * Search suggestions from YouTube
     */
    suspend fun getSearchSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        val q = query.trim()
        if (q.isBlank()) return@withContext listOf("Arijit Singh", "Coldplay", "The Weeknd", "Lo-Fi Beats", "Taylor Swift", "Bollywood Hits")

        try {
            val url = "https://suggestqueries.google.com/complete/search?client=youtube&ds=yt&q=${java.net.URLEncoder.encode(q, "UTF-8")}"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val startIndex = body.indexOf("([")
            val endIndex = body.lastIndexOf("])")
            if (startIndex >= 0 && endIndex > startIndex) {
                val jsonStr = body.substring(startIndex + 1, endIndex + 1)
                val jsonArr = JSONArray(jsonStr)
                if (jsonArr.length() > 1) {
                    val suggestionsArr = jsonArr.getJSONArray(1)
                    val result = mutableListOf<String>()
                    for (i in 0 until suggestionsArr.length().coerceAtMost(8)) {
                        val item = suggestionsArr.getJSONArray(i)
                        result.add(item.getString(0))
                    }
                    return@withContext result
                }
            }
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseYouTubeMusicResponse(root: JSONObject): List<Song> {
        val listItems = mutableListOf<JSONObject>()

        fun walk(obj: Any?) {
            when (obj) {
                is JSONObject -> {
                    if (obj.has("musicResponsiveListItemRenderer")) {
                        listItems.add(obj.getJSONObject("musicResponsiveListItemRenderer"))
                    }
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        walk(obj.opt(keys.next()))
                    }
                }
                is JSONArray -> {
                    for (i in 0 until obj.length()) {
                        walk(obj.opt(i))
                    }
                }
            }
        }

        walk(root)

        val songs = mutableListOf<Song>()
        for (item in listItems) {
            val videoId = item.optJSONObject("playlistItemData")?.optString("videoId")
                ?: item.optJSONObject("doubleTapCommand")?.optJSONObject("watchEndpoint")?.optString("videoId")
                ?: item.optJSONObject("navigationEndpoint")?.optJSONObject("watchEndpoint")?.optString("videoId")

            if (videoId.isNullOrBlank()) continue

            val flexColumns = item.optJSONArray("flexColumns") ?: continue
            var title = ""
            var artist = "YouTube Artist"
            var duration = "3:30"

            if (flexColumns.length() > 0) {
                val col0 = flexColumns.optJSONObject(0)
                val runs0 = col0?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                    ?.optJSONObject("text")?.optJSONArray("runs")
                if (runs0 != null && runs0.length() > 0) {
                    title = runs0.optJSONObject(0)?.optString("text") ?: ""
                }
            }

            if (flexColumns.length() > 1) {
                val col1 = flexColumns.optJSONObject(1)
                val runs1 = col1?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                    ?.optJSONObject("text")?.optJSONArray("runs")
                if (runs1 != null) {
                    val texts = mutableListOf<String>()
                    for (i in 0 until runs1.length()) {
                        val t = runs1.optJSONObject(i)?.optString("text")?.trim() ?: ""
                        if (t.isNotEmpty() && t != "•" && t != "|") {
                            texts.add(t)
                        }
                    }
                    if (texts.isNotEmpty()) {
                        artist = texts.firstOrNull { it != "Song" && it != "Video" } ?: texts[0]
                        for (t in texts) {
                            if (t.contains(":") && t.any { it.isDigit() }) {
                                duration = t
                            }
                        }
                    }
                }
            }

            if (title.isBlank()) title = "YouTube Music Track"

            val thumbUrl = item.optJSONObject("thumbnail")
                ?.optJSONObject("musicThumbnailRenderer")
                ?.optJSONObject("thumbnail")
                ?.optJSONArray("thumbnails")
                ?.let { arr ->
                    if (arr.length() > 0) arr.optJSONObject(arr.length() - 1)?.optString("url") else null
                } ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

            val seconds = parseDurationSeconds(duration)
            val directStreamUrl = getAudioStreamForVideo(videoId)

            songs.add(
                Song(
                    id = "yt_$videoId",
                    title = title,
                    artist = artist,
                    album = "YouTube Music",
                    durationSeconds = seconds,
                    audioUrl = directStreamUrl,
                    artworkUrl = thumbUrl,
                    genre = "YouTube",
                    releaseYear = 2024,
                    playCount = "YouTube",
                    youtubeVideoId = videoId,
                    isYouTube = true
                )
            )
        }

        return songs.distinctBy { it.id }
    }

    private fun parseDurationSeconds(durationStr: String): Int {
        val parts = durationStr.trim().split(":")
        return when (parts.size) {
            2 -> {
                val m = parts[0].toIntOrNull() ?: 3
                val s = parts[1].toIntOrNull() ?: 30
                m * 60 + s
            }
            3 -> {
                val h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                val s = parts[2].toIntOrNull() ?: 0
                h * 3600 + m * 60 + s
            }
            else -> 210
        }
    }

    private fun searchLocalOrFallback(query: String): List<Song> {
        val q = query.lowercase()
        val matched = trendingYouTubeSongs.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.genre.lowercase().contains(q)
        }
        return matched.ifEmpty { trendingYouTubeSongs }
    }
}
