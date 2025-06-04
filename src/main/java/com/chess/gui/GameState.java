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
            
            System.out.println("游戏状态已保存到 " + SAVE_FILE);
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
            } else if (line.startsWith("BOARD_SIZE:")) {
                // 开始读取棋盘数据，此时可以创建游戏实例
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
        
        // 读取剩余的游戏状态（这里简化处理，实际应该还原完整状态）
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("GAME_END:")) {
                break;
            }
            // 可以在这里添加更多的状态还原逻辑
        }
        
        return game;
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