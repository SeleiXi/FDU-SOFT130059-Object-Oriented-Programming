package com.chess.entity;

public class Board {
    private static final int SIZE = 8;
    private Piece[][] grid;
    private int filledPositions;

    public Board() {
        grid = new Piece[SIZE][SIZE];
        filledPositions = 0;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Piece.EMPTY;
            }
        }
    }

    public boolean placePiece(int row, int col, Piece piece) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }

        if (grid[row][col] != Piece.EMPTY) {
            return false;
        }

        grid[row][col] = piece;
        filledPositions++;
        return true;
    }

    public boolean isFull() {
        return filledPositions >= SIZE * SIZE;
    }

    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    public int getSize() {
        return SIZE;
    }
}