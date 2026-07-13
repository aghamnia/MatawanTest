package org.aghamnia.matawan.infrastructure.persistence.interfaces;

import org.aghamnia.matawan.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for TeamEntity.
 */
@Repository
public interface JpaTeamRepository extends JpaRepository<TeamEntity, Long>{
    /**
     * Paginated fetch of teams with their players eagerly loaded.
     */
    @Query("SELECT DISTINCT t FROM TeamEntity t LEFT JOIN FETCH t.players")
    Page<TeamEntity> findAllWithPlayers(Pageable pageable);

    /**
     * Checks if a team with the given acronym already exists.
     */
    boolean existsByAcronym(String acronym);
}
