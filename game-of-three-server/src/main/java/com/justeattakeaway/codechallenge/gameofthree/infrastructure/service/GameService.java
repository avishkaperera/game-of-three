package com.justeattakeaway.codechallenge.gameofthree.infrastructure.service;

import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.JoinRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;

public interface GameService {

    void joinGame(JoinRequest request, CustomPrincipal principal);

    void makeNextMove(MoveRequest request, CustomPrincipal principal);
}
