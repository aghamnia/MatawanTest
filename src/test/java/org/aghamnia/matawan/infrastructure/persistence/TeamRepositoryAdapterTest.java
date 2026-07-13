package org.aghamnia.matawan.infrastructure.persistence;

import org.aghamnia.matawan.domain.model.Budget;
import org.aghamnia.matawan.domain.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class TeamRepositoryAdapterTest {

    @Autowired
    private TeamRepositoryAdapter teamRepositoryAdapter;

    @Test
    void shouldSaveAndRetrieveTeam() {
        Team team = new Team();
        team.setName("OGC Nice");
        team.setAcronym("OGCN");
        team.setBudget(new Budget(new BigDecimal("10000000")));

        Team saved = teamRepositoryAdapter.save(team);
        assertThat(saved.getId()).isNotNull();

        Page<Team> result = teamRepositoryAdapter.findAll(PageRequest.of(0, 10));
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("OGC Nice");
    }
}