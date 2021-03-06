package dkeep.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * Represents a game
 * 
 * @author davidfalcao
 *
 */
public class Logic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	 * @param typeGuard type of Guard
	 * @param nOgres number of ogres
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
		ArrayList<ArrayList<Integer>> temp = map.getInitValues();
		initHero(temp.get(0));
		initGuard(temp.get(1));
		initOgres(temp.get(2));

	}
	
	/**
	 * Init the hero in current map
	 * 
	 * @param heroArr - arraylist with initialization of all
	 * hero atributes
	 * 
	 */
	private void initHero(ArrayList<Integer> heroArr){
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
		
		hero = new Hero(heroArr.get(0), heroArr.get(1), hero_has_key, hero_is_armed);
		
	}
	
	/**
	 * Init the hero in current map
	 * 
	 * @param guardArr - arraylist with initialization of all
	 * guard atributes
	 * 
	 */
	private void initGuard(ArrayList<Integer> guardArr)
	{
		boolean guard_playing;
		if (guardArr.get(2) == 0)
			guard_playing = false;
		else guard_playing = true;
		
		if (typeGuard == 0)
			guard = new Rookie(guardArr.get(0), guardArr.get(1), guard_playing);
		else if(typeGuard == 1)
			guard = new Drunken(guardArr.get(0), guardArr.get(1), guard_playing);
		else guard = new Suspicious(guardArr.get(0), guardArr.get(1), guard_playing);
	}
	
	/**
	 * Init the hero in current map
	 * 
	 * @param heroArr - arraylist with initialization of all
	 * ogre atributes
	 * 
	 */
	private void initOgres(ArrayList<Integer> ogreArr) {
		if (ogreArr.get(0) == 1) {

			for (int i = 0; i < nOgres; i++) {
				Position pos;
				do {
					pos = new Position(rand.nextInt(map.getMapSize() - 3) + 1, rand.nextInt(map.getMapSize() - 3) + 1,'O');
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

		if (heroKilledByGuard())
			return true;

		for (Ogre ogre : ogres) {
			if (heroKilledByOgre(ogre) || heroKilledByClub(ogre))
				return true;
		}
		
		return false;
	}

	/**
	 * Check if the hero was catch by the guard
	 * 
	 * @return true or false
	 */
	public boolean heroKilledByGuard()
	{
		if (guard.isPlaying()) {
			if (guard.isAwake()) {
				for (Position pos : guard.getPosition().getSurroundings()) {
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
	 * Check if the hero was killed by the ogre
	 * 
	 * @param ogre
	 * @return true or false
	 */
	public boolean heroKilledByOgre(Ogre ogre)
	{
		if (!hero.is_armed()) {
			for (Position pos : ogre.getPosition().getSurroundings()) {
				if (pos.equals(hero.getPosition())) {
					condition = status.DEFEAT;
					return true;
				}
			}

		}
		return false;
	}
	
	/**
	 * Check if the hero was killed by a massive club
	 * 
	 * @param ogre
	 * @return
	 */
	public boolean heroKilledByClub(Ogre ogre)
	{
		if (ogre.getClubVisibily())
		{
			for (Position pos : ogre.getClub().getPosition().getSurroundings()) {
				if (pos.equals(hero.getPosition())) {
					condition = status.DEFEAT;
					return true;
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

		heroPickUpKey();
		heroOpenDoors();

	}
 
	/**
	 * Hero pick up a key
	 */
	private void heroPickUpKey() {
		if (hero.getPosition().equals(map.getKey())) {
			switch (map.getKey().getType()) {
			case 1:
				map.changeDoors();
				hero.comeBack();
				break;

			case 2:
				hero.pickUpKey();
				map.pickUpKey();
				break;
			}
		}
	}
	
	/**
	 * Hero has a key and he opens the doors
	 */
	private void heroOpenDoors() {
		if (map.getMap()[hero.getPosition().getY()][hero.getPosition().getX()] == 'I') {
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
		if (hero.getPosition().distance(temp) <= 4) // 4 for default, it can be changed
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
		Position temp1 = hero.getPosition();
		Position temp = tryToMoveHero(direction);

		if (temp.equals(temp1))
			return this;
		
		updateHeroMovement(temp);
		checkObjectives();
		
		return checkLevel();
	}

	/**
	 * Returns a temporary position of hero acording to the input
	 * 
	 * @param direction
	 * @return
	 */
	public Position tryToMoveHero(char direction) {
		Position temp = new Position();
		if ('w' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 4);
		else if ('a' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 3);
		else if ('s' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 2);
		else if ('d' == direction)
			temp = hero.moveCharacter(map.getMapSize(), 1);

		return temp;
	}
	
	/**
	 * Check if the movement is possible and update the hero's position
	 * 
	 * @param temp position
	 */
	public void updateHeroMovement(Position temp)
	{
		if (map.isFreeForHero(temp.getX(), temp.getY()) && positionClear(temp)) {
			hero.updateLastPosition();
			hero.setPosition(temp.getX(), temp.getY());
			hero.updateDirection();
		}
	
	}
	
	/**
	 * Check if the hero passed the level and if the game has another level
	 * 
	 * @return Logic
	 */
	public Logic checkLevel()
	{
		if (levelUp()) {
			if (map.nextMap() != null)
				return new Logic(map.nextMap(), typeGuard, nOgres);
			else
				condition = status.WON;
		}

		return this;
	}
	
	/**
	 * Move all villains according to the level
	 * 
	 */
	public void moveAllVillains() {
		Position pos = new Position();
		
		moveGuard(pos);
		moveOgres(pos);
		
	}

	/**
	 * Move the guard
	 * 
	 * @param pos
	 */
	private void moveGuard(Position pos)
	{
		if (guard.isPlaying()) {
			do {
				guard.updateLastPosition();
				pos = guard.moveCharacter(map.getMapSize());
				guard.updateDirection();
			} while (!this.map.isFree(pos.getX(), pos.getY()));
		}
	}
	
	/**
	 * Move all ogres
	 * 
	 * @param pos
	 */
	private void moveOgres(Position pos){
		for (Ogre ogre : ogres) {
			if (ogre.isPlaying()) {
				do {
					pos = ogre.moveCharacter(map.getMapSize());
				} while (!map.isFree(pos.getX(), pos.getY()));

				ogre.updateLastPosition();
				ogre.setPosition(pos.getX(), pos.getY());
				ogre.updateDirection();
				moveClub(pos,ogre);
				

			}
		}
		
	}
	
	/**
	 * Move the club of all ogres
	 * 
	 * @param position
	 * @param ogre
	 */
	private void moveClub(Position pos, Ogre ogre){
		pos = ogre.moveClub();

		if (map.isFree(pos.getX(), pos.getY()) && positionClear(pos)) {
			ogre.setClub(pos);
		} else
			ogre.setClubNotVisible();
		
	}
	
	/**
	 * Hero atacks villains if they are surrounding him
	 * 
	 */
	public void atack_villains() {
		if (hero.is_armed()) {
			for (Position pos : hero.getPosition().getSurroundings()) {
				for (int i = 0; i < ogres.size(); i++) {
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
	 * Returns ogre (used only for tests)
	 * 
	 * @return ogre
	 */
	public Ogre getOgre()
	{
		return ogres.get(0);
		
	}
	
	/**
	 * Returns all the ogres
	 * 
	 * @return ogres
	 */
	public ArrayList<Ogre> getOgres()
	{
		return ogres;
	}
	
	/**
	 * Prints the current map on the console
	 */
	public void printMap() {

		char[][] map1 = map.getMap();

		int tam = getAllCharacters().size();
		ArrayList<Character> temp = getAllCharacters();

		for (int k = 0; k < tam; k++) {
			map1[temp.get(k).getPosition().getY()][temp.get(k).getPosition().getX()] = temp.get(k).getRepresentation();
		}
		for (int i = 0; i < map1.length; i++) {
			for (int k = 0; k < map1.length; k++) {
				System.out.print(map1[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println();

	}

}
