package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

public class GameExceptionEvent extends GameEvent {
    public GameExceptionEvent(GameState gameState, String message) {
        super(gameState, message, "GAME_EXCEPTION_EVENT");
    }
}
