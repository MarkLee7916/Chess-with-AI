package chess;

import java.io.Serializable;

public class Position implements Serializable {
	private final int row;
	private final int column;
	
	public Position(int X, int Y) {
		row = X;
		column = Y;
	}
	
	public boolean isOnBoard() {
		return row >= 0 && row < 8 && column >= 0 && column < 8;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Position))
			return false;
		
		Position position = (Position) obj;
		
		return row == position.row && column == position.column;
	}
	
	@Override
	public int hashCode() {
		return row * 13 + column;
	}
	
	// Position in standard chess format (i.e A1)
	@Override
	public String toString() {
		return displayColumn(column) + "" + displayRow(row);
	}
	
	private char displayColumn(int column) {
		return (char) (64 + column + 1);
	}
	
	private int displayRow(int row) {
		return 8 - row;
	}

	public int row() {
		return row;
	}

	public int column() {
		return column;
	}
}
