package com.potatosheep.kite.core.media

/**
 * A basic (i.e. non-robust) HLS parser. Note that this parser was specifically designed to work
 * with relatively short videos on Redlib instances, and may not work outside of such an
 * environment.
 *
 * It is also unable to correctly parse longer videos (i.e. multiple `.ts` files) at this time.
 */
object HLSParser {

    /**
     * Retrieves the audio URI in the last `#EXT-X-MEDIA` entry and the video URI from first
     * `#EXT-X-STREAM-INF` entry in a given HLS playlist. You should append this to the end of the
     * URL, replacing the playlist URI.
     *
     * e.g., if this method returns `HLS_720.m3u8` and the playlist URL is:
     *
     * `https://www.example.com/123/HLSPlaylist.m3u8`
     *
     * the new URL should be:
     *
     * `https://www.example.com/123/HLS_720.m3u8`
     *
     * @param playlist the HLS playlist as a [List] of [String]. Each new line in a HLS playlist
     * file should correspond with an element in the list.
     *
     * @return a [HLSUri] object containing the video and (if it exists; empty otherwise) audio
     * URI.
     *
     * @throws IllegalStateException if the playlist file is empty
     * @throws IllegalArgumentException if the playlist file does not contain a `#EXTM3U` entry, or
     * if no video or audio (if the entry for it exists) URI could be found.
     */
    fun parsePlaylist(playlist: List<String>): HLSUri = getFirstStream(playlist)

    /**
     * Retrieves the file URI of the first `#EXT-X-BYTERANGE` entry in an M3U8 file. You should
     * append this to the end of the URL, replacing the M3U8 URI.
     *
     * e.g., if this method returns `HLS_720.ts` and the playlist URL is:
     *
     * `https://www.example.com/123/HLS_720.m3u8`
     *
     * the new URL should be:
     *
     * `https://www.example.com/123/HLS_720.ts`
     *
     * @param m3u8 the contents of the M3U8 file represented as a [List] of [String]. Each new line
     * in the file should correspond with an element in the list.
     *
     * @return a [List] of [String] containing the URI of the video/audio file.
     *
     * @throws IllegalStateException if the M3U8 file is empty.
     * @throws IllegalArgumentException if the M3U8 file does not contain a `#EXTM3U` and
     * `#EXT-X-ENDLIST` entry, or if the `#EXT-X-BYTERANGE` entry does not provide a URI.
     */
    fun parseM3U8(m3u8: List<String>): String = getFirstFile(m3u8)
}

/*
 * Gets the first `#EXT-X-STREAM-INF` entry, which is usually the highest quality one. Also tries to
 * obtain the last audio URI from the `#EXT-X-MEDIA` entry, if it exists.
 *
 * Could be improved.
 */
private fun getFirstStream(playlist: List<String>): HLSUri {
    if (playlist.isEmpty())
        throw IllegalStateException("The file is empty")

    if (playlist[0] != START_KEY)
        throw IllegalArgumentException("The file must start with $START_KEY")

    var videoLink = ""
    var audioLink = ""

    playlist.forEachIndexed { i, line ->
        if (line.contains(STREAM_KEY)) {
            // We only one the first entry, so only assign videoLink a value if is empty.
            if (videoLink.isEmpty()) {
                videoLink = playlist[i+1]
            }
        } else if (line.contains(MEDIA_KEY) && line.contains(AUDIO_TYPE)) {
            // Best audio quality is usually the last entry, to the best of my knowledge, so no
            // checks are needed.
            val audioEntry = line.split(",", ":")
                .find { it.startsWith("URI=") }

            audioLink = audioEntry?.substring(5, audioEntry.length - 1) ?:
            throw IllegalArgumentException("Audio URI could not be found")
        }
    }

    if (videoLink.isEmpty()) throw IllegalArgumentException("Video URI could not be found")

    return HLSUri(videoLink, audioLink)
}

/*
 * Retrieves the file URI of the first `#EXT-X-BYTERANGE` entry in an M3U8 file, which, in the case
 * of Redlib, is usually the same as all the other entries (from limited testing).
 *
 * Could be improved.
 */
private fun getFirstFile(m3u8: List<String>): String {
    if (m3u8.isEmpty())
        throw IllegalStateException("The file is empty")

    if (m3u8[0] != START_KEY)
        throw IllegalArgumentException("The file must start with $START_KEY")

    if (!m3u8.contains(END_KEY))
        throw IllegalArgumentException("The file must end with $END_KEY")

    var uri = ""

    m3u8.forEachIndexed { i, line ->
        if (line.contains(BYTERANGE_KEY) && uri.isEmpty()) {
            uri = m3u8[i+1]
        }
    }

    if (uri.isEmpty()) throw IllegalArgumentException("No valid URI found")

    return uri
}

const val START_KEY = "#EXTM3U"
const val END_KEY = "#EXT-X-ENDLIST"
// const val SEGMENT_KEY = "#EXTINF"
const val STREAM_KEY = "#EXT-X-STREAM-INF"
const val MEDIA_KEY = "#EXT-X-MEDIA"
const val AUDIO_TYPE = "TYPE=AUDIO"
const val BYTERANGE_KEY = "#EXT-X-BYTERANGE"