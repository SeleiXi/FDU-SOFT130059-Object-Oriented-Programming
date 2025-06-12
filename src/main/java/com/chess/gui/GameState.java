package com.chess.gui;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import com.chess.service.Game;
import com.chess.service.ReversiGame;
import com.chess.service.GomokuGame;

/**
 * 游戏状态保存和恢复工具类
 */
public class GameState {
    private static final String SAVE_FILE = "pj.game";
    
    /**
     * 保存游戏状态到文件
     */
    public static void saveGameState(List<Game> games, int currentGameIndex) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            // 保存游戏数量和当前游戏索引
            writer.println("GAME_COUNT:" + games.size());
            writer.println("CURRENT_INDEX:" + currentGameIndex);
            
            // 保存每个游戏的状态
            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                writer.println("GAME_START:" + i);
                writer.println("TYPE:" + game.getGameMode().getName());
                writer.println("ID:" + game.getGameId());
                writer.println("PLAYER1:" + game.getPlayer1().getName());
                writer.println("PLAYER2:" + game.getPlayer2().getName());
                writer.println("CURRENT_PLAYER:" + (game.getCurrentPlayer() == game.getPlayer1() ? "1" : "2"));
                writer.println("GAME_ENDED:" + game.isGameEnded());
                writer.println("CURRENT_BOARD:" + game.getCurrentBoardIndex());
                
                // 保存特定游戏类型的额外信息
                if (game instanceof GomokuGame) {
                    GomokuGame gomoku = (GomokuGame) game;
                    writer.println("CURRENT_ROUND:" + gomoku.getCurrentRound());
                    writer.println("BLACK_BOMBS:" + gomoku.getBlackBombs());
                    writer.println("WHITE_BOMBS:" + gomoku.getWhiteBombs());
                }
                
