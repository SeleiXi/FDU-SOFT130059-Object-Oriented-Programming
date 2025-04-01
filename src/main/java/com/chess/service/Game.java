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
        REVERSI("reversi");
        
        private final String name;
        
        GameMode(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    protected static final int BOARD_COUNT = 3;
    protected static final int SCREEN_CLEAR_LINES = 80;
    
    // 游戏管理相关属性
    private static List<Game> gameList = new ArrayList<>();
    private static int currentGameIndex = 0;
    
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
        
        // 初始化棋盘（具体由子类实现）
        initializeBoard();
    }
    
    // 为保持向后兼容，提供原来的构造函数
    public Game(String player1Name, String player2Name) {
        this(player1Name, player2Name, GameMode.PEACE, 1);
    }
    
    // 初始化棋盘方法，由子类实现具体逻辑
    protected void initializeBoard() {
        // 和平模式不需要特殊初始化，保持空棋盘
    }

    // 静态方法：初始化游戏列表
    public static void initializeGames() {
        if (gameList.isEmpty()) {
            gameList.add(new Game("Player1", "Player2", GameMode.PEACE, 1));
            gameList.add(new ReversiGame("Player1", "Player2", 2));
            currentGameIndex = 0; // 从第一个游戏开始
        }
    }
    
    // 静态方法：添加新游戏
    public static void addNewGame(String gameType) {
        if (gameType.equalsIgnoreCase("peace")) {
            gameList.add(new Game("Player1", "Player2", GameMode.PEACE, gameList.size() + 1));
        } else if (gameType.equalsIgnoreCase("reversi")) {
            gameList.add(new ReversiGame("Player1", "Player2", gameList.size() + 1));
        }
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
            currentGame.displayGameList();
            
            // 执行当前游戏的一轮，包括所有输入处理
            currentGame.playOneRound();
        }
    }
    
    // 执行一轮游戏
    public void playOneRound() {
        // 无论游戏是否结束，都使用makeMove来处理输入
        if (isGameEnded) {
            System.out.println("当前游戏已结束，请切换到其他游戏或添加新游戏");
        }
        
        makeMove();
        
        if (!isGameEnded) {
            switchPlayer();
            checkGameEnd();
            
            if (isGameEnded) {
                displayGameResult();
            }
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

    // 清屏
    protected void clearScreen() {
        for (int i = 0; i < SCREEN_CLEAR_LINES; i++) {
            System.out.println();
        }
    }

    // 显示棋盘
    protected void displayBoard() {
        System.out.println("当前棋盘：" + (currentBoardIndex + 1) + " (模式: " + gameMode.getName() + ")");
        System.out.println("  A B C D E F G H");
        
        for (int i = 0; i < boardSize; i++) {
            System.out.print((i + 1));
            for (int j = 0; j < boardSize; j++) {
                System.out.print(" " + boards[currentBoardIndex].getPiece(i, j).getSymbol());
            }

            // 显示玩家信息
            if (i == boardMiddle - 1) {
                System.out.print("  游戏#" + gameId + " (" + gameMode.getName() + ")");
            } else if (i == boardMiddle) {
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

    // 显示游戏列表
    protected void displayGameList() {
        System.out.println("游戏列表：");
        for (int i = 0; i < gameList.size(); i++) {
            Game game = gameList.get(i);
            System.out.println((i + 1) + ". " + game.gameMode.getName() + 
                    (i == currentGameIndex ? " (当前游戏)" : ""));
        }
        System.out.println();
    }

    // 处理落子，添加对quit命令的处理
    protected void makeMove() {
        boolean validMove = false;
        while (!validMove) {
            System.out.print("请玩家[" + currentPlayer.getName() + "]输入落子位置或棋盘号(1-" + BOARD_COUNT + ")，或输入peace/reversi添加新游戏，或输入游戏序号切换游戏，输入quit退出：");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("输入不能为空，请重新输入");
                continue;
            }
            
            // 检查是否为退出命令
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("游戏结束，谢谢使用！");
                System.exit(0); // 直接退出程序
                return;
            }
            
            // 检查是否为添加新游戏命令
            if (input.equalsIgnoreCase("peace") || input.equalsIgnoreCase("reversi")) {
                addNewGame(input);
                clearScreen();
                displayBoard();
                displayGameList();
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
            } else {
                System.out.println("输入格式有误，请使用1-" + BOARD_COUNT + "的数字或数字+字母（如：1a）");
            }
        }
    }

    // 处理切换棋盘
    protected void processBoardSelection(String input) {
        try {
            int boardNumber = Integer.parseInt(input);
            if (boardNumber >= 1 && boardNumber <= BOARD_COUNT) {
                currentBoardIndex = boardNumber - 1;
                clearScreen();
                displayBoard();
                displayGameList();
            } else {
                System.out.println("棋盘号必须在1-" + BOARD_COUNT + "之间，请重新输入！");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式有误，请使用1-" + BOARD_COUNT + "的数字或数字+字母（如：1a）");
        }
    }

    // 处理落子输入
    protected boolean processMoveInput(String input) {
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

    // 切换玩家
    protected void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}