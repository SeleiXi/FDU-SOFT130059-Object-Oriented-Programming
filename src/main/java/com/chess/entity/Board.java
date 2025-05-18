package com.chess.entity;

public class Board {
    protected static final int DEFAULT_SIZE = 8;
    protected int size;
    private Piece[][] grid;
    private int filledPositions;

    public Board(boolean isFullyEmpty) {
        this(DEFAULT_SIZE, isFullyEmpty);
    }
    
    public Board(int size, boolean isFullyEmpty) {
        this.size = size;
        grid = new Piece[size][size];
        filledPositions = 0;
        initializeBoard(isFullyEmpty);
    }

    private void initializeBoard(boolean isFullyEmpty) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Piece.EMPTY;
            }
        }
        if (!isFullyEmpty) {
            // 设置初始布局：⿊棋位于 4E 和 5D，⽩棋位于 4D 和 5E
            int middle = size / 2;
            grid[middle-1][middle] = Piece.BLACK; // 4E
            grid[middle][middle-1] = Piece.BLACK; // 5D
            grid[middle-1][middle-1] = Piece.WHITE; // 4D
            grid[middle][middle] = Piece.WHITE; // 5E
        }
        filledPositions = 4;
    }

    public boolean placePiece(int row, int col, Piece piece, boolean isFliped) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
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
        return filledPositions >= size * size;
    }

    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    public int getSize() {
        return size;
    }
}