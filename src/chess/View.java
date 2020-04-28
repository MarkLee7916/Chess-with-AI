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
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class View extends Observable {
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

	// Displays any information on the game (i.e checks, illegal moves)
	private final JTextField gameStatus;

	// These components represent the filemenu dropdown menu for saving and loading
	private final JMenuBar fileMenuBar;
	private final JMenu fileMenu;
	private final JMenuItem save;
	private final JMenuItem load;

	// Allows view to tell the controller any requests that come from the player
	private UpdateType updateType;

	public View() {
		frame = new JFrame("Chess");
		board = new JPanel(new GridLayout(0, 8));

		fileMenuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		save = new JMenuItem("Save");
		load = new JMenuItem("Load");
		setUpFileMenu();

		playerOptions = new JPanel();
		setupPlayerOptions();

		gameStatus = new JTextField("");
		gameStatus.setHorizontalAlignment(JTextField.CENTER);

		tiles = new JButton[8][8];
		setupBoardButtons();
		addBoardBehaviour();

		pieceToImage = new HashMap<>();
		addPieceImagesToMap();

		addComponentsToFrame();
		configureFrame();
	}

	private void configureFrame() {
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void setUpFileMenu() {
		fileMenu.add(save);
		fileMenu.add(load);
		fileMenuBar.add(fileMenu);

		addSaveBehaviour();
		addLoadBehaviour();
	}

	// Tells program what to do when save button is pressed
	private void addSaveBehaviour() {
		save.addActionListener(actionEvent -> {
			File file = getFileFromUser();

			if (file != null) {
				updateType = UpdateType.SAVE;
				setChanged();
				notifyObservers(file);
				updateType = UpdateType.NONE;
			}
		});
	}

	// Tells program what to do when load button is pressed
	private void addLoadBehaviour() {
		load.addActionListener(actionEvent -> {
			File file = getFileFromUser();

			if (file != null) {
				updateType = UpdateType.LOAD;
				setChanged();
				notifyObservers(file);
				updateType = UpdateType.NONE;
			}
		});
	}

	public void fileIOError() {
		JOptionPane.showMessageDialog(null, "Error when loading in file");
	}

	// Allows user to select a file from their computer's file menu
	private File getFileFromUser() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile();

		return null;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void gameOverMessage(GameStatus status, Team team) {
		if (status == GameStatus.STALEMATE)
			JOptionPane.showMessageDialog(null, "Game has ended in a stalemate");
		else
			JOptionPane.showMessageDialog(null, "Checkmate, " + Team.toString(Team.otherTeam(team)) + " has won");
	}

	// Updates the images displayed on the board for a move
	public void updateTile(Position position, String update) {
		tiles[position.row()][position.column()].setIcon(new ImageIcon(pieceToImage.get(update)));
	}

	// Remove image from a tile
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

	private void addComponentsToFrame() {
		frame.getContentPane().add(BorderLayout.CENTER, board);
		frame.getContentPane().add(BorderLayout.SOUTH, playerOptions);
		frame.getContentPane().add(BorderLayout.NORTH, gameStatus);
	}

	private void setupPlayerOptions() {
		playerOptions.add(fileMenuBar);
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
			updateType = UpdateType.MOVE;
			setChanged();
			notifyObservers(new Position(row, column));
			updateType = UpdateType.NONE;
		});
	}

	// Create buttons and add to panel
	private void setupBoardButtons() {
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

	// Get piece images from file
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
}
