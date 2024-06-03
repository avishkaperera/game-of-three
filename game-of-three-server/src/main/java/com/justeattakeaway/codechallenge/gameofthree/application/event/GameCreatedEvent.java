package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

public class GameCreatedEvent extends GameEvent {
    public GameCreatedEvent(GameState gameState, String message) {
        super(gameState, message, "GAME_CREATED_EVENT");
    }
}
