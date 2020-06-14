package chess;

/*
 * Uses the minimax algorithm with alpha beta pruning to make moves
 */
public class MinimaxAI {
	private final int maxDepth;
	private final Team team;

	public MinimaxAI(int m, Team t) {
		maxDepth = m;
		team = t;
	}

	// Return move that minimax algorithm wants to make by
	// running minimax on all possible moves
	public Move pickMove(Board board) {
		int max = Integer.MIN_VALUE;
		int current;
		Move optimalMove = null;

		for (Move move : board.generatePossibleMovesForTeam(team)) {
			if (board.makeMove(move)) {
				current = min(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
				if (current >= max) {
					optimalMove = move;
					max = current;
				}

				board.reverseLastMove();
			}
		}
		
		board.clearCache();
		return optimalMove;
	}

	// For all moves the opposing team could make, return least optimal for the AI
	private int min(Board board, int depth, int alpha, int beta) {
		if (depth == maxDepth)
			return board.generateHeuristicValue(team);

		for (Move move : board.generatePossibleMovesForTeam(Team.otherTeam(team))) {
			if (board.makeMove(move)) {
				beta = Math.min(max(board, depth + 1, alpha, beta), beta);
				board.reverseLastMove();
			}
			
			if (alpha >= beta)
				break;
		}

		return beta;
	}

	// For all moves the AI could make, return most optimal
	private int max(Board board, int depth, int alpha, int beta) {
		if (depth == maxDepth)
			return board.generateHeuristicValue(team);

		for (Move move : board.generatePossibleMovesForTeam(team)) {
			if (board.makeMove(move)) {
				alpha = Math.max(min(board, depth + 1, alpha, beta), alpha);
				board.reverseLastMove();
			}

			if (alpha >= beta)
				break;
		}

		return alpha;
	}
}
