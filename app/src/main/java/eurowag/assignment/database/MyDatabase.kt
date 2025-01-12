package eurowag.assignment.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocationPoint::class], version = 3)
abstract class MyDatabase : RoomDatabase() {
    abstract fun locationPointDao(): LocationPointDao

    companion object {
        const val DATABASE_NAME: String = "my_database"
    }
}