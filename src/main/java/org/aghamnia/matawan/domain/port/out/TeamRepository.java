package org.aghamnia.matawan.domain.port.out;

import org.aghamnia.matawan.domain.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Team persistence.
 */
public interface TeamRepository {
    /**
     * Saves a team (new or existing).
     * @param team the team to save
     * @return the saved team with id populated
     */
    Team save(Team team);

    /**
     * Retrieves a page of teams, sorted according to the given pageable.
     */
    Page<Team> findAll(Pageable pageable);

    /**
     * Checks if a team with the given acronym already exists.
     */
    boolean existsByAcronym(String acronym);
}
