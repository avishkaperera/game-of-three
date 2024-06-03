package com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model;

import java.security.Principal;

public record CustomPrincipal(String playerId) implements Principal {
    @Override
    public String getName() {
        return playerId;
    }
}
