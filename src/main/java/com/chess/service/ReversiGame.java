package com.chess.service;

import java.util.ArrayList;
import com.chess.entity.Piece;
import com.chess.entity.Player;

public class ReversiGame extends Game {
    
    // 构造函数
    public ReversiGame(String player1Name, String player2Name, int gameId) {
        super(player1Name, player2Name, GameMode.REVERSI, gameId);
    }
    
    // 初始化黑白棋棋盘
    @Override
    protected void initializeBoard() {
        for (int i = 0; i < BOARD_COUNT; i++) {
            // 放置初始的四个棋子
            boards[i].placePiece(boardMiddle - 1, boardMiddle - 1, Piece.WHITE);
            boards[i].placePiece(boardMiddle, boardMiddle, Piece.WHITE);
            boards[i].placePiece(boardMiddle - 1, boardMiddle, Piece.BLACK);
            boards[i].placePiece(boardMiddle, boardMiddle - 1, Piece.BLACK);
        }
        
        // 确保当前玩家是黑棋(Player 1)
        currentPlayer = player1;
    }
    
    // 重写开始游戏方法
    @Override
    public void start() {
        while (!isGameEnded) {
            checkGameEnd();
            clearScreen();
            displayBoard();
            displayGameList();
            
            // 处理没有合法落子的情况
            if (!hasValidMove(currentPlayer)) {
                System.out.println("玩家[" + currentPlayer.getName() + "]没有合法落子位置，弃权一次");
                switchPlayer();
                
                // 如果下一个玩家也没有合法落子，游戏结束
                if (!hasValidMove(currentPlayer)) {
                    isGameEnded = true;
                    System.out.println("两位玩家都没有合法落子位置，游戏结束！");
                    continue;
                }
            }
            
            makeMove();
            switchPlayer();
        }
        clearScreen();
        displayBoard();
        displayGameList();
        displayGameResult();
    }
    
    // 重写一轮游戏方法
    @Override
    public void playOneRound() {
        if (isGameEnded) {
            System.out.println("当前游戏已结束，请切换到其他游戏或添加新游戏");
            return;
        }
        
        // 处理没有合法落子的情况
        if (!hasValidMove(currentPlayer)) {
            System.out.println("玩家[" + currentPlayer.getName() + "]没有合法落子位置，弃权一次");
            switchPlayer();
            
            // 如果下一个玩家也没有合法落子，游戏结束
            if (!hasValidMove(currentPlayer)) {
                isGameEnded = true;
                System.out.println("两位玩家都没有合法落子位置，游戏结束！");
                displayGameResult();
                return;
            }
        }
        
        makeMove();
        switchPlayer();
        checkGameEnd();
    }
    
    // 重写显示棋盘方法
    @Override
    protected void displayBoard() {
        System.out.println("当前棋盘：" + (currentBoardIndex + 1) + " (模式: " + gameMode.getName() + ")");
        System.out.println("  A B C D E F G H");
        
        for (int i = 0; i < boardSize; i++) {
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

            // 显示玩家信息和得分
            if (i == boardMiddle - 1) {
                System.out.print("  游戏#" + gameId + " (" + gameMode.getName() + ")");
            } else if (i == boardMiddle) {
                System.out.print("  玩家[" + player1.getName() + "] " +
                        (currentPlayer == player1 ? player1.getPieceType().getSymbol() : ""));
                System.out.print(" 得分: " + countPieces(Piece.BLACK));
            } else if (i == boardMiddle + 1) {
                System.out.print("  玩家[" + player2.getName() + "] " +
                        (currentPlayer == player2 ? player2.getPieceType().getSymbol() : ""));
                System.out.print(" 得分: " + countPieces(Piece.WHITE));
            }

            System.out.println();
        }
        System.out.println();
    }
    
    // 重写处理落子输入方法
    @Override
    protected boolean processMoveInput(String input) {
        try {
            int row = Integer.parseInt(input.substring(0, 1)) - 1;
            char colChar = Character.toUpperCase(input.charAt(1));
            int col = colChar - 'A';

            if (!isValidMove(row, col, currentPlayer.getPieceType())) {
                System.out.println("不是合法的落子位置，请重新输入！");
                return false;
            }
            
            // 落子并翻转
            boards[currentBoardIndex].placePiece(row, col, currentPlayer.getPieceType());
            flipPieces(row, col, currentPlayer.getPieceType());
            
            return true;
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
    
    // 判断是否有合法落子
    protected boolean hasValidMove(Player player) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (isValidMove(i, j, player.getPieceType())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // 判断位置是否为合法落子
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
    
    // 翻转棋子
    protected void flipPieces(int row, int col, Piece pieceType) {
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        Piece opponentPiece = (pieceType == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            
            // 临时存储可能需要翻转的位置
            ArrayList<int[]> toFlip = new ArrayList<>();
            boolean validDirection = false;
            
            // 检查是否有对手棋子相邻
            while (isWithinBoard(r, c) && boards[currentBoardIndex].getPiece(r, c) == opponentPiece) {
                toFlip.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
                
                // 如果找到自己的棋子，则这个方向有效
                if (isWithinBoard(r, c) && boards[currentBoardIndex].getPiece(r, c) == pieceType) {
                    validDirection = true;
                    break;
                }
            }
            
            // 如果这个方向有效，翻转所有棋子
            if (validDirection) {
                for (int[] pos : toFlip) {
                    boards[currentBoardIndex].placePiece(pos[0], pos[1], pieceType);
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
    protected boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }
} 