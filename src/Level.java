import java.util.Random;

public class Level {
	public Dungeon levelDungeon;
	private TurnCounter turnCounter=new TurnCounter(this);
	private Random dice=new Random();
	public Stairs[] downStairs = new Stairs[10];
	public Stairs[] upStairs = new Stairs[10];
	
	public Tunneler tunnelMaker;
	public Tile [][] layout;
	public Room[] rooms=new Room[10];
	
	private Branch levelBranch;
	
	public int floor;

	public int xSize = 35; 
	public int ySize = 30;
	public int roomcount;
	
	public MonsterGenerator monsterGenerator=null;
	public ItemGenerator itemGenerator=null;
	
	public Monster[] levelMonsters=new Monster[300];
	Random rng=new Random();
	
	public final static char EMPTY_TILE_ICON = '·';
	public final static char WALL_ICON = 'X';
	public final static char CLOSED_DOOR_ICON = '+';
	public final static char VERTICAL_OPEN_DOOR_ICON = '-';
	public final static char HORIZONTAL_OPEN_DOOR_ICON = '|';
	public final static char TUNNEL_ICON = '#';
	public final static char UP_STAIRS_ICON = '<';
	public final static char DOWN_STAIRS_ICON = '>';
	public final static char TRAP_ICON = '^';
	//public final static char SHRINE_ICON = 'Û©';
	
	public final static char[] ICONS={EMPTY_TILE_ICON, WALL_ICON, CLOSED_DOOR_ICON,VERTICAL_OPEN_DOOR_ICON
		,HORIZONTAL_OPEN_DOOR_ICON,TUNNEL_ICON,UP_STAIRS_ICON,DOWN_STAIRS_ICON,TRAP_ICON};
	
	
	//Constructors					
	
	public Level(){	//a blank level, to be edited later.
		levelBranch=null;
		this.floor=-1;
		layout = new Tile[xSize][ySize];
	}
	
	public Level(int floor, Branch branch) {	
	
		setBranch(branch);
		this.floor=floor;
		layout = new Tile[xSize][ySize];
		LevelGenerator generator=new LevelGenerator(this,branch.levelType);
	}
	
	public Level(int floor, Branch branch, String type){	//for unique levels. Note that this type of level does use its branch. (a branch has it, however.)
		setBranch(branch);
		this.floor=floor;
		layout = new Tile[xSize][ySize];
		LevelGenerator generator=new LevelGenerator(this,type);	
	}
	
	public void overwrite(Level newLevel){
		layout =newLevel.layout;
	}
	
	public int levelArea(){
		return xSize*ySize;
	}
	
	public void startTurnCounter(){
		turnCounter.startTurnCounter(this);
	}
	
	//checks to see if there is room to put a room at the given place.
	public boolean roomForRoom(int x1, int y1, int x2, int y2){		//TODO: write another version of this to check if there is room for *any* room (create the smallest possible room, then use a nested for loop to try to fit it everywhere.
		
		if((x2-x1)>5*(y2-y1)
		|| ((y2-y1)>5*(x2-x1))
		|| x1-2<0 || x2+2>xSize
		|| y1-2<0 || y2+2>ySize
		|| x2-x1>12 || y2-y1>12)
			return false;
		
		for(int i=x1;i<=x2;i++){
			for(int j=y1;j<=y2;j++){
				if(getTile(i,j).icon!=' ')
					return false;
				if(i==x1){
					if(containsTile(i-1,j)
					&& getTile(i-1,j).icon!=' ')
						return false;
				}
				if(i==x2){
					if(containsTile(i+1,j)
					&& getTile(i+1,j).icon!=' ')
						return false;
				}
				if(j==y1){
					if(containsTile(i,j-1)
					&& getTile(i,j-1).icon!=' ')
						return false;
				}
				if(j==y2){
					if(containsTile(i,j+1)
					&& getTile(i,j+1).icon!=' ')
						return false;
				}
			}
		}
		return true;
	}
	
	public void addRoom(Room addedRoom){
		addRoomToList(addedRoom);
		for (int i = addedRoom.x1Coord; i < addedRoom.x2Coord; i++) {
			for (int j = addedRoom.y1Coord; j < addedRoom.y2Coord; j++) {
			if(i==addedRoom.x1Coord||j==addedRoom.y1Coord||i== addedRoom.x2Coord-1||j== addedRoom.y2Coord-1)
				addWall(i,j);
			else{
				addEmptyTile(i,j);
				layout[i][j].setIsRoom(true);
				}
			}
		}
	}
	
