package org.aghamnia.matawan.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aghamnia.matawan.domain.model.Budget;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.domain.port.out.TeamRepository;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.PlayerRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void shouldCreateTeamWithPlayers() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest()
                .name("OGC Nice")
                .acronym("OGCN")
                .budget(10000000.0)
                .players(Collections.singletonList(
                        new PlayerRequest().name("Dante").position(PlayerRequest.PositionEnum.DEFENDER)
                ));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("OGC Nice"))
                .andExpect(jsonPath("$.acronym").value("OGCN"))
                .andExpect(jsonPath("$.budget").value(10000000.0))
                .andExpect(jsonPath("$.players", hasSize(1)))
                .andExpect(jsonPath("$.players[0].name").value("Dante"))
                .andExpect(jsonPath("$.players[0].position").value("DEFENDER"));
    }

    @Test
    void shouldCreateTeamWithoutPlayers() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest()
                .name("Olympique Marseille")
                .acronym("OM")
                .budget(5000000.0);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.players", empty()));
    }

    @Test
    void shouldReturnValidationErrorsWhenMandatoryFieldsMissing() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest(); // vide
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.acronym").exists())
                .andExpect(jsonPath("$.budget").exists());
    }

    @Test
    void shouldGetPaginatedTeams() throws Exception {
        // Given
        Team team1 = new Team();
        team1.setName("OGC Nice");
        team1.setAcronym("OGCN");
        team1.setBudget(new Budget(new BigDecimal("10000000")));
        Team team2 = new Team();
        team2.setName("Paris SG");
        team2.setAcronym("PSG");
        team2.setBudget(new Budget(new BigDecimal("500000000")));
        teamRepository.save(team1);
        teamRepository.save(team2);

        // GET /api/teams?page=0&size=10&sort=name,asc
        mockMvc.perform(get("/api/teams")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("OGC Nice"))
                .andExpect(jsonPath("$.content[1].name").value("Paris SG"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldSortTeamsByBudgetDesc() throws Exception {
        Team team1 = new Team();
        team1.setName("OGC Nice");
        team1.setAcronym("OGCN");
        team1.setBudget(new Budget(new BigDecimal("10000000")));
        Team team2 = new Team();
        team2.setName("Paris SG");
        team2.setAcronym("PSG");
        team2.setBudget(new Budget(new BigDecimal("500000000")));
        teamRepository.save(team1);
        teamRepository.save(team2);

        mockMvc.perform(get("/api/teams")
                        .param("sort", "budget,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Paris SG"))
                .andExpect(jsonPath("$.content[1].name").value("OGC Nice"));
    }

    @Test
    void shouldReturnConflictWhenAcronymAlreadyExists() throws Exception {
        // Première équipe créée avec succès
        TeamCreateRequest firstRequest = new TeamCreateRequest()
                .name("OGC Nice")
                .acronym("OGCN")
                .budget(10000000.0);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Tentative de création d'une autre équipe avec le même acronyme
        TeamCreateRequest duplicateRequest = new TeamCreateRequest()
                .name("Autre Club")
                .acronym("OGCN")
                .budget(5000000.0);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("An acronym 'OGCN' already exists"));
    }
}