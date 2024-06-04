package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import com.justeattakeaway.codechallenge.gameofthree.domain.exception.GameException;
import com.justeattakeaway.codechallenge.gameofthree.domain.util.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GameTest {

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @Test
    void testGameStartAndValidateGameState() throws GameException {
        Game game = TestData.aGame();
        assertThat(game.getGameState()).isEqualTo(GameState.READY);
        game.start(messagingTemplate);
        assertThat(game.getGameState()).isEqualTo(GameState.COMPLETED);
    }

    @Test
    void testGameStartAndValidateExceptionWhenPlayerOneNotFound() {
        Game game = TestData.aGame();
        Player playerTwo = Player.aNewPlayer(UUID.randomUUID().toString(), "RandomPlayerTwo", PlayMode.AUTOMATIC.toString());
        playerTwo.setPlayerRole(PlayerRole.PLAYER2);
        game.setPlayers(List.of(playerTwo));
        assertThat(game.getGameState()).isEqualTo(GameState.READY);
        GameException gameException = assertThrows(GameException.class, () -> game.start(messagingTemplate));
        assertThat(gameException.getMessage()).isEqualTo("Player one not found. Start a new game.");
    }
}