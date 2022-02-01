package com.example.model

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class PointsDatabaseTest {

    @AfterTest
    fun resetDatabase(): Unit = PointsDatabase.clear()

    @Test
    fun testAddTransaction() {

        val addPointsTransaction = AddPointsTransaction(payer = "DANNON", points = 1000, timestamp = "2020-11-02T14:00:00Z" )

        PointsDatabase.addTransaction(addPointsTransaction)

        assertTrue(PointsDatabase.contains(addPointsTransaction), "The record was successfully added")
    }

    @Test
    fun testHasPointBalance(){
        val spendPointsRequest = SpendPointsTransaction(points = 5000)

        val addPointsTransaction1 = AddPointsTransaction(payer = "DANNON", points = 1000, timestamp = "2020-11-02T14:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction1)

        val addPointsTransaction2 = AddPointsTransaction(payer = "UNILEVER", points = 200, timestamp = "2020-10-31T11:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction2)

        val response = PointsDatabase.hasAvailableBalance(spendPointsRequest)

        assertFalse(response, "The user does not have the required points. ")

        val addPointsTransaction3 = AddPointsTransaction(payer = "DANNON", points = 7000, timestamp = "2020-11-02T14:00:00Z" )
        val secondResponse = PointsDatabase.addTransaction(addPointsTransaction3)

        assertTrue(secondResponse, "Now the user does have the required points.")
    }

    @Test
    fun testSpendPoints() {
        val addPointsTransaction1 = AddPointsTransaction(payer = "DANNON", points = 1000, timestamp = "2020-11-02T14:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction1)

        val addPointsTransaction2 = AddPointsTransaction(payer = "UNILEVER", points = 200, timestamp = "2020-10-31T11:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction2)

        val addPointsTransaction3 = AddPointsTransaction(payer = "DANNON", points = -200, timestamp = "2020-10-31T15:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction3)

        val addPointsTransaction4 = AddPointsTransaction(payer = "MILLER COORS", points = 10000, timestamp = "2020-11-01T14:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction4)

        val addPointsTransaction5 = AddPointsTransaction(payer = "DANNON", points = 300, timestamp = "2020-10-31T10:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction5)

        val spendPointsRequest = SpendPointsTransaction(points = 5000)
        val response = PointsDatabase.spendPoints(spendPointsRequest)

        assertTrue(response.size == 3, "There are 3 elements returned ")
        assertEquals( "DANNON" ,response[0].payer, "The first element payer is DANNON")
        assertEquals(-100, response[0].points, "The first element points is -300")
        assertEquals("UNILEVER", response[1].payer, "The payer for the second element is UNILEVER")
        assertEquals(-200, response[1].points, "The points for the second element is -200")
        assertEquals("MILLER COORS", response[2].payer, "The last payer is MILLER COORS")
        assertEquals(-4700, response[2].points,  "The last point total is -4500")
    }

    @Test
    fun testGetAllPoints() {
        val addPointsTransaction1 = AddPointsTransaction(payer = "DANNON", points = 1000, timestamp = "2020-11-02T14:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction1)

        val addPointsTransaction2 = AddPointsTransaction(payer = "UNILEVER", points = 200, timestamp = "2020-10-31T11:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction2)

        val addPointsTransaction3 = AddPointsTransaction(payer = "DANNON", points = -200, timestamp = "2020-10-31T15:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction3)

        val addPointsTransaction4 = AddPointsTransaction(payer = "MILLER COORS", points = 10000, timestamp = "2020-11-01T14:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction4)

        val addPointsTransaction5 = AddPointsTransaction(payer = "DANNON", points = 300, timestamp = "2020-10-31T10:00:00Z" )
        PointsDatabase.addTransaction(addPointsTransaction5)

        val spendPointsRequest = SpendPointsTransaction(points = 5000)
        PointsDatabase.spendPoints(spendPointsRequest)

        val response = PointsDatabase.getPointsByPayer()

        assertTrue(response.size == 3, "There are 3 elements returned ")
        assertEquals( "DANNON" ,response[0].payer, "The first element payer is DANNON")
        assertEquals(1000, response[0].points, "The first element points is 1000")
        assertEquals("UNILEVER", response[1].payer, "The payer for the second element is UNILEVER")
        assertEquals(0, response[1].points, "The points for the second element is 0")
        assertEquals("MILLER COORS", response[2].payer, "The last payer is MILLER COORS")
        assertEquals(5300, response[2].points,  "The last point total is 5300")
    }
}