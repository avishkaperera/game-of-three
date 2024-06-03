package com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket;

import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.JoinRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @MessageMapping("/join")
    public void joinGame(JoinRequest request, CustomPrincipal principal) {
        gameService.joinGame(request, principal);
    }

    @MessageMapping("/play")
    public void makeNextMove(MoveRequest request, CustomPrincipal principal) {
        gameService.makeNextMove(request, principal);
    }
}