package org.aghamnia.matawan.infrastructure.rest.mapper;

import org.aghamnia.matawan.domain.model.Budget;
import org.aghamnia.matawan.domain.model.Player;
import org.aghamnia.matawan.domain.model.Position;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.PlayerRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamCreateRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeamMapperTest {

    @Autowired
    private TeamMapper teamMapper;

    @Test
    void shouldMapCreateRequestToDomain() {
        TeamCreateRequest request = new TeamCreateRequest()
                .name("OGC Nice")
                .acronym("OGCN")
                .budget(10000000.0)
                .players(List.of(new PlayerRequest().name("Dante").position(PlayerRequest.PositionEnum.DEFENDER)));

        Team team = teamMapper.toDomain(request);

        assertThat(team.getId()).isNull(); // id ignored
        assertThat(team.getName()).isEqualTo("OGC Nice");
        assertThat(team.getAcronym()).isEqualTo("OGCN");
        assertThat(team.getBudget().amount()).isEqualByComparingTo(new BigDecimal("10000000"));
        assertThat(team.getPlayers()).hasSize(1);
        Player player = team.getPlayers().getFirst();
        assertThat(player.name()).isEqualTo("Dante");
        assertThat(player.position()).isEqualTo(Position.DEFENDER);
    }

    @Test
    void shouldMapDomainToResponse() {
        Team team = new Team();
        team.setId(1L);
        team.setName("OGC Nice");
        team.setAcronym("OGCN");
        team.setBudget(new Budget(new BigDecimal("5000000")));
        team.setPlayers(List.of(new Player(10L, "Dante", Position.DEFENDER)));

        TeamResponse response = teamMapper.toResponse(team);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("OGC Nice");
        assertThat(response.getBudget().toString()).isEqualTo("5000000");
        assertThat(response.getPlayers()).hasSize(1);
        assertThat(response.getPlayers().getFirst().getName()).isEqualTo("Dante");
        assertThat(response.getPlayers().getFirst().getPosition()).isEqualTo("DEFENDER");
    }
}