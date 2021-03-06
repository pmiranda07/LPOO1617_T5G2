package dkeep.gui;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dkeep.logic.*;
import dkeep.logic.Logic.status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;

/**
 * Window that suport the game
 * 
 * @author davidfalcao
 *
 */
public class GameFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private JButton btnNewGame;
	private JButton btnBackMenu;
	private JButton btnQuitGame;
	private JButton btnSave;
	private JPanel buttonsPanel1;
	private JButton btnUp;
	private JButton btnDown;
	private JButton btnLeft;
	private JButton btnRight;
	private GamePanel gamePanel;
	private Menu menu;
	private Logic game;
	
	private int guardType;
	private int nOgres;

	/**
	 * Class constructor.
	 * @param menu menu
	 * @param guardType type of the guard
	 * @param nOgres number of ogres
	 */
	public GameFrame(Menu menu, int guardType, int nOgres) {
		setTitle("Dungeon Explorer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		game = new Logic(new Maze1(), guardType, nOgres);
		this.menu = menu;
		this.nOgres = nOgres;
		this.guardType = guardType;
		gamePanel = new GamePanel();
		gamePanel.setBackground(new Color(30,65,39));
		buttonsPanel1 = new JPanel(new GridLayout(4, 1, 0, 10));
		setUpButtons();
		getContentPane().setLayout(new BorderLayout(0, 0));
		addButtons();
		getContentPane().add(gamePanel);
	}

	/**
	 * Close the window
	 */
	private void close()
	{
		this.dispose();
		
	}

	/**
	 * Inicialize the buttons
	 * 
	 */
	private void setUpButtons() {
		// Play Game button
		btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String msg = "Do you want to start a new game?";
				int res = JOptionPane.showConfirmDialog(rootPane, msg);

				if (res == JOptionPane.YES_OPTION) {
					setSize(1024, 600);

					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

					// starting new game with new options
					game = new Logic(new Maze1(), guardType, nOgres);
					buttonsPanel1.setVisible(true);
					gamePanel.setWin(false);
					gamePanel.setLose(false);
					updateGamePanel();
					gamePanel.repaint();

				}
				requestFocusInWindow();

			}
		});

		// Back to menu
		btnBackMenu = new JButton("Back to Menu");
		btnBackMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String msg = "Are you sure you want to go back to menu?";
				int res = JOptionPane.showConfirmDialog(rootPane, msg);

				if (res == JOptionPane.YES_OPTION) {

					close();
					menu = new Menu();
					menu.start();
				}
				requestFocusInWindow();

			}
		});

		// Quit Game button
		btnQuitGame = new JButton("Quit");
		btnQuitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String msg = "Are you sure you want to quit?";
				int res = JOptionPane.showConfirmDialog(rootPane, msg);
				
				
				if (res == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				requestFocusInWindow();

			}
		});

		btnUp = new JButton("Up");
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				updateGame('w');
				requestFocusInWindow();

			}
		});

		btnDown = new JButton("Down");
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				updateGame('s');
				requestFocusInWindow();

			}
		});

		btnLeft = new JButton("Left");
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				updateGame('a');
				requestFocusInWindow();

			}
		});

		btnRight = new JButton("Right");
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				updateGame('d');
				requestFocusInWindow();

			}
		});
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int res = chooser.showSaveDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					
					SaveLoad sv = new SaveLoad();
					String path = chooser.getSelectedFile().getAbsolutePath()+ ".ser";
					sv.save(game, path);
					

				}
				requestFocusInWindow();
				
			}
		});
	
	}

	/**
	 * Display the buttons in the window
	 * 
	 */
	private void addButtons() {
		gamePanel.add(btnNewGame,BorderLayout.NORTH);
		gamePanel.add(btnSave,BorderLayout.NORTH);
		gamePanel.add(btnBackMenu,BorderLayout.NORTH);
		gamePanel.add(btnQuitGame,BorderLayout.NORTH);
				
		buttonsPanel1.add(btnUp);
		buttonsPanel1.add(btnLeft);
		buttonsPanel1.add(btnRight);
		buttonsPanel1.add(btnDown);
		buttonsPanel1.setPreferredSize(new Dimension(50, 20));

		add(buttonsPanel1, BorderLayout.WEST);
	}

	/**
	 * Start the window
	 * 
	 */
	public void start() {
		setSize(1024, 600);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
		setVisible(true);
		requestFocusInWindow();
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyMovement(e);
			}
		});
		updateGamePanel();
		gamePanel.repaint();
	}

	/**
	 * Listener of keyword
	 * 
	 * @param e
	 */
	private void keyMovement(KeyEvent e) {
		char key;
		key = e.getKeyChar();

		if (key == 'w' || e.getKeyCode() == KeyEvent.VK_UP)
			updateGame('w');
		else if (key == 's' || e.getKeyCode() == KeyEvent.VK_DOWN)
			updateGame('s');
		else if (key == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT)
			updateGame('a');
		else if (key == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT)
			updateGame('d');

	}

	/**
	 * Updates the game
	 * 
	 * @param direction
	 */
	private void updateGame(char i) {
		if (game.condition == status.RUNNING) {
			moveAllCharacters(i);

			if (game.condition == status.RUNNING) {
				updateGamePanel();
			} else if (game.condition == status.DEFEAT) {
				lose();
			} else if (game.condition == status.WON) {
				win();
			}
			gamePanel.repaint();
			requestFocusInWindow();
		}
	}

	/**
	 * Update the game panel
	 * 
	 */
	private void updateGamePanel() {
		gamePanel.setMap(getMapGui());
		gamePanel.setObjectives(getObjectivesGui());
		gamePanel.setCharacters(getCharactersGui());
		
		//game.printMap();

		// Testar mapa GUI
		  
//		  
//		for (ArrayList<String> linha : getMapGui()) {
//			for (String a : linha) {
//				String temp;
//
//				if (a.equals(" "))
//					temp = "|    ";
//
//				else
//					switch (a.length()) {
//					case 1:
//						temp = "|" + a + "   ";
//						break;
//
//					case 2:
//						temp = "|" + a + "  ";
//						break;
//
//					case 3:
//						temp = "|" + a + " ";
//						break;
//
//					default:
//						temp = "|" + a;
//						break;
//					}
//
//				System.out.print(temp + " ");
//
//			}
//
//			System.out.println();
//		}
//		System.out.println();
		 
	}
	
	/**
	 * Call all movement function 
	 * 
	 * @param i
	 */
	private void moveAllCharacters(char i)
	{
		game = game.moveHero(i);
		game.atack_villains();
		game.moveAllVillains();
		game.atack_villains();
		game.Over();
	}
	
	/**
	 * Set the game as won
	 * 
	 */
	private void win()
	{
		gamePanel.setWin(true);
		buttonsPanel1.setVisible(false);
	}
	
	/**
	 * Set the game as lost 
	 *
	 */
	private void lose()
	{
		gamePanel.setLose(true);
		buttonsPanel1.setVisible(false);
	}
	
	/**
	 * Redefines the game Logic
	 * 
	 * @param game Atual game logic
	 */
	public void setGame(Logic game){
		this.game = game;
		
		
	}
	
	/**
	 * Returns all Characters 
	 * 
	 * @return characters
	 */
	public ArrayList<Position> getCharactersGui()
	{
		
		ArrayList<Position> array = new ArrayList<Position>();
		array.addAll(getLights());

		array.add(new Position(game.getHero().getPosition().getX(),game.getHero().getPosition().getY(), game.getHero() + ""));
		
		if (game.getGuard().isPlaying()) {
			array.add(new Position(game.getGuard().getPosition().getX(),game.getGuard().getPosition().getY(), game.getGuard() + ""));
		}

		getOgresGui(array);
		
		return array;
	}
	
	/**
	 * Return the lights (guard's torch)
	 * 
	 * @return positions
	 */
	public ArrayList<Position> getLights()
	{
		ArrayList<Position> temp = new ArrayList<Position>();
		
		if (game.getGuard().isPlaying())
			if (game.getGuard().isAwake())
			{
				int x = game.getGuard().getPosition().getX();
				int y = game.getGuard().getPosition().getY();

				temp.add(new Position(x - 2, y - 1, "L1"));
				temp.add(new Position(x - 1, y - 1, "L2"));
				temp.add(new Position(x, y - 1, "L3"));

				temp.add(new Position(x - 1, y, "L4"));
				temp.add(new Position(x, y, "L5"));
				temp.add(new Position(x + 1, y, "L6"));
				
				String a = getMapGui().get(y+1).get(x);
				String b = getMapGui().get(y+1).get(x+1);
				String c = getMapGui().get(y+1).get(x+2);
				
				
				if (a.equals("XS") || a.equals(" ") || a.equals("XC") || a.equals("XC1") || a.equals("X03") || a.equals("X07"))
					if (!a.equals("X07") || game.getGuard().getDirection() == 'D')
						temp.add(new Position(x, y + 1, "L7"));

				if (b.equals("XS") || b.equals(" ") || b.equals("XC") || b.equals("XC1") || b.equals("X03"))
					temp.add(new Position(x + 1, y + 1, "L8"));

				if (c.equals("XS") || c.equals(" ") || c.equals("X03") || c.equals("XC") || c.equals("XC1"))
					temp.add(new Position(x + 2, y + 1, "L9"));
	
			}
		
		
		
		
		return temp;
	}
	
	/**
	 * Returns all ogres
	 * 
	 * @param array array of positions
	 */
	public void getOgresGui(ArrayList<Position> array) {
		for (Ogre ogre : game.getOgres()) {
			if (ogre.getPosition().equals(game.getMap().getKey()))
				array.add(new Position(ogre.getPosition().getX(), ogre.getPosition().getY(), "OK"));
			else
				array.add(new Position(ogre.getPosition().getX(), ogre.getPosition().getY(), ogre + ""));

			if (ogre.getClubVisibily()) {
				array.add(new Position(ogre.getClub().getPosition().getX(), ogre.getClub().getPosition().getY(),
						ogre.getClub() + ""));

			}

		}

	}
	
	/**
	 * Return the key, lever and endpositions
	 * 
	 * @return array of positions
	 */
	public ArrayList<Position> getObjectivesGui() {
		ArrayList<Position> array = new ArrayList<Position>();
		boolean open = false;

		for (int y = 0; y < game.getMap().getMapSize(); y++)
			for (int x = 0; x < game.getMap().getMapSize(); x++) {
				if (game.getMap().getMap()[y][x] == 'I') {
					open = false;
					array.add(new Position(x, y, "I"));
				} else if (game.getMap().getMap()[y][x] == 'S') {
					open = true;
					array.add(new Position(x, y, "S"));
				}
			}

		if (open) {
			for (Position pos : game.getMap().getEndPositions()) {
				array.add(new Position(pos.getX(), pos.getY(), "E"));
			}

			if (game.getMap().getKey().getType() == 1)
				array.add(new Position(game.getMap().getKey().getX(), game.getMap().getKey().getY(), "LD"));
			else
				array.add(new Position(game.getMap().getKey().getX(), game.getMap().getKey().getY(), "K"));
		} else {
			if (game.getMap().getKey().getType() == 1)
				array.add(new Position(game.getMap().getKey().getX(), game.getMap().getKey().getY(), "LU"));
			else
				array.add(new Position(game.getMap().getKey().getX(), game.getMap().getKey().getY(), "K"));
		}

		return array;
	}

	/**
	 * Add perspective to the map
	 * 
	 * @param array array of position
	 */
	private void addPerspective(ArrayList<ArrayList<String>> array) {
		for (int i = 0; i < array.size(); i++)
			for (int j = 0; j < array.get(i).size(); j++) {
				if ((array.get(i).get(j) == "X03") || (array.get(i).get(j) == "X04")
						|| (array.get(i).get(j) == "X05")) {
					if (j + 1 < array.get(i).size())
						array.get(i).set(j + 1, "XC");
					else
						array.get(i).add("XC");
				} else if (array.get(i).get(j) == "X01")
				{
					if (j + 1 < array.get(i).size())
						array.get(i).set(j + 1, "XS");
					else
						array.get(i).add(j + 1, "XS");
				}
				else if (array.get(i).get(j) == "X08") {
					if (j + 1 < array.get(i).size())
						array.get(i).set(j + 1, "XC1");
					else
						array.get(i).add(j + 1, "XC1");
				}
				else if (array.get(i).get(j) == "X07") {
					if (j + 1 < array.get(i).size())
					{
						if (array.get(i).get(j +1) == " " )
							if (i == 0)
								array.get(i).set(j + 1, "XC1");
							else if (!(array.get(i-1).get(j) == " " ))
								array.get(i).set(j + 1, "XC1");
					}
					else {
						if (i != 0)
							array.get(i).add(j + 1, "XC1");
					}
				}
				


			}

	}

	/**
	 * Check if the wall is horizontal
	 * 
	 * @param wall string of wall
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String horizontalWall(String wall, boolean[] nsew)
	{
		wall = wall00(nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = wall01(nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = wall02(nsew);
		if (!wall.equals("X"))
			return wall;
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 0
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall00(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if ((n && !s && e && w) || (!n && !s && e && w))
		{
			return "X00";
		}
		else return "X";
		
	}
	
	/**
	 * Check if the wall is of type 1
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall01(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if ((!n && !s && !e && w) || (!n && !s && !e && !w) )
		{
			return "X01";
		}
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 2
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall02(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if ((n && !s && e && !w) || (!n && !s && e && !w))
		{
			return "X02";
		}
		
		return "X";
	}
	
	/**
	 * Check if the wall is vertical
	 * 
	 * @param wall string of wall
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String verticalWall(String wall, boolean[] nsew)
	{
		wall = wall03(nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = wall04(nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = wall05(nsew);
		if (!wall.equals("X"))
			return wall;
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 3
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall03(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if (n && s && !e && !w)
		{
			return "X03";
		}
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 4
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall04(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if (n && !s && !e && !w) 
		{
			return "X04";
		}
		
		return "X";
		
	}
	
	/**
	 * Check if the wall is of type 5
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall05(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if (!n && s && !e && !w) 
		{
			return "X05";
		}
		
		return "X";
		
	}
	
	/**
	 * Check if the wall is a crossing wall
	 * 
	 * @param wall string of wall
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String crossingWall(String wall, boolean[] nsew)
	{
		wall = wall06(nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = wall07(nsew);
		if (!wall.equals("X"))
			return wall;
		
		
		wall = wall08(nsew);
		if (!wall.equals("X"))
			return wall;
		
		return wall;
	}
	
	/**
	 * Check if the wall is of type 6
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall06(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if ((n && s && e && !w ) || (!n && s && e && !w ))
			return "X06";
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 7
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall07(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if ((n && s && e && w ) || (!n && s && e && w ) || (n && s && !e && w ) || (!n && s && !e && w ) )
			return "X07";
		
		return "X";
	}
	
	/**
	 * Check if the wall is of type 8
	 * 
	 * @param nsew north? south? east? weast?
	 * @return wall
	 */
	private String wall08(boolean[] nsew)
	{
		boolean n = nsew[0], s = nsew[1], e = nsew[2], w = nsew[3];
		
		if (n && !s && !e && w)
		{
			return "X08";
		}
		
		return "X";
	}

	/**
	 * Return the gui representation of the wall
	 * 
	 * @param wall string of wall
	 * @param x x position
	 * @param y y position
	 * @return wall
	 */
	private String typeOfWall(String wall, int x, int y )
	{
		if (!wall.equals("X")) {
			return " ";
		}

		char[][] map1 = game.getMap().getMap();
		boolean n, s, w, e;

		if (x == 0) {
			w = false;
			e = (map1[y][x + 1] == 'X');
		} else if (x == game.getMap().getMapSize() - 1) {
			w = (map1[y][x - 1] == 'X');
			e = false;
		} else {
			w = (map1[y][x - 1] == 'X');
			e = (map1[y][x + 1] == 'X');
		}

		if (y == 0) {
			n = false;
			s = (map1[y + 1][x] == 'X');
		} else if (y == game.getMap().getMapSize() - 1) {
			n = (map1[y - 1][x] == 'X');
			s = false;
		} else {
			n = (map1[y - 1][x] == 'X');
			s = (map1[y + 1][x] == 'X');
		}
		
		boolean[] nsew = {n,s,e,w};
		
		wall = horizontalWall(wall, nsew);
		
		if (!wall.equals("X"))
			return wall;
		
		wall = verticalWall(wall,nsew);
		if (!wall.equals("X"))
			return wall;
		
		wall = crossingWall(wall, nsew);
		if (!wall.equals("X"))
			return wall;
		
		return "ERROR";
		

	}
	
	/**
	 * Returns the gui map
	 * 
	 * @return map
	 */
	private ArrayList<ArrayList<String>> getMapGui()
	{
		ArrayList<ArrayList<String>> board = new ArrayList<ArrayList<String>>();

		for (int k = 0; k < game.getMap().getMapSize(); k++) {
			ArrayList<String> line = new ArrayList<String>();
			for (int i = 0; i < game.getMap().getMapSize(); i++) {
				String temp;
				temp = "" + game.getMap().getMap()[k][i];

				temp = typeOfWall(temp, i, k);
				line.add(temp);
			}
			board.add(line);

		}
		addPerspective(board);
		return board;
	}
	
	
	
	
}