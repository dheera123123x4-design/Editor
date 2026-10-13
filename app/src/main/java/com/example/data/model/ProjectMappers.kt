package com.example.data.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ProjectMappers {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val clipsListType = Types.newParameterizedType(List::class.java, Clip::class.java)
    private val clipsAdapter = moshi.adapter<List<Clip>>(clipsListType)

    private val textListType = Types.newParameterizedType(List::class.java, TextOverlay::class.java)
    private val textAdapter = moshi.adapter<List<TextOverlay>>(textListType)

    private val audioAdapter = moshi.adapter(AudioTrack::class.java)

    fun clipsToJson(clips: List<Clip>): String {
        return try {
            clipsAdapter.toJson(clips)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToClips(json: String?): List<Clip> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            clipsAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun textOverlaysToJson(texts: List<TextOverlay>): String {
        return try {
            textAdapter.toJson(texts)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToTextOverlays(json: String?): List<TextOverlay> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            textAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun audioTrackToJson(audio: AudioTrack?): String? {
        if (audio == null) return null
        return try {
            audioAdapter.toJson(audio)
        } catch (e: Exception) {
            null
        }
    }

    fun jsonToAudioTrack(json: String?): AudioTrack? {
        if (json.isNullOrBlank()) return null
        return try {
            audioAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
