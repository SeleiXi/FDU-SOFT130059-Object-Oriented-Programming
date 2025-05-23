package com.chess.service;

import java.util.ArrayList;
import com.chess.entity.Piece;

public class ReversiGame extends Game {
    
    public ReversiGame(String player1Name, String player2Name, int gameId) {
        super(player1Name, player2Name, GameMode.REVERSI, gameId);
    }
    
    
    @Override
    public void playOneRound() {
        if (isGameEnded) {
            System.out.println("当前游戏已结束，请切换到其他游戏或添加新游戏");
        }
        
        if (!hasValidMove(player1) && !hasValidMove(player2)) {
                isGameEnded = true;
                System.out.println("两位玩家都没有合法落子位置，游戏结束！");
                displayGameResult();
        }   
        
        makeMove(true,false);
        checkGameEnd();
    }
    
    @Override
    protected void displayBoard() {
        System.out.println("  A B C D E F G H");
        
        for (int i = 0; i < Math.max(boardSize, 6 + gameList.size() - 2); i++) {
            // 显示棋盘行（如果在棋盘范围内）
            if (i < boardSize) {
                System.out.print((i + 1));
                for (int j = 0; j < boardSize; j++) {
                    // 显示合法落子位置
                    if (boards[currentBoardIndex].getPiece(i, j) == Piece.EMPTY && 
                        isValidMove(i, j, currentPlayer.getPieceType())) {
                        System.out.print(" +");
                    } else {
                        System.out.print(" " + boards[currentBoardIndex].getPiece(i, j).getSymbol());
                    }
                }
            } else {
                // 如果超出棋盘范围，只需要为游戏列表留出空间
                System.out.print("                 ");
            }

            // 右侧显示游戏信息、玩家得分和游戏列表
            if (i == 3) {
                System.out.print("  游戏#" + gameId + " (" + gameMode.getName() + ")           游戏列表");
            } else if (i == 4) {
                // 计算第一行玩家信息
                String playerInfo = String.format("  玩家[%s] %s 得分: %d", 
                    player1.getName(),
                    (currentPlayer == player1 ? player1.getPieceType().getSymbol() : ""),
                    countPieces(Piece.BLACK));
                
                System.out.print(playerInfo);
                
                // 确保游戏列表项始终从第25列开始（为黑白棋模式左移5格）
                int targetColumn = 25; 
                int spaces = targetColumn - playerInfo.length();
                for (int s = 0; s < spaces; s++) {
                    System.out.print(" ");
                }
                
                if (0 < gameList.size()) {
                    System.out.print("1. " + gameList.get(0).gameMode.getName() + 
                        (0 == currentGameIndex ? " (当前)" : ""));
                }
            } else if (i == 5) {
                // 计算第二行玩家信息
                String playerInfo = String.format("  玩家[%s] %s 得分: %d", 
                    player2.getName(),
                    (currentPlayer == player2 ? player2.getPieceType().getSymbol() : ""),
                    countPieces(Piece.WHITE));
                
                System.out.print(playerInfo);
                
                // 确保游戏列表项始终从第25列开始（为黑白棋模式左移5格）
                int targetColumn = 25;
                int spaces = targetColumn - playerInfo.length();
                for (int s = 0; s < spaces; s++) {
                    System.out.print(" ");
                }
                
                if (1 < gameList.size()) {
                    System.out.print("2. " + gameList.get(1).gameMode.getName() + 
                        (1 == currentGameIndex ? " (当前)" : ""));
                }
            } else if (i >= 6 && i < 6 + gameList.size() - 2) {
                // 从第三个游戏开始，顺序显示剩余的游戏列表项
                int gameIndex = i - 6 + 2; // 从第三个游戏(索引2)开始
                if (gameIndex < gameList.size()) {
                    // 使用35列（相比第一二行右移5格）
                    StringBuilder spaces = new StringBuilder();
                    for (int s = 0; s < 29; s++) {
                        spaces.append(" ");
                    }
                    System.out.print(spaces.toString() + (gameIndex + 1) + ". " + 
                            gameList.get(gameIndex).gameMode.getName() + 
                            (gameIndex == currentGameIndex ? " (当前)" : ""));
                }
            }

            System.out.println();
        }
        System.out.println();
    }
    
