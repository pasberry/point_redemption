package com.example.model.routes

import com.example.model.route.registerPointsRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.testing.*
import io.ktor.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PointsRouteTest {

    @Test
    fun testAddTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "DANNON",
                        "points": 300,
                        "timestamp": "2020-10-31T10:00:00Z"
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }

    @Test
    fun testEmptyPayerAddTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "",
                        "points": 300,
                        "timestamp": "2020-10-31T10:00:00Z"
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testEmptyTimestampAddTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "DANNON",
                        "points": 300,
                        "timestamp": ""
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testImproperTimestampAddTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "DANNON",
                        "points": 300,
                        "timestamp": "Not A Date"
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testEmptyBodyAddTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{}"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testEmptyPointsTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "DANNON",
                        
                        "timestamp": "2020-10-31T10:00:00Z"
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testNonNumberPointsTransactionRequest(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/add/transaction") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{
                        "payer": "DANNON",
                        "points": k
                        "timestamp": "2020-10-31T10:00:00Z"
                    }"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testSendingNonNumericPoints(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/spend") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{"points": spendMorePoints}"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun testSendingRequestForTooPointsRedemption(){
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            install(StatusPages){
                exception<Throwable> {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            registerPointsRoutes()
        }){
            handleRequest (HttpMethod.Post, "/points/spend") {
                addHeader("Content-Type","application/json")
                setBody(
                    """{"points": 3000000000000}"""
                )
            }.apply {

                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }
}