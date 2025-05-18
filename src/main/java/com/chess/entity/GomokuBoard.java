package com.chess.entity;

public class GomokuBoard extends Board {
    public static final int GOMOKU_SIZE = 15;
    
    public GomokuBoard(boolean isFullyEmpty) {
        super(GOMOKU_SIZE, isFullyEmpty);
    }
    
    // 将十进制行号转换为十六进制显示
    public static String getRowLabel(int row) {
        return Integer.toHexString(row + 1).toUpperCase();
    }
    
    // 将列号转换为字母 (A-O)
    public static String getColLabel(int col) {
        return String.valueOf((char)('A' + col));
    }
    
    // 将十六进制行号转换为十进制索引
    public static int parseRowLabel(String rowLabel) {
        try {
            return Integer.parseInt(rowLabel, 16) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // 将列字母转换为索引
    public static int parseColLabel(String colLabel) {
        if (colLabel.length() != 1) {
            return -1;
        }
        char colChar = Character.toUpperCase(colLabel.charAt(0));
        if (colChar < 'A' || colChar > 'O') {
            return -1;
        }
        return colChar - 'A';
    }
} 