package chess;

import java.io.Serializable;

public enum Team implements Serializable {
	WHITE, BLACK;
	public static Team team;
	
	public static Team otherTeam(Team team) {
		if (team == BLACK)
			return WHITE;
		else
			return BLACK;
	}
	
	public static String toString(Team colour) {
		if (colour == BLACK)
			return "Black";
		else
			return "White";
	}
}
