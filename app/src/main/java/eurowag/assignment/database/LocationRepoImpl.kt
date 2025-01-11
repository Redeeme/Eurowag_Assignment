package eurowag.assignment.database

import kotlinx.coroutines.flow.Flow

class LocationRepoImpl(
    private val locationPointDao: LocationPointDao
) : LocationRepository {
    override suspend fun getAll(): Flow<List<LocationPoint>> = locationPointDao.getAll()

    override suspend fun insert(location: LocationPoint) = locationPointDao.insert(location)

    override suspend fun delete(id: Int) = locationPointDao.deleteById(id)

    override suspend fun update(location: LocationPoint) = locationPointDao.updateLocations(location)
}