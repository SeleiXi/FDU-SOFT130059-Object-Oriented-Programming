package com.chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.chess.service.Game;
import com.chess.service.ReversiGame;
import com.chess.service.GomokuGame;
import com.chess.entity.Piece;
import com.chess.entity.GomokuBoard;

public class SimpleChessGUI extends JFrame {
    
    private JPanel chessBoardPanel;
    private JList<String> gameList;
    private DefaultListModel<String> gameListModel;
    private JLabel gameInfoLabel;
    private JLabel playerInfoLabel;
    private JLabel statusLabel;
    private JButton passButton;
    private JButton bombButton;
    private JButton demoButton;
    
    private List<Game> games;
    private int currentGameIndex = 0;
    private boolean bombMode = false;
    private Game currentGame;
    private JButton[][] chessCells;
    
    public SimpleChessGUI() {
        initializeGames();
        initializeComponents();
        setupLayout();
        updateDisplay();
        
        setTitle("Chess Games - Swing版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initializeGames() {
        games = new ArrayList<>();
        games.add(new Game("Player1", "Player2", Game.GameMode.PEACE, 1));
        games.add(new ReversiGame("Player1", "Player2", 2));
        games.add(new GomokuGame("Player1", "Player2", 3));
        currentGame = games.get(currentGameIndex);
    }
    
    private void initializeComponents() {
        chessBoardPanel = new JPanel();
        gameListModel = new DefaultListModel<>();
        gameList = new JList<>(gameListModel);
        gameInfoLabel = new JLabel("<html>游戏信息</html>");
        playerInfoLabel = new JLabel("<html>玩家信息</html>");
        statusLabel = new JLabel("状态: 就绪");
        
        passButton = new JButton("Pass");
        bombButton = new JButton("炸弹模式");
        demoButton = new JButton("演示模式");
        
        // 设置按钮事件
        passButton.addActionListener(e -> handlePass());
        bombButton.addActionListener(e -> toggleBombMode());
        demoButton.addActionListener(e -> handleDemo());
        
        gameList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = gameList.getSelectedIndex();
                if (selected >= 0 && selected < games.size()) {
                    currentGameIndex = selected;
                    currentGame = games.get(currentGameIndex);
                    updateDisplay();
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 左侧棋盘区域
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("棋盘"));
        leftPanel.add(chessBoardPanel, BorderLayout.CENTER);
        
        // 右侧信息区域
        JPanel rightPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 游戏信息面板
        JPanel gameInfoPanel = new JPanel(new BorderLayout());
        gameInfoPanel.setBorder(BorderFactory.createTitledBorder("游戏信息"));
        JPanel infoContainer = new JPanel(new GridLayout(3, 1, 5, 5));
        infoContainer.add(gameInfoLabel);
        infoContainer.add(playerInfoLabel);
        infoContainer.add(statusLabel);
        gameInfoPanel.add(infoContainer, BorderLayout.NORTH);
        
        // 游戏列表面板
        JPanel gameListPanel = new JPanel(new BorderLayout());
        gameListPanel.setBorder(BorderFactory.createTitledBorder("游戏列表"));
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(gameList);
        listScroll.setPreferredSize(new Dimension(200, 200));
        gameListPanel.add(listScroll, BorderLayout.CENTER);
        
        // 控制面板
        JPanel controlPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("操作"));
        controlPanel.add(passButton);
        controlPanel.add(bombButton);
        controlPanel.add(demoButton);
        controlPanel.add(new JSeparator());
        
        // 新建游戏按钮
        JButton newPeaceButton = new JButton("新建Peace");
        JButton newReversiButton = new JButton("新建Reversi");
        JButton newGomokuButton = new JButton("新建Gomoku");
        
        newPeaceButton.addActionListener(e -> addNewGame("peace"));
        newReversiButton.addActionListener(e -> addNewGame("reversi"));
        newGomokuButton.addActionListener(e -> addNewGame("gomoku"));
        
        controlPanel.add(newPeaceButton);
        controlPanel.add(newReversiButton);
        controlPanel.add(newGomokuButton);
        
        rightPanel.add(gameInfoPanel);
        rightPanel.add(gameListPanel);
        rightPanel.add(controlPanel);
        
        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void updateDisplay() {
        updateChessBoard();
        updateGameInfo();
        updateGameList();
        updateButtons();
    }
    
    private void updateChessBoard() {
        chessBoardPanel.removeAll();
        
        if (currentGame == null) {
            chessBoardPanel.revalidate();
            chessBoardPanel.repaint();
            return;
        }
        
        int size = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getSize();
        boolean isGomoku = currentGame instanceof GomokuGame;
        
        chessBoardPanel.setLayout(new GridLayout(size + 1, size + 1, 1, 1));
        chessCells = new JButton[size][size];
        
        // 添加空白角落
        chessBoardPanel.add(new JLabel(""));
        
        // 添加列标签
        for (int j = 0; j < size; j++) {
            String colLabel = isGomoku ? 
                GomokuBoard.getColLabel(j) : String.valueOf((char)('A' + j));
            JLabel label = new JLabel(colLabel, SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 12));
            chessBoardPanel.add(label);
        }
        
        // 添加行和棋盘格子
        for (int i = 0; i < size; i++) {
            // 行标签
            String rowLabel = isGomoku ? 
                GomokuBoard.getRowLabel(i) : String.valueOf(i + 1);
            JLabel label = new JLabel(rowLabel, SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 12));
            chessBoardPanel.add(label);
            
            // 棋盘格子
            for (int j = 0; j < size; j++) {
                JButton cell = createChessCell(i, j);
                chessCells[i][j] = cell;
                chessBoardPanel.add(cell);
            }
        }
        
