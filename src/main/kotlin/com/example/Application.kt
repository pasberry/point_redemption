package com.example

import com.example.model.route.registerPointsRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(ContentNegotiation){
        json()
    }

    install(StatusPages){
        exception<Throwable> {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    registerPointsRoutes()
}
