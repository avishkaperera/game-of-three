package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import com.justeattakeaway.codechallenge.gameofthree.application.event.GameCompletedEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.GameCreatedEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.GameInitEvent;
import com.justeattakeaway.codechallenge.gameofthree.application.event.MoveMadeEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.MessageFormat;
import java.util.UUID;

import static com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayerRole.PLAYER1;

@Getter
@Setter
@Builder
public class Player {

    private String playerId;
    private String name;
    private PlayMode playMode;
    private Move move;
    private PlayerRole playerRole;

    public void publishGameCreatedEvent(SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format("{0} created game and waiting for someone to join...", name);
        messagingTemplate.convertAndSendToUser(getPlayerId(), "/queue/game.event", new GameCreatedEvent(GameState.READY, message));
    }

    public void publishMoveMadeEvent(GameState gameState, SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format(
                "{0} made the move with number {1}. Current game value is {2}",
                getName(),
                getMove().getAddedValue(),
                getMove().getResultingValue());
        messagingTemplate.convertAndSend("/topic/game.event", new MoveMadeEvent(gameState, message));
    }

    public void notifyWinner(SimpMessagingTemplate messagingTemplate) {
        String message = MessageFormat.format("Game completed. Winner - {0}", name);
        messagingTemplate.convertAndSend("/topic/game.event", new GameCompletedEvent(GameState.COMPLETED, message));
    }

    public void announceInitMessage(UUID gameId, SimpMessagingTemplate messagingTemplate) {
        GameInitEvent event = new GameInitEvent(GameState.IN_PROGRESS, getPlayMode().initMessage(getPlayerRole()), getPlayerRole(), gameId);
        messagingTemplate.convertAndSendToUser(getPlayerId(), "/queue/game.event", event);
    }

    public boolean isPlayerOne() {
        return PLAYER1.equals(getPlayerRole());
    }
}