    // 重写处理落子输入方法
    @Override
    protected boolean processMoveInput(String input) {
        try {
            // 找到第一个非数字字符的位置
            int letterPos = 0;
            while (letterPos < input.length() && Character.isDigit(input.charAt(letterPos))) {
                letterPos++;
            }
            
            // 如果没有找到字母或者数字部分为空，则输入格式错误
            if (letterPos == 0 || letterPos >= input.length()) {
                System.out.println("输入格式有误，请使用数字+字母（如：1a）");
                return false;
            }
            
            // 解析行号和列号
            int row = Integer.parseInt(input.substring(0, letterPos)) - 1;
            char colChar = Character.toUpperCase(input.charAt(letterPos));
            int col = colChar - 'A';
            
            // 验证行列是否在有效范围内
            if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                System.out.println("输入超出棋盘范围，请重新输入！");
                return false;
            }

            if (!isValidMove(row, col, currentPlayer.getPieceType())) {
                System.out.println("不是合法的落子位置，请重新输入！");
                return false;
            }
            
            // 先放置棋子再翻转
            boolean result = boards[currentBoardIndex].placePiece(row, col, currentPlayer.getPieceType(), true);
            if (result) {
                flipPieces(row, col, currentPlayer.getPieceType());
            }
            
            return result;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.out.println("输入格式有误，请使用数字+字母（如：1a）");
            return false;
        }
    }
    
    // 重写检查游戏结束方法
    @Override
    protected void checkGameEnd() {
        // 检查棋盘是否已满或者两位玩家都无法落子
        boolean boardFull = boards[currentBoardIndex].isFull();
        boolean noValidMoves = !hasValidMove(player1) && !hasValidMove(player2);
        isGameEnded = boardFull || noValidMoves;
    }
    
    // 重写显示游戏结果方法
    @Override
    protected void displayGameResult() {
        int blackCount = countPieces(Piece.BLACK);
        int whiteCount = countPieces(Piece.WHITE);
        
        System.out.println("游戏结束！");
        System.out.println("玩家[" + player1.getName() + "] 得分: " + blackCount);
        System.out.println("玩家[" + player2.getName() + "] 得分: " + whiteCount);
        
        if (blackCount > whiteCount) {
            System.out.println("玩家[" + player1.getName() + "]获胜！");
        } else if (whiteCount > blackCount) {
            System.out.println("玩家[" + player2.getName() + "]获胜！");
        } else {
            System.out.println("游戏平局！");
        }
    }
    

    
    // 判断位置是否为合法落子
    @Override
    protected boolean isValidMove(int row, int col, Piece pieceType) {
        // 位置必须为空
        if (boards[currentBoardIndex].getPiece(row, col) != Piece.EMPTY) {
            return false;
        }
        
        // 检查8个方向
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        Piece opponentPiece = (pieceType == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            
            // 检查是否有对手棋子相邻
            if (isWithinBoard(r, c) && boards[currentBoardIndex].getPiece(r, c) == opponentPiece) {
                r += dir[0];
                c += dir[1];
                
                // 继续沿此方向检查
                while (isWithinBoard(r, c)) {
                    if (boards[currentBoardIndex].getPiece(r, c) == Piece.EMPTY) {
                        break;
                    }
                    if (boards[currentBoardIndex].getPiece(r, c) == pieceType) {
                        return true; // 找到了可翻转的棋子
                    }
                    r += dir[0];
                    c += dir[1];
                }
            }
        }
        
        return false;
    }
    // 翻转被夹住的对手棋子
    protected void flipPieces(int row, int col, Piece pieceType) {
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        Piece opponentPiece = (pieceType == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        
        for (int[] dir : directions) {
            ArrayList<int[]> toFlip = new ArrayList<>();
            int r = row + dir[0];
            int c = col + dir[1];
            
            // 收集一条直线上的对手棋子
            while (isWithinBoard(r, c) && boards[currentBoardIndex].getPiece(r, c) == opponentPiece) {
                toFlip.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
            
            // 只有当这条线上最后遇到自己的棋子时，才翻转中间的对手棋子
            if (isWithinBoard(r, c) && boards[currentBoardIndex].getPiece(r, c) == pieceType && !toFlip.isEmpty()) {
                for (int[] pos : toFlip) {
                    boards[currentBoardIndex].placePiece(pos[0], pos[1], pieceType,true);
                }
            }
        }
    }

    // 计算特定类型棋子的数量
    protected int countPieces(Piece pieceType) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boards[currentBoardIndex].getPiece(i, j) == pieceType) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // 检查坐标是否在棋盘内
    private boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }
} 