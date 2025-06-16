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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.chess.service.Game;
import com.chess.service.ReversiGame;
import com.chess.service.GomokuGame;
import com.chess.entity.Piece;
import com.chess.entity.GomokuBoard;
import com.chess.gui.GameState;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

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
    private TextArea logArea;
    
    private List<Game> games;
    private int currentGameIndex = 0;
    private boolean bombMode = false;
    private Game currentGame;
    private Button[][] chessCells;
    private Stage primaryStage;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeComponents();
        initializeGames();
        setupLayout();
        updateDisplay();
        logMessage("应用程序启动成功");
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Chess Games - JavaFX版");
        primaryStage.setScene(scene);
        
        // 设置窗口最小尺寸，确保所有元素都能显示
        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(800);
        
        // 设置窗口关闭时自动保存游戏状态
        primaryStage.setOnCloseRequest(event -> {
            try {
                GameState.saveGameState(games, currentGameIndex);
                System.out.println("程序退出，游戏状态已保存到 pj.game");
            } catch (Exception e) {
                System.out.println("保存游戏状态失败: " + e.getMessage());
            }
        });
        
        primaryStage.show();
    }
    
    private void initializeGames() {
        // 尝试加载保存的游戏状态
        GameState.GameStateData savedData = GameState.loadGameState();
        if (savedData != null && !savedData.games.isEmpty()) {
            // 加载成功，使用保存的状态
            this.games = savedData.games;
            this.currentGameIndex = savedData.currentGameIndex;
            // 确保索引在有效范围内
            if (currentGameIndex >= games.size()) {
                currentGameIndex = games.size() - 1;
            }
            currentGame = games.get(currentGameIndex);
            logMessage("已加载上次保存的游戏进度，共 " + games.size() + " 个游戏");
        } else {
            // 没有保存的状态或加载失败，创建默认游戏
            games = new ArrayList<>();
            games.add(new Game("Player1", "Player2", Game.GameMode.PEACE, 1));
            games.add(new ReversiGame("Player1", "Player2", 2));
            games.add(new GomokuGame("Player1", "Player2", 3));
            currentGame = games.get(currentGameIndex);
            logMessage("创建了默认游戏，共 " + games.size() + " 个游戏");
        }
    }
    
    private void initializeComponents() {
        root = new BorderPane();
        chessBoard = new GridPane();
        gameListView = new ListView<>();
        gameInfoLabel = new Label("游戏信息");
        playerInfoLabel = new Label("玩家信息");
        statusLabel = new Label("状态: 就绪");
        logArea = new TextArea();
        
        // 设置日志区域属性
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(200);
        logArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 12px;");
        
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
                logMessage("切换到游戏 #" + currentGame.getGameId() + " (" + currentGame.getGameMode().getName() + ")");
                updateDisplay();
            }
        });
    }
    
    private void setupLayout() {
        // 左侧棋盘区域 - 固定大小
        VBox leftPanel = new VBox();
        leftPanel.setPadding(new Insets(20));
        leftPanel.setSpacing(10);
        leftPanel.setMinWidth(800);  // 增加宽度以容纳15x15棋盘
        leftPanel.setMaxWidth(800);  // 设置固定最大宽度
        leftPanel.setPrefWidth(800); // 设置固定首选宽度
        
        Label chessTitle = new Label("棋盘");
        chessTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        chessTitle.setAlignment(Pos.CENTER);
        chessTitle.setMaxWidth(Double.MAX_VALUE);
        
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setHgap(2);
        chessBoard.setVgap(2);
        chessBoard.setPadding(new Insets(20));
        chessBoard.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #888888; -fx-border-width: 2px;");
        
        // 棋盘容器，居中显示，固定大小
        VBox chessBoardContainer = new VBox();
        chessBoardContainer.setAlignment(Pos.CENTER);
        chessBoardContainer.getChildren().add(chessBoard);
        chessBoardContainer.setMinHeight(700);  // 增加高度以容纳15x15棋盘
        chessBoardContainer.setMaxHeight(700);  // 设置固定高度
        chessBoardContainer.setPrefHeight(700); // 设置固定高度
        
        leftPanel.getChildren().addAll(chessTitle, chessBoardContainer);
        
        // 右侧信息区域 - 自适应布局，占据剩余空间
        BorderPane rightPanel = new BorderPane();
        rightPanel.setPadding(new Insets(20));
        // 移除固定宽度设置，让它自适应
        
        // 顶部三列区域：游戏信息、游戏列表、操作
        HBox topSection = new HBox();
        topSection.setSpacing(15);
        topSection.setPadding(new Insets(0, 0, 15, 0));
        
        // 第一列：游戏信息 - 自适应宽度
        VBox gameInfoColumn = new VBox();
        gameInfoColumn.setSpacing(10);
        // 移除固定宽度，使用比例分配
        
        // 游戏信息面板
        VBox gameInfoPanel = new VBox();
        gameInfoPanel.setSpacing(10);
        Label gameInfoTitle = new Label("游戏信息");
        gameInfoTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        gameInfoTitle.setAlignment(Pos.CENTER);
        gameInfoTitle.setMaxWidth(Double.MAX_VALUE);
        
        // 使用ScrollPane包装信息标签，防止文本过长
        VBox infoContent = new VBox(5);
        infoContent.getChildren().addAll(gameInfoLabel, playerInfoLabel, statusLabel);
        ScrollPane infoScroll = new ScrollPane(infoContent);
        infoScroll.setFitToWidth(true);
        infoScroll.setPrefHeight(300);
        infoScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        infoScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        gameInfoPanel.getChildren().addAll(gameInfoTitle, infoScroll);
        gameInfoColumn.getChildren().add(gameInfoPanel);
        
        // 第二列：游戏列表 - 自适应宽度
        VBox gameListColumn = new VBox();
        gameListColumn.setSpacing(10);
        // 移除固定宽度，使用比例分配
        
        // 游戏列表面板
        VBox gameListPanel = new VBox();
        gameListPanel.setSpacing(10);
        Label gameListTitle = new Label("游戏列表");
        gameListTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        gameListTitle.setAlignment(Pos.CENTER);
        gameListTitle.setMaxWidth(Double.MAX_VALUE);
        gameListView.setPrefHeight(300);
        gameListPanel.getChildren().addAll(gameListTitle, gameListView);
        VBox.setVgrow(gameListView, Priority.ALWAYS);
        
        gameListColumn.getChildren().add(gameListPanel);
        
        // 第三列：控制按钮 - 自适应宽度
        VBox controlColumn = new VBox();
        controlColumn.setSpacing(10);
        // 移除固定宽度，使用比例分配
        
        // 控制面板
        VBox controlPanel = new VBox();
        controlPanel.setSpacing(10);
        Label controlTitle = new Label("操作");
        controlTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        controlTitle.setAlignment(Pos.CENTER);
        controlTitle.setMaxWidth(Double.MAX_VALUE);
        
        // 操作按钮容器
        VBox buttonContainer = new VBox(5);
        buttonContainer.getChildren().addAll(
            passButton,
            bombButton,
            demoButton,
            new Separator()
        );
        
        // 新建游戏按钮
        Button newPeaceButton = new Button("新建Peace");
        Button newReversiButton = new Button("新建Reversi");
        Button newGomokuButton = new Button("新建Gomoku");
        
        // 设置按钮宽度一致
        newPeaceButton.setMaxWidth(Double.MAX_VALUE);
        newReversiButton.setMaxWidth(Double.MAX_VALUE);
        newGomokuButton.setMaxWidth(Double.MAX_VALUE);
        passButton.setMaxWidth(Double.MAX_VALUE);
        bombButton.setMaxWidth(Double.MAX_VALUE);
        demoButton.setMaxWidth(Double.MAX_VALUE);
        
        newPeaceButton.setOnAction(e -> addNewGame("peace"));
        newReversiButton.setOnAction(e -> addNewGame("reversi"));
        newGomokuButton.setOnAction(e -> addNewGame("gomoku"));
        
        // 退出游戏按钮
        Button exitButton = new Button("退出游戏");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> {
            try {
                GameState.saveGameState(games, currentGameIndex);
                logMessage("程序退出，游戏状态已保存到 pj.game");
            } catch (Exception ex) {
                logMessage("保存游戏状态失败: " + ex.getMessage());
            }
            primaryStage.close();
        });
        
        buttonContainer.getChildren().addAll(
            newPeaceButton, 
            newReversiButton, 
            newGomokuButton,
            new Separator(),
            exitButton
        );
        
        // 将按钮容器放入ScrollPane
        ScrollPane buttonScroll = new ScrollPane(buttonContainer);
        buttonScroll.setFitToWidth(true);
        buttonScroll.setPrefHeight(300);
        buttonScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        buttonScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        controlPanel.getChildren().addAll(controlTitle, buttonScroll);
        
        controlColumn.getChildren().add(controlPanel);
        
        // 设置三列的增长策略 - 均匀分配可用空间
        HBox.setHgrow(gameInfoColumn, Priority.ALWAYS);
        HBox.setHgrow(gameListColumn, Priority.ALWAYS);
        HBox.setHgrow(controlColumn, Priority.ALWAYS);
        
        topSection.getChildren().addAll(gameInfoColumn, gameListColumn, controlColumn);
        
        rightPanel.setTop(topSection);
        
        // 日志信息面板 - 自适应宽度
        VBox logPanel = new VBox();
        logPanel.setSpacing(10);
        Label logTitle = new Label("日志信息");
        logTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        logTitle.setAlignment(Pos.CENTER);
        logTitle.setMaxWidth(Double.MAX_VALUE);
        
        ScrollPane logScroll = new ScrollPane(logArea);
        logScroll.setFitToWidth(true);
        logScroll.setPrefHeight(200);
        logScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        logPanel.getChildren().addAll(logTitle, logScroll);
        
        rightPanel.setCenter(logPanel);
        
        // 主布局 - 使用HBox实现固定左侧+自适应右侧
        HBox mainLayout = new HBox();
        mainLayout.setSpacing(10);
        
        // 设置左侧棋盘为固定大小，右侧信息区域为自适应
        HBox.setHgrow(leftPanel, Priority.NEVER);   // 左侧不增长，保持固定大小
        HBox.setHgrow(rightPanel, Priority.ALWAYS); // 右侧自适应增长，占据剩余空间
        
        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        
        // 将主布局放入BorderPane的中心
        root.setCenter(mainLayout);
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
        cell.setMinSize(40, 40);
        cell.setMaxSize(40, 40);
        cell.setStyle("-fx-background-color: #e6f3ff; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px;");
        
        Piece piece = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getPiece(row, col);
        
        // 设置棋子显示 - 统一使用文本，确保格子大小不变
        if (piece == Piece.BLACK) {
            cell.setText("●");
            cell.setStyle("-fx-background-color: #e6f3ff; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px; -fx-text-fill: #000000; -fx-font-weight: bold;");
        } else if (piece == Piece.WHITE) {
            cell.setText("●");
            cell.setStyle("-fx-background-color: #e6f3ff; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        } else if (piece == Piece.BLOCK) {
            cell.setText("■");
            cell.setStyle("-fx-background-color: #888888; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px; -fx-text-fill: #444444;");
        } else if (piece == Piece.CRATER) {
            cell.setText("@");
            cell.setStyle("-fx-background-color: #ffcccc; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px; -fx-text-fill: red; -fx-font-weight: bold;");
        } else if (currentGame instanceof ReversiGame && 
                   ((ReversiGame)currentGame).isValidMove(row, col, currentGame.getCurrentPlayer().getPieceType())) {
            cell.setText("+");
            cell.setStyle("-fx-background-color: #ccffcc; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px; -fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            cell.setText("");
            cell.setStyle("-fx-background-color: #e6f3ff; -fx-border-color: #333333; -fx-border-width: 1px; -fx-font-size: 18px;");
        }
        
        // 设置点击事件
        final int finalRow = row;
        final int finalCol = col;
        cell.setOnAction(e -> handleCellClick(finalRow, finalCol));
        
        return cell;
    }
    
    private void handleCellClick(int row, int col) {
        if (currentGame.isGameEnded()) {
            logMessage("当前游戏已结束，无法继续下棋");
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
        
        String playerName = currentGame.getCurrentPlayer().getName();
        logMessage("玩家 " + playerName + " 尝试在 " + input + " 位置落子");
        
        boolean success = currentGame.processMoveInput(input);
        if (success) {
            logMessage("玩家 " + playerName + " 在 " + input + " 位置落子成功");
            currentGame.switchPlayer();
            currentGame.checkGameEnd();
            updateDisplay();
            
            // 自动保存游戏状态
            saveCurrentState();
            
            if (currentGame.isGameEnded()) {
                logMessage("游戏结束！");
                showGameResult();
            }
        } else {
            logMessage("玩家 " + playerName + " 在 " + input + " 位置落子失败");
        }
    }
    
    private void handleBombClick(int row, int col) {
        GomokuGame gomoku = (GomokuGame) currentGame;
        String input = "@" + GomokuBoard.getRowLabel(row) + GomokuBoard.getColLabel(col);
        String playerName = currentGame.getCurrentPlayer().getName();
        
        logMessage("玩家 " + playerName + " 使用炸弹在 " + input.substring(1) + " 位置");
        
        boolean success = gomoku.processMoveInput(input);
        if (success) {
            logMessage("炸弹使用成功，切换到下一位玩家");
            bombMode = false;
            bombButton.setText("炸弹模式");
            gomoku.switchPlayer();
            updateDisplay();
            
            // 自动保存游戏状态
            saveCurrentState();
        } else {
            logMessage("炸弹使用失败 - 可能是无效位置或炸弹不足");
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
        demoButton.setVisible(true); // 演示模式支持所有游戏类型
    }
    
    private void handlePass() {
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            String playerName = currentGame.getCurrentPlayer().getName();
            
            if (!reversi.hasValidMove(currentGame.getCurrentPlayer())) {
                logMessage("玩家 " + playerName + " Pass - 无合法落子位置");
                currentGame.switchPlayer();
                updateDisplay();
                
                // 自动保存游戏状态
                saveCurrentState();
            } else {
                logMessage("玩家 " + playerName + " 尝试Pass失败 - 还有合法落子位置");
                showAlert("无法Pass", "当前有合法落子位置，不能Pass", Alert.AlertType.WARNING);
            }
        }
    }
    
    private void toggleBombMode() {
        if (currentGame instanceof GomokuGame) {
            bombMode = !bombMode;
            bombButton.setText(bombMode ? "取消炸弹" : "炸弹模式");
            statusLabel.setText(bombMode ? "状态: 炸弹模式 - 请选择要炸掉的位置" : "状态: 就绪");
            
            String playerName = currentGame.getCurrentPlayer().getName();
            if (bombMode) {
                logMessage("玩家 " + playerName + " 进入炸弹模式");
            } else {
                logMessage("玩家 " + playerName + " 退出炸弹模式");
            }
        }
    }
    
    private void handleDemo() {
       
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择演示脚本");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("脚本文件", "*.txt", "*.cmd", "*.script"),
                new FileChooser.ExtensionFilter("所有文件", "*.*")
            );
            
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                runPlayback(selectedFile.getAbsolutePath());
            }

    }
    
    /**
     * 执行演示脚本（Playback模式）
     * 支持所有游戏类型：Peace、Reversi、Gomoku
     * 
     * 脚本格式：每行一个命令
     * - Peace/Reversi游戏落子：数字+字母格式，如 "1A", "2B" 等
     * - Gomoku游戏落子：字母+数字格式，如 "A1", "B2" 等
     * - 炸弹命令（仅Gomoku）：@字母数字格式，如 "@A1", "@B2" 等
     * - Pass命令（仅Reversi）：pass
     * - 注释：# 开头的行会被忽略
     * 
     * @param filename 脚本文件路径
     */
    private void runPlayback(String filename) {
        // if (!(currentGame instanceof GomokuGame)) {
        //     logMessage("演示模式仅支持Gomoku游戏");
        //     showAlert("演示模式", "演示模式仅支持Gomoku游戏", Alert.AlertType.WARNING);
        //     return;
        // }
        
        // 在后台线程执行，避免阻塞UI
        CompletableFuture.runAsync(() -> {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename))) {
                javafx.application.Platform.runLater(() -> {
                    logMessage("开始执行playback命令，读取文件: " + filename);
                });
                
                String command;
                int lineNumber = 0;
                while ((command = reader.readLine()) != null) {
                    lineNumber++;
                    final int currentLineNumber = lineNumber;
                    command = command.trim();
                    
                    // 跳过空行和注释
                    if (command.isEmpty() || command.startsWith("#")) {
                        continue;
                    }
                    
                    // 检查游戏是否已结束
                    if (currentGame.isGameEnded()) {
                        javafx.application.Platform.runLater(() -> {
                            logMessage("演示结束：游戏已结束");
                        });
                        break;
                    }
                    
                    final String finalCommand = command;
                    
                    // 在UI线程中执行命令
                    javafx.application.Platform.runLater(() -> {
                        boolean commandExecuted = executeDemoCommand(finalCommand, currentLineNumber);
                        if (commandExecuted) {
                            updateDisplay();
                            saveCurrentState();
                        }
                    });
                    
                    // 延迟1秒，让用户看清楚
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    
                    // 检查游戏是否结束
                    if (currentGame.isGameEnded()) {
                        javafx.application.Platform.runLater(() -> {
                            logMessage("演示过程中游戏结束！");
                            showGameResult();
                        });
                        break;
                    }
                }
                
                javafx.application.Platform.runLater(() -> {
                    logMessage("playback命令执行完成");
                });
                
            } catch (java.io.IOException e) {
                javafx.application.Platform.runLater(() -> {
                    logMessage("读取文件时出错: " + e.getMessage());
                    showAlert("错误", "无法读取脚本文件: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        });
    }
    
    /**
     * 执行单个演示命令
     * @param command 要执行的命令
     * @param lineNumber 行号
     * @return 是否成功执行命令
     */
    private boolean executeDemoCommand(String command, int lineNumber) {
        String playerName = currentGame.getCurrentPlayer().getName();
        
        // 处理pass命令 - 仅适用于Reversi游戏
        if (command.equalsIgnoreCase("pass")) {
            if (currentGame instanceof ReversiGame) {
                ReversiGame reversi = (ReversiGame) currentGame;
                if (!reversi.hasValidMove(currentGame.getCurrentPlayer())) {
                    logMessage("第 " + lineNumber + " 行: 玩家 " + playerName + " Pass");
                    currentGame.switchPlayer();
                    return true;
                } else {
                    logMessage("第 " + lineNumber + " 行: 玩家 " + playerName + " Pass失败 - 有合法落子位置");
                    return false;
                }
            } else {
                logMessage("第 " + lineNumber + " 行: Pass命令仅在Reversi游戏中支持");
                return false;
            }
        }
        
        // 处理落子命令
        if (command.length() >= 2) {
            boolean success = false;
            
            if (command.startsWith("@")) {
                // 炸弹命令 - 仅适用于Gomoku游戏
                if (currentGame instanceof GomokuGame) {
                    GomokuGame gomoku = (GomokuGame) currentGame;
                    logMessage("第 " + lineNumber + " 行: 玩家 " + playerName + " 使用炸弹 " + command);
                    success = gomoku.processMoveInput(command);
                    if (success) {
                        logMessage("炸弹使用成功");
                    } else {
                        logMessage("炸弹使用失败");
                    }
                } else {
                    logMessage("第 " + lineNumber + " 行: 炸弹命令仅在Gomoku游戏中支持");
                    return false;
                }
            } else {
                // 普通落子命令 - 适用于所有游戏类型
                logMessage("第 " + lineNumber + " 行: 玩家 " + playerName + " 在 " + command + " 位置落子");
                success = currentGame.processMoveInput(command);
                if (success) {
                    logMessage("落子成功");
                } else {
                    logMessage("落子失败 - 可能是无效位置或位置已被占用");
                }
            }
            
            if (success) {
                currentGame.switchPlayer();
                currentGame.checkGameEnd();
                return true;
            }
        }
        
        logMessage("第 " + lineNumber + " 行: 无效命令 - " + command);
        return false;
    }
    
    private void addNewGame(String gameType) {
        Game newGame;
        int newId = games.size() + 1;
        
        switch (gameType.toLowerCase()) {
            case "peace":
                newGame = new Game("Player1", "Player2", Game.GameMode.PEACE, newId);
                logMessage("创建新的Peace游戏 #" + newId);
                break;
            case "reversi":
                newGame = new ReversiGame("Player1", "Player2", newId);
                logMessage("创建新的Reversi游戏 #" + newId);
                break;
            case "gomoku":
                newGame = new GomokuGame("Player1", "Player2", newId);
                logMessage("创建新的Gomoku游戏 #" + newId);
                break;
            default:
                logMessage("未知游戏类型: " + gameType);
                return;
        }
        
        games.add(newGame);
        updateGameList();
        
        // 自动保存游戏状态
        saveCurrentState();
    }
    
    private void showGameResult() {
        StringBuilder result = new StringBuilder("游戏结束！\n");
        StringBuilder logResult = new StringBuilder("游戏结束 - ");
        
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            int blackCount = reversi.countPieces(Piece.BLACK);
            int whiteCount = reversi.countPieces(Piece.WHITE);
            result.append("玩家[").append(currentGame.getPlayer1().getName()).append("] 得分: ").append(blackCount).append("\n");
            result.append("玩家[").append(currentGame.getPlayer2().getName()).append("] 得分: ").append(whiteCount).append("\n");
            
            if (blackCount > whiteCount) {
                result.append("玩家[").append(currentGame.getPlayer1().getName()).append("]获胜！");
                logResult.append("玩家[").append(currentGame.getPlayer1().getName()).append("]获胜 (").append(blackCount).append(" vs ").append(whiteCount).append(")");
            } else if (whiteCount > blackCount) {
                result.append("玩家[").append(currentGame.getPlayer2().getName()).append("]获胜！");
                logResult.append("玩家[").append(currentGame.getPlayer2().getName()).append("]获胜 (").append(whiteCount).append(" vs ").append(blackCount).append(")");
            } else {
                result.append("游戏平局！");
                logResult.append("平局 (").append(blackCount).append(" vs ").append(whiteCount).append(")");
            }
        } else if (currentGame instanceof GomokuGame) {
            result.append("游戏结束");
            logResult.append("Gomoku游戏结束");
        } else {
            result.append("游戏结束");
            logResult.append("Peace游戏结束");
        }
        
        logMessage(logResult.toString());
        showAlert("游戏结果", result.toString(), Alert.AlertType.INFORMATION);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // 添加日志记录方法
    private void logMessage(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logArea.appendText(logEntry);
        
        // 自动滚动到底部
        logArea.setScrollTop(Double.MAX_VALUE);
    }
    
    private void saveCurrentState() {
        try {
            GameState.saveGameState(games, currentGameIndex);
            // 不记录日志，避免在自动保存时产生过多日志信息
        } catch (Exception e) {
            logMessage("保存游戏状态失败: " + e.getMessage());
        }
    }
    
    private void saveCurrentStateWithLog() {
        try {
            GameState.saveGameState(games, currentGameIndex);
            logMessage("游戏状态已手动保存");
        } catch (Exception e) {
            logMessage("保存游戏状态失败: " + e.getMessage());
        }
    }
    
    private void loadSavedState() {
        GameState.GameStateData data = GameState.loadGameState();
        if (data != null && !data.games.isEmpty()) {
            this.games = data.games;
            this.currentGameIndex = data.currentGameIndex;
            // 确保索引在有效范围内
            if (currentGameIndex >= games.size()) {
                currentGameIndex = games.size() - 1;
            }
            this.currentGame = games.get(currentGameIndex);
            updateDisplay();
            logMessage("游戏状态已恢复，共加载 " + games.size() + " 个游戏");
        } else {
            logMessage("没有找到保存的游戏状态或文件损坏");
        }
    }
} 