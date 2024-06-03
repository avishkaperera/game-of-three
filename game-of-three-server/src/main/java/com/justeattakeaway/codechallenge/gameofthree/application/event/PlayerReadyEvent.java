package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayerRole;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerReadyEvent extends GameEvent {
    private final PlayerRole playerRole;
    private final UUID gameId;

    public PlayerReadyEvent(GameState gameState, String message, PlayerRole playerRole, UUID gameId) {
        super(gameState, message, "PLAYER_READY_EVENT");
        this.playerRole = playerRole;
        this.gameId = gameId;
    }
}
