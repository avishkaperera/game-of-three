package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

public enum PlayMode {
    MANUAL {
        @Override
        public String initMessage(PlayerRole playerRole) {
            return PlayerRole.PLAYER1.equals(playerRole) ? "Waiting for user to enter initial number" : "Waiting for the other player";
        }
    },
    AUTOMATIC {
        @Override
        public String initMessage(PlayerRole playerRole) {
            return PlayerRole.PLAYER1.equals(playerRole) ? "Generating initial number." : "Waiting for the other player";
        }
    };

    public abstract String initMessage(PlayerRole playerRole);
}
