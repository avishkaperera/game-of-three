package com.justeattakeaway.codechallenge.gameofthree.infrastructure.out.cache;

import com.justeattakeaway.codechallenge.gameofthree.application.repository.GameRepository;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.Game;
import com.justeattakeaway.codechallenge.gameofthree.domain.entity.GameState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final Map<UUID, Game> gameStore = new ConcurrentHashMap<>();

    @Override
    public Optional<Game> findByState(GameState gameState) {
        return gameStore.entrySet().stream()
                .filter(entry -> gameState.equals(entry.getValue().getGameState()))
                .findFirst().map(Map.Entry::getValue);
    }

    @Override
    public Optional<Game> findById(UUID gameId) {
        return gameStore.entrySet().stream()
                .filter(entry -> gameId.equals(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    @Override
    public void save(Game game) {
        gameStore.put(game.getGameId(), game);
    }
}
