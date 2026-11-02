package com.example.data.repository

import com.example.data.model.GenreCategory
import com.example.data.model.LyricLine
import com.example.data.model.Playlist
import com.example.data.model.Song

object MusicCatalog {

    val songs: List<Song> = listOf(
        Song(
            id = "song_1",
            title = "Midnight Horizon",
            artist = "Neon Mirage",
            album = "Cyber Dreams",
            durationSeconds = 372,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            genre = "Synthwave",
            releaseYear = 2024,
            playCount = "4.8M",
            lyrics = listOf(
                LyricLine(0, "Neon lights reflecting in the rain..."),
                LyricLine(12000, "Driving down the endless highway line"),
                LyricLine(24000, "Shadows whisper secrets in my mind"),
                LyricLine(38000, "Chasing after dreams we left behind"),
                LyricLine(52000, "Electric pulse, the rhythm starts to glow"),
                LyricLine(66000, "Into the midnight horizon we go"),
                LyricLine(82000, "Synthesizer waves crash upon the shore"),
                LyricLine(98000, "We found a beat we never knew before"),
                LyricLine(115000, "Echoes in the dark, calling out your name"),
                LyricLine(135000, "Nothing in this city stays the same"),
                LyricLine(155000, "Under purple skies, the sparks ignite"),
                LyricLine(180000, "Lost inside the pulse of digital light"),
                LyricLine(210000, "Midnight horizon, take me home again")
            )
        ),
        Song(
            id = "song_2",
            title = "Solaris Drift",
            artist = "Aura Bloom",
            album = "Starlight Odyssey",
            durationSeconds = 423,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            genre = "Chillout",
            releaseYear = 2024,
            playCount = "3.2M",
            lyrics = listOf(
                LyricLine(0, "Drifting slow across the velvet deep"),
                LyricLine(15000, "Starry secrets that the planets keep"),
                LyricLine(32000, "Floating weightless, let the cosmos breathe"),
                LyricLine(50000, "All the golden rays begin to weave"),
                LyricLine(70000, "Solaris shines, illuminate the dark"),
                LyricLine(92000, "Every beat a gentle wandering spark"),
                LyricLine(116000, "No gravity to pull us to the ground"),
                LyricLine(140000, "In this quiet ocean of sweet sound"),
                LyricLine(175000, "Infinite horizons open wide"),
                LyricLine(210000, "Peaceful journey on the solar tide")
            )
        ),
        Song(
            id = "song_3",
            title = "Velvet Coffee House",
            artist = "The Lo-Fi Collective",
            album = "Rainy Sunday Afternoon",
            durationSeconds = 345,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            genre = "Lo-Fi",
            releaseYear = 2023,
            playCount = "8.1M",
            lyrics = listOf(
                LyricLine(0, "Raindrops tapping on the windowpane"),
                LyricLine(14000, "Warm espresso washes off the strain"),
                LyricLine(28000, "Gentle piano keys drift through the air"),
                LyricLine(42000, "Books and vinyl scattered everywhere"),
                LyricLine(58000, "Take a breath and let the worries fade"),
                LyricLine(75000, "In the mellow corner that we made"),
                LyricLine(95000, "Crackling vinyl, comforting and slow"),
                LyricLine(120000, "Nowhere else we really have to go"),
                LyricLine(150000, "Just sipping coffee as the hours glide"),
                LyricLine(180000, "Warm and peaceful safe inside")
            )
        ),
        Song(
            id = "song_4",
            title = "Tokyo Neon Pulse",
            artist = "Kitsune Beats",
            album = "Shibuya Nights",
            durationSeconds = 312,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            releaseYear = 2024,
            playCount = "5.5M",
            lyrics = listOf(
                LyricLine(0, "Crossing Shibuya in the midnight haze"),
                LyricLine(14000, "Flashing billboards in a dizzy maze"),
                LyricLine(28000, "Bassline kicking through the subway floor"),
                LyricLine(42000, "Every alleyway opens a door"),
                LyricLine(60000, "Turn the volume up and feel the groove"),
                LyricLine(80000, "Neon rhythm making thousands move"),
                LyricLine(105000, "Tokyo pulse electric in our veins"),
                LyricLine(130000, "Dancing freely, breaking all our chains"),
                LyricLine(160000, "High above the skyscrapers tonight"),
                LyricLine(190000, "Burning brighter than the city light")
            )
        ),
        Song(
            id = "song_5",
            title = "Ocean Breeze Acoustic",
            artist = "Maya Lin",
            album = "Coastal Serenade",
            durationSeconds = 380,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            genre = "Acoustic",
            releaseYear = 2023,
            playCount = "2.9M",
            lyrics = listOf(
                LyricLine(0, "Barefoot footprints washed by gentle sea"),
                LyricLine(16000, "Where the ocean breeze whispers to me"),
                LyricLine(32000, "Acoustic strings resonating sweet"),
                LyricLine(50000, "Sun sinks down where sky and water meet"),
                LyricLine(70000, "Golden hour coloring the bay"),
                LyricLine(92000, "Let the tide take yesterday away"),
                LyricLine(120000, "Singing softly with the evening swell"),
                LyricLine(150000, "Stories only open seas can tell"),
                LyricLine(185000, "Breeze of summer, hold me in your arms")
            )
        ),
        Song(
            id = "song_6",
            title = "Quantum Rush",
            artist = "Hyperion Protocol",
            album = "Overdrive 3000",
            durationSeconds = 398,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            releaseYear = 2024,
            playCount = "6.1M",
            lyrics = listOf(
                LyricLine(0, "Energy buildup, systems engage"),
                LyricLine(12000, "Stepping onto the sonic stage"),
                LyricLine(24000, "Quantum acceleration in the code"),
                LyricLine(38000, "Blast through limits on the speedway road"),
                LyricLine(55000, "Drop the bass and elevate the high"),
                LyricLine(75000, "Sonic fire reaching to the sky"),
                LyricLine(100000, "Overdrive kicking into gear"),
                LyricLine(130000, "Sound waves roaring crystal clear")
            )
        ),
        Song(
            id = "song_7",
            title = "Autumn Echoes",
            artist = "Cedric Vance",
            album = "Whispering Pines",
            durationSeconds = 415,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1447752875215-b2761acb3c5d?w=600&auto=format&fit=crop&q=80",
            genre = "Folk & Indie",
            releaseYear = 2023,
            playCount = "1.8M",
            lyrics = listOf(
                LyricLine(0, "Amber leaves are falling to the ground"),
                LyricLine(18000, "Through the forest, quiet echoes sound"),
                LyricLine(36000, "Crisp morning air and campfire smoke"),
                LyricLine(55000, "Promises of memories we spoke"),
                LyricLine(80000, "Seasons change but melodies remain"),
                LyricLine(110000, "Shelter through the autumn mountain rain"),
                LyricLine(145000, "Golden days are slipping slow away")
            )
        ),
        Song(
            id = "song_8",
            title = "Midnight Drive",
            artist = "RetroWave Boys",
            album = "Outrun The Dawn",
            durationSeconds = 350,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop&q=80",
            genre = "Synthwave",
            releaseYear = 2024,
            playCount = "7.3M",
            lyrics = listOf(
                LyricLine(0, "Tachometer redlining in the night"),
                LyricLine(14000, "Chasing after the neon tail light"),
                LyricLine(30000, "Cassette tape rolling, heavy drum machine"),
                LyricLine(46000, "Best damn drive this city's ever seen"),
                LyricLine(66000, "Fast forward into 1985"),
                LyricLine(88000, "This is what it means to feel alive"),
                LyricLine(115000, "Outrun the sunrise, never look behind")
            )
        ),
        Song(
            id = "song_9",
            title = "Emerald Meadows",
            artist = "Seraphina",
            album = "Tranquil Reverie",
            durationSeconds = 360,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
            genre = "Ambient",
            releaseYear = 2023,
            playCount = "3.4M",
            lyrics = listOf(
                LyricLine(0, "Deep breath in, stillness in the air"),
                LyricLine(20000, "Emerald grasses whispering a prayer"),
                LyricLine(45000, "Gentle chimes floating on the wind"),
                LyricLine(70000, "Where the worries of the world rescind"),
                LyricLine(100000, "Rest your mind and let the spirit soar"),
                LyricLine(135000, "Find the calm you've been looking for")
            )
        ),
        Song(
            id = "song_10",
            title = "Cosmic Groove",
            artist = "Funk Dimension",
            album = "Galaxy Disco",
            durationSeconds = 388,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            artworkUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            genre = "Disco & Funk",
            releaseYear = 2024,
            playCount = "5.1M",
            lyrics = listOf(
                LyricLine(0, "Slap that bassline, feel the planetary heat"),
                LyricLine(15000, "Everybody on the moon is on their feet"),
                LyricLine(30000, "Glitter balls spinning in outer space"),
                LyricLine(48000, "Smile lighting up on every single face"),
                LyricLine(70000, "Cosmic groove, get down tonight"),
                LyricLine(95000, "Dance until the stars shine bright")
            )
        )
    )

