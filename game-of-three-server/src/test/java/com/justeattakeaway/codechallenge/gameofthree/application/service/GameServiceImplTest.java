package com.justeattakeaway.codechallenge.gameofthree.application.service;

import com.justeattakeaway.codechallenge.gameofthree.application.repository.GameRepository;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.*;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void testMakeNextMove() {
        Game game = Game.aNewGame();
        game.setGameState(GameState.PLAYER2_TURN);
        game.setCurrentNumber(4);
        String playerId = UUID.randomUUID().toString();
        Player playerTwo = Player.aNewPlayer(playerId, "RandomPlayerTwo", PlayMode.MANUAL.toString());
        playerTwo.setPlayerRole(PlayerRole.PLAYER2);
        game.setPlayers(List.of(playerTwo));
        doReturn(Optional.of(game)).when(gameRepository).findById(any());

        Game resultingGame = gameService.makeNextMove(new MoveRequest(-1, game.getGameId()), new CustomPrincipal(playerId));

        assertThat(resultingGame.getGameState()).isEqualTo(GameState.COMPLETED);
        assertThat(resultingGame.getWinner()).isEqualTo("RandomPlayerTwo");
    }
}