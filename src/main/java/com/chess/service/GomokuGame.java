package com.chess.service;

import com.chess.entity.Piece;
import com.chess.entity.Player;
import com.chess.entity.GomokuBoard;

public class GomokuGame extends Game {
    
    private int currentRound = 1;
    private int blackBombs = 2;
    private int whiteBombs = 3;
    private boolean isDemoMode = false;
    
    public GomokuGame(String player1Name, String player2Name, int gameId) {
        super(player1Name, player2Name, GameMode.GOMOKU, gameId);
    }
    
    @Override
    protected void initializeBoard() {
        // 使用GomokuBoard替换默认的Board
        for (int i = 0; i < BOARD_COUNT; i++) {
            boards[i] = new GomokuBoard(true);
        }
        boardSize = boards[0].getSize();
        boardMiddle = boardSize / 2;
        // 添加障碍物
        int[][] blockPositions = {
            {GomokuBoard.parseRowLabel("3"), GomokuBoard.parseColLabel("F")}, // 3F
            {GomokuBoard.parseRowLabel("8"), GomokuBoard.parseColLabel("G")}, // 8G
            {GomokuBoard.parseRowLabel("9"), GomokuBoard.parseColLabel("F")}, // 9F
            {GomokuBoard.parseRowLabel("C"), GomokuBoard.parseColLabel("K")}  // CK
        };
        for (int[] pos : blockPositions) {
            if (pos[0] >= 0 && pos[1] >= 0 && pos[0] < boardSize && pos[1] < boardSize) {
                boards[0].placePiece(pos[0], pos[1], Piece.BLOCK, true);
            }
        }
        // 确保当前玩家是黑棋(Player 1)
        currentPlayer = player1;
    }
    
    @Override
    public void playOneRound() {
        // 无论游戏是否结束，都使用makeMove来处理输入
        if (isGameEnded) {
            displayGameResult();
            System.out.println("当前游戏已结束，请切换到其他游戏或添加新游戏");
            makeMoveAfterGameEnd();
            
        }
        
        else {
            makeMove(false,true);
            // switchPlayer();
            checkGameEnd();
            
        }
    }

    private void makeMoveAfterGameEnd() {
        boolean validMove = false;
        while (!validMove) {
            System.out.print("请玩家[" + currentPlayer.getName() + "]输入游戏编号 (如1,2) / 新游戏类型("+String.join(",", GameModeList) + ") / 退出程序(quit)：");
 
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


            boolean breakRound = false;
            for (GameMode mode : GameMode.values()){
                if(input.equalsIgnoreCase(mode.getName())){
                    addNewGame(mode.getName());
                    clearScreen();
                    displayBoard();
                    breakRound = true;
                    break;
                }
            }
            if(breakRound){
                break;
            }

            // if (input.equalsIgnoreCase("peace") || input.equalsIgnoreCase("reversi") || input.equalsIgnoreCase("gomoku")) {
            //     addNewGame(input);
            //     clearScreen();
            //     displayBoard();
            //     // continue是因为需要保留在原本的游戏里面,因此还需要判定一次valid的move
            //     continue;
            // }

            
            try {
                int gameIndex = Integer.parseInt(input) - 1;
                if (gameIndex >= 0 && gameIndex < gameList.size()) {
                    switchToGame(gameIndex);
                    return; // 切换游戏后退出当前循环
                }
            } catch (NumberFormatException e) {
                // 继续处理其他输入类型
            }
            
            // 切换棋盘
            if (input.length() == 1) {
                processBoardSelection(input);
            } 

            else {
                System.out.println("输入格式有误，请使用如(1,2)的棋盘号的数字。");
            }
        }
    }
        

