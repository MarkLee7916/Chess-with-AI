package chess;

import java.util.ArrayList;
import java.util.List;

public final class Queen extends Piece {

	public Queen(Team t) {
		super(t);
	}

	@Override
	public String toString() {
		if (getTeam() == Team.WHITE)
			return "Q";
		else
			return "q";
	}

	@Override
	public List<Move> generateMoveList(Position start) {
		List<Move> ret = new ArrayList<>();
		
		for (int i = 1; i < 8; i++) {
			addPositionToMoveList(ret, start, new Position(start.row() - i, start.column() - i));
			addPositionToMoveList(ret, start, new Position(start.row() - i, start.column()));
			addPositionToMoveList(ret, start, new Position(start.row() - i, start.column() + i));
			addPositionToMoveList(ret, start, new Position(start.row(), start.column() - i));
			addPositionToMoveList(ret, start, new Position(start.row(), start.column() + i));
			addPositionToMoveList(ret, start, new Position(start.row() + i, start.column() - i));
			addPositionToMoveList(ret, start, new Position(start.row() + i, start.column() + i));
			addPositionToMoveList(ret, start, new Position(start.row() + i, start.column()));
		}
				
		return ret;
	}
}
