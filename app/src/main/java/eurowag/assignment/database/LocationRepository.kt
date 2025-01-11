package eurowag.assignment.database

import kotlinx.coroutines.flow.Flow


interface LocationRepository {
    suspend fun getAll(): Flow<List<LocationPoint>>

    suspend fun insert(location: LocationPoint)

    suspend fun delete(id: Int)

    suspend fun update(location: LocationPoint)
}