package chess;

import java.util.ArrayList;
import java.util.List;

public final class Knight extends Piece {

	public Knight(Team t) {
		super(t);
	}

	@Override
	public String toString() {
		if (getTeam() == Team.WHITE)
			return "N";
		else
			return "n";
	}

	@Override
	public List<Move> generateMoveList(Position start) {
		List<Move> moves = new ArrayList<>();
		
		addPositionToMoveList(moves, start, new Position(start.row() - 2, start.column() - 1));
		addPositionToMoveList(moves, start, new Position(start.row() - 1, start.column() - 2));
		addPositionToMoveList(moves, start, new Position(start.row() - 1, start.column() + 2));
		addPositionToMoveList(moves, start, new Position(start.row() - 2, start.column() + 1));
		addPositionToMoveList(moves, start, new Position(start.row() + 1, start.column() - 2));
		addPositionToMoveList(moves, start, new Position(start.row() + 2, start.column() - 1));
		addPositionToMoveList(moves, start, new Position(start.row() + 1, start.column() + 2));
		addPositionToMoveList(moves, start, new Position(start.row() + 2, start.column() + 1));	
		
		return moves;
	}
}
