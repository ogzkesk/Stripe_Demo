package com.example.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.httpsredirect.*

fun Application.configureHTTP() {

    install(HttpsRedirect) {
        sslPort = 443
        permanentRedirect = true
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
}
