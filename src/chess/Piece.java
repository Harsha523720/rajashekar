// chess/Piece.java
package chess;

public class Piece {
    public enum Type {
        KING('K', 20000),
        QUEEN('Q', 900),
        ROOK('R', 500),
        BISHOP('B', 330),
        KNIGHT('N', 320),
        PAWN('P', 100);
        
        public final char symbol;
        public final int value;
        
        Type(char symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }
    }
    
    public final Type type;
    public final boolean isWhite;
    public boolean hasMoved = false;
    
    public Piece(Type type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }
    
    public Piece copy() {
        Piece p = new Piece(type, isWhite);
        p.hasMoved = this.hasMoved;
        return p;
    }
    
    public String getSymbol() {
        String symbols = isWhite ? "♔♕♖♗♘♙" : "♚♛♜♝♞♟";
        switch (type) {
            case KING: return symbols.substring(0, 1);
            case QUEEN: return symbols.substring(1, 2);
            case ROOK: return symbols.substring(2, 3);
            case BISHOP: return symbols.substring(3, 4);
            case KNIGHT: return symbols.substring(4, 5);
            case PAWN: return symbols.substring(5, 6);
            default: return "?";
        }
    }
    
    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type.name();
    }
}
