package com.autotagauditor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Structures for sending data to Gemini
@Serializable data class GeminiRequest(val contents: List<Content>)
@Serializable data class Content(val parts: List<Part>)
@Serializable data class Part(val text: String)
// Structures for receiving SUCCESS response from Gemini
@Serializable data class GeminiResponse(val candidates: List<Candidate>?)
@Serializable data class Candidate(val content: Content?)
// Structures for receiving ERRORS from gemini
@Serializable data class GeminiErrorResponse(val error: GeminiErrorDetails?)
@Serializable data class GeminiErrorDetails(val code: Int?, val message: String?, val status: String?)


object AiService {
    private const val API_KEY = Secrets.GEMINI_API_KEY
    private const val URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"

    private val client = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Now this works because of the import
        }
    }

    suspend fun analyzeTags(tags: List<String>): String {
        if (tags.isEmpty()) return "No tags found to analyze."

        // The Prompt: We tell the AI exactly what we want
        val prompt = "I found these tracking tags on a website: $tags. " +
                "Explain to a non-technical marketing manager exactly what these specific tags are used for. Keep it under 2 sentences."

        try {
            val response = client.post("$URL?key=$API_KEY") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(listOf(Content(listOf(Part(prompt))))))
            }

            val responseBody = response.bodyAsText()

            println("HTTP Status: ${response.status}")
            println("Raw Body:$responseBody")

            if (response.status.value != 200) {
                try {
                    val errorObj = Json { ignoreUnknownKeys = true }.decodeFromString<GeminiErrorResponse>(responseBody)
                    return "AI Error: ${errorObj.error?.message ?: "Unkown API Error"}"
                } catch (e: Exception) {
                    return "AI Error: Google returned status ${response.status}"
                }
            }

            // Extract the text from the complex JSON response
            val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<GeminiResponse>(responseBody)
            return parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "AI Analysis Unavailable"

        } catch (e: Exception) {
            println("AI Error: ${e.message}")
            return "Could not generate AI analysis. (Check API Key)"
        }
    }
}