package com.chess.service;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import com.chess.entity.Board;
import com.chess.entity.Piece;
import com.chess.entity.Player;

public class Game {
    // 游戏模式枚举
    public enum GameMode {
        PEACE("peace"),
        REVERSI("reversi"),
        GOMOKU("gomoku");
        
        private final String name;
        
        GameMode(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    protected static int BOARD_COUNT = 2;
    protected static final int SCREEN_CLEAR_LINES = 80;
    
    // 游戏管理相关属性
    protected static List<Game> gameList = new ArrayList<>();
    protected static int currentGameIndex = 0;
    
    protected final Board[] boards;
    protected final Player player1;
    protected final Player player2;
    protected final Scanner scanner;
    protected final GameMode gameMode;
    protected final int gameId;
    
    protected int boardSize;
    protected int boardMiddle;
    protected int currentBoardIndex;
    protected Player currentPlayer;
    protected boolean isGameEnded;

    // 构造函数，添加游戏模式和ID参数
    public Game(String player1Name, String player2Name, GameMode gameMode, int gameId) {
        this.gameMode = gameMode;
        this.gameId = gameId;
        
        boards = new Board[BOARD_COUNT];
        for (int i = 0; i < BOARD_COUNT; i++) {
            boards[i] = new Board(true);
        }
        currentBoardIndex = 0;
        
        player1 = new Player(player1Name, Piece.BLACK);
        player2 = new Player(player2Name, Piece.WHITE);
        currentPlayer = player1;
        
        scanner = new Scanner(System.in);
        boardSize = boards[0].getSize();
        boardMiddle = boardSize / 2;
        isGameEnded = false;
        
        // 初始化棋盘（具体由子类实现）
        initializeBoard();
    }
    
    // 为保持向后兼容，提供原来的构造函数
    public Game(String player1Name, String player2Name) {
        this(player1Name, player2Name, GameMode.PEACE, 1);
    }
    
    // 初始化棋盘方法，由子类实现具体逻辑
    protected void initializeBoard() {
        for (int i = 0; i < BOARD_COUNT; i++) {
            // 放置初始的中间的四个棋子
            boards[i].placePiece(boardMiddle - 1, boardMiddle - 1, Piece.WHITE, false);
            boards[i].placePiece(boardMiddle, boardMiddle, Piece.WHITE, false);
            boards[i].placePiece(boardMiddle - 1, boardMiddle, Piece.BLACK, false);
            boards[i].placePiece(boardMiddle, boardMiddle - 1, Piece.BLACK, false);
        }
//        fulfillBoardExceptForOne(Piece.BLACK);
        
        // 确保当前玩家是黑棋(Player 1)
        currentPlayer = player1;
    }

    // 静态方法：初始化游戏列表
    public static void initializeGames() {
        if (gameList.isEmpty()) {
            gameList.add(new Game("Player1", "Player2", GameMode.PEACE, 1));
            gameList.add(new ReversiGame("Player1", "Player2", 2));
            gameList.add(new GomokuGame("Player1", "Player2", 3));
            currentGameIndex = 0; // 从第一个游戏开始
        }
    }
    
    // 静态方法：添加新游戏
    public static void addNewGame(String gameType) {
        if (gameType.equalsIgnoreCase("peace")) {
            gameList.add(new Game("Player1", "Player2", GameMode.PEACE, gameList.size() + 1));
        } else if (gameType.equalsIgnoreCase("reversi")) {
            gameList.add(new ReversiGame("Player1", "Player2", gameList.size() + 1));
        } else if (gameType.equalsIgnoreCase("gomoku")) {
            gameList.add(new GomokuGame("Player1", "Player2", gameList.size() + 1));
        }
        BOARD_COUNT++;
    }
    
    // 静态方法：切换到指定游戏
    public static void switchToGame(int gameIndex) {
        if (gameIndex >= 0 && gameIndex < gameList.size()) {
            currentGameIndex = gameIndex;
        }
    }
    
    // 静态方法：启动游戏系统，统一使用makeMove处理所有输入
    public static void startGameSystem() {
        initializeGames();
        
        while (true) {
            Game currentGame = gameList.get(currentGameIndex);
            currentGame.clearScreen();
            currentGame.displayBoard();
            
            // 执行当前游戏的一轮，包括所有输入处理
            currentGame.playOneRound();
        }
    }
    
    // 执行一轮游戏
    public void playOneRound() {
        // 无论游戏是否结束，都使用makeMove来处理输入
        if (isGameEnded) {
            System.out.println("当前游戏已结束，请切换到其他游戏或添加新游戏");
            makeMove(true);
            // 不能return，否则会无限循环，因为没有进入makeMove从而进入input()等待
            // return;
            
        }
        
        else {
            makeMove(false);
            // switchPlayer();
            checkGameEnd();
            
        }
    }

    // 显示游戏结果
    protected void displayGameResult() {
        System.out.println("游戏结束，棋盘已满！");
    }

    // 检查游戏是否结束
    protected void checkGameEnd() {
        isGameEnded = true;
        for (int i = 0; i < BOARD_COUNT; i++) {
            if (!boards[i].isFull()) {
                isGameEnded = false;
                break;
            }
        }
    }

    // 判断是否有合法落子位置
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
    
    // 判断位置是否为合法落子位置
    protected boolean isValidMove(int row, int col, Piece pieceType) {
        // 基本检查：位置必须在棋盘内且为空
        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
            return false;
        }
        return boards[currentBoardIndex].getPiece(row, col) == Piece.EMPTY;
    }
    
