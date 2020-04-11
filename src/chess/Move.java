package chess;

import java.util.ArrayList;
import java.util.List;

public class Move {
	private final Position start;
	private final Position end;

	public Move(Position s, Position e) {
		start = s;
		end = e;	
	}

	// Example: drawPath((1, 1), (4, 4)) returns [(2, 2), (3, 3)]
	public List<Position> drawPath() {
		List<Position> path = new ArrayList<>();
		MovementType movementType = getMovementType();
		
		// Not necessary for horse, return empty list
		if (movementType == MovementType.HORSE)
			return path;

		int rowIncrement = getIncrementValues(movementType)[0] * getRowDirection();
		int columnIncrement = getIncrementValues(movementType)[1] * getColumnDirection();

		int rowOffset = rowIncrement;
		int columnOffset = columnIncrement;

		// Draw path until we reach end position
		while (start.row() + rowOffset != end.row() || start.column() + columnOffset != end.column()) {
			path.add(new Position(start.row() + rowOffset, start.column() + columnOffset));

			rowOffset += rowIncrement;
			columnOffset += columnIncrement;
		}

		return path;
	}

	// Returns 1 if piece moved down, -1 if moved up, 0 if piece didn't change row
	private int getRowDirection() {
		if (end.row() - start.row() > 0)
			return 1;
		else if (end.row() - start.row() < 0)
			return -1;
		else
			return 0;
	}

	// Returns 1 if piece moved right, -1 if moved left, 0 if piece didn't change column
	private int getColumnDirection() {
		if (end.column() - start.column() > 0)
			return 1;
		else if (end.column() - start.column() < 0)
			return -1;
		else
			return 0;
	}

	public Position getDestination() {
		return end;
	}

	public Position getStart() {
		return start;
	}

	private MovementType getMovementType() {
		if (Math.abs(start.row() - end.row()) == Math.abs(start.column() - end.column()))
			return MovementType.DIAGONAL;
		if (start.row() == end.row())
			return MovementType.HORIZONTAL;
		if (start.column() == end.column())
			return MovementType.VERTICAL;

		return MovementType.HORSE;
	}

	// Returns the change in co-ordinates that came from a movement
	private int[] getIncrementValues(MovementType movement) {
		int rowIncrement = 0;
		int columnIncrement = 0;

		switch (movement) {

		case DIAGONAL:
			rowIncrement = 1;
			columnIncrement = 1;
			break;
		case HORIZONTAL:
			columnIncrement = 1;
			break;
		case VERTICAL:
			rowIncrement = 1;
			break;
		default:
			throw new AssertionError("Enum doesn't seem to match with any supported types");
		}

		return new int[] { rowIncrement, columnIncrement };
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Move))
			return false;

		Move move = (Move) obj;

		return start.equals(move.start) && end.equals(move.end);
	}

	@Override
	public int hashCode() {
		return start.hashCode() * 27832 + end.hashCode();
	}

	@Override
	public String toString() {
		return start.toString() + " to " + end.toString();
	}

	enum MovementType {
		DIAGONAL, HORIZONTAL, VERTICAL, HORSE
	}
}
