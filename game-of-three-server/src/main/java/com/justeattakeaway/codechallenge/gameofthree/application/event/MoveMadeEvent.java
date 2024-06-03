package com.justeattakeaway.codechallenge.gameofthree.application.event;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

public class MoveMadeEvent extends GameEvent {
    public MoveMadeEvent(GameState gameState, String message) {
        super(gameState, message, "MOVE_MADE_EVENT");
    }
}
