package com.chess.entity;

public enum Piece {
    BLACK("○"),
    WHITE("●"),
    EMPTY("."),
    BLOCK("#");

    private final String symbol;

    Piece(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}