		public void addRoom(int x1, int y1, int x2, int y2){	//add a room at the specified position 		
			addRoom(makeRoom(x1, y1, x2, y2));
		}
		//Creates rooms for a level
		public Room makeRoom(int x1, int y1, int x2, int y2) {
		return new Room(floor, x1, y1, x2, y2, layout);		
		
		}
	
	public Room getRoom(int index){
		if(index>=0&&index<rooms.length)
			return rooms[index];
		else
			return null;
	}
	
	public Room getRandomRoom(){ //pick a random room from the level.
		Room randomRoom = null;
		while(randomRoom==null||randomRoom.noRoom())
			randomRoom = rooms[rng.nextInt(rooms.length)];
		return randomRoom;
	}
	
	public Tile randomEmptyRoomTile(Room chosenRoom){	//pick a random tile from a room.
		int xRelative=-1;
		int yRelative=-1;
		while(getTile(chosenRoom,xRelative,yRelative).icon!=EMPTY_TILE_ICON){
			xRelative=1+rng.nextInt(chosenRoom.xSize()-1);
			yRelative=1+rng.nextInt(chosenRoom.ySize()-1);
		}
		//return layout[xRelative][yRelative];
		return getTile(chosenRoom,xRelative,yRelative);
	}
	
	
	public void addRoomToList(Room addedRoom){
		int index=0;
		while(index<rooms.length&&rooms[index]!=null)
			index++;
		if(index<rooms.length){
			rooms[index]=addedRoom;
			addedRoom.roomLevel=this;
		}
	}
	
	public Tile[] getRoomWalls(Room walledRoom){
		int x1=walledRoom.x1Coord;
		int x2=walledRoom.x2Coord-1;
		int y1=walledRoom.y1Coord;
		int y2=walledRoom.y2Coord-1;
		Tile[] wallTiles= new Tile[walledRoom.getPerimeter()];		//300 is an arbitrary max. need a better one.
		int tileIndex=0;
		for(int i=x1+1; i<x2-1;i++){
			if(containsTile(i,y1)
			&& getTile(i,y1).icon==WALL_ICON){
				wallTiles[tileIndex]=getTile(i,y1);
				tileIndex++;
			}
			if(containsTile(i,y2)
					&& getTile(i,y2).icon==WALL_ICON){
						wallTiles[tileIndex]=getTile(i,y2);
						tileIndex++;
					}
		}
		for(int i=y1+1; i<y2-1;i++){		//not a nested for loop because we don't need one to check only the perimeter of the room.
			if(containsTile(x1,i)
			&& getTile(x1,i).icon==WALL_ICON){
				wallTiles[tileIndex]=getTile(x1,i);
				tileIndex++;
			}
			if(containsTile(x2,i)
					&& getTile(x2,i).icon==WALL_ICON){
						wallTiles[tileIndex]=getTile(x2,i);
						tileIndex++;
					}
			}
		return wallTiles;
	}
	
	//tunnel methods
	
	public void addTunneler(int xPos, int yPos){
		if(containsTile(xPos,yPos))
			tunnelMaker=new Tunneler(this, getTile(xPos,yPos));
	}
	
	public void addHorizontalTunnel(int x1, int x2, int y){
		for(int i=x1;i<x2;i++){			
			addTunnel(i, y);
		}
	}
	
	public void addVerticalTunnel(int y1, int y2, int x){
		for(int i=y1;i<y2;i++){			
			addTunnel(x, i);
		}
	}
	
	public char initialTunnelerDirection(Tile startTile){	//checks in 4 directions from tunneler's start position (a door) to look for a ' ' tile.
		Tile nextTile=Movement.tileInDirection(this, startTile,'8'); //up
		if(containsTile(nextTile.xCoord,nextTile.yCoord)
		&& nextTile.icon==' ')
			return '8';
		nextTile=Movement.tileInDirection(this, startTile,'6'); //right
		if(containsTile(nextTile.xCoord,nextTile.yCoord)
		&& nextTile.icon==' ')
			return '6';
		nextTile=Movement.tileInDirection(this, startTile,'4'); //left
		if(containsTile(nextTile.xCoord,nextTile.yCoord)
		&& nextTile.icon==' ')
			return '4';
		nextTile=Movement.tileInDirection(this, startTile,'2'); //down
		if(containsTile(nextTile.xCoord,nextTile.yCoord)
		&& nextTile.icon==' ')
			return '2';
		return 0;
	}
	
	//toString method

	public String getLevel(){	
		String level = "";
		for (int i = 0; i < ySize; i++) {
			for (int j = 0; j < xSize; j++) {
				if(layout[j][i]!=null)
					level+=layout[j][i].icon;
				else
					level+='Q';		//error case
			}
			level+=("\n");
		}
		return level;
	}
	
