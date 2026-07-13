package org.aghamnia.matawan.domain.port.in;

import org.aghamnia.matawan.domain.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Retrieves paginated and sorted teams.
 */
@FunctionalInterface
public interface GetTeamsUseCase {
    Page<Team> getTeams(Pageable pageable);
}
