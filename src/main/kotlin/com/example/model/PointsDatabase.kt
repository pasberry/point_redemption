package com.example.model

import java.time.ZonedDateTime
import kotlin.math.abs

internal data class PointsRecord(val payer:String, val points:Int, val timestamp: ZonedDateTime)

internal val internalDataSet = mutableSetOf<PointsRecord>()

object PointsDatabase {

    fun addTransaction(transaction: AddPointsTransaction)  = internalDataSet.add(
        PointsRecord(
            transaction.payer as String,
            transaction.points as Int,
            ZonedDateTime.parse(transaction.timestamp))
    )

    fun contains(transaction: AddPointsTransaction) : Boolean {

        val record = PointsRecord(
            transaction.payer as String,
            transaction.points as Int,
            ZonedDateTime.parse(transaction.timestamp)
        )

        return internalDataSet.contains(record)
    }

    fun hasAvailableBalance(spendPointsTransaction: SpendPointsTransaction) : Boolean {
        val pointsBalance = internalDataSet
            .filter { it.points != 0 }
            .sumOf { it.points }

        return pointsBalance > spendPointsTransaction.points!!
    }

    fun spendPoints(spendPointsTransaction: SpendPointsTransaction) : List<SpendPointsResponse> {

        val sortedByDate = internalDataSet
            .filter { it.points != 0 }
            .sortedBy { it.timestamp }

        var totalPointsToRedeem = spendPointsTransaction.points as Int
        val modifiedAndMarkedForDeletion = mutableListOf<PointsRecord>()
        val redeemedPayers = mutableMapOf<String, SpendPointsResponse>()

        for (record in sortedByDate) {

            if ( totalPointsToRedeem > 0) {

                //if a payee has all the funds needed to fulfill the redemption
                when {
                    record.canSatisfyRedemption(totalPointsToRedeem) -> {

                        internalDataSet.add(record.satisfyRedemption(totalPointsToRedeem))

                        val entry = redeemedPayers[record.payer]

                        if(entry != null) {
                            redeemedPayers[record.payer] = entry.copy(points = entry.increaseRedemptionTotals(record.points))
                        } else {
                            redeemedPayers[record.payer] = SpendPointsResponse(payer = record.payer, points = 0 - totalPointsToRedeem)
                        }

                        modifiedAndMarkedForDeletion.add(record)

                        totalPointsToRedeem = 0

                    }
                    record.hasPoints() -> { //apply the point you do have and update your record total to zero

                        totalPointsToRedeem -= record.points

                        val entry = redeemedPayers[record.payer]

                        if(entry != null) {
                            redeemedPayers[record.payer] = entry.copy(points = entry.increaseRedemptionTotals(record.points))
                        } else {
                            redeemedPayers[record.payer] = SpendPointsResponse(payer = record.payer, points = 0 - record.points)
                        }

                        modifiedAndMarkedForDeletion.add(record)
                        internalDataSet.add(record.allocatePoints())
                    }
                    else -> {

                        //the balance is negative, handle the charge back case
                        totalPointsToRedeem += abs(record.points)

                        val entry = redeemedPayers[record.payer]

                        if(entry != null) {
                            redeemedPayers[record.payer] = entry.copy(points = entry.processChargeBack(record.points))
                        } else {
                            redeemedPayers[record.payer] = SpendPointsResponse(payer = record.payer, abs(record.points))
                        }

                        modifiedAndMarkedForDeletion.add(record)
                        internalDataSet.add(record.allocatePoints())

                    }
                }

            }
        }
        internalDataSet.removeAll(modifiedAndMarkedForDeletion)

        return redeemedPayers.values.toList()
    }

    fun getPointsByPayer(): List<PointsBalanceResponse> {

        val distinctPayers = internalDataSet
            .map { it.payer }
            .distinct()

        val pointBalances = mutableListOf<PointsBalanceResponse>()

        for (payer in distinctPayers) {

            val pointTotal = internalDataSet
                .filter { it.payer == payer }
                .sumOf { it.points }

            pointBalances.add(PointsBalanceResponse(payer = payer, points = pointTotal))
        }
        return pointBalances
    }

    fun clear(): Unit = internalDataSet.clear()

}

internal fun PointsRecord.canSatisfyRedemption(pointAmount : Int) : Boolean = this.points >= pointAmount

internal fun PointsRecord.satisfyRedemption(pointAmount: Int) : PointsRecord = this.copy(points = this.points - pointAmount)

internal fun PointsRecord.hasPoints() : Boolean = this.points > 0

internal fun PointsRecord.allocatePoints() : PointsRecord = this.copy(points = 0)

internal fun SpendPointsResponse.increaseRedemptionTotals(pointAmount: Int): Int = this.points - abs(pointAmount)

internal fun SpendPointsResponse.processChargeBack(pointAmount: Int): Int = this.points + abs(pointAmount)

