// chess/ChessBoard.java
package chess;

import java.util.*;

public class ChessBoard {
    private Piece[][] board;
    private boolean whiteToMove;
    private int[] enPassantSquare; // [row, col] or null
    private boolean whiteKingsideCastle, whiteQueensideCastle;
    private boolean blackKingsideCastle, blackQueensideCastle;
    private List<Move> moveHistory;
    private int halfMoveClock;
    private int fullMoveNumber;
    
    // Piece-square tables for positional evaluation
    private static final int[][] PAWN_TABLE = {
        {0,  0,  0,  0,  0,  0,  0,  0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5,  5, 10, 25, 25, 10,  5,  5},
        {0,  0,  0, 20, 20,  0,  0,  0},
        {5, -5,-10,  0,  0,-10, -5,  5},
        {5, 10, 10,-20,-20, 10, 10,  5},
        {0,  0,  0,  0,  0,  0,  0,  0}
    };
    
    private static final int[][] KNIGHT_TABLE = {
        {-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-30,  0, 10, 15, 15, 10,  0,-30},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    
    private static final int[][] BISHOP_TABLE = {
        {-20,-10,-10,-10,-10,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5, 10, 10,  5,  0,-10},
        {-10,  5,  5, 10, 10,  5,  5,-10},
        {-10,  0, 10, 10, 10, 10,  0,-10},
        {-10, 10, 10, 10, 10, 10, 10,-10},
        {-10,  5,  0,  0,  0,  0,  5,-10},
        {-20,-10,-10,-10,-10,-10,-10,-20}
    };
    
    private static final int[][] ROOK_TABLE = {
        {0,  0,  0,  0,  0,  0,  0,  0},
        {5, 10, 10, 10, 10, 10, 10,  5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {0,  0,  0,  5,  5,  0,  0,  0}
    };
    
    private static final int[][] QUEEN_TABLE = {
        {-20,-10,-10, -5, -5,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5,  5,  5,  5,  0,-10},
        {-5,  0,  5,  5,  5,  5,  0, -5},
        {0,  0,  5,  5,  5,  5,  0, -5},
        {-10,  5,  5,  5,  5,  5,  0,-10},
        {-10,  0,  5,  0,  0,  0,  0,-10},
        {-20,-10,-10, -5, -5,-10,-10,-20}
    };
    
    private static final int[][] KING_MIDDLE_TABLE = {
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-20,-30,-30,-40,-40,-30,-30,-20},
        {-10,-20,-20,-20,-20,-20,-20,-10},
        {20, 20,  0,  0,  0,  0, 20, 20},
        {20, 30, 10,  0,  0, 10, 30, 20}
    };
    
    private static final int[][] KING_END_TABLE = {
        {-50,-40,-30,-20,-20,-30,-40,-50},
        {-30,-20,-10,  0,  0,-10,-20,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-30,  0,  0,  0,  0,-30,-30},
        {-50,-30,-30,-30,-30,-30,-30,-50}
    };
    
    public ChessBoard() {
        board = new Piece[8][8];
        whiteToMove = true;
        whiteKingsideCastle = whiteQueensideCastle = true;
        blackKingsideCastle = blackQueensideCastle = true;
        enPassantSquare = null;
        moveHistory = new ArrayList<>();
        halfMoveClock = 0;
        fullMoveNumber = 1;
        setupInitialPosition();
    }
    
    private void setupInitialPosition() {
        // Black pieces (row 0)
        board[0][0] = new Piece(Piece.Type.ROOK, false);
        board[0][1] = new Piece(Piece.Type.KNIGHT, false);
        board[0][2] = new Piece(Piece.Type.BISHOP, false);
        board[0][3] = new Piece(Piece.Type.QUEEN, false);
        board[0][4] = new Piece(Piece.Type.KING, false);
        board[0][5] = new Piece(Piece.Type.BISHOP, false);
        board[0][6] = new Piece(Piece.Type.KNIGHT, false);
        board[0][7] = new Piece(Piece.Type.ROOK, false);
        
        // Black pawns (row 1)
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Piece(Piece.Type.PAWN, false);
        }
        
        // White pawns (row 6)
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Piece(Piece.Type.PAWN, true);
        }
        
