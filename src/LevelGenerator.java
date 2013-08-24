import java.util.Random;

//This class handles level generation.
public class LevelGenerator {
	//IDEA:
	//TODO: steadily move generation methods from the level class to here.
	//TODO: many methods (such as adding stairs/monsters/items) are based on the assumption that the level contains rooms. Try making
	//		these methods more general. (alternately, change their names to indicate they are room-specific, and make other methods
	//		for other level types.
	public static final String EMPTY="empty";
	public static final String STANDARD="standard";
	public static final String CAVERN="cavern";
	public static final String GLITCH="glitch";
	//public static final String[] levelTypes=new String[20];		//TODO: delete this if it is never used, or use it if it is useful. (Could be used for xml stuff, maybe)
	
	public LevelGenerator(Level level, String levelType){		//for unique levels. "LevelType" is used instead of a branch, because only this particular level generator will use that type.
		this.level=level;					
		//TODO: if previous branch is null, there are no first stairs, only stairs which progress through the branch.				
		this.levelType=levelType;
		generateLevel();
	}
	
	public void generateLevel(){	//TODO: look for common steps between different level types, and move them up to where populate() is
		populate();
		switch(levelType){
		case(EMPTY):
			makeEmptyRoom();
			break;
		case(STANDARD):
			roomify();	
			addDoorsAndTunnels();
			addStairs();
			level.monsterGenerator=new MonsterGenerator(level);
			level.itemGenerator=new ItemGenerator(level);
			level.setGoldRange();
			//addTraps();	//what happened to traps? (try to remember/find where traps are added)
			break;
		case(CAVERN):
			cavernize();
			placeRandomStairs();
			level.monsterGenerator=new MonsterGenerator(level);
			level.itemGenerator=new ItemGenerator(level);
			level.setGoldRange();
			//addTraps();
			//TODO: itemGenerator
			break;
		case(GLITCH):
			ruinEverything();
			placeRandomStairs();
			//addTraps();
			//TODO: itemGenerator
			break;
		default:	//TODO: add error message saying "invalid level type" or something
			break;
		}
	}
	
	public void populate(){	//create a blank dungeon, ready to add rooms to.
		for (int i = 0; i < level.xSize; i++) {
			for (int j = 0; j < level.ySize; j++) {
				level.addBlankWall(i,j);
			}
		}
	}
	
	//empty room methods
	public void makeEmptyRoom(){	//make the entire level a large, empty room.
		level.addRoom(1,1,level.xSize-1,level.ySize-1);
	}
	
	public Room makeRoom(){			//TODO: FIX INFINITE ROOM ERROR
		int x1 = rng.nextInt(level.xSize-4);
		int y1 = rng.nextInt(level.ySize-4);
		int x2 = x1 + 4+ rng.nextInt(level.xSize-x1-4);
		int y2 = y1 + 4+ rng.nextInt(level.xSize-y1-4);
		if(level.roomForRoom(x1,y1,x2,y2))
			return new Room(level.floor, x1, y1, x2, y2, level.layout);	
		else
			return makeRoom();
	}
	
	//standard methods (may also be used in some non-standard generation types)
	public void roomify() {
		int roomCount=4+rng.nextInt(3);
		   while (level.roomcount < roomCount&&roomForAnyRoom()) {	//should this really be 6? 
		     level.addRoom(makeRoom());
		  level.roomcount++;
		   }
		 }
	
	private boolean roomForAnyRoom() {
		for(int i=0;i<level.xSize-6;i++){
			for(int j=0;j<level.ySize-6;j++){
				if(level.roomForRoom(i, j, i+6, j+6))
					return true;
			}
		}
		return false;
	}

	public void addDoorsAndTunnels(){	//randomly adds doors and tunnels to the rooms. (currently adds 2 doors and 2 tunnels per room.)
		for(int i=0;i<level.rooms.length&&level.rooms[i]!=null;i++){
			for(int j=0;j<2;j++){
				Tile[] roomWalls=level.getRoomWalls(level.rooms[i]);		//TODO: these parts should have their own methods once they work.
				Tile newDoor=roomWalls[level.rng.nextInt(level.rooms[i].getPerimeter())];
				while(newDoor==null)
					newDoor=roomWalls[rng.nextInt(level.rooms[i].getPerimeter())];
				if(newDoor!=null){
					level.addTunneler(newDoor.xCoord,newDoor.yCoord);
					level.addDoor(newDoor.xCoord,newDoor.yCoord);
					}
				}
			}
	}
	
	public void addStairs(){	//adds upstairs and downstairs.
		Room upStairsRoom=level.getRandomRoom();
		Room downStairsRoom=upStairsRoom;
		while(downStairsRoom==upStairsRoom)
			downStairsRoom=level.getRandomRoom();
		if(!level.firstInBranch())
			level.addRandomUpStairs(upStairsRoom);
		if(!level.lastInBranch())
			level.addRandomDownStairs(downStairsRoom);
	}
	