    val featuredPlaylists: List<Playlist> = listOf(
        Playlist(
            id = "pl_top_hits",
            title = "Today's Top Hits",
            description = "The hottest streaming tracks right now. Cover: Neon Mirage & Kitsune Beats.",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            songIds = listOf("song_1", "song_4", "song_6", "song_8", "song_10")
        ),
        Playlist(
            id = "pl_chill_lofi",
            title = "Chill Lofi Beats",
            description = "Calm your mind, study, or relax with cozy rainy day beats and ambient melodies.",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            songIds = listOf("song_3", "song_2", "song_5", "song_9")
        ),
        Playlist(
            id = "pl_synthwave_run",
            title = "Synthwave 1984",
            description = "High-octane retro electronic, outrun, and cyberpunk night drive anthems.",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            songIds = listOf("song_1", "song_8", "song_4", "song_6")
        ),
        Playlist(
            id = "pl_deep_focus",
            title = "Deep Focus & Flow",
            description = "Ambient soundscapes and smooth instrumental grooves to boost your productivity.",
            coverUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
            songIds = listOf("song_9", "song_2", "song_7", "song_3")
        ),
        Playlist(
            id = "pl_indie_acoustic",
            title = "Acoustic Sunrise",
            description = "Warm acoustic guitars, indie folk gems, and soothing coastal vocals.",
            coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            songIds = listOf("song_5", "song_7", "song_2")
        )
    )

    val categories: List<GenreCategory> = listOf(
        GenreCategory("cat_synth", "Synthwave", 0xFF8A2387, 0xFFE94057, "waves"),
        GenreCategory("cat_lofi", "Lo-Fi Beats", 0xFF11998E, 0xFF38EF7D, "coffee"),
        GenreCategory("cat_edm", "Electronic", 0xFF4776E6, 0xFF8E54E9, "bolt"),
        GenreCategory("cat_chill", "Chillout", 0xFF2C3E50, 0xFF3498DB, "cloud"),
        GenreCategory("cat_acoustic", "Acoustic", 0xFFF2994A, 0xFFF2C94C, "nature"),
        GenreCategory("cat_ambient", "Ambient", 0xFF0F2027, 0xFF2C5364, "spa"),
        GenreCategory("cat_funk", "Disco & Funk", 0xFFFF512F, 0xFFDD2476, "nightlife"),
        GenreCategory("cat_indie", "Indie Folk", 0xFF56AB2F, 0xFFA8E063, "park")
    )

    fun getSongById(id: String): Song? = songs.find { it.id == id }

    fun searchSongs(query: String): List<Song> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return songs
        return songs.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q) ||
            it.genre.lowercase().contains(q)
        }
    }
}
