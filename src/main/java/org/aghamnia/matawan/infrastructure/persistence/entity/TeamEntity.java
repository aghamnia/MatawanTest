package org.aghamnia.matawan.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for Team.
 */
@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String acronym;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlayerEntity> players = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal budget;

    public void addPlayer(PlayerEntity player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(PlayerEntity player) {
        players.remove(player);
        player.setTeam(null);
    }
}
