package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayerRole;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GameInitEvent extends GameEvent {
    private final PlayerRole playerRole;
    private final UUID gameId;

    public GameInitEvent(GameState gameState, String message, PlayerRole playerRole, UUID gameId) {
        super(gameState, message, "GAME_INIT_EVENT");
        this.playerRole = playerRole;
        this.gameId = gameId;
    }
}
