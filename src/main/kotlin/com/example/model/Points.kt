package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class AddPointsTransaction(val payer: String? = null , val points: Int? = null , val timestamp: String? = null )

@Serializable
data class SpendPointsTransaction(val points: Int? = null)

@Serializable
data class SpendPointsResponse(val payer:String , val points :Int)

@Serializable
data class PointsBalanceResponse(val payer:String , val points :Int)
