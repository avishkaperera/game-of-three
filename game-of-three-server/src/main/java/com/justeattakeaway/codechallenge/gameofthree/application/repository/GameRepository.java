package com.justeattakeaway.codechallenge.gameofthree.application.repository;

import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    Optional<Game> findByState(GameState gameState);

    Optional<Game> findById(UUID gameId);

    void save(Game game);
}
