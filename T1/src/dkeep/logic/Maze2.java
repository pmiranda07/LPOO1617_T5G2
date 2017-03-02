package dkeep.logic;

public class Maze2 extends Map{
	
	public Maze2(){
		this.MAP_SIZE = 9;
		char[][]temp={{'X','X','X','X','X','X','X','X','X'} ,
					  {'I',' ',' ',' ',' ',' ',' ','K','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X',' ',' ',' ',' ',' ',' ',' ','X'} ,
					  {'X','X','X','X','X','X','X','X','X'} };
		
		int i = 0;
		for( char[] c : temp){
			this.map[i] = c;
			i++;
		}
		
		Position t1 = new Position(0,1,'S');
		endPositions.add(t1);
	}
	
	public void openDoors(){
		this.map[1][0] = 'S';
	}
	
	public Map nextMap(){
		return null;
	}

	public void pickUpKey(){
		this.map[1][8] = ' ';
	}

}