	//branch stuff
	
	public int branchIndex(){
		return getBranch().branchIndex;
	}
	
	public int branchFloor(){
		return getBranch().relativeIndex(floor);
	}
	
	public Branch getBranch() {
		return levelBranch;
	}
	
	public void setBranch(Branch branch){
		levelBranch=branch;
		
	}
	
	public boolean firstInBranch(){	//need math.min because some branches go up instead of down, meaning the "first" is more like "highest point".
		return floor==getBranch().startDepth();
	}
	
	public boolean lastInBranch() {
		return floor==getBranch().endDepth();
	}
	
	//Print function
	public void printLevel() {
		System.out.println(getLevel());
	}

	public Tile getTile(int xPos, int yPos){
		if(containsTile(xPos,yPos))
			return layout[xPos][yPos];
		else
			return null;
	}
	
	public Tile getTile(Room tileRoom, int xPosRelative, int yPosRelative){
		return layout[tileRoom.x1Coord+xPosRelative][tileRoom.y1Coord+yPosRelative];
	}
	
	public int emptyTileCount(){	//counts the empty tiles in the room. (NOTE: if this causes lag, keep track of the number with an int instead.)
		int count=0;
		for(int i=0;i<xSize;i++){
			for(int j=0;j<ySize;j++){
				if(getTile(i,j).isEmpty())
					count++;
			}
		}
		return count;
	}
	
	private Tile randomEmptyTile(){	//random empty tile, regardless of room.
		int xPos=0; int yPos=0;
		while((xPos==0||yPos==0)
			||getTile(xPos,yPos).permanentIcon!=Level.EMPTY_TILE_ICON
			||!getTile(xPos,yPos).isEmpty()){
			xPos=1+rng.nextInt(xSize-1);
			yPos=1+rng.nextInt(ySize-1);
		}
		return getTile(xPos, yPos);
	}
	
	public Tile randomClearTile(){	//same as above, but can be in a tunnel
		int xPos=0; int yPos=0;
		while((xPos==0||yPos==0)
			||!(getTile(xPos,yPos).permanentIcon==Level.EMPTY_TILE_ICON||getTile(xPos,yPos).permanentIcon==Level.TUNNEL_ICON)
			||!getTile(xPos,yPos).isEmpty()){
			xPos=1+rng.nextInt(xSize-1);
			yPos=1+rng.nextInt(ySize-1);
		}
		return getTile(xPos, yPos);
	}
	
	//tile checking methods
	
	public boolean isPassable(int xPos, int yPos) {
		return (layout[xPos][yPos].isPassable);
	}

	public boolean containsTile(int xPos, int yPos) {
		return xPos>=0 && yPos>=0 && xPos<xSize && yPos < ySize;
	}
	
	public boolean containsTile(Tile checkTile) {
		return checkTile!=null 
			&& containsTile(checkTile.xCoord,checkTile.yCoord);
	}
	
	public boolean notEdgeTile(int xPos, int yPos) {	//same as contains tile, but cannot be on the edge of the room.
		return xPos>0 && yPos>0 && xPos<xSize-1 && yPos < ySize-1;
	}
	
	public boolean notEdgeTile(Tile checkTile) {
		return checkTile!=null 
				&& notEdgeTile(checkTile.xCoord,checkTile.yCoord);
	}
	
	public void mapTile(int xPos, int yPos) {
		if(containsTile(xPos,yPos))
			getTile(xPos,yPos).playerMapped=true;
	}
	
	public void unmapTile(int xPos, int yPos) {
		if(containsTile(xPos,yPos))
			getTile(xPos,yPos).playerMapped=false;
	}
	
	//monster methods
	
	public Monster getMonster(int index){
		return levelMonsters[index];
	}
	
	public void addPlayer(Player player){
		addMonster(player, upStairs[0]);
		if(player.hasVisitedLevel(this))
			placeInitialMonsters();
		player.visitLevel(this);
		
		//player.fov.enterPlayerView();
		placeItems();	//TODO: consider taking the player's Luck stat as an arg here, to influence how "good" the items are. (need a way to measure "good", or just make more items)
	}
	
	public void addMonster(Monster newMonster){	
		if(levelBranch.levelType==LevelGenerator.STANDARD){
			Room monsterRoom = getRandomRoom();
			while (monsterRoom.containsPlayer()
					|| monsterRoom.noRoom())	//make sure not to add a monster in the same room as the player. also, make sure there is room to spawn a monster.
				monsterRoom = getRandomRoom();
			Tile monsterTile = randomEmptyRoomTile(monsterRoom);
			addMonster(newMonster, monsterTile);
			return;
		}
		addMonsterAnywhere(newMonster);		//TODO: consider not adding monsters too close to stairs.
	}
	