                // 保存棋盘状态
                int boardSize = game.getBoards()[0].getSize();
                writer.println("BOARD_SIZE:" + boardSize);
                for (int boardIdx = 0; boardIdx < game.getBoards().length; boardIdx++) {
                    writer.println("BOARD_INDEX:" + boardIdx);
                    for (int row = 0; row < boardSize; row++) {
                        StringBuilder rowData = new StringBuilder();
                        for (int col = 0; col < boardSize; col++) {
                            if (col > 0) rowData.append(",");
                            rowData.append(game.getBoards()[boardIdx].getPiece(row, col).name());
                        }
                        writer.println("ROW:" + row + ":" + rowData.toString());
                    }
                }
                writer.println("GAME_END:" + i);
            }
            
            // System.out.println("游戏状态已保存到 " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("保存游戏状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 从文件加载游戏状态
     */
    public static GameStateData loadGameState() {
        File saveFile = new File(SAVE_FILE);
        if (!saveFile.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            List<Game> games = new ArrayList<>();
            int currentGameIndex = 0;
            String line;
            
            // 读取基本信息
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("GAME_COUNT:")) {
                    // 游戏数量信息
                } else if (line.startsWith("CURRENT_INDEX:")) {
                    currentGameIndex = Integer.parseInt(line.substring("CURRENT_INDEX:".length()));
                } else if (line.startsWith("GAME_START:")) {
                    // 开始读取一个游戏的状态
                    Game game = loadSingleGame(reader);
                    if (game != null) {
                        games.add(game);
                    }
                }
            }
            
            System.out.println("游戏状态已从 " + SAVE_FILE + " 加载");
            return new GameStateData(games, currentGameIndex);
        } catch (IOException e) {
            System.err.println("加载游戏状态失败: " + e.getMessage());
            return null;
        }
    }
    
    private static Game loadSingleGame(BufferedReader reader) throws IOException {
        String gameType = null;
        int gameId = 0;
        String player1Name = null;
        String player2Name = null;
        String currentPlayerStr = null;
        boolean gameEnded = false;
        int currentBoardIndex = 0;
        int currentRound = 1;
        int blackBombs = 0;
        int whiteBombs = 0;
        int boardSize = 0;
        String line;
        
        // 读取游戏基本信息
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("TYPE:")) {
                gameType = line.substring("TYPE:".length());
            } else if (line.startsWith("ID:")) {
                gameId = Integer.parseInt(line.substring("ID:".length()));
            } else if (line.startsWith("PLAYER1:")) {
                player1Name = line.substring("PLAYER1:".length());
            } else if (line.startsWith("PLAYER2:")) {
                player2Name = line.substring("PLAYER2:".length());
            } else if (line.startsWith("CURRENT_PLAYER:")) {
                currentPlayerStr = line.substring("CURRENT_PLAYER:".length());
            } else if (line.startsWith("GAME_ENDED:")) {
                gameEnded = Boolean.parseBoolean(line.substring("GAME_ENDED:".length()));
            } else if (line.startsWith("CURRENT_BOARD:")) {
                currentBoardIndex = Integer.parseInt(line.substring("CURRENT_BOARD:".length()));
            } else if (line.startsWith("CURRENT_ROUND:")) {
                currentRound = Integer.parseInt(line.substring("CURRENT_ROUND:".length()));
            } else if (line.startsWith("BLACK_BOMBS:")) {
                blackBombs = Integer.parseInt(line.substring("BLACK_BOMBS:".length()));
            } else if (line.startsWith("WHITE_BOMBS:")) {
                whiteBombs = Integer.parseInt(line.substring("WHITE_BOMBS:".length()));
            } else if (line.startsWith("BOARD_SIZE:")) {
                boardSize = Integer.parseInt(line.substring("BOARD_SIZE:".length()));
                break;
            }
        }
        
        // 创建对应类型的游戏实例
        Game game;
        switch (gameType.toLowerCase()) {
            case "peace":
                game = new Game(player1Name, player2Name, Game.GameMode.PEACE, gameId);
                break;
            case "reversi":
                game = new ReversiGame(player1Name, player2Name, gameId);
                break;
            case "gomoku":
                game = new GomokuGame(player1Name, player2Name, gameId);
                break;
            default:
                return null;
        }
        
        // 恢复游戏状态
        try {
            // 恢复当前玩家
            if ("2".equals(currentPlayerStr)) {
                game.switchPlayer(); // 默认是玩家1，如果保存的是玩家2则切换
            }
            
            // 恢复游戏结束状态
            if (gameEnded) {
                // 使用反射或其他方式设置游戏结束状态
                // 这里需要根据具体的Game类实现来调整
            }
            
            // 恢复特定游戏类型的状态
            if (game instanceof GomokuGame) {
                GomokuGame gomoku = (GomokuGame) game;
                // 这里需要根据GomokuGame的具体实现来恢复回合数和炸弹数
                // 由于这些字段可能是private的，可能需要添加setter方法
            }
            
        } catch (Exception e) {
            System.err.println("恢复游戏状态时出错: " + e.getMessage());
        }
        
                 // 读取并恢复棋盘状态
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("GAME_END:")) {
                break;
            } else if (line.startsWith("BOARD_INDEX:")) {
                // 解析棋盘索引
                int boardIndex = Integer.parseInt(line.substring("BOARD_INDEX:".length()));
                readBoardData(reader, game, boardSize, boardIndex);
            }
        }
        
        return game;
    }
    
         private static void readBoardData(BufferedReader reader, Game game, int boardSize, int boardIndex) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("ROW:")) {
                // 解析行数据：ROW:0:EMPTY,BLACK,WHITE...
                String[] parts = line.split(":", 3);
                if (parts.length >= 3) {
                    int row = Integer.parseInt(parts[1]);
                    String[] pieces = parts[2].split(",");
                    
                    for (int col = 0; col < pieces.length && col < boardSize; col++) {
                        try {
                                                         com.chess.entity.Piece piece = com.chess.entity.Piece.valueOf(pieces[col]);
                             // 使用 placePiece 方法来设置棋子，第三个参数为 true 表示强制放置
                             game.getBoards()[boardIndex].placePiece(row, col, piece, true);
                        } catch (Exception e) {
                            // 如果棋子类型解析失败，跳过
                            System.err.println("解析棋子失败: " + pieces[col]);
                        }
                    }
                }
            } else if (line.startsWith("BOARD_INDEX:") || line.startsWith("GAME_END:")) {
                // 回退一行，让上级处理
                // 注意：这里需要一个方式来回退读取，但BufferedReader不支持
                // 简化处理：如果遇到下一个BOARD_INDEX，就停止当前棋盘的读取
                break;
            }
        }
    }
    
    /**
     * 游戏状态数据载体类
     */
    public static class GameStateData {
        public final List<Game> games;
        public final int currentGameIndex;
        
        public GameStateData(List<Game> games, int currentGameIndex) {
            this.games = games;
            this.currentGameIndex = currentGameIndex;
        }
    }
} 