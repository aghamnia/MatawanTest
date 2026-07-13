package org.aghamnia.matawan.infrastructure.rest.mapper;

import org.aghamnia.matawan.domain.model.Budget;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamCreateRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * MapStruct mapper for Team ↔ DTO conversions.
 */
@Mapper(componentModel = "spring", uses = PlayerMapper.class)
public interface TeamMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "players", source = "players")
    Team toDomain(TeamCreateRequest request);

    TeamResponse toResponse(Team team);

    default BigDecimal mapBudget(Budget budget) {
        if (Objects.nonNull(budget) && Objects.nonNull(budget.amount())) {
            return budget.amount();
        }
        return null;
    }

    default Budget mapBudget(Double amount) {
        if (Objects.isNull(amount)) {
            return null;
        }
        else return Budget.of(String.valueOf(amount));
    }
}
