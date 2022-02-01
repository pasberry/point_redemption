package com.example.model.route

import com.example.model.AddPointsTransaction
import com.example.model.PointsDatabase
import com.example.model.SpendPointsTransaction
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException


fun Route.addTransaction() {
    post("/points/add/transaction"){
        val transaction = call.receive<AddPointsTransaction>()

        transaction.points ?: return@post call.respondText(
            text = "Please provide the points",
            status = HttpStatusCode.BadRequest
        )

        transaction.payer ?: return@post call.respondText(
            text = "Please provide payer information",
            status = HttpStatusCode.BadRequest
        )

        when {
            transaction.payer.isNullOrBlank() || transaction.payer.isNullOrEmpty() -> return@post call.respondText(
                text = "Please provide payer information",
                status = HttpStatusCode.BadRequest
            )
        }

        transaction.timestamp ?: return@post call.respondText(
            text = "Please provide timestamp information",
            status = HttpStatusCode.BadRequest
        )

        when {
            transaction.timestamp.isNullOrBlank() || transaction.timestamp.isNullOrEmpty() -> return@post call.respondText(
                text = "Please provide timestamp information",
                status = HttpStatusCode.BadRequest
            )
        }

        try {
            ZonedDateTime.parse(transaction.timestamp)
        }catch (e: DateTimeParseException) {
            return@post call.respondText(
                text = "The timestamp is not in the correct format yyyy-MM-ddTHH:mm:Z",
                status = HttpStatusCode.BadRequest

            )
        } catch (e:Throwable){
            return@post call.respondText(
                text = "The timestamp is not in the correct format yyyy-MM-ddTHH:mm:Z",
                status = HttpStatusCode.BadRequest
            )
        }

        PointsDatabase.addTransaction(transaction)

        call.respondText(
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.Created,
            text = "Transaction Complete" )
    }
}

fun Route.spendPoints() {
    post ("/points/spend"){
        val spendTransaction = call.receive<SpendPointsTransaction>()

        spendTransaction.points ?: return@post call.respondText(
            text = "Please provide the amount of points to redeem",
            status = HttpStatusCode.BadRequest
        )

        val hasPointsForRedemption = PointsDatabase.hasAvailableBalance(spendTransaction)

        when (hasPointsForRedemption){
            true -> {
                val response = PointsDatabase.spendPoints(spendTransaction)
                call.respond(response)
            }
            else -> {
               call.respondText(
                    text = "There are not enough points to make the redemption",
                    status = HttpStatusCode.BadRequest
                )
            }
        }

    }
}

fun Route.getAllPoints() {
    get("/points"){

        val response = PointsDatabase.getPointsByPayer()
        call.respond(response)
    }
}

fun Application.registerPointsRoutes() {
    routing {
        addTransaction()
        spendPoints()
        getAllPoints()
    }
}