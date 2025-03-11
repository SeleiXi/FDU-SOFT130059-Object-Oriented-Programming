package com.chess;

import com.chess.service.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game("张三", "李四");
        game.start();
    }
}