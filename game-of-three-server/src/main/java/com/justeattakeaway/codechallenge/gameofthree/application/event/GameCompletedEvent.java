package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

public class GameCompletedEvent extends GameEvent {
    public GameCompletedEvent(GameState gameState, String message) {
        super(gameState, message, "GAME_COMPLETED_EVENT");
    }
}
