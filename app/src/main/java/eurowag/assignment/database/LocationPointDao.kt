package eurowag.assignment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {

    @Query("SELECT * FROM locations_table ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<LocationPoint>>

    @Query("SELECT * FROM locations_table ORDER BY timestamp DESC")
    suspend fun getAll(): List<LocationPoint>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: LocationPoint)

    @Query("DELETE FROM locations_table")
    suspend fun deleteAll()

    @Update
    suspend fun updateLocations(location: LocationPoint)
}