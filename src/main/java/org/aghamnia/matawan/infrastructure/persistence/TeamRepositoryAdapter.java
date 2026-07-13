package org.aghamnia.matawan.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.domain.port.out.TeamRepository;
import org.aghamnia.matawan.infrastructure.persistence.entity.TeamEntity;
import org.aghamnia.matawan.infrastructure.persistence.interfaces.JpaTeamRepository;
import org.aghamnia.matawan.infrastructure.persistence.mapper.TeamEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamRepositoryAdapter implements TeamRepository {
    private final JpaTeamRepository jpaTeamRepository;
    private final TeamEntityMapper teamEntityMapper;

    @Override
    @Transactional
    public Team save(Team team) {
        TeamEntity entity = teamEntityMapper.toEntity(team);
        entity.getPlayers().forEach(playerEntity -> playerEntity.setTeam(entity));
        TeamEntity savedEntity = jpaTeamRepository.save(entity);
        return teamEntityMapper.toDto(savedEntity);
    }

    @Override
    public Page<Team> findAll(Pageable pageable) {
        return jpaTeamRepository.findAllWithPlayers(pageable)
                .map(teamEntityMapper::toDto);
    }

    @Override
    public boolean existsByAcronym(String acronym) {
        return jpaTeamRepository.existsByAcronym(acronym);
    }
}
