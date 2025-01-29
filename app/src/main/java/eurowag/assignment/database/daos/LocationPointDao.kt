package eurowag.assignment.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eurowag.assignment.database.entities.LocationPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {

    @Query("SELECT * FROM locations_table ORDER BY timestamp ASC")
    fun getAllFlow(): Flow<List<LocationPointEntity>>

    @Query("SELECT * FROM locations_table ORDER BY timestamp ASC")
    suspend fun getAll(): List<LocationPointEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: LocationPointEntity)

    @Query("DELETE FROM locations_table")
    suspend fun deleteAll()

    @Update
    suspend fun updateLocations(location: LocationPointEntity)
}