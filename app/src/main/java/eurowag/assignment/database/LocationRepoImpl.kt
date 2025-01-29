package eurowag.assignment.database

import eurowag.assignment.database.daos.LocationPointDao
import eurowag.assignment.database.entities.LocationPointEntity
import kotlinx.coroutines.flow.Flow

class LocationRepoImpl(
    private val locationPointDao: LocationPointDao
) : LocationRepository {

    override suspend fun getAllFlow(): Flow<List<LocationPointEntity>> = locationPointDao.getAllFlow()

    override suspend fun getAll(): List<LocationPointEntity> = locationPointDao.getAll()

    override suspend fun insert(location: LocationPointEntity) = locationPointDao.insert(location)

    override suspend fun deleteAll() = locationPointDao.deleteAll()

    override suspend fun update(location: LocationPointEntity) =
        locationPointDao.updateLocations(location)
}