	public void addInitialMonster(Monster newMonster){	//add a monster to the level at a random position

		if(levelBranch.levelType==LevelGenerator.STANDARD){
			Room monsterRoom = getRandomRoom();
			while (monsterRoom.containsUpStairs()
					|| monsterRoom.containsPlayer()
					|| monsterRoom.noRoom())	//since the room is populated with monsters before the player enters, we avoid adding monsters to the room with the stairs the player will come from.
				monsterRoom = getRandomRoom();
			Tile monsterTile = randomEmptyRoomTile(monsterRoom);
			addMonster(newMonster, monsterTile);
			return;
		}
		addMonsterAnywhere(newMonster);
	}
	
	private void addMonsterAnywhere(Monster monster){
		Tile monsterTile=randomEmptyTile();
		addMonster(monster,monsterTile);
	}
	
	public void addMonster(Monster newMonster, Tile monsterTile){
		if(newMonster==null)
			return;
		int index=0;							//TODO: this should be grouped with other functions so that a monster is always placed somewhere when added to the dungeon.
		while(levelMonsters[index]!=null)
			index++;
		levelMonsters[index]=newMonster;
		newMonster.setCurrentLevel(this);
		newMonster.setPosition(monsterTile.xCoord, monsterTile.yCoord);
		newMonster.currentTile.monster=newMonster;
		newMonster.fov.refreshFOV();
		
			//is this necessary for the player??
		if(newMonster.getClass()==Player.class){			//currently, the player automatically draws all monster aggro when entering the dungeon.
			newMonster.drawAllMonsterAggro();				//since a monster's list of hostiles should be persistent, this should stay even after fov is implemented.												//(instead of constantly moving towards monsters they are aggroed towards, monsters will only move towards hostiles they can see.)
		}
		else{
		newMonster.inventory=new Inventory(newMonster.inventory);	//only perform this for non-player monsters, since their inventory items should be copied, not referenced.
		if(levelContainsPlayer())
			newMonster.addEnemy(levelPlayer());	
		}	
	}
	
	public void addAvailableMonster(Monster monster, int spawnOffset){	//TODO: change this method appropriately once monster gradient spawing is set up.
		//if(spawnOffset!=0)
		//	monster.spawnChance*=offsetAdjustment(spawnOffset);
		if(monster!=null&&monsterGenerator!=null)
			monsterGenerator.addMonster(monster);
	}
	
	public void placeInitialMonsters(){	//add a few monsters to the level as the game starts.
		int tiles=emptyTileCount();
		int divisor=monsterDivisor(tiles);
		
		int mininumMonsters=(int)(0.6*(tiles/divisor));		//TODO: as monster randomization gets more organized, these numbers should be getters based on the level size.
		int maximumMonsters=(int)(1.4*(tiles/divisor));
		
		if(monsterGenerator!=null&&monsterGenerator.knownMonsterCount()>0){
			int initialMonsterCount = Math.max(1,mininumMonsters+rng.nextInt(maximumMonsters-mininumMonsters+1));
			for(int i=0;i<initialMonsterCount;i++){
				addInitialMonster(monsterGenerator.chooseRandomMonster());
			}
		}
	}
	
	public void removeMonster(Monster removedMonster) {	//this might be a good place to tell other monsters in the level to remove this monster from their hostile list.
		int index=0;
		while(levelMonsters[index]!=null){
			if(levelMonsters[index]==removedMonster){
				levelMonsters[index]=levelMonsters[index+1];
			while(levelMonsters[index+1]!=null){
				index++;
				levelMonsters[index]=levelMonsters[index+1];
				}
			}
			index++;
		}
	}
	
	public int monsterDivisor(int tiles){	//TODO: tweak this as necessary. 
		return (int) Math.min(tiles, 16*(Math.pow(tiles, 0.2)));
	}
	
	public Player levelPlayer(){
		for(int i=0;i<levelMonsters.length&&levelMonsters[i]!=null;i++){
			if(levelMonsters[i].getClass().equals(Player.class))
				return (Player)levelMonsters[i];
		}
			return null;
	}
	
	public boolean levelContainsPlayer(){
		for(int i=0;i<levelMonsters.length&&levelMonsters[i]!=null;i++){
			if(levelMonsters[i].getClass().equals(Player.class))
				return true;
		}
			return false;
	}
	
	//item adding methods
	
