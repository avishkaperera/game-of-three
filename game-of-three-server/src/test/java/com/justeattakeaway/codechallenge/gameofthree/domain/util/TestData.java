package com.justeattakeaway.codechallenge.gameofthree.domain.util;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayMode;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Player;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.PlayerRole;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestData {

    public Game aGame() {
        Game game = Game.aNewGame();
        Player playerOne = Player.aNewPlayer(UUID.randomUUID().toString(), "RandomPlayerOne", PlayMode.AUTOMATIC.toString());
        playerOne.setPlayerRole(PlayerRole.PLAYER1);
        Player playerTwo = Player.aNewPlayer(UUID.randomUUID().toString(), "RandomPlayerTwo", PlayMode.AUTOMATIC.toString());
        playerTwo.setPlayerRole(PlayerRole.PLAYER2);
        game.setPlayers(List.of(playerOne, playerTwo));
        return game;
    }
}
