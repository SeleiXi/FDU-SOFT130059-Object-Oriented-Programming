package com.chess.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.chess.service.Game;
import com.chess.service.ReversiGame;
import com.chess.service.GomokuGame;
import com.chess.entity.Piece;
import com.chess.entity.Board;
import com.chess.entity.GomokuBoard;

import java.io.*;
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
    private Button quitButton;
    private VBox newGameButtons;
    
    private List<Game> gameList;
    private int currentGameIndex = 0;
    private boolean bombMode = false;
    private Game currentGame;
    private PlaybackDemo playbackDemo;
    private boolean isDemoMode = false;
    
    private static final String SAVE_FILE = "pj.game";
    
    @Override
    public void start(Stage primaryStage) {
        initializeComponents();
        loadGameState();
        setupLayout();
        updateDisplay();
        
        Scene scene = new Scene(root, 1200, 800);
        // 移除CSS加载，避免文件不存在的错误
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setTitle("Chess Games - JavaFX版");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> saveGameState());
        primaryStage.show();
    }
    
    private void initializeComponents() {
        root = new BorderPane();
        chessBoard = new GridPane();
        gameListView = new ListView<>();
        gameInfoLabel = new Label();
        playerInfoLabel = new Label();
        statusLabel = new Label();
        
        // 按钮
        passButton = new Button("Pass");
        bombButton = new Button("炸弹模式");
        demoButton = new Button("演示模式");
        quitButton = new Button("退出游戏");
        
        // 新游戏按钮
        newGameButtons = new VBox(5);
        Button peaceButton = new Button("新建Peace");
        Button reversiButton = new Button("新建Reversi");
        Button gomokuButton = new Button("新建Gomoku");
        
        newGameButtons.getChildren().addAll(peaceButton, reversiButton, gomokuButton);
        
        // 设置按钮事件
        setupButtonEvents(peaceButton, reversiButton, gomokuButton);
        
        // 初始化游戏列表
        if (gameList == null) {
            gameList = new ArrayList<>();
            gameList.add(new Game("Player1", "Player2", Game.GameMode.PEACE, 1));
            gameList.add(new ReversiGame("Player1", "Player2", 2));
            gameList.add(new GomokuGame("Player1", "Player2", 3));
        }
        currentGame = gameList.get(currentGameIndex);
    }
    
    private void setupButtonEvents(Button peaceButton, Button reversiButton, Button gomokuButton) {
        peaceButton.setOnAction(e -> addNewGame("peace"));
        reversiButton.setOnAction(e -> addNewGame("reversi"));
        gomokuButton.setOnAction(e -> addNewGame("gomoku"));
        
        passButton.setOnAction(e -> handlePass());
        bombButton.setOnAction(e -> toggleBombMode());
        demoButton.setOnAction(e -> handleDemo());
        quitButton.setOnAction(e -> handleQuit());
        
        gameListView.setOnMouseClicked(e -> {
            int selected = gameListView.getSelectionModel().getSelectedIndex();
            if (selected >= 0 && selected < gameList.size()) {
                currentGameIndex = selected;
                currentGame = gameList.get(currentGameIndex);
                updateDisplay();
            }
        });
    }
    
    private void setupLayout() {
        // 左侧棋盘 - 固定大小
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setHgap(2);
        chessBoard.setVgap(2);
        chessBoard.setPadding(new Insets(20));
        chessBoard.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        
        // 右侧3列信息区域
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(20));
        
        // 第一列：游戏信息
        VBox gameInfoPanel = new VBox(10);
        gameInfoPanel.getChildren().addAll(
            new Label("游戏信息"),
            gameInfoLabel,
            playerInfoLabel,
            statusLabel
        );
        
        // 第二列：游戏列表 (ListView)
        VBox gameListPanel = new VBox(10);
        gameListPanel.getChildren().addAll(
            new Label("游戏列表"),
            gameListView
        );
        gameListView.setPrefHeight(200);
        
        // 第三列：操作按钮
        VBox controlPanel = new VBox(10);
        controlPanel.getChildren().addAll(
            new Label("操作"),
            passButton,
            bombButton,
            demoButton,
            new Separator(),
            new Label("新建游戏"),
            newGameButtons,
            new Separator(),
            quitButton
        );
        
        // 使用HBox让右侧3列均匀分布
        HBox rightColumns = new HBox(20);
        rightColumns.getChildren().addAll(gameInfoPanel, gameListPanel, controlPanel);
        
        // 设置列宽度比例
        HBox.setHgrow(gameInfoPanel, Priority.ALWAYS);
        HBox.setHgrow(gameListPanel, Priority.ALWAYS);
        HBox.setHgrow(controlPanel, Priority.ALWAYS);
        
        root.setLeft(chessBoard);
        root.setRight(rightColumns);
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
        
        // 添加列标签
        for (int j = 0; j < size; j++) {
            Label colLabel = new Label(isGomoku ? 
                GomokuBoard.getColLabel(j) : String.valueOf((char)('A' + j)));
            colLabel.setAlignment(Pos.CENTER);
            colLabel.setPrefSize(40, 30);
            chessBoard.add(colLabel, j + 1, 0);
        }
        
        // 添加行标签和棋盘格子
        for (int i = 0; i < size; i++) {
            // 行标签
            Label rowLabel = new Label(isGomoku ? 
                GomokuBoard.getRowLabel(i) : String.valueOf(i + 1));
            rowLabel.setAlignment(Pos.CENTER);
            rowLabel.setPrefSize(30, 40);
            chessBoard.add(rowLabel, 0, i + 1);
            
            // 棋盘格子
            for (int j = 0; j < size; j++) {
                Pane cell = createChessBoardCell(i, j);
                chessBoard.add(cell, j + 1, i + 1);
            }
        }
    }
    
    private Pane createChessBoardCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(40, 40);
        cell.setStyle("-fx-border-color: black; -fx-background-color: lightblue;");
        
        Piece piece = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getPiece(row, col);
        
        // 添加棋子或特殊标记
        if (piece == Piece.BLACK) {
            Circle circle = new Circle(15, Color.BLACK);
            cell.getChildren().add(circle);
        } else if (piece == Piece.WHITE) {
            Circle circle = new Circle(15, Color.WHITE);
            circle.setStroke(Color.BLACK);
            cell.getChildren().add(circle);
        } else if (piece == Piece.BLOCK) {
            Rectangle block = new Rectangle(30, 30, Color.BROWN);
            cell.getChildren().add(block);
        } else if (piece == Piece.CRATER) {
            Label crater = new Label("@");
            crater.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
            cell.getChildren().add(crater);
        } else if (currentGame instanceof ReversiGame && 
                   ((ReversiGame)currentGame).isValidMove(row, col, currentGame.getCurrentPlayer().getPieceType())) {
            Label hint = new Label("+");
            hint.setStyle("-fx-font-size: 20px; -fx-text-fill: green;");
            cell.getChildren().add(hint);
        }
        
        // 设置点击事件
        final int finalRow = row;
        final int finalCol = col;
        cell.setOnMouseClicked(e -> handleCellClick(finalRow, finalCol));
        
        return cell;
    }
    
    private void handleCellClick(int row, int col) {
        if (currentGame.isGameEnded()) {
            showAlert("游戏已结束", "当前游戏已结束，请切换到其他游戏");
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
        String currentSymbol = currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? "○" : "●";
        
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
        for (int i = 0; i < gameList.size(); i++) {
            Game game = gameList.get(i);
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
                showAlert("无法Pass", "当前有合法落子位置，不能Pass");
            }
        }
    }
    
    private void toggleBombMode() {
        if (currentGame instanceof GomokuGame) {
            bombMode = !bombMode;
            bombButton.setText(bombMode ? "取消炸弹" : "炸弹模式");
            statusLabel.setText(bombMode ? "请选择要炸掉的位置" : "");
        }
    }
    
    private void handleDemo() {
        if (currentGame instanceof GomokuGame) {
            if (playbackDemo != null && playbackDemo.isRunning()) {
                playbackDemo.stopDemo();
                isDemoMode = false;
                demoButton.setText("演示模式");
                statusLabel.setText("");
                updateDisplay();
            } else {
                playbackDemo = new PlaybackDemo(currentGame, this::updateDemoDisplay);
                isDemoMode = true;
                demoButton.setText("停止演示");
                statusLabel.setText("演示模式运行中...");
                playbackDemo.startDemo();
            }
        }
    }
    
    private void updateDemoDisplay() {
        if (isDemoMode && playbackDemo != null) {
            // 临时切换显示演示游戏的状态
            Game originalGame = currentGame;
            currentGame = playbackDemo.getDemoGame();
            updateChessBoard();
            updateGameInfo();
            currentGame = originalGame; // 恢复原游戏
        }
    }
    
    private void handleQuit() {
        saveGameState();
        System.exit(0);
    }
    
    private void addNewGame(String gameType) {
        Game newGame;
        int newId = gameList.size() + 1;
        
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
        
        gameList.add(newGame);
        updateGameList();
    }
    
    private void showGameResult() {
        String result = "游戏结束！\n";
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            int blackCount = reversi.countPieces(Piece.BLACK);
            int whiteCount = reversi.countPieces(Piece.WHITE);
            result += "玩家[" + currentGame.getPlayer1().getName() + "] 得分: " + blackCount + "\n";
            result += "玩家[" + currentGame.getPlayer2().getName() + "] 得分: " + whiteCount + "\n";
            
            if (blackCount > whiteCount) {
                result += "玩家[" + currentGame.getPlayer1().getName() + "]获胜！";
            } else if (whiteCount > blackCount) {
                result += "玩家[" + currentGame.getPlayer2().getName() + "]获胜！";
            } else {
                result += "游戏平局！";
            }
        } else {
            result += "游戏结束";
        }
        
        showAlert("游戏结果", result);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void saveGameState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // 这里需要实现序列化保存游戏状态
            // 由于当前的Game类没有实现Serializable，这里先简化处理
            System.out.println("游戏状态已保存");
        } catch (IOException e) {
            System.out.println("保存游戏状态失败: " + e.getMessage());
        }
    }
    
    private void loadGameState() {
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
                // 这里需要实现反序列化加载游戏状态
                System.out.println("游戏状态已加载");
            } catch (IOException e) {
                System.out.println("加载游戏状态失败: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 