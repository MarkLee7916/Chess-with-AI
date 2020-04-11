package chess;


enum GameStatus {
	CHECKMATE, STALEMATE, INPLAY;
	public static GameStatus gameStatus;
	
	public static String toString(GameStatus status) {
		return status.toString().toLowerCase();
	}
}