    // 清屏
    protected void clearScreen() {
        for (int i = 0; i < SCREEN_CLEAR_LINES; i++) {
            System.out.println();
        }
    }

    // 显示棋盘
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
            } else if (i >= 6 && i < 6 + gameList.size() - 2) {
                // 从第三个游戏开始，顺序显示剩余的游戏列表项
                int gameIndex = i - 6 + 2; // 从第三个游戏(索引2)开始
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

    protected void makeMove(boolean hasPassMethod) {
        boolean validMove = false;
        while (!validMove) {
            int validBoardCount = countInitializedBoards();
            if (hasPassMethod) {
                System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置(如1a) / 游戏编号 (如1,2) / 新游戏类型(peace,reversi,gomoku) / 跳过行棋（Pass） / 退出程序(quit)：");
            } else {
                System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置(如1a) / 游戏编号 (如1,2) / 新游戏类型(peace,reversi,gomoku) / 退出程序(quit)：");
            }
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("输入不能为空，请重新输入");
                continue;
            }
            
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("游戏结束，谢谢使用！");
                System.exit(0); 
                return;
            }
            
            // 检查是否为添加新游戏命令

            // TODO：改成从参数表里面读
            if (input.equalsIgnoreCase("peace") || input.equalsIgnoreCase("reversi") || input.equalsIgnoreCase("gomoku")) {
                addNewGame(input);
                clearScreen();
                displayBoard();
                continue;
            }

            if(input.equalsIgnoreCase("pass")) {
                if(!hasPassMethod) {
                    System.out.println("当前游戏不是Reversi模式，不能Pass");
                }
                else if(!hasValidMove(currentPlayer)) {  
                    switchPlayer();
                }
                else{
                    System.out.println("当前游戏不是处于对局状态且你不能落子的情况，不能Pass");
                }
                continue;
            }
            
            // 检查是否为游戏序号
            try {
                int gameIndex = Integer.parseInt(input) - 1;
                if (gameIndex >= 0 && gameIndex < gameList.size()) {
                    switchToGame(gameIndex);
                    return; // 切换游戏后退出当前循环
                }
            } catch (NumberFormatException e) {
                // 继续处理其他输入类型
            }
            
            // 处理落子或切换棋盘
            if (input.length() == 1) {
                processBoardSelection(input);
            } else if (input.length() >= 2) {
                validMove = processMoveInput(input);
                if(validMove==true){
                    switchPlayer();
                }

            } else {
                System.out.println("输入格式有误，请使用1-" + validBoardCount + "的数字或数字+字母（如：1a）");
            }
        }
    }

    // 修复处理切换棋盘方法，正确检查棋盘边界
    protected void processBoardSelection(String input) {
        try {
            int boardNumber = Integer.parseInt(input);
            // 检查棋盘号是否在有效范围内（1到已初始化的棋盘数量）
            if (boardNumber >= 1 && boardNumber <= boards.length && boards[boardNumber - 1] != null) {
                currentBoardIndex = boardNumber - 1;
                clearScreen();
                displayBoard();
            } else {
                System.out.println("无效的棋盘号");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式有误，请使用棋盘号（如1,2）或数字+字母（如：1a）");
        }
    }

    // 添加辅助方法，计算初始化的棋盘数量
    private int countInitializedBoards() {
        int count = 0;
        for (Board board : boards) {
            if (board != null) {
                count++;
            }
        }
        return count;
    }

    // 修改处理落子输入的方法，支持多位数字行号
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

            boolean validMove = boards[currentBoardIndex].placePiece(row, col, currentPlayer.getPieceType(), false);
            if (!validMove) {
                System.out.println("落子位置有误，请重新输入！");
                return false;
            }
            
            
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.out.println("输入格式有误，请使用数字+字母（如：1a）");
            return false;
        }
    }

    // 切换玩家
    protected void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public void fulfillBoardExceptForOne(Piece pieceType) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boards[currentBoardIndex].placePiece(i, j, pieceType, true);
                if(i==boardMiddle && j==boardMiddle){
                    boards[currentBoardIndex].placePiece(i, j, Piece.EMPTY, true);;
                }
                if(i==boardMiddle+1 && j==boardMiddle+1){
                    boards[currentBoardIndex].placePiece(i, j, Piece.WHITE, true);
                }
            }
        }
    }







}