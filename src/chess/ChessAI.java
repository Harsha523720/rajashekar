// chess/ChessAI.java
package chess;

import java.util.*;

public class ChessAI {
    private int maxDepth;
    private int nodesSearched;
    private Map<Long, TranspositionEntry> transpositionTable;
    private Move[][] killerMoves;
    private int[][] historyHeuristic;
    
    private static final int INFINITY = 1000000;
    private static final int MATE_SCORE = 100000;
    
    public ChessAI(int depth) {
        this.maxDepth = depth;
        this.transpositionTable = new HashMap<>();
        this.killerMoves = new Move[64][2];
        this.historyHeuristic = new int[64][64];
    }
    
    public Move findBestMove(ChessBoard board) {
        nodesSearched = 0;
        transpositionTable.clear();
        
        List<Move> legalMoves = board.generateLegalMoves();
        if (legalMoves.isEmpty()) return null;
        
        Move bestMove = null;
        int bestScore = -INFINITY;
        int alpha = -INFINITY;
        int beta = INFINITY;
        
        // Order moves for better pruning
        legalMoves = orderMoves(board, legalMoves, 0);
        
        // Iterative deepening
        for (int depth = 1; depth <= maxDepth; depth++) {
            int currentBestScore = -INFINITY;
            Move currentBestMove = null;
            
            for (Move move : legalMoves) {
                ChessBoard newBoard = board.copy();
                newBoard.makeMove(move);
                
                int score = -negamax(newBoard, depth - 1, -beta, -alpha, 1);
                
                if (score > currentBestScore) {
                    currentBestScore = score;
                    currentBestMove = move;
                }
                
                alpha = Math.max(alpha, score);
            }
            
            if (currentBestMove != null) {
                bestMove = currentBestMove;
                bestScore = currentBestScore;
            }
            
            // Reorder moves for next iteration
            final Move bestForOrdering = bestMove;
            legalMoves.sort((a, b) -> {
                if (a.equals(bestForOrdering)) return -1;
                if (b.equals(bestForOrdering)) return 1;
                return 0;
            });
        }
        
        return bestMove;
    }
    
