package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import com.justeattakeaway.codechallenge.gameofthree.application.event.GameCompletedEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.GameCreatedEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.GameStartedEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.util.RandomNumberGenerator;
import com.justeattakeaway.codechallenge.gameofthree.application.util.Topic;
import com.justeattakeaway.codechallenge.gameofthree.domain.exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

@Getter
@Setter
@AllArgsConstructor
public class Game {

    private UUID gameId;
    private List<Player> players;
    private GameState gameState;
    private Integer currentNumber;
    private String winner;

    public void start(SimpMessagingTemplate messagingTemplate) throws GameException {
        setGameState(getGameState().nextState());
        publishGameStartedEvent(messagingTemplate);
        getPlayers().forEach(player -> player.publishPlayerReadyEvent(getGameId(), getGameState(), messagingTemplate));
        setGameState(getGameState().nextState());
        Player playerOne = getPlayers().stream().filter(player -> player.getPlayerRole().equals(PlayerRole.PLAYER1))
                .findFirst().orElseThrow(() -> new GameException("Player one not found. Start a new game."));
        initiateMove(messagingTemplate, playerOne);
    }

    public void play(Player player, Integer playerMove, SimpMessagingTemplate messagingTemplate) throws GameException {
        if (player.getPlayMode().equals(PlayMode.AUTOMATIC)) {
            initiateAutomaticMove(player, messagingTemplate);
        } else {
            initiateManualMove(player, playerMove, messagingTemplate);
        }
    }

    public void publishGameCreatedEvent(SimpMessagingTemplate messagingTemplate, String name, String playerId) {
        String message = MessageFormat.format("{0} created game and waiting for someone to join...", name);
        messagingTemplate.convertAndSendToUser(playerId, Topic.SPECIFIC, new GameCreatedEvent(getGameState(), message));
    }

    private void initiateMove(SimpMessagingTemplate messagingTemplate, Player playerOne) throws GameException {
        if (playerOne.getPlayMode().equals(PlayMode.AUTOMATIC)) {
            initiateAutomaticMove(playerOne, messagingTemplate);
        }
    }

    private void initiateManualMove(Player player, Integer playerMove, SimpMessagingTemplate messagingTemplate) throws GameException {
        if (getCurrentNumber() == 0) {
            setCurrentNumber(playerMove);
            firstPlay(player, messagingTemplate, p -> p.makeFirstMove(getCurrentNumber()));
            nextMove(player, messagingTemplate);
        } else {
            if (Objects.nonNull(playerMove)) {
                subsequentPlay(player, messagingTemplate, p -> p.makeMove(getCurrentNumber(), playerMove));
                if (getCurrentNumber() <= 1) {
                    winningPlay(player, messagingTemplate);
                    return;
                }
                nextMove(player, messagingTemplate);
            }
        }
    }

    private void subsequentPlay(Player player, SimpMessagingTemplate messagingTemplate, ToIntFunction<Player> move) {
        setCurrentNumber(move.applyAsInt(player));
        setGameState(getGameState().nextState());
        player.publishMoveMadeEvent(getGameState(), messagingTemplate);
    }

    private void firstPlay(Player player, SimpMessagingTemplate messagingTemplate, Consumer<Player> move) {
        move.accept(player);
        setGameState(getGameState().nextState());
        player.publishMoveMadeEvent(getGameState(), messagingTemplate);
    }

    private void initiateAutomaticMove(Player player, SimpMessagingTemplate messagingTemplate) throws GameException {
        if (getCurrentNumber() == 0) {
            setCurrentNumber(RandomNumberGenerator.nextNumber());
            firstPlay(player, messagingTemplate, p -> p.makeFirstMove(getCurrentNumber()));
        } else {
            subsequentPlay(player, messagingTemplate, p -> p.makeMove(getCurrentNumber()));
            if (getCurrentNumber() <= 1) {
                winningPlay(player, messagingTemplate);
                return;
            }
        }
        nextMove(player, messagingTemplate);
    }

    private void winningPlay(Player player, SimpMessagingTemplate messagingTemplate) {
        setGameState(GameState.COMPLETED);
        setWinner(player.getName());
        publishGameCompletedEvent(messagingTemplate, player.getName());
    }

    public static Game aNewGame() {
        List<Player> players = new ArrayList<>();
        return new Game(UUID.randomUUID(), players, GameState.READY, 0, null);
    }

    private void nextMove(Player previousPlayer, SimpMessagingTemplate messagingTemplate) throws GameException {
        Player nextPlayer = getPlayers().stream().filter(p -> !p.getPlayerId().equals(previousPlayer.getPlayerId()))
                .findFirst().orElseThrow(() -> new GameException("Opponent of " + previousPlayer.getName() + " not found. Start a new game."));
        play(nextPlayer, null, messagingTemplate);
    }

    private void publishGameStartedEvent(SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format(
                "Game started with player 1 - {0} and player 2 - {1}",
                getPlayers().getFirst().getName(),
                getPlayers().getLast().getName());
        GameStartedEvent event = new GameStartedEvent(getGameState(), message);
        messagingTemplate.convertAndSend(Topic.GENERIC, event);
    }

    private void publishGameCompletedEvent(SimpMessagingTemplate messagingTemplate, String winnerName) {
        String message = MessageFormat.format("Game completed. Winner - {0}", winnerName);
        messagingTemplate.convertAndSend(Topic.GENERIC, new GameCompletedEvent(getGameState(), message));
    }
}
