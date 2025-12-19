package com.autotagauditor

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.autotagauditor.BotService

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("AutoTag Auditor Initialized")
        }

        //The Audit Endpoint
        //Usage: /audit?url=https://example.com
        get("/audit") {
            //Get URL from query parameter
            val targetUrl = call.request.queryParameters["url"]

            //Input validation
            if (targetUrl == null) {
                call.respondText("Missing url")
                return@get
            }

            val initialResult = BotService.auditUrl(targetUrl)

            //Ask Gemini to explain the tags
            var analysis = "No tags to analyze."
            if (initialResult.tagsFound.isNotEmpty()) {
                analysis = AiService.analyzeTags(initialResult.tagsFound)
            }

            //Combine Results
            val finalResult = initialResult.copy(aiAnalysis = analysis)

            FirestoreService.saveScan(finalResult)

            call.respond(finalResult)
        }
    }
}
