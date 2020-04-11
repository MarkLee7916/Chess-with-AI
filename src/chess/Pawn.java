package chess;

import java.util.ArrayList;
import java.util.List;

public final class Pawn extends Piece {
	private boolean leftTake, rightTake;
	private boolean isPieceInFront, isPieceTwoInFront;

	public Pawn(Team t) {
		super(t);
	}

	@Override
	public String toString() {
		if (getTeam() == Team.WHITE)
			return "P";
		else
			return "p";
	}

	// Update pawn object with the positions needed to tell which moves it can make
	public void setSurroundingPositions(boolean l, boolean r, boolean inFront, boolean twoInFront) {
		leftTake = l;
		rightTake = r;
		isPieceInFront = inFront;
		isPieceTwoInFront = twoInFront;
	}

	@Override
	public List<Move> generateMoveList(Position start) {
		List<Move> ret = new ArrayList<>();
		int directionModifier;
		int originalRow;

		if (getTeam() == Team.WHITE) {
			directionModifier = 1;
			originalRow = 1;
		} else {
			directionModifier = -1;
			originalRow = 6;
		}

		// If opposing teams piece at top left, can move there
		if (leftTake)
			addPositionToMoveList(ret, start, new Position(start.row() + directionModifier, start.column() - 1));

		// If opposing teams piece at top right, can move there
		if (rightTake)
			addPositionToMoveList(ret, start, new Position(start.row() + directionModifier, start.column() + 1));

		// If there's no piece blocking it, can move forward
		if (!isPieceInFront)
			addPositionToMoveList(ret, start, new Position(start.row() + directionModifier, start.column()));

		// If there's no piece blocking it and pawn is still in original position, can move forward 2 spots
		if (!isPieceTwoInFront && start.row() == originalRow)
			addPositionToMoveList(ret, start, new Position(start.row() + (directionModifier * 2), start.column()));

		return ret;
	}
}
