package chess;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class View {
	
	// Allows us to access a tile given a position on the board
	private final JButton[][] tiles;

	// Main frame that the GUI runs on
	private final JFrame frame;
	
	// Main panel that all tiles on the board are placed on
	private final JPanel board;
	
	// Panel that holds any buttons the player needs
	private final JPanel playerOptions;
	
	// Maps string representation of a piece to its image
	private final Map<String, Image> pieceToImage;
	
	private final JTextField gameStatus;
	
	private Position startOfMove, endOfMove;

	public View() {
		frame = new JFrame("Chess");
		frame.setSize(1000, 1000);

		board = new JPanel(new GridLayout(0, 8));
		playerOptions = new JPanel();

		gameStatus = new JTextField("");
		gameStatus.setHorizontalAlignment(JTextField.CENTER);

		tiles = new JButton[8][8];
		pieceToImage = new HashMap<>();

		// Add components to JFrame
		frame.getContentPane().add(BorderLayout.CENTER, board);
		frame.getContentPane().add(BorderLayout.SOUTH, playerOptions);
		frame.getContentPane().add(BorderLayout.NORTH, gameStatus);

		setupPlayerOptions();
		addPieceImagesToMap();
		setUpButtons();
		initialiseWhitePieceImages();
		initialiseBlackPieceImages();
		addBoardBehaviour();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void setupPlayerOptions() {
		JButton button = new JButton("Reset Move");
		playerOptions.add(button);

		button.addActionListener(actionEvent -> {
			resetMove();
		});
	}

	public void gameOverMessage(GameStatus status, Team team) {
		if (status == GameStatus.STALEMATE)
			JOptionPane.showMessageDialog(null, "Game has ended in a stalemate");
		else
			JOptionPane.showMessageDialog(null, "Checkmate, " + Team.toString(Team.otherTeam(team)) + " has won");
	}

	// Adds the actionlistener to every button in the board
	private void addBoardBehaviour() {
		for (int row = 0; row < 8; row++)
			for (int column = 0; column < 8; column++)
				addButtonBehaviour(row, column);
	}

	// Allows user to select pieces for a move
	private void addButtonBehaviour(final int row, final int column) {
		tiles[row][column].addActionListener(actionEvent -> {
			if (startOfMove == null)
				startOfMove = new Position(row, column);
			else
				endOfMove = new Position(row, column);
		});
	}

	// Adds images of white pieces to the board at the start of the game
	private void initialiseWhitePieceImages() {
		tiles[0][0].setIcon(new ImageIcon(pieceToImage.get("R")));
		tiles[0][1].setIcon(new ImageIcon(pieceToImage.get("N")));
		tiles[0][2].setIcon(new ImageIcon(pieceToImage.get("B")));
		tiles[0][3].setIcon(new ImageIcon(pieceToImage.get("Q")));
		tiles[0][4].setIcon(new ImageIcon(pieceToImage.get("K")));
		tiles[0][5].setIcon(new ImageIcon(pieceToImage.get("B")));
		tiles[0][6].setIcon(new ImageIcon(pieceToImage.get("N")));
		tiles[0][7].setIcon(new ImageIcon(pieceToImage.get("R")));

		for (int i = 0; i < 8; i++)
			tiles[1][i].setIcon(new ImageIcon(pieceToImage.get("P")));
	}

	// Adds images of black pieces to the board at the start of the game
	private void initialiseBlackPieceImages() {
		tiles[7][0].setIcon(new ImageIcon(pieceToImage.get("r")));
		tiles[7][1].setIcon(new ImageIcon(pieceToImage.get("n")));
		tiles[7][2].setIcon(new ImageIcon(pieceToImage.get("b")));
		tiles[7][3].setIcon(new ImageIcon(pieceToImage.get("q")));
		tiles[7][4].setIcon(new ImageIcon(pieceToImage.get("k")));
		tiles[7][5].setIcon(new ImageIcon(pieceToImage.get("b")));
		tiles[7][6].setIcon(new ImageIcon(pieceToImage.get("n")));
		tiles[7][7].setIcon(new ImageIcon(pieceToImage.get("r")));

		for (int i = 0; i < 8; i++)
			tiles[6][i].setIcon(new ImageIcon(pieceToImage.get("p")));
	}

	// Create buttons and add to panel
	private void setUpButtons() {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				JButton button = new JButton();
				setBackgroundForTile(row, column, button);
				tiles[row][column] = button;
				board.add(button);
			}
		}
	}

	private void setBackgroundForTile(int row, int column, JButton button) {
		if ((column % 2 == 0 && row % 2 == 0) || (column % 2 == 1 && row % 2 == 1))
			button.setBackground(Color.WHITE);
		else
			button.setBackground(Color.BLACK);
	}

	private void addPieceImagesToMap() {
		Image[][] pieceImages = new Image[2][6];
		readPieceImages(pieceImages);

		pieceToImage.put("q", pieceImages[0][0]);
		pieceToImage.put("k", pieceImages[0][1]);
		pieceToImage.put("r", pieceImages[0][2]);
		pieceToImage.put("n", pieceImages[0][3]);
		pieceToImage.put("b", pieceImages[0][4]);
		pieceToImage.put("p", pieceImages[0][5]);

		pieceToImage.put("Q", pieceImages[1][0]);
		pieceToImage.put("K", pieceImages[1][1]);
		pieceToImage.put("R", pieceImages[1][2]);
		pieceToImage.put("N", pieceImages[1][3]);
		pieceToImage.put("B", pieceImages[1][4]);
		pieceToImage.put("P", pieceImages[1][5]);
	}

	private void readPieceImages(Image[][] pieceImages) {
		int imageSize = 64;

		try {
			BufferedImage imageBuffer = ImageIO.read(new File("piece_images.png"));
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < 6; j++)
					pieceImages[i][j] = imageBuffer.getSubimage(j * imageSize, i * imageSize, imageSize, imageSize);

		} catch (IOException io) {
			System.out.println("Error with handling images");
			io.printStackTrace();
		}
	}

	// Returns players move when they've selected it
	public Move pickMove() {
		while (startOfMove == null || endOfMove == null)
			System.out.print("");

		Move ret = new Move(startOfMove, endOfMove);
		resetMove();

		return ret;
	}

	private void resetMove() {
		startOfMove = null;
		endOfMove = null;
	}

	// Updates the images displayed on the board for a move
	public void updateTile(Position position, String update) {
		tiles[position.row()][position.column()].setIcon(new ImageIcon(pieceToImage.get(update)));
	}

	public void clearTile(Position position) {
		tiles[position.row()][position.column()].setIcon(null);
	}

	public void invalidMoveMessage(Move move) {
		gameStatus.setText("Attempted move " + move + " is invalid");
	}

	public void moveMessage(Move move) {
		gameStatus.setText(move.toString());
	}

	public void checkMessage(Team team) {
		gameStatus.setText(Team.toString(team) + " would be checked as the result of that move");
	}
}