    @Override
    protected boolean processMoveInput(String input) {
        input = input.trim();
        if (input.equalsIgnoreCase("demo")) {
            isDemoMode = true;
            System.out.println("已进入Demo演示模式，系统将自动操作演示五子棋玩法。");
            runDemo();
            return false;
        }
        try {
            input = input.toUpperCase();
            // 炸弹道具输入：@XY
            if (input.startsWith("@")) {
                if ((currentPlayer == player1 && blackBombs == 0) || (currentPlayer == player2 && whiteBombs == 0)) {
                    System.out.println("你没有剩余炸弹了！");
                    return false;
                }
                if (input.length() < 3) {
                    System.out.println("炸弹输入格式有误，请使用@+纵坐标+横坐标（如：@FA）");
                    return false;
                }
                String rowStr = input.substring(1, input.length() - 1);
                String colStr = input.substring(input.length() - 1);
                int row = GomokuBoard.parseRowLabel(rowStr);
                int col = GomokuBoard.parseColLabel(colStr);
                if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                    System.out.println("输入超出棋盘范围，请重新输入！");
                    return false;
                }
                Piece target = boards[currentBoardIndex].getPiece(row, col);
                // 只能炸掉对方棋子，不能炸空、障碍物、弹坑、自己棋子
                if (target == Piece.EMPTY || target == Piece.BLOCK || target == Piece.CRATER || target == currentPlayer.getPieceType()) {
                    System.out.println("只能炸掉对方的棋子！");
                    return false;
                }
                // 执行炸弹效果
                boards[currentBoardIndex].placePiece(row, col, Piece.CRATER, true);
                if (currentPlayer == player1) {
                    blackBombs--;
                } else {
                    whiteBombs--;
                }
                System.out.println("炸弹已使用，位置(" + rowStr + colStr + ")已变为弹坑！");
                return true;
            }
            // 普通落子
            if (input.length() < 2) {
                System.out.println("输入格式有误，请使用纵坐标+横坐标（如：1A / FA）");
                return false;
            }
            String rowStr = input.substring(0, input.length() - 1);
            String colStr = input.substring(input.length() - 1);
            int row = GomokuBoard.parseRowLabel(rowStr);
            int col = GomokuBoard.parseColLabel(colStr);
            if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                System.out.println("输入超出棋盘范围，请重新输入！");
                return false;
            }
            // 检查障碍物和弹坑
            Piece cell = boards[currentBoardIndex].getPiece(row, col);
            if (cell == Piece.BLOCK) {
                System.out.println("该位置为障碍物，无法落子！");
                return false;
            }
            if (cell == Piece.CRATER) {
                System.out.println("该位置为弹坑，无法落子！");
                return false;
            }
            boolean validMove = boards[currentBoardIndex].placePiece(row, col, currentPlayer.getPieceType(), false);
            if (!validMove) {
                System.out.println("落子位置有误，请重新输入！");
                return false;
            }
            if (currentPlayer == player1) {
                currentRound++;
            }
            return true;
        } catch (Exception e) {
            System.out.println("输入格式有误，请使用纵坐标+横坐标（如：1A / FA），或@+坐标使用炸弹");
            return false;
        }
    }
    
    @Override
    protected void displayBoard() {
        // 显示列标签 (A-O)
        System.out.print(" ");
        for (int j = 0; j < boardSize; j++) {
            System.out.print(" " + GomokuBoard.getColLabel(j));
        }
        System.out.println();
        int infoColumn = 25; // 统一信息起始列
        for (int i = 0; i < Math.max(boardSize, 6 + gameList.size() - 2); i++) {
            // 显示棋盘行（如果在棋盘范围内）
            if (i < boardSize) {
                System.out.print(GomokuBoard.getRowLabel(i));
                for (int j = 0; j < boardSize; j++) {
                    System.out.print(" " + boards[currentBoardIndex].getPiece(i, j).getSymbol());
                }
            } else {
                // 如果超出棋盘范围，只需要为游戏列表留出空间
                System.out.print("                 ");
            }
            // 右侧显示游戏信息和游戏列表
            if (i == 3) {
                // int len = ("游戏#" + gameId + " (" + gameMode.getName() + ")").length();
                // for (int s = 0; s < infoColumn - len; s++) System.out.print(" ");
                System.out.print("  游戏#" + gameId + " (" + gameMode.getName() + ")              游戏列表");
            } else if (i == 4) {
                String player1Info = "  玩家[" + player1.getName() + "] "  + (currentPlayer == player1 ? player1.getPieceType().getSymbol() + "   "  : "    ") + "炸弹:" + blackBombs;
                int len = player1Info.length();
                System.out.print(player1Info);
                for (int s = 0; s < infoColumn - len; s++) System.out.print(" ");
                if (0 < gameList.size()) {
                    System.out.print("1. " + gameList.get(0).gameMode.getName() + (0 == currentGameIndex ? " (当前)" : ""));
                }
            } else if (i == 5) {
                String player2Info = "  玩家[" + player2.getName() + "] " + (currentPlayer == player2 ? player2.getPieceType().getSymbol() + "   " : "    ") + "炸弹:" + whiteBombs;
                int len = player2Info.length();
                System.out.print(player2Info);
                for (int s = 0; s < infoColumn - len; s++) System.out.print(" ");
                if (1 < gameList.size()) {
                    System.out.print("2. " + gameList.get(1).gameMode.getName() + (1 == currentGameIndex ? " (当前)" : ""));
                }
            } else if (i == 6) {
                String roundInfo = "  当前回合: " + currentRound;
                int len = roundInfo.length();
                System.out.print(roundInfo);
                for (int s = 0; s < infoColumn - len; s++) System.out.print(" ");
                if (2 < gameList.size()) {
                    System.out.print("3. " + gameList.get(2).gameMode.getName() + (2 == currentGameIndex ? " (当前)" : ""));
                }
            } else if (i >= 7 && i < 7 + gameList.size() - 3) {
                int gameIndex = i - 7 + 3; // 从第4个游戏(索引3)开始
                StringBuilder spaces = new StringBuilder();
                for (int s = 0; s < infoColumn+4; s++) spaces.append(" "); 
                if (gameIndex < gameList.size()) {
                    System.out.print(spaces.toString() + (gameIndex + 1) + ". " + gameList.get(gameIndex).gameMode.getName() + (gameIndex == currentGameIndex ? " (当前)" : ""));
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
                    if (j <= boardSize - 5) { // 五子棋，因此一定是5
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

    // Demo模式自动演示
    private void runDemo() {
        // 黑方依次1A~7A，白方依次1B~5B，黑方在4A后用炸弹@3A
        String[] blackMoves = {"1A", "2A", "3A", "4A", "5A","6A"};
        String[] whiteMoves = {"1B", "2B", "3B", "@3A", "4B", "5B"};
        int bIdx = 0, wIdx = 0;
        // boolean afterBomb = false;
        while (!isGameEnded && (bIdx < blackMoves.length || wIdx < whiteMoves.length)) {
            if (currentPlayer == player1 && bIdx < blackMoves.length) {
                String move = blackMoves[bIdx++];
                processMoveInput(move);
                displayBoard();
                System.out.println("上述操作为黑方输入了 " + move);

                // if (move.startsWith("@")) {
                //     // 炸弹
                //     processMoveInput(move);
                //     displayBoard();
                //     System.out.println("上述操作为黑方输入了 " + move);
                //     // afterBomb = true;
                // } else {
                //     processMoveInput(move);
                //     displayBoard();
                //     System.out.println("上述操作为黑方输入了 " + move);
                // }
                switchPlayer();
            } else if (currentPlayer == player2 && wIdx < whiteMoves.length) {
                String move = whiteMoves[wIdx++];
                processMoveInput(move);
                displayBoard();
                System.out.println("上述操作为白方输入了 " + move);
                switchPlayer();
            } else {
                System.out.println("error");
                break;
            }
            checkGameEnd();
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
        }
        if (isGameEnded) {
            displayGameResult();
        } else {
            System.out.println("Demo演示已结束。");
        }
        isDemoMode = false;
        // 清空board为初始状态
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boards[currentBoardIndex].placePiece(i, j, Piece.EMPTY, true);
            }
        }
        initializeBoard();
    }
} 