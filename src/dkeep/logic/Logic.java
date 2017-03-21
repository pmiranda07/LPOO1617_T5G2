package dkeep.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Logic {
	private Map map;
	private Guard guard; 
	private ArrayList<Ogre> ogres = new ArrayList<Ogre>();
	private Hero hero;
	public status condition = status.RUNNING;
	private Random rand = new Random();
	private int nOgres;
	private int typeGuard = rand.nextInt(3);
	
	/**
	 * Game status
	 * 
	 * @author davidfalcao
	 *
	 */
	public enum status {
		WON, DEFEAT, RUNNING
	}
	
	/**
	 * Level Constructor
	 * 
	 * @param map
	 */
	public Logic(Map map, int typeGuard, int nOgres) {
		this.map = map;
		this.typeGuard = typeGuard;
		this.nOgres = nOgres;
		initCharacters();	
	}

	/**
	 * Init all the characters based on current map
	 * 
	 */
	private void initCharacters()
	{
		Random rand = new Random();
		ArrayList<ArrayList<Integer>> temp = map.getInitValues();
		
		/**
		 * Init Hero
		 * 
		 */
		ArrayList<Integer> heroArr = temp.get(0);
		int heroX = heroArr.get(0);
		int heroY = heroArr.get(1);
		int hero_key = heroArr.get(2);
		boolean hero_has_key;
		if (hero_key == 0)
			hero_has_key = false;
		else hero_has_key = true;
		int hero_armed = heroArr.get(3);
		boolean hero_is_armed;
		if (hero_armed ==0)
			hero_is_armed = false;
		else hero_is_armed = true;
		
		hero = new Hero(heroX, heroY, hero_has_key, hero_is_armed);
		
		/**
		 * Init Guard
		 * 
		 */
		ArrayList<Integer> guardArr = temp.get(1);
		int guardX = guardArr.get(0);
		int guardY = guardArr.get(1);
		int guard_play = guardArr.get(2);
		boolean guard_playing;
		if (guard_play == 0)
			guard_playing = false;
		else guard_playing = true;
		

		switch (typeGuard) {
		
		case 0:
			guard = new Rookie(guardX, guardY, guard_playing);
			break;
	
		case 1:
			guard = new Drunken(guardX, guardY, guard_playing);
			break;

		case 2:
			guard = new Suspicious(guardX, guardY, guard_playing);
			break;

		}
		
		/**
		 * Init Ogres
		 * 
		 */
		ArrayList<Integer> ogreArr = temp.get(2);
		
		int ogre_play = ogreArr.get(0);
		boolean ogre_playing;
		if (ogre_play == 0)
			ogre_playing = false;
		else ogre_playing = true;

		if (ogre_playing) {

			for (int i = 0; i < nOgres; i++) {

				int x;
				int y;
				Position pos;

				do {
					x = rand.nextInt(map.getMapSize() - 3) + 1;
					y = rand.nextInt(map.getMapSize() - 3) + 1;

					pos = new Position(x, y, 'O');

				} while (!secureStart(pos));

				ogres.add(new Ogre(pos.getX(), pos.getY()));
			}
			
		}
		
	}
	
	/**
	 * Check if the hero has passed the level
	 * 
	 * @return true or false
	 */
	public boolean levelUp() {
		for (Position end : map.getEndPositions()) {
			if (hero.getPosition().equals(end))
				return true;
		}

		return false;
	}

	/**
	 * Check if the villains catch the hero and the game ends
	 * 
	 * @return true or false
	 */
	public boolean Over() {

		if (guard.isPlaying()) {
			if (guard.isAwake()) {
				for (Position pos : guard.getPosition().getSurroundings()) {
					if (pos.equals(hero.getPosition()))
					{
						condition = status.DEFEAT;
						return true;
					}	
				}

			}
		}
		
		for (Ogre ogre : ogres)
		{
			if (!hero.is_armed())
			{
				for (Position pos : ogre.getPosition().getSurroundings()) {
					if (pos.equals(hero.getPosition())) {
						condition = status.DEFEAT;
						return true;
					}
				}
				
			}
			
			
			if (ogre.getClubVisibily())
			{
				for (Position pos : ogre.getClub().getPosition().getSurroundings()) {
					if (pos.equals(hero.getPosition())) {
						condition = status.DEFEAT;
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Check level objectives such as keys and levers
	 * 
	 */
	private void checkObjectives() {

		if (hero.getPosition().equals(map.getKey())) {
			int type = map.getKey().getType();

			switch (type) {
			case 1:
				map.changeDoors();
				hero.comeBack();
				break;

			case 2:
				hero.pickUpKey();
				map.pickUpKey();
				break;

			}
		} else if (map.getMap()[hero.getPosition().getY()][hero.getPosition().getX()] == 'I') {
			if (hero.hasKey()) {
				map.openDoors();
				hero.comeBack();
			} else {
				hero.comeBack();
			}
		}

	}
 
	/**
	 * Check if there is no ogre stunned or guard sleeping in a certain position
	 * 
	 * @param temp
	 * @return true or false
	 */
	private boolean positionClear(Position temp) {
		if (temp.equals(guard.getPosition()))
			return false;

		for (Ogre ogre : ogres) {
			if (temp.equals(ogre.getPosition()))
				return false;
		}
		
		return true;

	}

	/**
	 * Determine if the position of the ogre is far enought for a secure start of the hero 
	 * We set this distance as more than 3 positions
	 * 
	 * @param position
	 * @return true or false
	 */
	private boolean secureStart(Position temp)
	{
		if (hero.getPosition().distance(temp) <= 3) // 3 for default, it can be changed
			return false;
		else return true;
	}
	
	/**
	 * Move hero and check if hero has passed the level. Case affirmative, returns a new level.
	 * 
	 * @param direction
	 * @return Logic
	 */
	public Logic moveHero(char direction) {
		Position temp;

		if ('w' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 4);
		else if ('a' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 3);
		else if ('s' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 2);
		else if ('d' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 1);
		else
			return this;


		if (map.isFreeForHero(temp.getX(), temp.getY()) && positionClear(temp))
		{
			hero.updateLastPosition();
			hero.setPosition(temp.getX(), temp.getY());
			hero.updateDirection();
		}

		checkObjectives();
		
		if (levelUp()) {
			if (map.nextMap() != null)
				return new Logic(map.nextMap(), typeGuard, nOgres);
			else condition = status.WON;
		}
		return this;
	}

	/**
	 * Move all villains according to the level
	 * 
	 */
	public void moveAllVillains() {
		Position pos;
		if (guard.isPlaying()) {
			do {
				guard.updateLastPosition();
				pos = guard.moveCharacter(map.getMapSize());
				guard.updateDirection();
			} while (!this.map.isFree(pos.getX(), pos.getY()));
			

		}
		
		for (Ogre ogre : ogres) {
			if (ogre.isPlaying()) {
				do {
					pos = ogre.moveCharacter(map.getMapSize());
				} while (!map.isFree(pos.getX(), pos.getY()));

				ogre.updateLastPosition();
				ogre.setPosition(pos.getX(), pos.getY());
				ogre.updateDirection();
			}
		}
		for (int k = 0; k < ogres.size(); k++) {
			pos = ogres.get(k).moveClub();

			if (map.isFree(pos.getX(), pos.getY()) && positionClear(pos)) {
				ogres.get(k).setClub(pos);
			} else
				ogres.get(k).setClubNotVisible();

		}

	}

	/**
	 * Hero atacks villains if they are surrounding him
	 * 
	 */
	public void atack_villains()
	{
		if (hero.is_armed())
		{
			for (Position pos : hero.getPosition().getSurroundings())
			{
				for (int i = 0; i < ogres.size(); i++)
				{
					if (ogres.get(i).getPosition().equals(pos))
						ogres.get(i).stun();
				}
				
			}
			
		}
		
	}
	
	/**
	 * Returns current map
	 * 
	 * @return map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Return a ArrayList with all character of a level
	 * 
	 * @return
	 */
	public ArrayList<Character> getAllCharacters() {
		ArrayList<Character> temp = new ArrayList<Character>();
		temp.add(hero);

		if (guard.isPlaying())
			temp.add(guard);
		

		for (Ogre ogre : ogres)
		{
			if (ogre.isPlaying())
			{
				if (ogre.getPosition().equals(map.getKey()))
					ogre.setRepresentation('$');
				else if (!ogre.isStunned())
					ogre.setRepresentation('O');
				else ogre.setRepresentation('8');
					
				temp.add(ogre);
				
				if (ogre.getClubVisibily())
				{
					if (ogre.getClub().getPosition().equals(map.getKey()))
						ogre.getClub().setRepresentation('$');
					else ogre.getClub().setRepresentation('*');
					temp.add(ogre.getClub());
				}
			}
		}

		return temp;
	}
	
	/**
	 * Redefines hero 
	 * 
	 * @param hero
	 */
	public void setHero(Hero hero)
	{
		this.hero = hero;
	}
	
	/**
	 * Redefines ogre
	 * 
	 * @param ogre
	 */
	public void setOgre(Ogre ogre)
	{
		ogres.add(ogre);
	}
	
	/**
	 * Returns hero
	 * 
	 * @return hero
	 */
	public Hero getHero()
	{
		return hero;
		
	}

	/**
	 * Returns guard
	 * 	
	 * @return guard
	 */
	public Guard getGuard()
	{
		return guard;
	}
	
	/**
	 * Returns ogre
	 * 
	 * @return ogre
	 */
	public Ogre getOgre()
	{
		return ogres.get(0);
		
	}
	
	
	public void addPerspective(ArrayList<ArrayList<String>> array)
	{
		for (int i = 0; i < array.size(); i++)
		{
			for (int j = 0; j < array.get(i).size(); j++)
			{
				if (array.get(i).get(j) == "X02")
				{
					if (j + 1 < array.get(i).size())
						array.get(i).set(j+1, "XC02");
					else array.get(i).add("XC02");
				}
				else if (array.get(i).get(j) == "X03")
				{
					array.get(i).set(j+1, "XS");
				}

			}
			
		}
			
		
	}
	
	public String typeOfWall(String a , int x , int y) // implementar
	{
		char[][] map1 = map.getMap();
		
		if (!a.equals("X"))
			return " ";
		
		boolean n, s , w, e;
		
		if (x == 0)
		{
			w = false;
			e = (map1[y][x+1] == 'X');
		}
		else if (x ==  map.getMapSize()-1)
		{
			w = (map1[y][x-1] == 'X');
			e = false;
		}
		else {
			w = (map1[y][x-1] == 'X');
			e = (map1[y][x+1] == 'X');		
		}
			
		
		if (y == 0)
		{
			n = false;
			s = (map1[y+1][x] == 'X');
		}
		else if (y ==  map.getMapSize()-1)
		{
			n = (map1[y-1][x] == 'X');
			s = false;
		}
		else {
			n = (map1[y-1][x] == 'X');
			s = (map1[y+1][x] == 'X');		
		}
			
		
		if (!n && !s && w && e)
		{
			// parede a este e oeste
			return "X00";
		}
		
		if (!n && !s && !w && e)
		{
			// parede a este
			a = "X01";
			return a;
		}
		
		if (n && s && !w && !e)
		{
			// parede a norte e sul
			a = "X02";
			return a;
		}
		
		if (!n && s && !w && !e)
		{
			// parede a sul
			a = "X02";
			return a;
		}
		
		if (!n && !s && w && !e)
		{
			// parede a oeste
			a = "X03";
			return a;
		}
		
		if (!n && !s && !w && e)
		{
			// parede a este
			a = "X04";
			return a;
		}
		
		if (!n && !s && w && e)
		{
			// parede a este e oeste
			a = "X05";
			return a;
		}
		
		
		if (n && s && w && e)
		{
			// parede em todas as direções
			a = "X06";
			return a;
			
		}
		
		if (!n && s && w && e)
		{
			// parede em todas as direções menos norte
			a = "X06";
			return a;
		}
		
		if (n && !s && w && e)
		{
			// parede em todas as direções menos sul
			a = "X07";
			return a;
		}
		
//		if (n && s && !w && e)
//		{
//			// parede em todas as direções menos oeste
//			a = "X09";
//			return a;
//		}
		
		if (n && s && w && !e)
		{
			// parede em todas as direções menos este
			a = "X10";
			return a;
		}
		
		if (/*!n &&*/ s && !w && e)
		{
			// parede a sul e este
			a = "X11";
			return a;
		}
		
		if (n && s && !w && e)
		{
			// parede a sul, norte e este
			a = "X11";
			return a;
		}
		
		return a;
	}
	
	public ArrayList<Position> getObjectivesGui()
	{
		ArrayList<Position> array = new ArrayList<Position>();
		boolean open = false;
		
		for (int y = 0; y < map.getMapSize(); y++)
			for (int x = 0; x < map.getMapSize(); x++)
			{
				if (map.getMap()[y][x]== 'I')
				{
					open = false;
					array.add(new Position(x,y,"I"));
				}
				else if (map.getMap()[y][x]== 'S')
				{
					open = true;
					array.add(new Position(x,y,"S"));
				}
			}
		
		
		if (open)
		{
			for (Position pos : map.getEndPositions())
			{
				array.add(new Position(pos.getX(),pos.getY(),"E"));
			}
			
			if (map.getKey().getType() == 1)
				array.add(new Position(map.getKey().getX(),map.getKey().getY(), "LD"));	
			else array.add(new Position(map.getKey().getX(),map.getKey().getY(), "K"));	
		}
		else {
			if (map.getKey().getType() == 1)
				array.add(new Position(map.getKey().getX(),map.getKey().getY(), "LU"));	
			else array.add(new Position(map.getKey().getX(),map.getKey().getY(), "K"));	
		}
		
		return array;
	}
	
	public ArrayList<Position> getCharactersGui()
	{
		ArrayList<Position> array = new ArrayList<Position>();
		
		int x = hero.getPosition().getX();
		int y = hero.getPosition().getY();
		boolean armed = hero.is_armed();
		boolean key = hero.hasKey();
		String representation = "";
		
		if (hero.getDirection() == 'L' || hero.getDirection() == 'D') // front
		{
			if (!armed && !key)
				representation = "HF";
			else if (armed && !key)
				representation = "HFA";
			else if (!armed && key)
				representation = "HFK";
			else if (armed && key)
				representation = "HFAK";
	
		}
		else if (hero.getDirection() == 'U' || hero.getDirection() == 'R') //back
		{
			if (!armed)
				representation = "HB";
			else if (armed)
				representation = "HBA";
		}
		
		array.add(new Position(x,y,representation));
		
		if (guard.isPlaying())
		{
			x = guard.getPosition().getX();
			y = guard.getPosition().getY();
			
			if (guard.getDirection() == 'L' || guard.getDirection() == 'D') // front
			{
				if (guard.isAwake())
					representation = "GF";
				else representation = "GFS";
				
			}
			else if(guard.getDirection() == 'U' || guard.getDirection() == 'R') // back
			{
				if (guard.isAwake())
					representation = "GB";
				else representation = "GBS";
			}

		}
		array.add(new Position(x,y,representation));
		
		
		for (Ogre ogre : ogres)
		{
			x = ogre.getPosition().getX();
			y = ogre.getPosition().getY();
			
			if (ogre.getDirection() == 'L' || ogre.getDirection() == 'D') // front
			{
				if (ogre.isStunned())
					representation = "OFS";
				else representation = "OF";
				
			}
			else if(ogre.getDirection() == 'U' || ogre.getDirection() == 'R') // back
			{
				if (ogre.isStunned())
					representation = "OBS";
				else representation = "OB";
			}
			
			if (ogre.getPosition().equals(map.getKey()))
				representation = "OK";
			
			array.add(new Position(x,y,representation));
			
			if (ogre.getClubVisibily())
			{
				x = ogre.getClub().getPosition().getX();
				y = ogre.getClub().getPosition().getY();
				
				switch(ogre.getClub().getRepresentation())
				{
				case '*':
					representation = "*";
					break;
					
				case '$':
					representation = "*K";
					break;
				}
				
				
			}
			
		}
		
		
		
		
		return array;
	}
	
	public ArrayList<ArrayList<String>> getMapGui()
	{
		ArrayList<ArrayList<String>> board = new ArrayList<ArrayList<String>>();
		
		for (int k = 0; k < map.getMapSize(); k++) {
			ArrayList<String> line = new ArrayList<String>();
			for (int i = 0; i < map.getMapSize(); i++) {
				
				String temp;
				temp = "" + map.getMap()[k][i];
				
				temp = typeOfWall(temp,i,k);
				
				
				line.add(temp);
				
				
			}
			board.add(line);

		}
		
		addPerspective(board);
		return board;
	}
	
}
