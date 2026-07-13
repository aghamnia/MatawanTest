package org.aghamnia.matawan.domain.port.in;

import org.aghamnia.matawan.domain.model.Team;

/**
 * Adds a new team, optionally with players.
 */
@FunctionalInterface
public interface AddTeamUseCase {
    Team addTeam(Team team);
}
