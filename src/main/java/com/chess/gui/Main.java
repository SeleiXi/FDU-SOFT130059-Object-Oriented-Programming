package com.chess.gui;

/**
 * GUI版本主程序入口
 */
public class Main {
    public static void main(String[] args) {
        // 显示启动信息
        System.out.println("=== Chess Games GUI Version ===");
        System.out.println("启动图形界面...");
        
        // 启动Swing GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new SimpleChessGUI().setVisible(true);
        });
    }
} 