        // White pieces (row 7)
        board[7][0] = new Piece(Piece.Type.ROOK, true);
        board[7][1] = new Piece(Piece.Type.KNIGHT, true);
        board[7][2] = new Piece(Piece.Type.BISHOP, true);
        board[7][3] = new Piece(Piece.Type.QUEEN, true);
        board[7][4] = new Piece(Piece.Type.KING, true);
        board[7][5] = new Piece(Piece.Type.BISHOP, true);
        board[7][6] = new Piece(Piece.Type.KNIGHT, true);
        board[7][7] = new Piece(Piece.Type.ROOK, true);
    }
    
    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                copy.board[r][c] = board[r][c] != null ? board[r][c].copy() : null;
            }
        }
        copy.whiteToMove = whiteToMove;
        copy.whiteKingsideCastle = whiteKingsideCastle;
        copy.whiteQueensideCastle = whiteQueensideCastle;
        copy.blackKingsideCastle = blackKingsideCastle;
        copy.blackQueensideCastle = blackQueensideCastle;
        copy.enPassantSquare = enPassantSquare != null ? enPassantSquare.clone() : null;
        copy.halfMoveClock = halfMoveClock;
        copy.fullMoveNumber = fullMoveNumber;
        return copy;
    }
    
    public Piece getPiece(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return null;
        return board[row][col];
    }
    
    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }
    
    public boolean isWhiteToMove() {
        return whiteToMove;
    }
    
    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }
    
    public Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);
    }
    
    public List<Move> generateLegalMoves() {
        List<Move> legalMoves = new ArrayList<>();
        List<Move> pseudoMoves = generatePseudoLegalMoves();
        
        for (Move move : pseudoMoves) {
            ChessBoard testBoard = this.copy();
            testBoard.makeMove(move, false);
            if (!testBoard.isKingInCheck(!whiteToMove)) {
                legalMoves.add(move);
            }
        }
        
        return legalMoves;
    }
    
    private List<Move> generatePseudoLegalMoves() {
        List<Move> moves = new ArrayList<>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.isWhite == whiteToMove) {
                    generatePieceMoves(piece, row, col, moves);
                }
            }
        }
        
        return moves;
    }
    
    private void generatePieceMoves(Piece piece, int row, int col, List<Move> moves) {
        switch (piece.type) {
            case PAWN:
                generatePawnMoves(piece, row, col, moves);
                break;
            case KNIGHT:
                generateKnightMoves(piece, row, col, moves);
                break;
            case BISHOP:
                generateSlidingMoves(piece, row, col, moves, new int[][]{{-1,-1},{-1,1},{1,-1},{1,1}});
                break;
            case ROOK:
                generateSlidingMoves(piece, row, col, moves, new int[][]{{-1,0},{1,0},{0,-1},{0,1}});
                break;
            case QUEEN:
                generateSlidingMoves(piece, row, col, moves, new int[][]{{-1,-1},{-1,1},{1,-1},{1,1},{-1,0},{1,0},{0,-1},{0,1}});
                break;
            case KING:
                generateKingMoves(piece, row, col, moves);
                break;
        }
    }
    
    private void generatePawnMoves(Piece piece, int row, int col, List<Move> moves) {
        int direction = piece.isWhite ? -1 : 1;
        int startRow = piece.isWhite ? 6 : 1;
        int promotionRow = piece.isWhite ? 0 : 7;
        
        // Forward move
        int newRow = row + direction;
        if (newRow >= 0 && newRow <= 7 && board[newRow][col] == null) {
            if (newRow == promotionRow) {
                // Promotion
                for (Piece.Type type : new Piece.Type[]{Piece.Type.QUEEN, Piece.Type.ROOK, Piece.Type.BISHOP, Piece.Type.KNIGHT}) {
                    Move move = new Move(row, col, newRow, col, piece, null, false, false, new Piece(type, piece.isWhite), 0);
                    moves.add(move);
                }
            } else {
                moves.add(new Move(row, col, newRow, col, piece));
                
                // Double push from start
                if (row == startRow && board[row + 2 * direction][col] == null) {
                    moves.add(new Move(row, col, row + 2 * direction, col, piece));
                }
            }
        }
        
        // Captures
        for (int dc : new int[]{-1, 1}) {
            int newCol = col + dc;
            if (newCol >= 0 && newCol <= 7 && newRow >= 0 && newRow <= 7) {
                Piece target = board[newRow][newCol];
                if (target != null && target.isWhite != piece.isWhite) {
                    if (newRow == promotionRow) {
                        for (Piece.Type type : new Piece.Type[]{Piece.Type.QUEEN, Piece.Type.ROOK, Piece.Type.BISHOP, Piece.Type.KNIGHT}) {
                            moves.add(new Move(row, col, newRow, newCol, piece, target, false, false, new Piece(type, piece.isWhite), 0));
                        }
                    } else {
                        moves.add(new Move(row, col, newRow, newCol, piece, target));
                    }
                }
                
                // En passant
                if (enPassantSquare != null && newRow == enPassantSquare[0] && newCol == enPassantSquare[1]) {
                    Piece capturedPawn = board[row][newCol];
                    moves.add(new Move(row, col, newRow, newCol, piece, capturedPawn, false, true, null, 0));
                }
            }
        }
    }
    
    private void generateKnightMoves(Piece piece, int row, int col, List<Move> moves) {
        int[][] offsets = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        
        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece target = board[newRow][newCol];
                if (target == null || target.isWhite != piece.isWhite) {
                    moves.add(new Move(row, col, newRow, newCol, piece, target));
                }
            }
        }
    }
    
    private void generateSlidingMoves(Piece piece, int row, int col, List<Move> moves, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            while (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece target = board[newRow][newCol];
                if (target == null) {
                    moves.add(new Move(row, col, newRow, newCol, piece));
                } else {
                    if (target.isWhite != piece.isWhite) {
                        moves.add(new Move(row, col, newRow, newCol, piece, target));
                    }
                    break;
                }
                newRow += dir[0];
                newCol += dir[1];
            }
        }
    }
    
    private void generateKingMoves(Piece piece, int row, int col, List<Move> moves) {
        int[][] offsets = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        
        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece target = board[newRow][newCol];
                if (target == null || target.isWhite != piece.isWhite) {
                    moves.add(new Move(row, col, newRow, newCol, piece, target));
                }
            }
        }
        
        // Castling
        if (!piece.hasMoved && !isKingInCheck(piece.isWhite)) {
            // Kingside
            boolean canCastleKingside = piece.isWhite ? whiteKingsideCastle : blackKingsideCastle;
            if (canCastleKingside) {
                Piece rook = board[row][7];
                if (rook != null && rook.type == Piece.Type.ROOK && !rook.hasMoved) {
                    if (board[row][5] == null && board[row][6] == null) {
                        if (!isSquareAttacked(row, 5, !piece.isWhite) && !isSquareAttacked(row, 6, !piece.isWhite)) {
                            moves.add(new Move(row, col, row, 6, piece, null, true, false, null, 0));
                        }
                    }
                }
            }
            
            // Queenside
            boolean canCastleQueenside = piece.isWhite ? whiteQueensideCastle : blackQueensideCastle;
            if (canCastleQueenside) {
                Piece rook = board[row][0];
                if (rook != null && rook.type == Piece.Type.ROOK && !rook.hasMoved) {
                    if (board[row][1] == null && board[row][2] == null && board[row][3] == null) {
                        if (!isSquareAttacked(row, 2, !piece.isWhite) && !isSquareAttacked(row, 3, !piece.isWhite)) {
                            moves.add(new Move(row, col, row, 2, piece, null, true, false, null, 0));
                        }
                    }
                }
            }
        }
    }
    
    public void makeMove(Move move) {
        makeMove(move, true);
    }
    
    public void makeMove(Move move, boolean updateHistory) {
        Piece piece = board[move.fromRow][move.fromCol];
        
        // Update castling rights
        if (piece.type == Piece.Type.KING) {
            if (piece.isWhite) {
                whiteKingsideCastle = whiteQueensideCastle = false;
            } else {
                blackKingsideCastle = blackQueensideCastle = false;
            }
        }
        
        if (piece.type == Piece.Type.ROOK) {
            if (move.fromRow == 7 && move.fromCol == 0) whiteQueensideCastle = false;
            if (move.fromRow == 7 && move.fromCol == 7) whiteKingsideCastle = false;
            if (move.fromRow == 0 && move.fromCol == 0) blackQueensideCastle = false;
            if (move.fromRow == 0 && move.fromCol == 7) blackKingsideCastle = false;
        }
        
        // Handle castling
        if (move.isCastling) {
            // Move king
            board[move.toRow][move.toCol] = piece;
            board[move.fromRow][move.fromCol] = null;
            piece.hasMoved = true;
            
            // Move rook
            if (move.toCol == 6) { // Kingside
                Piece rook = board[move.fromRow][7];
                board[move.fromRow][5] = rook;
                board[move.fromRow][7] = null;
                rook.hasMoved = true;
            } else { // Queenside
                Piece rook = board[move.fromRow][0];
                board[move.fromRow][3] = rook;
                board[move.fromRow][0] = null;
                rook.hasMoved = true;
            }
        } else {
            // Handle en passant
            if (move.isEnPassant) {
                board[move.fromRow][move.toCol] = null; // Remove captured pawn
            }
            
            // Move piece
            if (move.promotionPiece != null) {
                board[move.toRow][move.toCol] = move.promotionPiece;
            } else {
                board[move.toRow][move.toCol] = piece;
            }
            board[move.fromRow][move.fromCol] = null;
            piece.hasMoved = true;
        }
        
        // Update en passant square
        if (piece.type == Piece.Type.PAWN && Math.abs(move.toRow - move.fromRow) == 2) {
            enPassantSquare = new int[]{(move.fromRow + move.toRow) / 2, move.fromCol};
        } else {
            enPassantSquare = null;
        }
        
        // Update move counters
        if (piece.type == Piece.Type.PAWN || move.capturedPiece != null) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }
        
        if (!whiteToMove) {
            fullMoveNumber++;
        }
        
        if (updateHistory) {
            moveHistory.add(move);
        }
        
        whiteToMove = !whiteToMove;
    }
    
    public boolean isKingInCheck(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], !white);
    }
    
    private int[] findKing(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.type == Piece.Type.KING && p.isWhite == white) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }
    
    public boolean isSquareAttacked(int row, int col, boolean byWhite) {
        // Check pawn attacks
        int pawnDir = byWhite ? 1 : -1;
        for (int dc : new int[]{-1, 1}) {
            int pr = row + pawnDir;
            int pc = col + dc;
            if (pr >= 0 && pr <= 7 && pc >= 0 && pc <= 7) {
                Piece p = board[pr][pc];
                if (p != null && p.type == Piece.Type.PAWN && p.isWhite == byWhite) {
                    return true;
                }
            }
        }
        
        // Check knight attacks
        int[][] knightOffsets = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] offset : knightOffsets) {
            int nr = row + offset[0];
            int nc = col + offset[1];
            if (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7) {
                Piece p = board[nr][nc];
                if (p != null && p.type == Piece.Type.KNIGHT && p.isWhite == byWhite) {
                    return true;
                }
            }
        }
        
        // Check sliding piece attacks
        int[][] directions = {{-1,-1},{-1,1},{1,-1},{1,1},{-1,0},{1,0},{0,-1},{0,1}};
        for (int i = 0; i < 8; i++) {
            int[] dir = directions[i];
            int nr = row + dir[0];
            int nc = col + dir[1];
            
            while (nr >= 0 && nr <= 7 && nc >= 0 && nc <= 7) {
                Piece p = board[nr][nc];
                if (p != null) {
                    if (p.isWhite == byWhite) {
                        boolean isDiagonal = i < 4;
                        if (p.type == Piece.Type.QUEEN) return true;
                        if (isDiagonal && p.type == Piece.Type.BISHOP) return true;
                        if (!isDiagonal && p.type == Piece.Type.ROOK) return true;
                    }
                    break;
                }
                nr += dir[0];
                nc += dir[1];
            }
        }
        
        // Check king attacks
        int[][] kingOffsets = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for (int[] offset : kingOffsets) {
            int kr = row + offset[0];
            int kc = col + offset[1];
            if (kr >= 0 && kr <= 7 && kc >= 0 && kc <= 7) {
                Piece p = board[kr][kc];
                if (p != null && p.type == Piece.Type.KING && p.isWhite == byWhite) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isCheckmate() {
        return isKingInCheck(whiteToMove) && generateLegalMoves().isEmpty();
    }
    
    public boolean isStalemate() {
        return !isKingInCheck(whiteToMove) && generateLegalMoves().isEmpty();
    }
    
    public boolean isDraw() {
        // 50-move rule
        if (halfMoveClock >= 100) return true;
        
        // Insufficient material
        if (isInsufficientMaterial()) return true;
        
        return false;
    }
    
    private boolean isInsufficientMaterial() {
        List<Piece> whitePieces = new ArrayList<>();
        List<Piece> blackPieces = new ArrayList<>();
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    if (p.isWhite) whitePieces.add(p);
                    else blackPieces.add(p);
                }
            }
        }
        
        // K vs K
        if (whitePieces.size() == 1 && blackPieces.size() == 1) return true;
        
        // K+B vs K or K+N vs K
        if (whitePieces.size() == 1 && blackPieces.size() == 2) {
            for (Piece p : blackPieces) {
                if (p.type == Piece.Type.BISHOP || p.type == Piece.Type.KNIGHT) return true;
            }
        }
        if (blackPieces.size() == 1 && whitePieces.size() == 2) {
            for (Piece p : whitePieces) {
                if (p.type == Piece.Type.BISHOP || p.type == Piece.Type.KNIGHT) return true;
            }
        }
        
        return false;
    }
    
    public int evaluate() {
        int score = 0;
        int whiteMaterial = 0;
        int blackMaterial = 0;
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    int pieceValue = p.type.value;
                    int posValue = getPieceSquareValue(p, r, c);
                    
                    if (p.isWhite) {
                        score += pieceValue + posValue;
                        whiteMaterial += pieceValue;
                    } else {
                        score -= pieceValue + posValue;
                        blackMaterial += pieceValue;
                    }
                }
            }
        }
        
        // Mobility bonus
        List<Move> currentMoves = generateLegalMoves();
        int mobilityBonus = currentMoves.size() * 5;
        score += whiteToMove ? mobilityBonus : -mobilityBonus;
        
        // King safety in middlegame
        if (whiteMaterial > 1500 && blackMaterial > 1500) {
            score += evaluateKingSafety(true) - evaluateKingSafety(false);
        }
        
        return whiteToMove ? score : -score;
    }
    
    private int getPieceSquareValue(Piece piece, int row, int col) {
        int r = piece.isWhite ? row : 7 - row;
        int c = col;
        
        switch (piece.type) {
            case PAWN: return PAWN_TABLE[r][c];
            case KNIGHT: return KNIGHT_TABLE[r][c];
            case BISHOP: return BISHOP_TABLE[r][c];
            case ROOK: return ROOK_TABLE[r][c];
            case QUEEN: return QUEEN_TABLE[r][c];
            case KING: return KING_MIDDLE_TABLE[r][c];
            default: return 0;
        }
    }
    
    private int evaluateKingSafety(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) return 0;
        
        int safety = 0;
        int kingRow = kingPos[0];
        int kingCol = kingPos[1];
        
        // Pawn shield
        int pawnDir = white ? -1 : 1;
        for (int dc = -1; dc <= 1; dc++) {
            int pr = kingRow + pawnDir;
            int pc = kingCol + dc;
            if (pr >= 0 && pr <= 7 && pc >= 0 && pc <= 7) {
                Piece p = board[pr][pc];
                if (p != null && p.type == Piece.Type.PAWN && p.isWhite == white) {
                    safety += 15;
                }
            }
        }
        
        return safety;
    }
}
