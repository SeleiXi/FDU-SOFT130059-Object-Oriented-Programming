package com.chess.gui;

import com.chess.service.Game;
import com.chess.service.GomokuGame;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class PlaybackDemo {
    
    private Game demoGame;
    private String[] moves;
    private int currentMoveIndex = 0;
    private Timeline timeline;
    private Runnable updateCallback;
    
    public PlaybackDemo(Game baseGame, Runnable updateCallback) {
        this.updateCallback = updateCallback;
        // 创建演示用的游戏实例
        if (baseGame instanceof GomokuGame) {
            this.demoGame = new GomokuGame("演示黑方", "演示白方", 999);
            // 预设的演示走法
            this.moves = new String[]{
                "1A", "1B", "2A", "2B", "3A", "@3A", "4A", "3B", "5A", "4B"
            };
        } else {
            // 其他游戏类型的演示走法可以在这里添加
            this.moves = new String[]{"1A", "1B", "2A", "2B"};
        }
    }
    
    public void startDemo() {
        currentMoveIndex = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            if (currentMoveIndex < moves.length && !demoGame.isGameEnded()) {
                String move = moves[currentMoveIndex++];
                boolean success = demoGame.processMoveInput(move);
                if (success) {
                    demoGame.switchPlayer();
                    demoGame.checkGameEnd();
                }
                updateCallback.run();
            } else {
                stopDemo();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    public void stopDemo() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    public Game getDemoGame() {
        return demoGame;
    }
    
    public boolean isRunning() {
        return timeline != null && timeline.getStatus() == Timeline.Status.RUNNING;
    }
} 