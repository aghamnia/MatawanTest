package org.aghamnia.matawan.infrastructure.persistence.mapper;

import org.aghamnia.matawan.domain.model.Player;
import org.aghamnia.matawan.infrastructure.persistence.entity.PlayerEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Player ↔ PlayerEntity.
 */
@Mapper(componentModel = "spring")
public interface PlayerEntityMapper {

    PlayerEntity toEntity(Player player);

    Player toDto(PlayerEntity entity);
}
