package eurowag.assignment.domain.mappers

interface Mapper<Domain, Entity, UiState> {

    fun asEntity(domain: List<Domain>): List<Entity>

    fun asDomainEntity(entity: List<Entity>): List<Domain>

    fun asDomainState(state: List<UiState>): List<Domain>

    fun asState(domain: List<Domain>): List<UiState>
}