	public void addAvailableItem(Item item, int spawnOffset){		//spawnOffset influences an item's spawn rate.
		if(spawnOffset!=0)
			item.spawnChance*=offsetAdjustment(spawnOffset);
		if(item!=null&&itemGenerator!=null)
			itemGenerator.addItem(item);
	}
	
	private double offsetAdjustment(int offset){	//how an item's spawn rate is adjust by offset
		if(offset==0)
			return 1.0;
		return 1.0/(double)offset;		//this formula is pretty rough and subject to change. Make the value smaller to make item spawing more rigidly bound to dungeon depth.
	}
		
	public void setGoldRange() {		
		int modifier=Math.max(1, (int)(2.2*Math.pow(Math.max(floor/2,1),1.1)));
		itemGenerator.setGoldRange(5*modifier, 15*modifier);
	}
	
	public void placeItems(){		//TODO: if lag becomes too much, try making this and placeInitialMonsters both call from the same method, and take the same int arg (only call the tile getter once.)
		
		int tiles=emptyTileCount();
		int itemDivisor=itemDivisor(tiles);	
		int maximumItems=(int)(3.2*((double)tiles/itemDivisor));
		
		if(itemGenerator!=null){
			Class<?>[] itemClasses=Item.ITEM_CLASSES;
			for(int i=0;i<itemClasses.length;i++){
				placeItems(itemClasses[i],maximumItems);
			}
		
				int goldDivisor=goldDivisor(tiles);
				
				int minimumGolds=(int)(0.6*(tiles/goldDivisor));	//"golds" is understood to mean "stacks of gold"
				int maximumGolds=(int)(1.4*(tiles/goldDivisor));
				int goldStackCount=minimumGolds+rng.nextInt(maximumGolds-minimumGolds);
				
				for(int i=0;i<goldStackCount;i++){
					int amount=itemGenerator.randomGoldAmount();
					addGoldRandom(amount);
			}
		}
	}
	
	private void placeItems(Class<?> itemClass,int maximumItems){
		int forceItemChance=1;	//chance (out of 10) of forcing a maximumItems of at least 1. (an item of that type is still not guaranteed, though.)
		if(itemGenerator.knownItemCount(itemClass)>0){
			//minimum is always 0. may change this if necessary.
			if(itemClass.equals(Ammo.class)){
				maximumItems/=5;
				forceItemChance=2;}	//don't need to force ammo too often.
			else if(itemClass.equals(Armor.class)){
				maximumItems/=7;
				forceItemChance=4;}
			else if(itemClass.equals(Food.class)){
				maximumItems/=5;
				forceItemChance=7;}
			else if(itemClass.equals(Potion.class)){
				maximumItems/=7;
				forceItemChance=1;}
			else if(itemClass.equals(Weapon.class)){
				maximumItems/=9;
				forceItemChance=3;}
			else return;	//return if the item class is not recognized.
			if(forceItemChance>rng.nextInt(11))
				maximumItems=Math.max(maximumItems, 1);
			if(maximumItems==0)	return;	//if maximumItems is still 0, we don't need to add that type of item.		
			int itemCount=rng.nextInt(maximumItems+1);
			for(int i=0;i<itemCount;i++){	//NOTE: if artifacts or rare items can spawn, this may grow more complicated
				Item addedItem=itemGenerator.chooseRandomItem(itemClass);
				addedItem.initialize(this); //set some starting traits of the item, like stack size and material.
				addItemRandom(addedItem);
			}
		}
	}
	
	public void addItem(Item addedItem, int xPos, int yPos){
		if(containsTile(xPos,yPos)
				&& getTile(xPos,yPos).isRoom){
			Tile itemTile=layout[xPos][yPos];
			itemTile.addItem(addedItem);
		}
	}
	
	public void addItem(Room itemRoom,Item addedItem, int xPosRelative, int yPosRelative){	//the position is based on the room position. Also, we might not need this method.
		if(itemRoom!=null){
			int xPos=itemRoom.x1Coord+xPosRelative;
			int yPos=itemRoom.y1Coord+yPosRelative;
			addItem(addedItem,xPos,yPos);
		}
	}
	
	public void addItemRandom(Room itemRoom, Item addedItem){
		Tile itemTile=randomEmptyRoomTile(itemRoom);
		itemTile.addItem(addedItem);
	}
	
	public void addItemRandom(Item addedItem){		//randomly adds an item to a random Room
		if(levelBranch.levelType==LevelGenerator.STANDARD){
			Room itemRoom = getRandomRoom();
			addItemRandom(itemRoom, addedItem);
			return;
		}
		addItemAnywhere(addedItem);
	}
	
