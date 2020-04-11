package chess;

public class Controller {
	private final Board board;
	private final View view;
	private final MinimaxAI ai;

	public Controller() {
		board = new Board();
		view = new View();
		ai = new MinimaxAI(4);
	}

	// Main control method for entire program
	public void run() {
		boolean running = true;
		Team currentTeam = Team.WHITE;
		Move move;
		GameStatus status;

		while (running) {

			// Check if there's a checkmate or stalemate. If there is, end of game
			status = board.getGameStatus(currentTeam);
			if (status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE) {
				view.gameOverMessage(status, currentTeam);
				running = false;
				continue;
			}

			move = getMove(currentTeam);

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
			currentTeam = getNextTurn(currentTeam);
		}
	}

	private Move getMove(Team currentTeam) {
		if (currentTeam == Team.WHITE)
			return ai.pickMove(board, currentTeam);
		else
			return view.pickMove();
	}

	private Team getNextTurn(Team currentTeam) {
		if (currentTeam == Team.BLACK)
			return Team.WHITE;
		else
			return Team.BLACK;
	}

	// Update GUI with new state of board resulting from a move
	private void updateView(Move move) {
		String updateNewPiecePos = board.getPieceString(move.getDestination());

		view.clearTile(move.getStart());
		view.updateTile(move.getDestination(), updateNewPiecePos);
	}
}
