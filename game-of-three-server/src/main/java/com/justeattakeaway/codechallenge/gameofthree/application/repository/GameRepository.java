package com.justeattakeaway.codechallenge.gameofthree.application.repository;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    /**
     * Find a game by game state
     *
     * @param gameState [READY, IN_PROGRESS, PLAYER1_TURN, PLAYER2_TURN, COMPLETED, ERROR]
     * @return An optional game
     */
    Optional<Game> findByState(GameState gameState);

    /**
     * Find a game by game id
     *
     * @param gameId Game ID
     * @return An optional game
     */
    Optional<Game> findById(UUID gameId);

    /**
     * Save a game
     *
     * @param game Game object to save
     */
    void save(Game game);
}
