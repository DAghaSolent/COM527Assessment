package com.example.danialaghaassessment
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="poi")
data class POI(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val type: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)