package org.aghamnia.matawan.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a football team with its players and budget.
 */
@NoArgsConstructor
@Getter
@Setter
public class Team {
    private Long id;
    private String name;
    private String acronym;
    private List<Player> players = new ArrayList<>();
    private Budget budget;

    /**
     * Adds a player to the team.
     * @param player the player to add, must not be null
     */
    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "player must not be null");
        this.players.add(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
