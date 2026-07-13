package org.aghamnia.matawan.application.service;

import org.aghamnia.matawan.domain.exception.DuplicateTeamException;
import org.aghamnia.matawan.domain.model.Budget;
import org.aghamnia.matawan.domain.model.Player;
import org.aghamnia.matawan.domain.model.Position;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.domain.port.out.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void shouldAddTeamSuccessfully() {
        // given
        Team teamToAdd = new Team();
        teamToAdd.setName("OGC Nice");
        teamToAdd.setAcronym("OGCN");
        teamToAdd.setBudget(new Budget(new BigDecimal("10000000")));
        Player player = new Player(null, "Dante", Position.DEFENDER);
        teamToAdd.addPlayer(player);

        Team savedTeam = new Team();
        savedTeam.setId(1L);
        savedTeam.setName("OGC Nice");
        savedTeam.setAcronym("OGCN");
        savedTeam.setBudget(new Budget(new BigDecimal("10000000")));
        savedTeam.setPlayers(List.of(new Player(10L, "Dante", Position.DEFENDER)));

        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        // when
        Team result = teamService.addTeam(teamToAdd);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("OGC Nice");
        assertThat(result.getPlayers()).hasSize(1);
        assertThat(result.getPlayers().getFirst().name()).isEqualTo("Dante");
        verify(teamRepository).save(teamToAdd);
    }

    @Test
    void shouldGetTeamsPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Team team = new Team();
        team.setId(1L);
        team.setName("OGC Nice");
        team.setAcronym("OGCN");
        team.setBudget(new Budget(new BigDecimal("5000000")));
        Page<Team> expectedPage = new PageImpl<>(Collections.singletonList(team), pageable, 1);

        when(teamRepository.findAll(pageable)).thenReturn(expectedPage);

        // when
        Page<Team> result = teamService.getTeams(pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("OGC Nice");
        verify(teamRepository).findAll(pageable);
    }

    @Test
    void shouldThrowExceptionWhenAcronymAlreadyExists() {
        // given
        Team team = new Team();
        team.setName("OGC Nice");
        team.setAcronym("OGCN");
        team.setBudget(new Budget(new BigDecimal("10000000")));

        when(teamRepository.existsByAcronym("OGCN")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> teamService.addTeam(team))
                .isInstanceOf(DuplicateTeamException.class)
                .hasMessage("An acronym 'OGCN' already exists");

        verify(teamRepository, never()).save(any());
    }
}