	private void addItemAnywhere(Item addedItem) {
		Tile itemTile = randomEmptyTile();
		addItem(addedItem, itemTile.xCoord,itemTile.yCoord);
	}
	
	private int itemDivisor(int tiles){
		return (int) (Math.min(tiles, 10*(Math.pow(tiles, 0.3))));
	}
	
	private int goldDivisor(int tiles){
		return (int) (Math.min(tiles, 11*(Math.pow(tiles, 0.35))));
	}

	//gold adding methods
	
	public void addGoldRandom(Room goldRoom, int amount){
		if(goldRoom!=null){
			Tile itemTile=randomEmptyRoomTile(goldRoom);
			itemTile.addGold(amount);
		}
	}
	
	public void addGoldRandom(int amount){
		if(levelBranch.levelType==LevelGenerator.STANDARD){
			Room goldRoom=getRandomRoom();
			addGoldRandom(goldRoom,amount);
			return;
		}
		addGoldAnywhere(amount);
	}
	
	private void addGoldAnywhere(int amount) {
		Tile goldTile=randomEmptyTile();
		goldTile.addGold(amount);
	}
	
	//door related methods

	public void toggleDoorOpen(Door toggleDoor){
		if(toggleDoor.isOpen){
			toggleDoor.setIcon(CLOSED_DOOR_ICON);
		}				
	}
	
	private boolean invisibleDoorRoll(){
		if(floor+rng.nextInt(105)>100-floor/5)
			return false;	//"False" means invisible
		return true;
	}

	//tile type adding mehtods
	
	public void addTile(char icon, int xPos, int yPos){	//TODO: add traps
		switch(icon){
		case(' '):
			addBlankWall(xPos,yPos);
		return;
		case(EMPTY_TILE_ICON):
			addEmptyTile(xPos,yPos);
		return;
		case(WALL_ICON):
			addWall(xPos,yPos);
		return;
		case(TUNNEL_ICON):
			addTunnel(xPos,yPos);
		return;
		case(CLOSED_DOOR_ICON):
			addDoor(xPos,yPos);
		return;
		case(UP_STAIRS_ICON):
			addUpStairs(xPos,yPos);
		return;
		case(DOWN_STAIRS_ICON):
			addDownStairs(xPos,yPos);
		return;
		}
	}
	
	public void addEmptyTile(int xPos, int yPos){
		if(containsTile(xPos,yPos))
			layout[xPos][yPos]=new Tile(EMPTY_TILE_ICON,true,true,floor,xPos,yPos);
	}
	
	public void addWall(int xPos, int yPos){
		if(containsTile(xPos,yPos)){
			layout[xPos][yPos]=new Tile(WALL_ICON,false,true,floor,xPos,yPos);
		}
	}
	
	public void addBlankWall(int xPos, int yPos){
		if(containsTile(xPos,yPos))
			layout[xPos][yPos]=new Tile(' ',false,true,floor,xPos,yPos);
	}	
	
	public void addTunnel(int xPos, int yPos){		
		if(containsTile(xPos,yPos)){
			layout[xPos][yPos]=new Tunnel(getTile(xPos,yPos),invisibleTunnelRoll());
		}
	}
	
	private boolean invisibleTunnelRoll(){
		if(floor+rng.nextInt(210)>200-floor/10)
			return false;	//"False" means invisible
		return true;
	}
	
	public void addDoor(int xPos, int yPos){
		if(containsTile(xPos,yPos)	//a valid door must not be up against an edge or corner of the screen.
		&& containsTile(xPos+1,yPos+1)
		&& containsTile(xPos-1,yPos-1))
			layout[xPos][yPos]=new Door(layout[xPos][yPos],invisibleDoorRoll());	
		Door newDoor=(Door)layout[xPos][yPos];
		if(getTile(xPos-1,yPos).icon == WALL_ICON	//then, check if the door is in a horizontal wall.
				&& getTile(xPos+1,yPos).icon == WALL_ICON)
			newDoor.horizontal=true;
				else
					newDoor.horizontal=false;
	}
	
	public void addTrap(int xPos, int yPos){
		if(containsTile(xPos,yPos)){
			layout[xPos][yPos]=new Trap(layout[xPos][yPos],invisibleTrapRoll());
		}
	}
	
	public void addRandomTile(int xPos, int yPos){	//don't need to add traps since they are added during generation.
		int roll=rng.nextInt(100);
		if(roll>=0&&roll<40)
			addEmptyTile(xPos,yPos);
		else if(roll>=40&&roll<50)
			addWall(xPos,yPos);
		else if(roll>=50&&roll<65)
			addBlankWall(xPos,yPos);
		else if(roll>=65&&roll<96)
			addTunnel(xPos,yPos);
		else
			addDoor(xPos,yPos);
	}
	
