package org.example.backend_med.Models;

public enum RatingVal {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    RatingVal(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}