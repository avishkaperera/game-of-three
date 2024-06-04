package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

    @Test
    void testMakeMoveReturnsCorrectResultInAutomaticMode() {
        Player playerOne = Player.aNewPlayer(UUID.randomUUID().toString(), "RandomPlayerOne", PlayMode.AUTOMATIC.toString());

        // Logic calculates which play to make [-1, 0, 1] to make the result a whole number in AUTOMATIC mode
        Integer result1 = playerOne.makeMove(29);
        assertThat(result1).isEqualTo(10);

        Integer result2 = playerOne.makeMove(30);
        assertThat(result2).isEqualTo(10);

        Integer result3 = playerOne.makeMove(31);
        assertThat(result3).isEqualTo(10);
    }

    @Test
    void testMakeMoveReturnsCorrectResultInManualMode() {
        Player playerOne = Player.aNewPlayer(UUID.randomUUID().toString(), "RandomPlayerOne", PlayMode.MANUAL.toString());

        Integer result1 = playerOne.makeMove(29, 1);
        assertThat(result1).isEqualTo(10);

        Integer result2 = playerOne.makeMove(30, -1);
        assertThat(result2).isEqualTo(9);

        Integer result3 = playerOne.makeMove(31, 0);
        assertThat(result3).isEqualTo(10);
    }
}