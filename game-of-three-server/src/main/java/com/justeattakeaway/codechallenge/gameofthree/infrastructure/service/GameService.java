package com.justeattakeaway.codechallenge.gameofthree.infrastructure.service;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.JoinRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;

public interface GameService {

    /**
     * Entrypoint for players to join a game
     *
     * @param request   JoinRequest includes playerName & playMode [AUTOMATIC, MANUAL]
     * @param principal CustomPrincipal containing playerId
     */
    void joinGame(JoinRequest request, CustomPrincipal principal);

    /**
     * Entrypoint to register a move
     *
     * @param request   MoveRequest includes gameId and player move
     * @param principal CustomPrincipal containing playerId
     * @return Game that the move was made on
     */
    Game makeNextMove(MoveRequest request, CustomPrincipal principal);
}
