package com.example.plugins

import com.example.routes.paymentSheet
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        get("/test") { call.respondText("Hello world") }
        paymentSheet()
        staticResources("/", "files") {
            default("index.html")
        }
    }
}