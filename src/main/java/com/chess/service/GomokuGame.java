package com.chess.service;

import com.chess.entity.Piece;
import com.chess.entity.Player;

public class GomokuGame extends Game {
    
    private int currentRound = 1;
    
    public GomokuGame(String player1Name, String player2Name, int gameId) {
        super(player1Name, player2Name, GameMode.GOMOKU, gameId);
    }
    
    @Override
    protected void initializeBoard() {
        // 五子棋初始棋盘为空，不需要放置任何棋子
        
        // 确保当前玩家是黑棋(Player 1)
        currentPlayer = player1;
    }
    
    @Override
    protected boolean processMoveInput(String input) {
        boolean result = super.processMoveInput(input);
        
        // 如果落子成功，增加回合数
        if (result) {
            if (currentPlayer == player1) {
                currentRound++;
            }
        }
        
        return result;
    }
    
    @Override
    protected void displayBoard() {
        System.out.println("  A B C D E F G H");
        
        for (int i = 0; i < Math.max(boardSize, 6 + gameList.size() - 2); i++) {
            // 显示棋盘行（如果在棋盘范围内）
            if (i < boardSize) {
                System.out.print((i + 1));
                for (int j = 0; j < boardSize; j++) {
                    System.out.print(" " + boards[currentBoardIndex].getPiece(i, j).getSymbol());
                }
            } else {
                // 如果超出棋盘范围，只需要为游戏列表留出空间
                System.out.print("                 ");
            }

            // 右侧显示游戏信息和游戏列表
            if (i == 3) {
                System.out.print("  游戏#" + gameId + " (" + gameMode.getName() + ")    游戏列表");
            } else if (i == 4) {
                System.out.print("  玩家[" + player1.getName() + "] " +
                        (currentPlayer == player1 ? player1.getPieceType().getSymbol() : "") + "   " + 
                        (0 < gameList.size() ? "1. " + gameList.get(0).gameMode.getName() + 
                        (0 == currentGameIndex ? " (当前)" : "") : ""));
            } else if (i == 5) {
                System.out.print("  玩家[" + player2.getName() + "] " +
                        (currentPlayer == player2 ? player2.getPieceType().getSymbol() : "") + "    " + 
                        (1 < gameList.size() ? "2. " + gameList.get(1).gameMode.getName() + 
                        (1 == currentGameIndex ? " (当前)" : "") : ""));
            } else if (i == 6) {
                // 在Player2下面显示当前回合数
                System.out.print("  当前回合: " + currentRound);
            } else if (i >= 7 && i < 7 + gameList.size() - 2) {
                // 从第三个游戏开始，顺序显示剩余的游戏列表项
                int gameIndex = i - 7 + 2; // 从第三个游戏(索引2)开始
                if (gameIndex < gameList.size()) {
                    System.out.print("                    " + (gameIndex + 1) + ". " + 
                            gameList.get(gameIndex).gameMode.getName() + 
                            (gameIndex == currentGameIndex ? " (当前)" : ""));
                }
            }

            System.out.println();
        }
        System.out.println();
    }
    
    @Override
    protected void checkGameEnd() {
        // 检查是否有玩家获胜（五子连珠）
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Piece currentPiece = boards[currentBoardIndex].getPiece(i, j);
                if (currentPiece != Piece.EMPTY) {
                    // 检查水平方向
                    if (j <= boardSize - 5) {
                        boolean win = true;
                        for (int k = 1; k < 5; k++) {
                            if (boards[currentBoardIndex].getPiece(i, j + k) != currentPiece) {
                                win = false;
                                break;
                            }
                        }
                        if (win) {
                            isGameEnded = true;
                            return;
                        }
                    }
                    
                    // 检查垂直方向
                    if (i <= boardSize - 5) {
                        boolean win = true;
                        for (int k = 1; k < 5; k++) {
                            if (boards[currentBoardIndex].getPiece(i + k, j) != currentPiece) {
                                win = false;
                                break;
                            }
                        }
                        if (win) {
                            isGameEnded = true;
                            return;
                        }
                    }
                    
                    // 检查右下对角线
                    if (i <= boardSize - 5 && j <= boardSize - 5) {
                        boolean win = true;
                        for (int k = 1; k < 5; k++) {
                            if (boards[currentBoardIndex].getPiece(i + k, j + k) != currentPiece) {
                                win = false;
                                break;
                            }
                        }
                        if (win) {
                            isGameEnded = true;
                            return;
                        }
                    }
                    
                    // 检查左下对角线
                    if (i <= boardSize - 5 && j >= 4) {
                        boolean win = true;
                        for (int k = 1; k < 5; k++) {
                            if (boards[currentBoardIndex].getPiece(i + k, j - k) != currentPiece) {
                                win = false;
                                break;
                            }
                        }
                        if (win) {
                            isGameEnded = true;
                            return;
                        }
                    }
                }
            }
        }
        
        // 检查棋盘是否已满
        boolean isFull = true;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boards[currentBoardIndex].getPiece(i, j) == Piece.EMPTY) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        
        isGameEnded = isFull;
    }
    
    @Override
    protected void displayGameResult() {
        System.out.println("游戏结束！");
        
        // 如果棋盘已满但没有人五子连珠，则平局
        boolean isFull = true;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boards[currentBoardIndex].getPiece(i, j) == Piece.EMPTY) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        
        if (isFull) {
            System.out.println("棋盘已满，游戏平局！");
            return;
        }
        
        // 如果不是平局，则当前玩家的对手获胜（因为在落子后就检查获胜条件）
        Player winner = (currentPlayer == player1) ? player2 : player1;
        System.out.println("恭喜玩家[" + winner.getName() + "]获胜！");
    }
} 