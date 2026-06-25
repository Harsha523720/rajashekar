// chess/Move.java
package chess;

public class Move {
    public final int fromRow, fromCol, toRow, toCol;
    public final Piece piece;
    public final Piece capturedPiece;
    public final boolean isCastling;
    public final boolean isEnPassant;
    public final Piece promotionPiece;
    public final int score;
    
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece) {
        this(fromRow, fromCol, toRow, toCol, piece, null, false, false, null, 0);
    }
    
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece, Piece capturedPiece) {
        this(fromRow, fromCol, toRow, toCol, piece, capturedPiece, false, false, null, 0);
    }
    
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece, 
                Piece capturedPiece, boolean isCastling, boolean isEnPassant, 
                Piece promotionPiece, int score) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
        this.promotionPiece = promotionPiece;
        this.score = score;
    }
    
    public Move withScore(int newScore) {
        return new Move(fromRow, fromCol, toRow, toCol, piece, capturedPiece, 
                       isCastling, isEnPassant, promotionPiece, newScore);
    }
    
    public Move withPromotion(Piece promotionPiece) {
        return new Move(fromRow, fromCol, toRow, toCol, piece, capturedPiece,
                       isCastling, isEnPassant, promotionPiece, score);
    }
    
    @Override
    public String toString() {
        String colNames = "abcdefgh";
        return "" + colNames.charAt(fromCol) + (8 - fromRow) + 
               colNames.charAt(toCol) + (8 - toRow);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return fromRow == move.fromRow && fromCol == move.fromCol &&
               toRow == move.toRow && toCol == move.toCol;
    }
    
    @Override
    public int hashCode() {
        return fromRow * 1000 + fromCol * 100 + toRow * 10 + toCol;
    }
}
