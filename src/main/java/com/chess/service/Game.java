package com.chess.service;

import java.io.IOException;
import java.util.Scanner;
import com.chess.entity.Board;
import com.chess.entity.Piece;
import com.chess.entity.Player;

public class Game {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner;

    public Game(String player1Name, String player2Name) {
        board = new Board();
        player1 = new Player(player1Name, Piece.BLACK);
        player2 = new Player(player2Name, Piece.WHITE);
        currentPlayer = player1;
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (!board.isFull()) {
            clearScreen();
            displayBoard();
            makeMove();
            switchPlayer();
        }
        clearScreen();
        displayBoard();
        System.out.println("游戏结束，棋盘已满！");
    }

    private void clearScreen() {
        // new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            // e.printStackTrace();
            System.out.println(123);
        }

    }

    private void displayBoard() {
        System.out.println("  A B C D E F G H");
        for (int i = 0; i < board.getSize(); i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < board.getSize(); j++) {
                System.out.print(" " + board.getPiece(i, j).getSymbol());
            }

            // Display player information on the right side of the board
            if (i == 4) {
                System.out.print("  玩家[" + player1.getName() + "] " +
                        (currentPlayer == player1 ? player1.getPieceType().getSymbol() : ""));
            } else if (i == 5) {
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
            System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置：");
            String input = scanner.nextLine().trim();

            if (input.length() < 2) {
                System.out.println("输入格式有误，请使用数字+字母（如：1a）");
                continue;
            }

            try {
                int row = Integer.parseInt(input.substring(0, 1)) - 1;
                char colChar = Character.toUpperCase(input.charAt(1));
                int col = colChar - 'A';

                validMove = board.placePiece(row, col, currentPlayer.getPieceType());
                if (!validMove) {
                    System.out.println("落子位置有误，请重新输入！");
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                System.out.println("输入格式有误，请使用数字+字母（如：1a）");
            }
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}