    private int negamax(ChessBoard board, int depth, int alpha, int beta, int ply) {
        nodesSearched++;
        
        // Check for terminal states
        if (board.isCheckmate()) {
            return -MATE_SCORE + ply;
        }
        if (board.isStalemate() || board.isDraw()) {
            return 0;
        }
        
        // Transposition table lookup
        long hash = computeHash(board);
        TranspositionEntry entry = transpositionTable.get(hash);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TranspositionEntry.EXACT) {
                return entry.score;
            } else if (entry.flag == TranspositionEntry.LOWER_BOUND) {
                alpha = Math.max(alpha, entry.score);
            } else if (entry.flag == TranspositionEntry.UPPER_BOUND) {
                beta = Math.min(beta, entry.score);
            }
            if (alpha >= beta) {
                return entry.score;
            }
        }
        
        if (depth <= 0) {
            return quiescence(board, alpha, beta, ply);
        }
        
        List<Move> moves = board.generateLegalMoves();
        if (moves.isEmpty()) {
            if (board.isKingInCheck(board.isWhiteToMove())) {
                return -MATE_SCORE + ply;
            }
            return 0;
        }
        
        moves = orderMoves(board, moves, ply);
        
        int bestScore = -INFINITY;
        int flag = TranspositionEntry.UPPER_BOUND;
        
        for (Move move : moves) {
            ChessBoard newBoard = board.copy();
            newBoard.makeMove(move);
            
            int score = -negamax(newBoard, depth - 1, -beta, -alpha, ply + 1);
            
            if (score > bestScore) {
                bestScore = score;
            }
            
            if (score > alpha) {
                alpha = score;
                flag = TranspositionEntry.EXACT;
            }
            
            if (alpha >= beta) {
                // Store killer move
                if (move.capturedPiece == null) {
                    killerMoves[ply][1] = killerMoves[ply][0];
                    killerMoves[ply][0] = move;
                    historyHeuristic[move.fromRow * 8 + move.fromCol][move.toRow * 8 + move.toCol] += depth * depth;
                }
                flag = TranspositionEntry.LOWER_BOUND;
                break;
            }
        }
        
        // Store in transposition table
        transpositionTable.put(hash, new TranspositionEntry(hash, depth, bestScore, flag));
        
        return bestScore;
    }
    
    private int quiescence(ChessBoard board, int alpha, int beta, int ply) {
        nodesSearched++;
        
        int standPat = board.evaluate();
        
        if (standPat >= beta) {
            return beta;
        }
        
        // Delta pruning
        int DELTA = 900; // Queen value
        if (standPat + DELTA < alpha) {
            return alpha;
        }
        
        if (standPat > alpha) {
            alpha = standPat;
        }
        
        List<Move> captures = new ArrayList<>();
        for (Move move : board.generateLegalMoves()) {
            if (move.capturedPiece != null || move.promotionPiece != null) {
                captures.add(move);
            }
        }
        
        // Sort captures by MVV-LVA
        captures.sort((a, b) -> {
            int aScore = (a.capturedPiece != null ? a.capturedPiece.type.value : 0) - a.piece.type.value / 10;
            int bScore = (b.capturedPiece != null ? b.capturedPiece.type.value : 0) - b.piece.type.value / 10;
            return bScore - aScore;
        });
        
        for (Move move : captures) {
            ChessBoard newBoard = board.copy();
            newBoard.makeMove(move);
            
            int score = -quiescence(newBoard, -beta, -alpha, ply + 1);
            
            if (score >= beta) {
                return beta;
            }
            
            if (score > alpha) {
                alpha = score;
            }
        }
        
        return alpha;
    }
    
    private List<Move> orderMoves(ChessBoard board, List<Move> moves, int ply) {
        List<Move> orderedMoves = new ArrayList<>(moves);
        
        orderedMoves.sort((a, b) -> {
            int aScore = getMoveScore(board, a, ply);
            int bScore = getMoveScore(board, b, ply);
            return bScore - aScore;
        });
        
        return orderedMoves;
    }
    
    private int getMoveScore(ChessBoard board, Move move, int ply) {
        int score = 0;
        
        // Captures (MVV-LVA)
        if (move.capturedPiece != null) {
            score += 10000 + move.capturedPiece.type.value - move.piece.type.value / 10;
        }
        
        // Promotions
        if (move.promotionPiece != null) {
            score += 9000 + move.promotionPiece.type.value;
        }
        
        // Killer moves
        if (ply < 64) {
            if (move.equals(killerMoves[ply][0])) {
                score += 5000;
            } else if (move.equals(killerMoves[ply][1])) {
                score += 4000;
            }
        }
        
        // History heuristic
        score += historyHeuristic[move.fromRow * 8 + move.fromCol][move.toRow * 8 + move.toCol];
        
        // Castling bonus
        if (move.isCastling) {
            score += 500;
        }
        
        return score;
    }
    
    private long computeHash(ChessBoard board) {
        long hash = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    hash ^= (long)(r * 8 + c + 1) * (p.type.ordinal() + 1) * (p.isWhite ? 1 : 2);
                }
            }
        }
        hash ^= board.isWhiteToMove() ? 1L << 63 : 0;
        return hash;
    }
    
    public int getNodesSearched() {
        return nodesSearched;
    }
    
    // Transposition table entry
    private static class TranspositionEntry {
        static final int EXACT = 0;
        static final int LOWER_BOUND = 1;
        static final int UPPER_BOUND = 2;
        
        long hash;
        int depth;
        int score;
        int flag;
        
        TranspositionEntry(long hash, int depth, int score, int flag) {
            this.hash = hash;
            this.depth = depth;
            this.score = score;
            this.flag = flag;
        }
    }
}