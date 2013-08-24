public class Room { 
//idea: maybe Room could extend Level, in order to borrow tile-adding methods? OR instead of using this class to create rooms, setters in Level
//could start with a fully blank level, then add rooms to blank areas.
  public Tile [][] tList;	//this isn't used right now. It might be better to have a "level" data item, to make sure the level's tiles are changed without shallow/deep copy issues
  public int floor;
  public int x1Coord;  //Bottom left corner of room (All rooms are going to be rectangular at this point)
  public int y1Coord;
  public int x2Coord; //Top right corner of room
  public int y2Coord;
  public Level roomLevel;	//the level this room is in
  public int isConnected;
  
	//Constructor
  public Room(int floor, int x1, int y1, int x2, int y2, Tile [][] layout) {
	this.floor = floor;
	x1Coord = x1;
	y1Coord = y1;
	x2Coord = x2;
	y2Coord = y2;
	//addTiles(layout);
	}
	
	//Add tiles to the rooms tile list
  public void addTiles(Tile [][] layout) {

   for (int i = x1Coord; i < x2Coord; i++) {
	for (int j = y1Coord; j < y2Coord; j++) {
	  layout[i][j].setIsRoom(true);
	  layout[i][j].setIcon(Level.EMPTY_TILE_ICON);
	}
   }
}
  
public boolean noRoom(){		//checks to see if there are any empty tiles
	for(int i=x1Coord;i<x2Coord;i++){
		for(int j=y1Coord;j<y2Coord;j++){
			if(roomLevel.getTile(i, j).isEmpty())
				return false;
		}
	}
	return true;
}
  
public boolean containsPlayer(){	//checks to see if the player is in the room. will be useful when fov is implemented.
	for(int i=x1Coord;i<x2Coord;i++){
		for(int j=y1Coord;j<y2Coord;j++){
			if(roomLevel.getTile(i, j).monster!=null
			&& roomLevel.getTile(i, j).monster.getClass()==Player.class)
				return true;
		}
	}
	return false;
}

//TODO: add a "containsMonster()" method to check if there are any monsters in the room. alternatively, count the monsters in the room and return an int. (useful for random monster generation.)

public boolean containsStairs(){
	return(containsUpStairs()||containsDownStairs());
}

public boolean containsUpStairs(){
	for(int i=x1Coord;i<x2Coord;i++){
		for(int j=y1Coord;j<y2Coord;j++){
			if(roomLevel.getTile(i, j).permanentIcon=='<')
				return true;
		}
	}
	return false;
}

public boolean containsDownStairs(){
	for(int i=x1Coord;i<x2Coord;i++){
		for(int j=y1Coord;j<y2Coord;j++){
			if(roomLevel.getTile(i, j).permanentIcon=='>')
				return true;
		}
	}
	return false;
}
  


 public int xSize(){
	 return x2Coord-x1Coord;
 }
 
 public int ySize(){
	 return y2Coord-y1Coord;
 }

public int getPerimeter() {
	return 2*(x2Coord-x1Coord)+2*(y2Coord-y1Coord)-4;	//-4 because corners are not included.
}
}