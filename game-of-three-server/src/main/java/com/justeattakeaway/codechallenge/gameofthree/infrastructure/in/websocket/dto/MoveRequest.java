package com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto;

import java.util.UUID;

public record MoveRequest(Integer playerMove, UUID gameId) {
}
