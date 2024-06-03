package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import com.justeattakeaway.codechallenge.gameofthree.application.event.GameStartedEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Game {

    private UUID gameId;
    private List<Player> players;
    private GameState gameState;
    private Integer currentNumber;
    private String winner;

    public void start(SimpMessagingTemplate messagingTemplate) {
        setGameState(GameState.IN_PROGRESS);
        publishGameStartedEvent(messagingTemplate);
        getPlayers().forEach(player -> player.announceInitMessage(getGameId(), messagingTemplate));
        setGameState(GameState.PLAYER1_TURN);
        getPlayers().stream().filter(player -> player.getPlayerRole().equals(PlayerRole.PLAYER1)).findFirst()
                .ifPresentOrElse(playerOne -> initiateMove(messagingTemplate, playerOne),
                        () -> messagingTemplate.convertAndSend(
                                "/topic/game.log",
                                "Player one has not connected!"));
    }

    public void play(Player player, Integer playerMove, SimpMessagingTemplate messagingTemplate) {
        if (player.getPlayMode().equals(PlayMode.AUTOMATIC)) {
            initiateAutomaticMove(player, messagingTemplate);
        } else {
            initiateManualMove(player, playerMove, messagingTemplate);
        }
    }

    private void initiateMove(SimpMessagingTemplate messagingTemplate, Player playerOne) {
        if (playerOne.getPlayMode().equals(PlayMode.AUTOMATIC)) {
            initiateAutomaticMove(playerOne, messagingTemplate);
        }
    }

    public void initiateManualMove(Player player, Integer playerMove, SimpMessagingTemplate messagingTemplate) {
        if (getCurrentNumber() == 0) {
            setCurrentNumber(playerMove);
            player.setMove(new Move(getCurrentNumber(), getCurrentNumber()));
            setGameState(determineGameState(player));
            player.publishMoveMadeEvent(getGameState(), messagingTemplate);
            Player otherPlayer = getPlayers().stream().filter(p -> !p.getPlayerId().equals(player.getPlayerId())).findFirst().get();
            play(otherPlayer, null, messagingTemplate);
        } else {
            if (Objects.nonNull(playerMove)) {
                player.getMove().nextMove(getCurrentNumber(), playerMove);
                setCurrentNumber(player.getMove().getResultingValue());
                setGameState(determineGameState(player));
                player.publishMoveMadeEvent(getGameState(), messagingTemplate);
                if (getCurrentNumber() <= 1) {
                    setGameState(GameState.COMPLETED);
                    setWinner(player.getName());
                    player.notifyWinner(messagingTemplate);
                    return;
                }
                Player otherPlayer = getPlayers().stream().filter(p -> !p.getPlayerId().equals(player.getPlayerId())).findFirst().get();
                play(otherPlayer, null, messagingTemplate);
            }

        }
    }

    public void initiateAutomaticMove(Player player, SimpMessagingTemplate messagingTemplate) {
        if (getCurrentNumber() == 0) {
            Random random = new Random();
            setCurrentNumber(random.nextInt(50, 100));
            player.setMove(new Move(getCurrentNumber(), getCurrentNumber()));
            setGameState(GameState.PLAYER2_TURN);
            player.publishMoveMadeEvent(getGameState(), messagingTemplate);
            Player otherPlayer = getPlayers().stream().filter(p -> !p.getPlayerId().equals(player.getPlayerId())).findFirst().get();
            play(otherPlayer, null, messagingTemplate);
        } else {
            player.getMove().nextMove(getCurrentNumber());
            setCurrentNumber(player.getMove().getResultingValue());
            setGameState(determineGameState(player));
            player.publishMoveMadeEvent(getGameState(), messagingTemplate);
            if (getCurrentNumber() <= 1) {
                setGameState(GameState.COMPLETED);
                setWinner(player.getName());
                player.notifyWinner(messagingTemplate);
                return;
            }
            Player otherPlayer = getPlayers().stream().filter(p -> !p.getPlayerId().equals(player.getPlayerId())).findFirst().get();
            play(otherPlayer, null, messagingTemplate);
        }
    }

    private GameState determineGameState(Player lastPlayer) {
        return lastPlayer.isPlayerOne() ? GameState.PLAYER2_TURN : GameState.PLAYER1_TURN;
    }

    public void publishGameStartedEvent(SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format(
                "Game started with player 1 - {0} and player 2 - {1}",
                getPlayers().getFirst().getName(),
                getPlayers().getLast().getName());
        GameStartedEvent event = new GameStartedEvent(getGameState(), message);
        messagingTemplate.convertAndSend("/topic/game.event", event);
    }
}
