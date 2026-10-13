package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
    private const val TAG = "GeminiAiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!isApiKeyConfigured()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your GEMINI_API_KEY in AI Studio Secrets panel.")
            )
        }

        try {
            val url = "$BASE_URL?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API call failed: ${response.code} $responseBody")
                return@withContext Result.failure(Exception("API Error (${response.code}): $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("Empty response received from Gemini model."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini generation", e)
            Result.failure(e)
        }
    }

    suspend fun generateCaptionsForVideo(themeOrSpeech: String): Result<List<SubtitleCue>> {
        val prompt = """
            You are a professional video editor AI for MotionAI Studio.
            Given this context or dialogue: "$themeOrSpeech",
            Generate 4 to 6 punchy, engaging subtitle captions with start and end timestamps in milliseconds.
            Format your response strictly as a JSON array of objects with keys: "startMs", "endMs", "text".
            Example:
            [
              {"startMs": 0, "endMs": 2500, "text": "Turn your imagination into motion."},
              {"startMs": 2500, "endMs": 5000, "text": "Cinematic visual storytelling unleashed."}
            ]
            Return ONLY the valid JSON array without markdown ticks.
        """.trimIndent()

        val result = generateContent(prompt)
        return result.mapCatching { rawText ->
            val cleanJson = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(cleanJson)
            val cues = mutableListOf<SubtitleCue>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                cues.add(
                    SubtitleCue(
                        startMs = item.optLong("startMs", i * 2500L),
                        endMs = item.optLong("endMs", (i + 1) * 2500L),
                        text = item.optString("text", "Caption ${i + 1}")
                    )
                )
            }
            cues
        }
    }
}

data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val text: String
)
