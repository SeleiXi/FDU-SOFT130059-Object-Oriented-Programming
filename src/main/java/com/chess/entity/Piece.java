package com.chess.entity;

public enum Piece {
    BLACK("○"),
    WHITE("●"),
    EMPTY("."),
    BLOCK("#"),
    CRATER("@");

    private final String symbol;

    Piece(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}