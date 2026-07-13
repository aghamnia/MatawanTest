package org.aghamnia.matawan.infrastructure.persistence.mapper;

import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.infrastructure.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Team ↔ TeamEntity.
 */
@Mapper(componentModel = "spring", uses = PlayerEntityMapper.class)
public interface TeamEntityMapper {

    @Mapping(target = "budget", expression = "java(team.getBudget().amount())")
    TeamEntity toEntity(Team team);

    @Mapping(target = "budget", expression = "java(new org.aghamnia.matawan.domain.model.Budget(entity.getBudget()))")
    Team toDto(TeamEntity entity);
}
