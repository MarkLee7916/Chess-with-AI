package chess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Board implements Serializable {
	private final Piece[][] board;

	// Cache is used to save moves in case you want to reverse them.
	private final Stack<Piece> deletedPieceCache;
	private final Stack<Move> moveCache;
	private final Stack<Position> pawnToQueenConversionCache;

	// Maps a pieces string representation onto it's relative value
	private final Map<String, Integer> heuristicMap;

	public Board() {
		board = new Piece[8][8];
		deletedPieceCache = new Stack<>();
		moveCache = new Stack<>();
		pawnToQueenConversionCache = new Stack<>();
		heuristicMap = new HashMap<>();

		buildHeuristicMapping();
		addPieces(0, 1, Team.WHITE);
		addPieces(7, 6, Team.BLACK);
	}

	public void reverseLastMove() {
		Move move = moveCache.pop();
		Position start = move.start();
		Position end = move.destination();

		board[start.row()][start.column()] = pieceAt(end);
		board[end.row()][end.column()] = deletedPieceCache.pop();

		checkForReversePawnReplacement();
	}

	// Returns true if last move was successful, false if unsuccessful
	public boolean makeMove(Move move) {
		Position start = move.start();
		Position end = move.destination();
		Team team = pieceAt(start).getTeam();

		cacheMove(move, end);
		movePiece(start, end);
		checkForPawnReplacement(start, end);

		if (isChecked(team)) {
			reverseLastMove();
			return false;
		}

		return true;
	}

	private void movePiece(Position start, Position end) {
		board[end.row()][end.column()] = pieceAt(start);
		board[start.row()][start.column()] = null;
	}

	private void cacheMove(Move move, Position end) {
		deletedPieceCache.push(pieceAt(end));
		moveCache.push(move);
	}

	public GameStatus getGameStatus(Team team) {
		for (Move move : generatePossibleMovesForTeam(team)) {
			if (makeMove(move)) {
				reverseLastMove();
				return GameStatus.INPLAY;
			}
		}

		// No moves can be made, game is either in checkmate or stalemate
		if (isChecked(team))
			return GameStatus.CHECKMATE;
		else
			return GameStatus.STALEMATE;
	}

	// Returns true if a move doesn't break the rules
	public boolean isValidMove(Move move, Team team) {
		if (pieceAt(move.start()) == null)
			return false;

		if (pieceAt(move.start()).getTeam() != team)
			return false;

		List<Move> possibleMoves = generatePossibleMovesForPiece(move.start());
		return possibleMoves.contains(move);
	}

	public List<Move> generatePossibleMovesForTeam(Team team) {
		List<Move> ret = new ArrayList<>();

		for (Position pos : getPositionsOfPiecesForTeam(team))
			ret.addAll(generatePossibleMovesForPiece(pos));

		return ret;
	}

	// Adds piece objects to board for each team
	private void addPieces(int backRow, int frontRow, Team team) {
		board[backRow][0] = new Rook(team);
		board[backRow][7] = new Rook(team);
		board[backRow][1] = new Knight(team);
		board[backRow][6] = new Knight(team);
		board[backRow][2] = new Bishop(team);
		board[backRow][5] = new Bishop(team);
		board[backRow][3] = new Queen(team);
		board[backRow][4] = new King(team);

		for (int i = 0; i < 8; i++)
			board[frontRow][i] = new Pawn(team);
	}

	private boolean isChecked(Team team) {
		Position kingsPosition = getKingPosition(team);
		Team otherTeam = Team.otherTeam(team);

		for (Position position : getPositionsOfPiecesForTeam(otherTeam)) {
			Move move = new Move(position, kingsPosition);
			if (isValidMove(move, otherTeam))
				return true;
		}

		return false;
	}

	// If pawn reached the end, replace with queen
	private void checkForPawnReplacement(Position start, Position end) {
		if (pieceAt(end) instanceof Pawn && (end.row() == 0 || end.row() == 7)) {
			replacePawnWithQueen(end);
			pawnToQueenConversionCache.push(start);
		} else
			pawnToQueenConversionCache.push(null);
	}

	private void replacePawnWithQueen(Position end) {
		board[end.row()][end.column()] = new Queen(pieceAt(end).getTeam());
	}

	// Uses cache to reverse a move where a pawn has turned into a queen
	private void checkForReversePawnReplacement() {
		Position pos = pawnToQueenConversionCache.pop();
		if (pos != null)
			board[pos.row()][pos.column()] = new Pawn(pieceAt(pos).getTeam());
	}

	private List<Move> generatePossibleMovesForPiece(Position start) {
		Piece piece = pieceAt(start);

		if (piece instanceof Pawn)
			updatePawnSurroundings(start);

		return removeInvalidMoves(piece.generateMoveList(start));
	}

	// Tells a pawn object where its surrounding pieces are so it can make a move
	private void updatePawnSurroundings(Position pawnPosition) {
		boolean leftTake = false, rightTake = false;
		boolean isPieceInFront = false, isPieceTwoInFront = false;

		Pawn pawn = (Pawn) pieceAt(pawnPosition);
		int directionModifier = getDirectionModifier(pawn.getTeam());
		Position pos;

		// True if an opposing teams piece is at top left of pawn
		pos = new Position(pawnPosition.row() + directionModifier, pawnPosition.column() + 1);
		if (pieceAt(pos) != null && pieceAt(pos).getTeam() != pawn.getTeam())
			rightTake = true;

		// True if an opposing teams piece is at top right of pawn
		pos = new Position(pawnPosition.row() + directionModifier, pawnPosition.column() - 1);
		if (pieceAt(pos) != null && pieceAt(pos).getTeam() != pawn.getTeam())
			leftTake = true;

		// True if a piece is in front of the pawn
		pos = new Position(pawnPosition.row() + directionModifier, pawnPosition.column());
		if (pieceAt(pos) != null)
			isPieceInFront = true;

		// True if no piece lies 2 spots ahead of pawn
		pos = new Position(pawnPosition.row() + (directionModifier * 2), pawnPosition.column());
		if (pieceAt(pos) != null)
			isPieceTwoInFront = true;

		pawn.setSurroundingPositions(leftTake, rightTake, isPieceInFront, isPieceTwoInFront);
	}

	// Returns the direction where a pawn should move given the team it's in
	private int getDirectionModifier(Team team) {
		if (team == Team.WHITE)
			return 1;
		else
			return -1;
	}

	// Filters out any moves that don't follow the rules of the game
	private List<Move> removeInvalidMoves(List<Move> moves) {
		List<Move> ret = new ArrayList<>();

		for (Move move : moves)
			if (isClearPath(move) && isValidDestination(move))
				ret.add(move);

		return ret;
	}

	// Returns true if no other pieces lie in a pieces path when moving
	private boolean isClearPath(Move move) {
		List<Position> path = move.drawPath();

		for (Position position : path)
			if (pieceAt(position) != null)
				return false;

		return true;
	}

	private Position getKingPosition(Team team) {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				Position pos = new Position(row, column);
				if (pieceAt(pos) != null && (pieceAt(pos) instanceof King) && pieceAt(pos).getTeam() == team)
					return pos;
			}
		}

		throw new AssertionError("King not found");
	}

	// Returns List of all positions of a given teams pieces that can make a move
	private List<Position> getPositionsOfPiecesForTeam(Team team) {
		List<Position> ret = new ArrayList<>();

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				Position pos = new Position(i, j);
				if (pieceAt(pos) != null && pieceAt(pos).getTeam() == team)
					if (generatePossibleMovesForPiece(pos).size() > 0)
						ret.add(pos);
			}

		return ret;
	}

	// Returns true if the destination isn't occupied by a pieces own team
	private boolean isValidDestination(Move move) {
		Position start = move.start();
		Position end = move.destination();
		Team team = pieceAt(start).getTeam();

		if (pieceAt(end) != null && pieceAt(end).getTeam() == team)
			return false;

		return true;
	}

	public Piece pieceAt(Position position) {
		if (!position.isOnBoard())
			return null;

		return board[position.row()][position.column()];
	}

	@SuppressWarnings("unused")
	private void printBoard() {
		for (Piece[] row : board) {
			System.out.println();
			for (Piece piece : row)
				if (piece == null)
					System.out.print("-");
				else
					System.out.print(piece);
		}
		System.out.println("\n");
	}

	public void clearCache() {
		deletedPieceCache.clear();
		moveCache.clear();
		pawnToQueenConversionCache.clear();
	}

	private void buildHeuristicMapping() {
		heuristicMap.put("k", 950);
		heuristicMap.put("q", 100);
		heuristicMap.put("r", 60);
		heuristicMap.put("b", 40);
		heuristicMap.put("n", 30);
		heuristicMap.put("p", 10);
	}

	public int generateHeuristicValue(Team team) {
		int value = 0;

		for (Piece[] row : board)
			for (Piece piece : row)
				if (piece != null) {
					if (team == piece.getTeam())
						value += heuristicMap.get(piece.toString().toLowerCase());
					else
						value -= heuristicMap.get(piece.toString().toLowerCase());
				}

		return value;
	}
}
