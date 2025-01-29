package eurowag.assignment.database

import androidx.room.Database
import androidx.room.RoomDatabase
import eurowag.assignment.database.daos.LocationPointDao
import eurowag.assignment.database.entities.LocationPointEntity

@Database(entities = [LocationPointEntity::class], version = 4)
abstract class MyDatabase : RoomDatabase() {
    abstract fun locationPointDao(): LocationPointDao

    companion object {
        const val DATABASE_NAME: String = "my_database"
    }
}