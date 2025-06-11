package com.chess;

// import com.chess.service.Game;
import com.chess.gui.ChessGameFX;

// public class Main {
//     public static void main(String[] args) {
//         Game.startGameSystem();
//     }
// }

/**
 * JavaFX版本主程序入口
 */
public class MainFX {
    public static void main(String[] args) {
        System.out.println("=== Chess Games JavaFX Version ===");
        System.out.println("启动JavaFX图形界面...");
        
        // 启动JavaFX应用
        ChessGameFX.main(args);
    }
} 