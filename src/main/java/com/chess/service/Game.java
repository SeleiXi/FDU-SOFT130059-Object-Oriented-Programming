package com.chess.service;

import java.util.Scanner;
import com.chess.entity.Board;
import com.chess.entity.Piece;
import com.chess.entity.Player;

public class Game {
    private static final int BOARD_COUNT = 3;
    private static final int SCREEN_CLEAR_LINES = 80;
    
    private final Board[] boards;
    private final Player player1;
    private final Player player2;
    private final Scanner scanner;
    
    private int boardSize;
    private int boardMiddle;
    private int currentBoardIndex;
    private Player currentPlayer;
    private boolean isGameEnded;

    public Game(String player1Name, String player2Name) {
        boards = new Board[BOARD_COUNT];
        for (int i = 0; i < BOARD_COUNT; i++) {
            boards[i] = new Board();
        }
        currentBoardIndex = 0;
        
        player1 = new Player(player1Name, Piece.BLACK);
        player2 = new Player(player2Name, Piece.WHITE);
        currentPlayer = player1;
        
        scanner = new Scanner(System.in);
        boardSize = boards[0].getSize();
        boardMiddle = boardSize / 2;
        isGameEnded = false;
    }

    public void start() {
        while (!isGameEnded) {
            checkGameEnd();
            clearScreen();
            displayBoard();
            makeMove();
            switchPlayer();
        }
        clearScreen();
        displayBoard();
        System.out.println("游戏结束，棋盘已满！");
    }

    private void checkGameEnd() {
        isGameEnded = true;
        for (int i = 0; i < BOARD_COUNT; i++) {
            if (!boards[i].isFull()) {
                isGameEnded = false;
                break;
            }
        }
    }

    private void clearScreen() {
        for (int i = 0; i < SCREEN_CLEAR_LINES; i++) {
            System.out.println();
        }
    }

    private void displayBoard() {
        System.out.println("当前棋盘：" + (currentBoardIndex + 1));
        System.out.println("  A B C D E F G H");
        for (int i = 0; i < boardSize; i++) {
            System.out.print((i + 1));
            for (int j = 0; j < boardSize; j++) {
                System.out.print(" " + boards[currentBoardIndex].getPiece(i, j).getSymbol());
            }

            if (i == boardMiddle) {
                System.out.print("  玩家[" + player1.getName() + "] " +
                        (currentPlayer == player1 ? player1.getPieceType().getSymbol() : ""));
            } else if (i == boardMiddle + 1) {
                System.out.print("  玩家[" + player2.getName() + "] " +
                        (currentPlayer == player2 ? player2.getPieceType().getSymbol() : ""));
            }

            System.out.println();
        }
        System.out.println();
    }

    private void makeMove() {
        boolean validMove = false;
        while (!validMove) {
            System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置或棋盘号(1-" + BOARD_COUNT + ")：");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("输入不能为空，请重新输入");
                continue;
            }
            
            // 根据文档的TIPS，根据输入长度判断是棋盘切换还是落子位置
            if (input.length() == 1) {
                processBoardSelection(input);
            } else if (input.length() >= 2) {
                validMove = processMoveInput(input);
            } else {
                System.out.println("输入格式有误，请使用1-" + BOARD_COUNT + "的数字或数字+字母（如：1a）");
            }
        }
    }

    private void processBoardSelection(String input) {
        try {
            int boardNumber = Integer.parseInt(input);
            if (boardNumber >= 1 && boardNumber <= BOARD_COUNT) {
                currentBoardIndex = boardNumber - 1;
                clearScreen();
                displayBoard();
            } else {
                System.out.println("棋盘号必须在1-" + BOARD_COUNT + "之间，请重新输入！");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式有误，请使用1-" + BOARD_COUNT + "的数字或数字+字母（如：1a）");
        }
    }

    private boolean processMoveInput(String input) {
        try {
            int row = Integer.parseInt(input.substring(0, 1)) - 1;
            char colChar = Character.toUpperCase(input.charAt(1));
            int col = colChar - 'A';

            boolean validMove = boards[currentBoardIndex].placePiece(row, col, currentPlayer.getPieceType());
            if (!validMove) {
                System.out.println("落子位置有误，请重新输入！");
            }
            return validMove;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.out.println("输入格式有误，请使用数字+字母（如：1a）");
            return false;
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}