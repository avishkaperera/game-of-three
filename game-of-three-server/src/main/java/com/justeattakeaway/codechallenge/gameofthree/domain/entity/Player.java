package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import com.justeattakeaway.codechallenge.gameofthree.application.event.MoveMadeEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.PlayerReadyEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.util.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.MessageFormat;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Player {

    private String playerId;
    private String name;
    private PlayMode playMode;
    private Move move;
    private PlayerRole playerRole;

    public void publishMoveMadeEvent(GameState gameState, SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format(
                "{0} made the move with number {1}. Current game value is {2}",
                getName(),
                getMove().getAddedValue(),
                getMove().getResultingValue());
        messagingTemplate.convertAndSend(Topic.GENERIC, new MoveMadeEvent(gameState, message));
    }

    public void publishPlayerReadyEvent(UUID gameId, GameState gameState, SimpMessagingTemplate messagingTemplate) {
        PlayerReadyEvent event = new PlayerReadyEvent(gameState, getPlayMode().playStartedMessage(getPlayerRole()), getPlayerRole(), gameId);
        messagingTemplate.convertAndSendToUser(getPlayerId(), Topic.SPECIFIC, event);
    }

    public void makeFirstMove(Integer currentGameNumber) {
        setMove(new Move(currentGameNumber, currentGameNumber));
    }

    public void makeMove(Integer currentGameNumber) {
        getMove().nextMove(currentGameNumber);
    }

    public void makeMove(Integer currentGameNumber, Integer playerMove) {
        getMove().nextMove(currentGameNumber, playerMove);
    }

    public static Player aNewPlayer(String playerId, String playerName, String playMode) {
        return new Player(playerId, playerName, PlayMode.valueOf(playMode), new Move(0, 0), null);
    }
}
