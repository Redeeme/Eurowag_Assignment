package eurowag.assignment.database

import kotlinx.coroutines.flow.Flow

class LocationRepoImpl(
    private val locationPointDao: LocationPointDao
) : LocationRepository {
    override suspend fun getAllFlow(): Flow<List<LocationPoint>> = locationPointDao.getAllFlow()
    override suspend fun getAll(): List<LocationPoint> = locationPointDao.getAll()

    override suspend fun insert(location: LocationPoint) = locationPointDao.insert(location)

    override suspend fun deleteAll() = locationPointDao.deleteAll()

    override suspend fun update(location: LocationPoint) =
        locationPointDao.updateLocations(location)
}