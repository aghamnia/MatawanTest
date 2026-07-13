package org.aghamnia.matawan.domain.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a football player.
 */
public record Player(Long id, String name, Position position) {
}
