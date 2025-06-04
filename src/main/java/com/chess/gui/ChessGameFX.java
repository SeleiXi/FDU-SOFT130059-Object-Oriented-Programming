package com.chess.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import com.chess.service.Game;
import com.chess.service.ReversiGame;
import com.chess.service.GomokuGame;
import com.chess.entity.Piece;
import com.chess.entity.GomokuBoard;

import java.util.ArrayList;
import java.util.List;

public class ChessGameFX extends Application {
    
    private BorderPane root;
    private GridPane chessBoard;
    private ListView<String> gameListView;
    private Label gameInfoLabel;
    private Label playerInfoLabel;
    private Label statusLabel;
    private Button passButton;
    private Button bombButton;
    private Button demoButton;
    
    private List<Game> games;
    private int currentGameIndex = 0;
    private boolean bombMode = false;
    private Game currentGame;
    private Button[][] chessCells;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        initializeGames();
        initializeComponents();
        setupLayout();
        updateDisplay();
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Chess Games - JavaFX版");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void initializeGames() {
        games = new ArrayList<>();
        games.add(new Game("Player1", "Player2", Game.GameMode.PEACE, 1));
        games.add(new ReversiGame("Player1", "Player2", 2));
        games.add(new GomokuGame("Player1", "Player2", 3));
        currentGame = games.get(currentGameIndex);
    }
    
    private void initializeComponents() {
        root = new BorderPane();
        chessBoard = new GridPane();
        gameListView = new ListView<>();
        gameInfoLabel = new Label("游戏信息");
        playerInfoLabel = new Label("玩家信息");
        statusLabel = new Label("状态: 就绪");
        
        passButton = new Button("Pass");
        bombButton = new Button("炸弹模式");
        demoButton = new Button("演示模式");
        
        // 设置按钮事件
        passButton.setOnAction(e -> handlePass());
        bombButton.setOnAction(e -> toggleBombMode());
        demoButton.setOnAction(e -> handleDemo());
        
        gameListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0 && newVal.intValue() < games.size()) {
                currentGameIndex = newVal.intValue();
                currentGame = games.get(currentGameIndex);
                updateDisplay();
            }
        });
    }
    
    private void setupLayout() {
        // 左侧棋盘区域
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(20));
        
        Label chessTitle = new Label("棋盘");
        chessTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setHgap(2);
        chessBoard.setVgap(2);
        chessBoard.setPadding(new Insets(20));
        chessBoard.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #888888; -fx-border-width: 2px;");
        
        leftPanel.getChildren().addAll(chessTitle, chessBoard);
        
        // 右侧信息区域
        HBox rightPanel = new HBox(20);
        rightPanel.setPadding(new Insets(20));
        
        // 游戏信息面板
        VBox gameInfoPanel = new VBox(10);
        gameInfoPanel.setPrefWidth(200);
        Label gameInfoTitle = new Label("游戏信息");
        gameInfoTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        gameInfoPanel.getChildren().addAll(
            gameInfoTitle,
            gameInfoLabel,
            playerInfoLabel,
            statusLabel
        );
        
        // 游戏列表面板
        VBox gameListPanel = new VBox(10);
        gameListPanel.setPrefWidth(200);
        Label gameListTitle = new Label("游戏列表");
        gameListTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        gameListView.setPrefHeight(200);
        gameListPanel.getChildren().addAll(gameListTitle, gameListView);
        
        // 控制面板
        VBox controlPanel = new VBox(10);
        controlPanel.setPrefWidth(200);
        Label controlTitle = new Label("操作");
        controlTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        controlPanel.getChildren().addAll(
            controlTitle,
            passButton,
            bombButton,
            demoButton,
            new Separator()
        );
        
        // 新建游戏按钮
        Button newPeaceButton = new Button("新建Peace");
        Button newReversiButton = new Button("新建Reversi");
        Button newGomokuButton = new Button("新建Gomoku");
        
        newPeaceButton.setOnAction(e -> addNewGame("peace"));
        newReversiButton.setOnAction(e -> addNewGame("reversi"));
        newGomokuButton.setOnAction(e -> addNewGame("gomoku"));
        
        controlPanel.getChildren().addAll(newPeaceButton, newReversiButton, newGomokuButton);
        
        rightPanel.getChildren().addAll(gameInfoPanel, gameListPanel, controlPanel);
        
        root.setLeft(leftPanel);
        root.setRight(rightPanel);
    }
    
    private void updateDisplay() {
        updateChessBoard();
        updateGameInfo();
        updateGameList();
        updateButtons();
    }
    
    private void updateChessBoard() {
        chessBoard.getChildren().clear();
        
        if (currentGame == null) return;
        
        int size = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getSize();
        boolean isGomoku = currentGame instanceof GomokuGame;
        
        chessCells = new Button[size][size];
        
        // 添加列标签
        for (int j = 0; j < size; j++) {
            String colLabel = isGomoku ? 
                GomokuBoard.getColLabel(j) : String.valueOf((char)('A' + j));
            Label label = new Label(colLabel);
            label.setAlignment(Pos.CENTER);
            label.setPrefSize(40, 30);
            label.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            chessBoard.add(label, j + 1, 0);
        }
        
        // 添加行标签和棋盘格子
        for (int i = 0; i < size; i++) {
            // 行标签
            String rowLabel = isGomoku ? 
                GomokuBoard.getRowLabel(i) : String.valueOf(i + 1);
            Label label = new Label(rowLabel);
            label.setAlignment(Pos.CENTER);
            label.setPrefSize(30, 40);
            label.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            chessBoard.add(label, 0, i + 1);
            
            // 棋盘格子
            for (int j = 0; j < size; j++) {
                Button cell = createChessCell(i, j);
                chessCells[i][j] = cell;
                chessBoard.add(cell, j + 1, i + 1);
            }
        }
    }
    
    private Button createChessCell(int row, int col) {
        Button cell = new Button();
        cell.setPrefSize(40, 40);
        cell.setStyle("-fx-background-color: #e6f3ff; -fx-border-color: #333333; -fx-border-width: 1px;");
        
        Piece piece = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getPiece(row, col);
        
        // 设置棋子显示
        if (piece == Piece.BLACK) {
            Circle circle = new Circle(15, Color.BLACK);
            cell.setGraphic(circle);
            cell.setText("");
        } else if (piece == Piece.WHITE) {
            Circle circle = new Circle(15, Color.WHITE);
            circle.setStroke(Color.BLACK);
            cell.setGraphic(circle);
            cell.setText("");
        } else if (piece == Piece.BLOCK) {
            cell.setText("■");
            cell.setStyle("-fx-background-color: #888888; -fx-text-fill: #444444;");
        } else if (piece == Piece.CRATER) {
            cell.setText("@");
            cell.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        } else if (currentGame instanceof ReversiGame && 
                   ((ReversiGame)currentGame).isValidMove(row, col, currentGame.getCurrentPlayer().getPieceType())) {
            cell.setText("+");
            cell.setStyle("-fx-background-color: #ccffcc; -fx-text-fill: green;");
        } else {
            cell.setText("");
            cell.setGraphic(null);
        }
        
        // 设置点击事件
        final int finalRow = row;
        final int finalCol = col;
        cell.setOnAction(e -> handleCellClick(finalRow, finalCol));
        
        return cell;
    }
    
    private void handleCellClick(int row, int col) {
        if (currentGame.isGameEnded()) {
            showAlert("游戏已结束", "当前游戏已结束，请切换到其他游戏", Alert.AlertType.INFORMATION);
            return;
        }
        
        if (bombMode && currentGame instanceof GomokuGame) {
            handleBombClick(row, col);
        } else {
            handleNormalMove(row, col);
        }
    }
    
    private void handleNormalMove(int row, int col) {
        boolean isGomoku = currentGame instanceof GomokuGame;
        String input = isGomoku ? 
            GomokuBoard.getRowLabel(row) + GomokuBoard.getColLabel(col) :
            (row + 1) + "" + (char)('A' + col);
        
        boolean success = currentGame.processMoveInput(input);
        if (success) {
            currentGame.switchPlayer();
            currentGame.checkGameEnd();
            updateDisplay();
            
            if (currentGame.isGameEnded()) {
                showGameResult();
            }
        }
    }
    
    private void handleBombClick(int row, int col) {
        GomokuGame gomoku = (GomokuGame) currentGame;
        String input = "@" + GomokuBoard.getRowLabel(row) + GomokuBoard.getColLabel(col);
        
        boolean success = gomoku.processMoveInput(input);
        if (success) {
            bombMode = false;
            bombButton.setText("炸弹模式");
            gomoku.switchPlayer();
            updateDisplay();
        }
    }
    
    private void updateGameInfo() {
        if (currentGame == null) return;
        
        StringBuilder info = new StringBuilder();
        info.append("游戏#").append(currentGame.getGameId())
            .append(" (").append(currentGame.getGameMode().getName()).append(")\n");
        
        if (currentGame instanceof GomokuGame) {
            GomokuGame gomoku = (GomokuGame) currentGame;
            info.append("当前回合: ").append(gomoku.getCurrentRound()).append("\n");
        }
        gameInfoLabel.setText(info.toString());
        
        // 玩家信息
        StringBuilder playerInfo = new StringBuilder();
        String currentSymbol = currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? "●" : "○";
        
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "")
                     .append(" 得分: ").append(reversi.countPieces(Piece.BLACK)).append("\n");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "")
                     .append(" 得分: ").append(reversi.countPieces(Piece.WHITE));
        } else if (currentGame instanceof GomokuGame) {
            GomokuGame gomoku = (GomokuGame) currentGame;
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "")
                     .append(" 炸弹: ").append(gomoku.getBlackBombs()).append("\n");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "")
                     .append(" 炸弹: ").append(gomoku.getWhiteBombs());
        } else {
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "").append("\n");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "");
        }
        playerInfoLabel.setText(playerInfo.toString());
    }
    
    private void updateGameList() {
        gameListView.getItems().clear();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            String item = (i + 1) + ". " + game.getGameMode().getName();
            if (i == currentGameIndex) {
                item += " (当前)";
            }
            gameListView.getItems().add(item);
        }
        gameListView.getSelectionModel().select(currentGameIndex);
    }
    
    private void updateButtons() {
        passButton.setVisible(currentGame instanceof ReversiGame);
        bombButton.setVisible(currentGame instanceof GomokuGame);
        demoButton.setVisible(currentGame instanceof GomokuGame);
    }
    
    private void handlePass() {
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            if (!reversi.hasValidMove(currentGame.getCurrentPlayer())) {
                currentGame.switchPlayer();
                updateDisplay();
            } else {
                showAlert("无法Pass", "当前有合法落子位置，不能Pass", Alert.AlertType.WARNING);
            }
        }
    }
    
    private void toggleBombMode() {
        if (currentGame instanceof GomokuGame) {
            bombMode = !bombMode;
            bombButton.setText(bombMode ? "取消炸弹" : "炸弹模式");
            statusLabel.setText(bombMode ? "状态: 炸弹模式 - 请选择要炸掉的位置" : "状态: 就绪");
        }
    }
    
    private void handleDemo() {
        if (currentGame instanceof GomokuGame) {
            showAlert("演示模式", "演示模式功能待实现", Alert.AlertType.INFORMATION);
        }
    }
    
    private void addNewGame(String gameType) {
        Game newGame;
        int newId = games.size() + 1;
        
        switch (gameType.toLowerCase()) {
            case "peace":
                newGame = new Game("Player1", "Player2", Game.GameMode.PEACE, newId);
                break;
            case "reversi":
                newGame = new ReversiGame("Player1", "Player2", newId);
                break;
            case "gomoku":
                newGame = new GomokuGame("Player1", "Player2", newId);
                break;
            default:
                return;
        }
        
        games.add(newGame);
        updateGameList();
    }
    
    private void showGameResult() {
        StringBuilder result = new StringBuilder("游戏结束！\n");
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            int blackCount = reversi.countPieces(Piece.BLACK);
            int whiteCount = reversi.countPieces(Piece.WHITE);
            result.append("玩家[").append(currentGame.getPlayer1().getName()).append("] 得分: ").append(blackCount).append("\n");
            result.append("玩家[").append(currentGame.getPlayer2().getName()).append("] 得分: ").append(whiteCount).append("\n");
            
            if (blackCount > whiteCount) {
                result.append("玩家[").append(currentGame.getPlayer1().getName()).append("]获胜！");
            } else if (whiteCount > blackCount) {
                result.append("玩家[").append(currentGame.getPlayer2().getName()).append("]获胜！");
            } else {
                result.append("游戏平局！");
            }
        } else {
            result.append("游戏结束");
        }
        
        showAlert("游戏结果", result.toString(), Alert.AlertType.INFORMATION);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 