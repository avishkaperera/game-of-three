package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

public class GameStartedEvent extends GameEvent {
    public GameStartedEvent(GameState gameState, String message) {
        super(gameState, message, "GAME_STARTED_EVENT");
    }
}
