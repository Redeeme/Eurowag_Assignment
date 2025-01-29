package eurowag.assignment.database

import eurowag.assignment.database.entities.LocationPointEntity
import kotlinx.coroutines.flow.Flow


interface LocationRepository {

    suspend fun getAllFlow(): Flow<List<LocationPointEntity>>

    suspend fun getAll(): List<LocationPointEntity>

    suspend fun insert(location: LocationPointEntity)

    suspend fun deleteAll()

    suspend fun update(location: LocationPointEntity)
}