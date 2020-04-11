package chess;

/*
 * Uses the minimax algorithm with alpha beta pruning and iterative deepening to make moves
 */
public class MinimaxAI {
	private final int maxDepth;

	public MinimaxAI(int m) {
		maxDepth = m;
	}

	// Return move that minimax algorithm wants to make by
	// running minimax on all possible moves
	public Move pickMove(Board board, Team team) {
		int max = Integer.MIN_VALUE;
		int current;
		Move optimalMove = null;

		for (Move move : board.generatePossibleMovesForTeam(team)) {
			if (board.makeMove(move)) {
				current = min(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, team);
				if (current > max) {
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
	private int min(Board board, int depth, int alpha, int beta, Team team) {
		if (depth == maxDepth)
			return board.generateHeuristicValue(team);

		for (Move move : board.generatePossibleMovesForTeam(Team.otherTeam(team))) {
			if (board.makeMove(move)) {
				beta = Math.min(max(board, depth + 1, alpha, beta, team), beta);
				board.reverseLastMove();
			}
			
			if (alpha >= beta)
				break;
		}

		return beta;
	}

	// For all moves the AI could make, return most optimal
	private int max(Board board, int depth, int alpha, int beta, Team team) {
		if (depth == maxDepth)
			return board.generateHeuristicValue(team);

		for (Move move : board.generatePossibleMovesForTeam(team)) {
			if (board.makeMove(move)) {
				alpha = Math.max(min(board, depth + 1, alpha, beta, team), alpha);
				board.reverseLastMove();
			}

			if (alpha >= beta)
				break;
		}

		return alpha;
	}
}
