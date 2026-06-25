// ui/ChessFrame.java
package ui;

import chess.*;
import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class ChessFrame extends JFrame {
    private String username;
    private ChessBoard chessBoard;
    private ChessAI chessAI;
    private ChessBoardPanel boardPanel;
    private JPanel infoPanel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JTextArea moveHistoryArea;
    private JButton newGameButton;
    private JButton backButton;
    
    private int selectedRow = -1, selectedCol = -1;
    private List<Move> possibleMoves = new ArrayList<>();
    private boolean playerIsWhite = true;
    private boolean isThinking = false;
    
    public ChessFrame(String username) {
        this.username = username;
        this.chessBoard = new ChessBoard();
        this.chessAI = new ChessAI(5); // Depth 5 for strong play
        
        initializeFrame();
        layoutComponents();
        startGame();
    }
    
    private void initializeFrame() {
        setTitle("Chess - " + username);
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 750, 30, 30));
    }
    
    private void layoutComponents() {
        GradientPanel mainPanel = new GradientPanel(UIUtils.BACKGROUND_START, UIUtils.BACKGROUND_END);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Chess board
        boardPanel = new ChessBoardPanel();
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setOpaque(false);
        boardContainer.add(boardPanel);
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        
        // Info panel
        infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);
        
        setContentPane(mainPanel);
        enableDrag(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("♔ Chess Arena");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        
        // Close button
        JLabel closeButton = new JLabel("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        closeButton.setForeground(UIUtils.TEXT_SECONDARY);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(UIUtils.ERROR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(UIUtils.TEXT_SECONDARY);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Player info
        JLabel playerLabel = new JLabel("♙ You (" + username + ")");
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playerLabel.setForeground(UIUtils.TEXT_PRIMARY);
        playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel aiLabel = new JLabel("♚ AI Opponent");
        aiLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        aiLabel.setForeground(UIUtils.TEXT_PRIMARY);
        aiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Turn indicator
        turnLabel = new JLabel("Your turn");
        turnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        turnLabel.setForeground(UIUtils.SUCCESS);
        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Status
        statusLabel = new JLabel("Game in progress");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(UIUtils.TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Move history
        JLabel historyLabel = new JLabel("Move History");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyLabel.setForeground(UIUtils.TEXT_PRIMARY);
        historyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        moveHistoryArea.setForeground(UIUtils.TEXT_SECONDARY);
        moveHistoryArea.setBackground(new Color(30, 41, 59));
        moveHistoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.SURFACE_LIGHT, 1));
        scrollPane.setPreferredSize(new Dimension(240, 200));
        scrollPane.setMaximumSize(new Dimension(240, 200));
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        
        // Buttons
        newGameButton = UIUtils.createModernButton("New Game", true);
        newGameButton.setMaximumSize(new Dimension(240, 45));
        newGameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        newGameButton.addActionListener(e -> resetGame());
        
        backButton = UIUtils.createModernButton("Back to Menu", false);
        backButton.setMaximumSize(new Dimension(240, 45));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            dispose();
            new WelcomeFrame(username).setVisible(true);
        });
        
        // Layout
        panel.add(playerLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(aiLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(turnLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(historyLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollPane);
        panel.add(Box.createVerticalGlue());
        panel.add(newGameButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);
        
        return panel;
    }
    
    private void startGame() {
        updateTurnIndicator();
    }
    
    private void resetGame() {
        chessBoard = new ChessBoard();
        selectedRow = selectedCol = -1;
        possibleMoves.clear();
        isThinking = false;
        moveHistoryArea.setText("");
        updateTurnIndicator();
        boardPanel.repaint();
    }
    
    private void handleSquareClick(int row, int col) {
        if (isThinking) return;
        
        boolean isPlayerTurn = (playerIsWhite && chessBoard.isWhiteToMove()) ||
                               (!playerIsWhite && !chessBoard.isWhiteToMove());
        
        if (!isPlayerTurn) return;
        
        if (selectedRow == -1) {
            // Select piece
            Piece piece = chessBoard.getPiece(row, col);
            if (piece != null && piece.isWhite == playerIsWhite) {
                selectedRow = row;
                selectedCol = col;
                possibleMoves = getMovesForSquare(row, col);
                boardPanel.repaint();
            }
        } else {
            // Try to make move
            Move selectedMove = findMove(selectedRow, selectedCol, row, col);
            
            if (selectedMove != null) {
                // Check for promotion
                if (selectedMove.piece.type == Piece.Type.PAWN) {
                    int promotionRow = playerIsWhite ? 0 : 7;
                    if (row == promotionRow) {
                        Piece.Type promotionType = showPromotionDialog();
                        selectedMove = selectedMove.withPromotion(new Piece(promotionType, playerIsWhite));
                    }
                }
                
                makePlayerMove(selectedMove);
            }
            
            // Deselect
            selectedRow = selectedCol = -1;
            possibleMoves.clear();
            boardPanel.repaint();
        }
    }
    
    private List<Move> getMovesForSquare(int row, int col) {
        List<Move> moves = new ArrayList<>();
        for (Move move : chessBoard.generateLegalMoves()) {
            if (move.fromRow == row && move.fromCol == col) {
                moves.add(move);
            }
        }
        return moves;
    }
    
    private Move findMove(int fromRow, int fromCol, int toRow, int toCol) {
        for (Move move : possibleMoves) {
            if (move.toRow == toRow && move.toCol == toCol) {
                return move;
            }
        }
        return null;
    }
    
    private Piece.Type showPromotionDialog() {
        String[] options = {"Queen ♕", "Rook ♖", "Bishop ♗", "Knight ♘"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose promotion piece:",
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 1: return Piece.Type.ROOK;
            case 2: return Piece.Type.BISHOP;
            case 3: return Piece.Type.KNIGHT;
            default: return Piece.Type.QUEEN;
        }
    }
    
    private void makePlayerMove(Move move) {
        chessBoard.makeMove(move);
        updateMoveHistory(move);
        boardPanel.repaint();
        
        if (checkGameEnd()) return;
        
        updateTurnIndicator();
        
        // AI's turn
        makeAIMove();
    }
    
    private void makeAIMove() {
        isThinking = true;
        turnLabel.setText("AI thinking...");
        turnLabel.setForeground(UIUtils.WARNING);
        
        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override
            protected Move doInBackground() {
                return chessAI.findBestMove(chessBoard);
            }
            
            @Override
            protected void done() {
                try {
                    Move aiMove = get();
                    if (aiMove != null) {
                        chessBoard.makeMove(aiMove);
                        updateMoveHistory(aiMove);
                        boardPanel.repaint();
                        
                        if (!checkGameEnd()) {
                            updateTurnIndicator();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isThinking = false;
            }
        };
        worker.execute();
    }
    
    private void updateTurnIndicator() {
        boolean isPlayerTurn = (playerIsWhite && chessBoard.isWhiteToMove()) ||
                               (!playerIsWhite && !chessBoard.isWhiteToMove());
        
        if (isPlayerTurn) {
            turnLabel.setText("Your turn");
            turnLabel.setForeground(UIUtils.SUCCESS);
        } else {
            turnLabel.setText("AI's turn");
            turnLabel.setForeground(UIUtils.WARNING);
        }
        
        if (chessBoard.isKingInCheck(chessBoard.isWhiteToMove())) {
            statusLabel.setText("Check!");
            statusLabel.setForeground(UIUtils.ERROR);
        } else {
            statusLabel.setText("Game in progress");
            statusLabel.setForeground(UIUtils.TEXT_SECONDARY);
        }
    }
    
    private void updateMoveHistory(Move move) {
        List<Move> history = chessBoard.getMoveHistory();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) {
                sb.append((i / 2 + 1)).append(". ");
            }
            sb.append(history.get(i).toString()).append(" ");
            if (i % 2 == 1) {
                sb.append("\n");
            }
        }
        
        moveHistoryArea.setText(sb.toString());
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
    }
    
    private boolean checkGameEnd() {
        if (chessBoard.isCheckmate()) {
            boolean playerWins = (playerIsWhite && !chessBoard.isWhiteToMove()) ||
                                 (!playerIsWhite && chessBoard.isWhiteToMove());
            
            statusLabel.setText(playerWins ? "Checkmate! You win!" : "Checkmate! AI wins!");
            statusLabel.setForeground(playerWins ? UIUtils.SUCCESS : UIUtils.ERROR);
            turnLabel.setText("Game Over");
            turnLabel.setForeground(UIUtils.TEXT_SECONDARY);
            
            showGameEndDialog(playerWins ? "Congratulations!" : "Better luck next time!", 
                             playerWins ? "You won by checkmate!" : "AI won by checkmate!");
            return true;
        }
        
        if (chessBoard.isStalemate()) {
            statusLabel.setText("Stalemate! Draw!");
            statusLabel.setForeground(UIUtils.WARNING);
            turnLabel.setText("Game Over");
            turnLabel.setForeground(UIUtils.TEXT_SECONDARY);
            showGameEndDialog("Draw!", "The game ended in stalemate.");
            return true;
        }
        
        if (chessBoard.isDraw()) {
            statusLabel.setText("Draw!");
            statusLabel.setForeground(UIUtils.WARNING);
            turnLabel.setText("Game Over");
            turnLabel.setForeground(UIUtils.TEXT_SECONDARY);
            showGameEndDialog("Draw!", "The game ended in a draw.");
            return true;
        }
        
        return false;
    }
    
    private void showGameEndDialog(String title, String message) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 350, 200, 20, 20));
        
        GradientPanel panel = new GradientPanel(UIUtils.BACKGROUND_START, UIUtils.BACKGROUND_END);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(UIUtils.TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton playAgainButton = UIUtils.createModernButton("Play Again", true);
        playAgainButton.setMaximumSize(new Dimension(200, 45));
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.addActionListener(e -> {
            dialog.dispose();
            resetGame();
        });
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(playAgainButton);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    private void enableDrag(JPanel panel) {
        final Point[] dragPoint = {null};
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point location = getLocation();
                    setLocation(location.x + e.getX() - dragPoint[0].x,
                               location.y + e.getY() - dragPoint[0].y);
                }
            }
        });
    }
    
    // Chess board panel
    private class ChessBoardPanel extends JPanel {
        private static final int SQUARE_SIZE = 75;
        private static final int BOARD_SIZE = SQUARE_SIZE * 8;
        
        public ChessBoardPanel() {
            setPreferredSize(new Dimension(BOARD_SIZE + 40, BOARD_SIZE + 40));
            setOpaque(false);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = (e.getX() - 20) / SQUARE_SIZE;
                    int row = (e.getY() - 20) / SQUARE_SIZE;
                    
                    if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                        handleSquareClick(row, col);
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            
            int offsetX = 20;
            int offsetY = 20;
            
            // Draw board shadow
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fill(new RoundRectangle2D.Float(offsetX + 5, offsetY + 5, BOARD_SIZE, BOARD_SIZE, 8, 8));
            
            // Draw board border
            g2.setColor(UIUtils.SURFACE);
            g2.fill(new RoundRectangle2D.Float(offsetX - 3, offsetY - 3, BOARD_SIZE + 6, BOARD_SIZE + 6, 8, 8));
            
            // Draw squares
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int x = offsetX + col * SQUARE_SIZE;
                    int y = offsetY + row * SQUARE_SIZE;
                    
                    // Base color
                    boolean isLight = (row + col) % 2 == 0;
                    g2.setColor(isLight ? UIUtils.CHESS_LIGHT : UIUtils.CHESS_DARK);
                    g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                    
                    // Highlight last move
                    Move lastMove = chessBoard.getLastMove();
                    if (lastMove != null) {
                        if ((row == lastMove.fromRow && col == lastMove.fromCol) ||
                            (row == lastMove.toRow && col == lastMove.toCol)) {
                            g2.setColor(UIUtils.CHESS_HIGHLIGHT);
                            g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                        }
                    }
                    
                    // Highlight selected square
                    if (row == selectedRow && col == selectedCol) {
                        g2.setColor(UIUtils.CHESS_HIGHLIGHT);
                        g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                    }
                    
                    // Show possible moves
                    for (Move move : possibleMoves) {
                        if (move.toRow == row && move.toCol == col) {
                            g2.setColor(UIUtils.CHESS_POSSIBLE_MOVE);
                            if (move.capturedPiece != null) {
                                // Draw ring for captures
                                g2.setStroke(new BasicStroke(4));
                                g2.drawOval(x + 5, y + 5, SQUARE_SIZE - 10, SQUARE_SIZE - 10);
                            } else {
                                // Draw dot for moves
                                g2.fillOval(x + SQUARE_SIZE/2 - 10, y + SQUARE_SIZE/2 - 10, 20, 20);
                            }
                        }
                    }
                    
                    // Highlight king in check
                    Piece piece = chessBoard.getPiece(row, col);
                    if (piece != null && piece.type == Piece.Type.KING) {
                        if (chessBoard.isKingInCheck(piece.isWhite)) {
                            g2.setColor(UIUtils.CHESS_CHECK);
                            g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                        }
                    }
                }
            }
            
            // Draw pieces
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = chessBoard.getPiece(row, col);
                    if (piece != null) {
                        drawPiece(g2, piece, offsetX + col * SQUARE_SIZE, offsetY + row * SQUARE_SIZE);
                    }
                }
            }
            
            // Draw coordinates
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(UIUtils.TEXT_SECONDARY);
            
            String files = "abcdefgh";
            for (int i = 0; i < 8; i++) {
                // File letters
                g2.drawString(String.valueOf(files.charAt(i)), 
                    offsetX + i * SQUARE_SIZE + SQUARE_SIZE/2 - 3, 
                    offsetY + BOARD_SIZE + 15);
                
                // Rank numbers
                g2.drawString(String.valueOf(8 - i), 
                    offsetX - 15, 
                    offsetY + i * SQUARE_SIZE + SQUARE_SIZE/2 + 4);
            }
            
            g2.dispose();
        }
        
        private void drawPiece(Graphics2D g2, Piece piece, int x, int y) {
            // Draw shadow
            g2.setColor(new Color(0, 0, 0, 40));
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 56));
            g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 22, y + SQUARE_SIZE/2 + 22);
            
            // Draw piece
            if (piece.isWhite) {
                // White pieces with dark outline
                g2.setColor(new Color(50, 50, 50));
                g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 23, y + SQUARE_SIZE/2 + 19);
                g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 21, y + SQUARE_SIZE/2 + 19);
                g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 22, y + SQUARE_SIZE/2 + 18);
                g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 22, y + SQUARE_SIZE/2 + 20);
                g2.setColor(new Color(255, 255, 255));
            } else {
                // Black pieces
                g2.setColor(new Color(30, 30, 30));
            }
            g2.drawString(piece.getSymbol(), x + SQUARE_SIZE/2 - 22, y + SQUARE_SIZE/2 + 19);
        }
    }
}
