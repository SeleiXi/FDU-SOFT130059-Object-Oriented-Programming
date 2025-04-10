package com.chess.entity;

public class Board {
    private static final int SIZE = 8;
    private Piece[][] grid;
    private int filledPositions;

    public Board(boolean isFullyEmpty) {
        grid = new Piece[SIZE][SIZE];
        filledPositions = 0;
        initializeBoard(isFullyEmpty);
    }

    private void initializeBoard(boolean isFullyEmpty) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Piece.EMPTY;
            }
        }
        if (!isFullyEmpty) {
            // 设置初始布局：⿊棋位于 4E 和 5D，⽩棋位于 4D 和 5E
            grid[3][4] = Piece.BLACK; // 4E
            grid[4][3] = Piece.BLACK; // 5D
            grid[3][3] = Piece.WHITE; // 4D
            grid[4][4] = Piece.WHITE; // 5E
        }
        filledPositions = 4;
    }

    public boolean placePiece(int row, int col, Piece piece, boolean isFliped) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }

        if (grid[row][col] != Piece.EMPTY && !isFliped) {
            return false;
        }

        grid[row][col] = piece;
        if(!isFliped) {
            filledPositions++;
        }
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