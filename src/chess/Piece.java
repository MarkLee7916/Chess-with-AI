package chess;

import java.io.Serializable;
import java.util.List;

abstract public class Piece implements Serializable { 
	private final Team team;
	
	public Piece(Team t) {
		team = t;
	}
	
	protected void addPositionToMoveList(List<Move> moves, Position start, Position pos) {
		if (pos.isOnBoard())
			moves.add(new Move(start, pos));			
	}
	
	public Team getTeam() {
		return team;
	}
	
	// Generates set of all possible positions a piece can move to
	public abstract List<Move> generateMoveList(Position start);
}
