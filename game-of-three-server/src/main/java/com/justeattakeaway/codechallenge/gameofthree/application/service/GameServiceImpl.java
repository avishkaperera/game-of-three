package com.justeattakeaway.codechallenge.gameofthree.application.service;

import com.justeattakeaway.codechallenge.gameofthree.application.repository.GameRepository;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.*;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.config.model.CustomPrincipal;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.JoinRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.in.websocket.dto.MoveRequest;
import com.justeattakeaway.codechallenge.gameofthree.infrastructure.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Player player = buildPlayer(request, principal);
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
        Game game = gameRepository.findById(request.gameId()).orElse(null);
        Player player = findPlayerInGame(principal, game);
        game.play(player, request.playerMove(), messagingTemplate);
    }

    private static Player findPlayerInGame(CustomPrincipal principal, Game game) {
        return game.getPlayers().stream()
                .filter(p -> principal.playerId().equals(p.getPlayerId()))
                .findFirst()
                .orElse(null);
    }

    private Player buildPlayer(JoinRequest request, CustomPrincipal principal) {
        return Player.builder().playerId(principal.playerId()).name(request.playerName())
                .playMode(PlayMode.valueOf(request.playerMode().toUpperCase()))
                .move(new Move(0, 0)).build();
    }

    private static Game buildGame(Player player, UUID gameId) {
        List<Player> players = new ArrayList<>();
        players.add(player);
        return Game.builder().gameId(gameId).players(players).gameState(READY).currentNumber(0).build();
    }

    private void joinPlayerToNewGame(Player player) {
        player.setPlayerRole(PlayerRole.PLAYER1);
        UUID gameId = UUID.randomUUID();
        Game game = buildGame(player, gameId);
        gameRepository.save(game);
        player.publishGameCreatedEvent(messagingTemplate);
    }

    private void joinPlayerToExistingGame(Player player, Game game) {
        player.setPlayerRole(PlayerRole.PLAYER2);
        game.getPlayers().add(player);
        game.start(messagingTemplate);
    }
}
