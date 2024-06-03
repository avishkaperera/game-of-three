package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

public enum GameState {
    READY {
        @Override
        public GameState nextState() {
            return IN_PROGRESS;
        }
    },
    IN_PROGRESS {
        @Override
        public GameState nextState() {
            return PLAYER1_TURN;
        }
    },
    PLAYER1_TURN {
        @Override
        public GameState nextState() {
            return PLAYER2_TURN;
        }
    },
    PLAYER2_TURN {
        @Override
        public GameState nextState() {
            return PLAYER1_TURN;
        }
    },
    COMPLETED {
        @Override
        public GameState nextState() {
            throw new IllegalArgumentException("No state change possible from here");
        }
    },
    ERROR {
        @Override
        public GameState nextState() {
            throw new IllegalArgumentException("No state change possible from here");
        }
    };

    public abstract GameState nextState();
}