	//trap methods
	
	public int trapRoll(){		//number of traps added
		return 1+(floor/12)+rng.nextInt(2+floor/8);
	}
	
	private boolean invisibleTrapRoll(){	//once a trap is added, determine whether or not it will be visible.
		if(floor+rng.nextInt(205)>200-floor/4)
			return false;	//"False" means invisible
		return true;
	}
	
	public void addTraps(){
		for(int i=0;i<trapRoll();i++){
			Tile trapTile=randomEmptyTile();
			addTrap(trapTile.xCoord, trapTile.yCoord);
		}
	}
	
	//stairs methods

	public void addDownStairs(int xPos, int yPos){
		if(containsTile(xPos,yPos)){
			Stairs downStairs=new Stairs(false, levelBranch, levelBranch,xPos,yPos);
			layout[xPos][yPos]=downStairs;
			int index=0;
			while(index<this.downStairs.length&&this.downStairs[index]!=null)
				index++;
			this.downStairs[index]=downStairs;
			
		}
	}
	
	public void addUpStairs(int xPos, int yPos){			//TODO: someting seems to be wrong with the way upStairs and downStairs are added to the array
		if(containsTile(xPos,yPos)){
			Stairs upStairs=new Stairs(true,levelBranch,levelBranch,xPos,yPos);
			layout[xPos][yPos]=upStairs;
			int index=0;
			while(index<this.upStairs.length&&this.upStairs[index]!=null)
				index++;
			this.upStairs[index]=upStairs;
		}
	}

	public void addDownStairs(Branch toBranch, int xPos, int yPos){
		if(containsTile(xPos,yPos)){
			Stairs downStairs=new Stairs(false, levelBranch, toBranch,xPos,yPos);
			layout[xPos][yPos]=downStairs;
			int index=0;
			while(index<this.downStairs.length&&this.downStairs[index]!=null)
				index++;
			this.downStairs[index]=downStairs;
		}
	}
	
	public void addUpStairs(Branch toBranch,int xPos, int yPos){		
		if(containsTile(xPos,yPos)){
			Stairs upStairs=new Stairs(true,levelBranch, toBranch,xPos,yPos);
			layout[xPos][yPos]=upStairs;
			int index=0;
			while(index<this.upStairs.length&&this.upStairs[index]!=null)
				index++;
			this.upStairs[index]=upStairs;
		}
	}
	
	public void addRandomUpStairs(Room stairsRoom){
		if(stairsRoom!=null){
			Tile stairsTile=randomEmptyRoomTile(stairsRoom);
			addUpStairs(stairsTile.xCoord,stairsTile.yCoord);
		}
	}
	
	public void addRandomDownStairs(Room stairsRoom){
		if(stairsRoom!=null){
			Tile stairsTile=randomEmptyRoomTile(stairsRoom);
			addDownStairs(stairsTile.xCoord,stairsTile.yCoord);
		}
	}
	
	public void addRandomUpStairs(Room stairsRoom, Branch toBranch){
		if(stairsRoom!=null){
			Tile stairsTile=randomEmptyRoomTile(stairsRoom);
			addUpStairs(toBranch, stairsTile.xCoord,stairsTile.yCoord);
		}
	}
	
	public void addRandomDownStairs(Room stairsRoom, Branch toBranch){
		if(stairsRoom!=null){
			Tile stairsTile=randomEmptyRoomTile(stairsRoom);
			addDownStairs(toBranch, stairsTile.xCoord,stairsTile.yCoord);
		}
	}
	
	public void addRandomUpStairs(Branch toBranch){
		switch(getBranch().levelType){
		case(LevelGenerator.EMPTY):
			Tile stairsTile=randomEmptyTile();
			addUpStairs(toBranch,stairsTile.xCoord,stairsTile.yCoord);
			break;
		case(LevelGenerator.STANDARD):
			Room upStairsRoom=getRandomRoom();
			while(upStairsRoom.containsStairs())
				upStairsRoom=getRandomRoom();
			addRandomUpStairs(upStairsRoom,toBranch);
			break;
		case(LevelGenerator.CAVERN):
			Tile cavernStairsTile=randomEmptyTile();	//TODO: should be different if I decide to place stairs differently in caverns.
			addUpStairs(toBranch,cavernStairsTile.xCoord,cavernStairsTile.yCoord);
			break;
		default:
			Tile randomTile=randomEmptyTile();
			addUpStairs(toBranch,randomTile.xCoord,randomTile.yCoord);
			break;
		}
	}
	
