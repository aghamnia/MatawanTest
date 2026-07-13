package org.aghamnia.matawan.infrastructure.rest.mapper;

import org.aghamnia.matawan.domain.model.Player;
import org.aghamnia.matawan.domain.model.Position;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.PlayerRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.PlayerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Player ↔ DTO conversions.
 */
@Mapper(componentModel = "spring", imports = Position.class)
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    Player toDomain(PlayerRequest request);

    @Mapping(target = "position", expression = "java(player.position().name())")
    PlayerResponse toResponse(Player player);
}
