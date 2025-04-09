package com.chess.service;

import com.chess.entity.Piece;
import com.chess.entity.Player;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    
    @Test
    public void testGameInitialization() {
        Game game = new Game("TestPlayer1", "TestPlayer2");
        assertNotNull(game);
        // 更多初始化测试...
    }
    
    @Test
    public void testFillBoard() {
        Game game = new Game("TestPlayer1", "TestPlayer2");
        game.fillBoard(Piece.BLACK);
        
        // 检查是否所有位置都填充了黑棋
        for (int i = 0; i < game.boardSize; i++) {
            for (int j = 0; j < game.boardSize; j++) {
                assertEquals(Piece.BLACK, game.boards[game.currentBoardIndex].getPiece(i, j));
            }
        }
    }
} 