        chessBoardPanel.revalidate();
        chessBoardPanel.repaint();
    }
    
    private JButton createChessCell(int row, int col) {
        JButton cell = new JButton();
        cell.setPreferredSize(new Dimension(40, 40));
        cell.setBackground(Color.LIGHT_GRAY);
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        Piece piece = currentGame.getBoards()[currentGame.getCurrentBoardIndex()].getPiece(row, col);
        
        // 设置棋子显示
        if (piece == Piece.BLACK) {
            cell.setText("●");
            cell.setForeground(Color.BLACK);
        } else if (piece == Piece.WHITE) {
            cell.setText("○");
            cell.setForeground(Color.BLACK);
        } else if (piece == Piece.BLOCK) {
            cell.setText("■");
            cell.setForeground(Color.DARK_GRAY);
            cell.setBackground(Color.GRAY);
        } else if (piece == Piece.CRATER) {
            cell.setText("@");
            cell.setForeground(Color.RED);
        } else if (currentGame instanceof ReversiGame && 
                   ((ReversiGame)currentGame).isValidMove(row, col, currentGame.getCurrentPlayer().getPieceType())) {
            cell.setText("+");
            cell.setForeground(Color.GREEN);
        } else {
            cell.setText("");
        }
        
        // 设置点击事件
        final int finalRow = row;
        final int finalCol = col;
        cell.addActionListener(e -> handleCellClick(finalRow, finalCol));
        
        return cell;
    }
    
    private void handleCellClick(int row, int col) {
        if (currentGame.isGameEnded()) {
            JOptionPane.showMessageDialog(this, "当前游戏已结束，请切换到其他游戏", "游戏已结束", JOptionPane.INFORMATION_MESSAGE);
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
        
        StringBuilder info = new StringBuilder("<html>");
        info.append("游戏#").append(currentGame.getGameId())
            .append(" (").append(currentGame.getGameMode().getName()).append(")<br>");
        
        if (currentGame instanceof GomokuGame) {
            GomokuGame gomoku = (GomokuGame) currentGame;
            info.append("当前回合: ").append(gomoku.getCurrentRound()).append("<br>");
        }
        info.append("</html>");
        gameInfoLabel.setText(info.toString());
        
        // 玩家信息
        StringBuilder playerInfo = new StringBuilder("<html>");
        String currentSymbol = currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? "●" : "○";
        
        if (currentGame instanceof ReversiGame) {
            ReversiGame reversi = (ReversiGame) currentGame;
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "")
                     .append(" 得分: ").append(reversi.countPieces(Piece.BLACK)).append("<br>");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "")
                     .append(" 得分: ").append(reversi.countPieces(Piece.WHITE));
        } else if (currentGame instanceof GomokuGame) {
            GomokuGame gomoku = (GomokuGame) currentGame;
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "")
                     .append(" 炸弹: ").append(gomoku.getBlackBombs()).append("<br>");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "")
                     .append(" 炸弹: ").append(gomoku.getWhiteBombs());
        } else {
            playerInfo.append("玩家[").append(currentGame.getPlayer1().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer1() ? currentSymbol : "").append("<br>");
            playerInfo.append("玩家[").append(currentGame.getPlayer2().getName()).append("] ")
                     .append(currentGame.getCurrentPlayer() == currentGame.getPlayer2() ? currentSymbol : "");
        }
        playerInfo.append("</html>");
        playerInfoLabel.setText(playerInfo.toString());
    }
    
    private void updateGameList() {
        gameListModel.clear();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            String item = (i + 1) + ". " + game.getGameMode().getName();
            if (i == currentGameIndex) {
                item += " (当前)";
            }
            gameListModel.addElement(item);
        }
        // 暂时移除监听器以避免无限递归
        javax.swing.event.ListSelectionListener[] listeners = gameList.getListSelectionListeners();
        for (javax.swing.event.ListSelectionListener listener : listeners) {
            gameList.removeListSelectionListener(listener);
        }
        gameList.setSelectedIndex(currentGameIndex);
        // 重新添加监听器
        for (javax.swing.event.ListSelectionListener listener : listeners) {
            gameList.addListSelectionListener(listener);
        }
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
                JOptionPane.showMessageDialog(this, "当前有合法落子位置，不能Pass", "无法Pass", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "演示模式功能待实现", "演示模式", JOptionPane.INFORMATION_MESSAGE);
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
        
        JOptionPane.showMessageDialog(this, result.toString(), "游戏结果", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleChessGUI().setVisible(true);
        });
    }
} 