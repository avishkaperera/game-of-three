package com.justeattakeaway.codechallenge.gameofthree.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Move {

    private Integer addedValue;
    private Integer resultingValue;

    public void nextMove(Integer currentNumber) {
        if (currentNumber % 3 == 0) {
            addedValue = 0;
            resultingValue = currentNumber / 3;
        } else if ((currentNumber + 1) % 3 == 0) {
            addedValue = 1;
            resultingValue = (currentNumber + 1) / 3;
        } else if ((currentNumber - 1) % 3 == 0) {
            addedValue = -1;
            resultingValue = (currentNumber - 1) / 3;
        }
    }

    public void nextMove(Integer currentNumber, Integer userMove) {
        if ((currentNumber + userMove) <= 1) {
            addedValue = userMove;
            resultingValue = (currentNumber + userMove);
        } else {
            addedValue = userMove;
            resultingValue = (currentNumber + userMove) / 3;
        }
    }
}
