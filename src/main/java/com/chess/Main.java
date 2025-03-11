package com.chess;

import com.chess.service.Game;

public class Main {
    public static void main(String[] args) {
        // 介于Lab文档里没有要求让用户输入自己的名字，此处硬编码
        Game game = new Game("张三", "李四");
        game.start();
    }
}