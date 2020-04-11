package chess;

import java.util.ArrayList;
import java.util.List;

public final class King extends Piece {

	public King(Team t) {
		super(t);
	}

	@Override
	public String toString() {
		if (getTeam() == Team.WHITE)
			return "K";
		else
			return "k";
	}

	@Override
	public List<Move> generateMoveList(Position start) {
		List<Move> ret = new ArrayList<>();
		
		addPositionToMoveList(ret, start, new Position(start.row() - 1, start.column() - 1));
		addPositionToMoveList(ret, start, new Position(start.row() - 1, start.column()));
		addPositionToMoveList(ret, start, new Position(start.row() - 1, start.column() + 1));
		addPositionToMoveList(ret, start, new Position(start.row(), start.column() - 1));
		addPositionToMoveList(ret, start, new Position(start.row(), start.column() + 1));
		addPositionToMoveList(ret, start, new Position(start.row() + 1, start.column() - 1));
		addPositionToMoveList(ret, start, new Position(start.row() + 1, start.column() + 1));
		addPositionToMoveList(ret, start, new Position(start.row() + 1, start.column()));
		
		return ret;		
	}
}
