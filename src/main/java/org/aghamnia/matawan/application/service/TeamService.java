package org.aghamnia.matawan.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aghamnia.matawan.domain.exception.DuplicateTeamException;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.domain.port.in.AddTeamUseCase;
import org.aghamnia.matawan.domain.port.in.GetTeamsUseCase;
import org.aghamnia.matawan.domain.port.out.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing team-related use cases.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class TeamService implements GetTeamsUseCase, AddTeamUseCase {

    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public Team addTeam(Team team) {
        log.info("Adding new team: {}", team.getName());
        if (teamRepository.existsByAcronym(team.getAcronym())) {
            throw new DuplicateTeamException("An acronym '" + team.getAcronym() + "' already exists");
        }
        return teamRepository.save(team);
    }

    @Override
    public Page<Team> getTeams(Pageable pageable) {
        log.info("Fetching teams with pageable: {}", pageable);
        return teamRepository.findAll(pageable);
    }
}
