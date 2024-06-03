package com.justeattakeaway.codechallenge.gameofthree.application.service;

import com.justeattakeaway.codechallenge.gameofthree.application.event.GameExceptionEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.repository.GameRepository;
import com.justeattakeaway.codechallenge.gameofthree.application.util.Topic;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Player;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayerRole;
import com.justeattakeaway.codechallenge.gameofthree.domain.exception.GameException;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.JoinRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState.READY;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameRepository gameRepository;

    @Override
    public void joinGame(JoinRequest request, CustomPrincipal principal) {
        Optional<Game> game = gameRepository.findByState(READY);
        Player player = Player.aNewPlayer(principal.getName(), request.playerName(), request.playMode());
        if (game.isPresent()) {
            log.info("Joining [{}] to game [{}]", player.getName(), game.get().getGameId());
            joinPlayerToExistingGame(player, game.get());
        } else {
            log.info("Joining [{}] to a new game", player.getName());
            joinPlayerToNewGame(player);
        }
    }

    @Override
    public void makeNextMove(MoveRequest request, CustomPrincipal principal) {
        try {
            Game game = gameRepository.findById(request.gameId()).orElseThrow(() -> new GameException("Game not found. Start a new game."));
            Player player = findPlayerInGame(principal, game);
            game.play(player, request.playerMove(), messagingTemplate);
        } catch (GameException e) {
            handleException(e);
        }
    }

    private static Player findPlayerInGame(CustomPrincipal principal, Game game) {
        return game.getPlayers().stream()
                .filter(p -> principal.playerId().equals(p.getPlayerId()))
                .findFirst()
                .orElse(null);
    }

    private void joinPlayerToNewGame(Player player) {
        player.setPlayerRole(PlayerRole.PLAYER1);
        Game game = Game.aNewGame();
        game.getPlayers().add(player);
        gameRepository.save(game);
        game.publishGameCreatedEvent(messagingTemplate, player.getName(), player.getPlayerId());
    }

    private void joinPlayerToExistingGame(Player player, Game game) {
        player.setPlayerRole(PlayerRole.PLAYER2);
        game.getPlayers().add(player);
        try {
            game.start(messagingTemplate);
        } catch (GameException e) {
            handleException(e);
        }
    }

    private void handleException(GameException ex) {
        GameExceptionEvent event = new GameExceptionEvent(GameState.ERROR, ex.getMessage());
        messagingTemplate.convertAndSend(Topic.GENERIC, event);
    }
}
