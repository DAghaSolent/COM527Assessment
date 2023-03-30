package com.example.danialaghaassessment
import androidx.room.*

@Dao
interface poiDAO {

    @Query("SELECT * FROM poi WHERE id= :id")
    fun getPoiByID(id: Long): POI

    @Query("SELECT* FROM poi")
    fun getAllPois(): List <POI>

    @Insert
    fun insert(POI: POI) : Long

    @Update
    fun update(POI: POI): Int

    @Delete
    fun delete(POI:POI) : Int
}