	//make specific amounts of "small", "medium", and "large" caverns. 
	//number of each cavern based on level size, meaning of each size based on Math.min(level.xSize, level.ySize))

	//cavern generation methods
	
	//TODO: since floor tiles are added in the same places repeatedly in cavern generation, add traps in a final level generation step.
	public void cavernize(){
		
		int xSize=level.xSize;
		int ySize=level.ySize;
		int centerX=0;
		int centerY=0;
		
		level.addWall(xSize/2,ySize/2);	//necessary as a starting point for the cavern building
		
		int cavernCount=Math.max((xSize+ySize)/4,2);
		for(int i=0;i<cavernCount;i++){
			while(centerX==0||centerY==0
				||(level.getTile(centerX,centerY).permanentIcon!='X')){		//instead of waiting for icon to be X, go to saved values (see TODO below)
				xSize=Math.max(level.xSize/5,rng.nextInt(level.xSize/2));
				ySize=Math.max(level.ySize/5,rng.nextInt(level.ySize/2));
				centerX=1+rng.nextInt(level.xSize-1);
				centerY=1+rng.nextInt(level.ySize-1);
			}
			carveCavern(xSize,ySize,centerX,centerY);
			placeWalls();
		}
	}
	
	public void carveCavern(int xSize, int ySize, int xCenter, int yCenter){
		int xMin=Math.max(xCenter-(xSize/2),1);
		int yMin=Math.max(yCenter-(ySize/2),1);
		
		int xMax=Math.min(xCenter+(xSize/2),level.xSize-1);
		int yMax=Math.min(yCenter+(ySize/2),level.ySize-1);
		
		int iterations=Math.max(((int)(((double)(xSize+ySize))/2.5)), 2);	//TODO: try different iteration count formulas.
		for(int i=0;i<iterations;i++){
			int x1=xMin+rng.nextInt(Math.max(xMax-xMin-4,1));
			int x2=x1+4+rng.nextInt(Math.max(xMax-x1-4,4));
			int y1=yMin+rng.nextInt(Math.max(yMax-yMin-4,1));
			int y2=y1+4+rng.nextInt(Math.max(yMax-y1-4,4));
			carveRectange(x1,y1,x2,y2);
		}
	}
	
	public void carveRectange(int x1, int y1, int x2, int y2){	//in contrast to addRoom, this method does not care what is currently in the tiles it carves.
		if(level.containsTile(x1,y1)&&level.containsTile(x2,y2)){
			for (int i = x1; i < x2; i++) {
				for (int j = y1; j <y2; j++) {
					level.addEmptyTile(i,j);
					level.layout[i][j].setIsRoom(false);
				}
			}
		}
	}
	
	//should this boolean be in Movement?
	private boolean adjacentToBlank(Tile tile){	//check to see if the tile is adjacent to a blank tile.

		char direction='1';
		while(direction!='2'){
			if(Movement.tileInDirection(level,tile,direction).permanentIcon==' ')
				return true;
			direction=Movement.rightOf(direction);
		}
		if(Movement.tileInDirection(level,tile,direction).permanentIcon==' ')	//extra check for when it gets to 2.
			return true;
		return false;
	}
	
	private void placeWalls(){
		for(int i=1;i<level.xSize-1;i++){
			for(int j=1; j<level.ySize-1;j++){
				Tile tile=level.getTile(i,j);
				if(tile.permanentIcon!=' '){
					if(adjacentToBlank(tile))
						level.addWall(i, j);
					else
						level.addEmptyTile(i, j);
				}
						
			}
		}
	}
	
	private void placeRandomStairs(){	//random place up and down stairs in a level, without worrying about rooms.
		Tile upStairTile=randomEmptyTile();
		if(!level.firstInBranch()){	
			level.addUpStairs(upStairTile.xCoord, upStairTile.yCoord);
		}
		Tile downStairTile=randomEmptyTile();
		if(!level.lastInBranch())
			level.addDownStairs(downStairTile.xCoord, downStairTile.yCoord);
	}
	
	private Tile randomEmptyTile(){	//random empty tile, regardless of room.
		int xPos=0; int yPos=0;
		while((xPos==0||yPos==0)
			||level.getTile(xPos,yPos).permanentIcon!=Level.EMPTY_TILE_ICON){
			xPos=1+rng.nextInt(level.xSize-1);
			yPos=1+rng.nextInt(level.ySize-1);
		}
		return level.getTile(xPos, yPos);
	}
	
	
	
	//glitch type generation methods
	
	private void ruinEverything(){		//I'll never love again ~ my world is ending
		for(int i=1;i<level.xSize-1;i++){
			for(int j=1;j<level.ySize-1;j++){
				level.addRandomTile(i, j);
			}
		}
	}
	
	private Random rng=new Random();
	private Level level;
	private String levelType;
}
