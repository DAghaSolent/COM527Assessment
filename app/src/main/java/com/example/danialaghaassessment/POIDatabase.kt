package com.example.danialaghaassessment
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(POI::class), version = 1, exportSchema =  false)
abstract class POIDatabase: RoomDatabase() {
    abstract fun poiDAO(): poiDAO

    companion object {
        private var instance: POIDatabase? = null

        fun getDatabase(ctx:Context) : POIDatabase {
            var tmpInstance =  instance

            if(tmpInstance == null){
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    POIDatabase::class.java,
                    "poiDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }

}