	public void addRandomDownStairs(Branch toBranch){
		if(getBranch()!=null){
		switch(getBranch().levelType){
		case(LevelGenerator.EMPTY):
			Tile stairsTile=randomEmptyTile();
			addDownStairs(toBranch,stairsTile.xCoord,stairsTile.yCoord);
			break;
		case(LevelGenerator.STANDARD):
			Room downStairsRoom=getRandomRoom();
			while(downStairsRoom.containsStairs())
				downStairsRoom=getRandomRoom();
			addRandomDownStairs(downStairsRoom,toBranch);
			break;
		case(LevelGenerator.CAVERN):
			Tile cavernStairsTile=randomEmptyTile();	//TODO: should be different if I decide to place stairs differently in caverns.
			addDownStairs(toBranch,cavernStairsTile.xCoord,cavernStairsTile.yCoord);
			break;
		default:
			Tile randomTile=randomEmptyTile();
			addDownStairs(toBranch,randomTile.xCoord,randomTile.yCoord);
			break;
			}
		}
	}
	
	public Stairs getLinkedUpStairs(Stairs downStairs) {		//get the up stairs that these down stairs connect to
		int index=0;
		while(index<upStairs.length
				&&(upStairs[index]==null||upStairs[index].toBranch()!=downStairs.fromBranch()))
				index++;
		if(upStairs[index]!=null)
			return upStairs[index];
		else
			return null;
	}
	public Stairs getLinkedDownStairs(Stairs upStairs) {		//get the down stairs that these up stairs connect to
		int index=0;
		while(index<downStairs.length
			&&(downStairs[index]==null||downStairs[index].toBranch()!=upStairs.fromBranch()))		//something seems to be causing problems here. No idea what.
			index++;
		if(downStairs[index]!=null)
			return downStairs[index];
		else
			return null;
	}

	public Stairs getUpStairs(int i) {	//for testing
		return upStairs[i];
	}
	
	public Stairs getDownStairs(int i) {	//for testing
		return downStairs[i];
	}

	public static boolean isValidIcon(int icon) {
		if(icon==' '||icon=='\n')
			return true;
		for(int i=0;i<ICONS.length;i++){
			if(icon==ICONS[i])
				return true;
		}
		return false;
	}
	
	//sound methods
	
	public void addSound(Sound sound, Tile sourceTile) {	//add a sound that originates from a given tile
		if(containsTile(sourceTile)){
			
			int radius=sound.getVolume();
			
			int xStart=sourceTile.xCoord-radius;
			int yStart=sourceTile.yCoord-radius;
		
			int xEnd=sourceTile.xCoord+radius;
			int yEnd=sourceTile.yCoord+radius;
			
			for(int i=xStart;i<xEnd;i++){
				for(int j=yStart;j<yEnd;j++){
					if(containsTile(i,j)&&getTile(i,j).monster!=null){
						Monster source=sourceTile.monster;
						if(source!=null)
							getTile(i,j).monster.hearSound(source,sound);
						else{
							//TODO: case for sounds that do not come from monsters belongs here.
						}
					}
				}	
			}
		}
	}

	//depth getters for spawning
	
	public int itemDepth(Class<?> itemClass){
		switch(itemClass.getCanonicalName()){
		case("Ammo"): return ammoDepth();
		case("Armor"): return armorDepth();
		case("Weapon"): return weaponDepth();
		default:
			return floor;
		}
	}
	
	public int monsterDepth(){
		return floor+levelBranch.monsterModifier();
	}
	
	public int weaponDepth() {
		return floor+levelBranch.itemModifier(Weapon.class);
	}

	public int armorDepth() {
		return floor+levelBranch.itemModifier(Armor.class);
	}

	public int ammoDepth() {
		return floor+levelBranch.itemModifier(Ammo.class);
	}

	public int randomLevelsUp(int max) {	//a random, valid number of levels up or down from this.
		int levelRoom=0;
		while(levelBranch.containsLevel(levelRoom-1)
			&& Math.abs(levelRoom)<max)
			levelRoom--;
		if(levelRoom==0)
			return 0;
		return dice.nextInt(Math.abs(levelRoom))+1;	
	}
	
	public int randomLevelsDown(int max) {
		int levelRoom=0;
		while(levelBranch.containsLevel(levelRoom+1)
			&& Math.abs(levelRoom)<max)
			levelRoom++;
		if(levelRoom==0)
			return 0;
		return dice.nextInt(Math.abs(levelRoom))+1;	
	}
} 