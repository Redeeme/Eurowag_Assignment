package eurowag.assignment.domain.mappers

import eurowag.assignment.database.entities.LocationPointEntity
import eurowag.assignment.domain.models.LocationPoint
import eurowag.assignment.ui.uiStates.LocationPointState

object LocationPointMapper : Mapper<LocationPoint, LocationPointEntity, LocationPointState> {

    override fun asEntity(domain: List<LocationPoint>): List<LocationPointEntity> {
        return domain.map { point ->
            LocationPointEntity(
                id = point.id,
                latitude = point.latitude,
                longitude = point.longitude,
                accuracy = point.accuracy,
                provider = point.provider,
                time = point.time,
                altitude = point.altitude
            )
        }
    }

    override fun asDomainEntity(entity: List<LocationPointEntity>): List<LocationPoint> {
        return entity.map { point ->
            LocationPoint(
                id = point.id,
                latitude = point.latitude,
                longitude = point.longitude,
                accuracy = point.accuracy,
                provider = point.provider,
                time = point.time,
                altitude = point.altitude
            )
        }
    }

    override fun asDomainState(state: List<LocationPointState>): List<LocationPoint> {
        return state.map { point ->
            LocationPoint(
                id = point.id,
                latitude = point.latitude,
                longitude = point.longitude,
                accuracy = point.accuracy,
                provider = point.provider,
                time = point.time,
                altitude = point.altitude
            )
        }
    }

    override fun asState(domain: List<LocationPoint>): List<LocationPointState> {
        return domain.map { point ->
            LocationPointState(
                id = point.id,
                latitude = point.latitude,
                longitude = point.longitude,
                accuracy = point.accuracy,
                provider = point.provider,
                time = point.time,
                altitude = point.altitude
            )
        }
    }
}