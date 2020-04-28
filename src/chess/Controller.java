package chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {
	private Board board;
	private final View view;
	private final MinimaxAI ai;

	private Position startOfPlayerMove;
	private Position endOfPlayerMove;
	private Team currentTeam;

	public Controller() {
		board = new Board();

		view = new View();
		setupBoardImages();
		view.addObserver(this);

		ai = new MinimaxAI(4, Team.WHITE);
	}

	// Main control method for entire program
	public void run() {
		currentTeam = Team.WHITE;
		Move move;
		GameStatus status;
		boolean running = true;

		while (running) {
			// Check if there's a checkmate or stalemate. If there is, end of game
			status = board.getGameStatus(currentTeam);
			if (status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE) {
				view.gameOverMessage(status, currentTeam);
				running = false;
				continue;
			}

			move = getMove();

			// Check if move follows the rules of Chess. If not, repeat turn
			if (!board.isValidMove(move, currentTeam)) {
				view.invalidMoveMessage(move);
				continue;
			}

			// Attempt to make move. If move results in the mover being checked, repeat turn
			if (!board.makeMove(move)) {
				view.checkMessage(currentTeam);
				continue;
			}

			// Update GUI and switch to next player
			updateView(move);
			view.moveMessage(move);
			currentTeam = getNextTurn();
		}
	}

	// Maps pieces on the board to the view
	private void setupBoardImages() {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				Position position = new Position(row, column);
				if (board.pieceAt(position) != null)
					view.updateTile(position, board.pieceAt(position).toString());
				else
					view.clearTile(position);
			}
		}
	}

	private Move getMove() {
		if (currentTeam == Team.WHITE)
			return ai.pickMove(board);
		else
			return pickPlayerMove();
	}

	private Move pickPlayerMove() {
		while (startOfPlayerMove == null || endOfPlayerMove == null)
			waitForValidInput();

		Move ret = new Move(startOfPlayerMove, endOfPlayerMove);
		resetMove();

		return ret;
	}

	private void waitForValidInput() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Team getNextTurn() {
		return Team.otherTeam(currentTeam);
	}

	// Update GUI with new state of board resulting from a move
	private void updateView(Move move) {
		String updateNewPiecePos = board.pieceAt(move.destination()).toString();

		view.clearTile(move.start());
		view.updateTile(move.destination(), updateNewPiecePos);
	}

	@Override
	public void update(Observable gui, Object information) {
		switch (view.getUpdateType()) {

		case SAVE:
			save(information);
			break;
		case LOAD:
			load(information);
			break;
		case MOVE:
			updatePlayerMove(information);
			break;
		default:
			throw new AssertionError("Enum doesn't seem to match with any supported types");
		}
	}

	private void updatePlayerMove(Object object) {
		if (!(object instanceof Position))
			throw new AssertionError("There doesn't seem to be a position here");

		Position position = (Position) object;

		if (isValidEndOfMove(position))
			endOfPlayerMove = position;
		else {
			startOfPlayerMove = position;
			endOfPlayerMove = null;
		}
	}

	private boolean isValidEndOfMove(Position position) {
		Piece selectedPiece = board.pieceAt(position);

		return (selectedPiece == null || selectedPiece.getTeam() != currentTeam) && startOfPlayerMove != null;
	}

	private void save(Object object) {
		if (!(object instanceof File))
			throw new AssertionError("There doesn't seem to be a file here");

		File file = (File) object;

		try (FileOutputStream fileStream = new FileOutputStream(file);
				ObjectOutputStream os = new ObjectOutputStream(fileStream)) {

			os.writeObject(board);

		} catch (IOException e) {
			e.printStackTrace();
			view.fileIOError();
		}
	}

	private void resetMove() {
		startOfPlayerMove = null;
		endOfPlayerMove = null;
	}

	private void load(Object object) {
		if (!(object instanceof File))
			throw new AssertionError("There doesn't seem to be a file here");

		File file = (File) object;

		try (FileInputStream fileStream = new FileInputStream(file);
				ObjectInputStream os = new ObjectInputStream(fileStream)) {

			board = (Board) os.readObject();
			resetMove();
			setupBoardImages();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			view.fileIOError();
		}
	}
}
