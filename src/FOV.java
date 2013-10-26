import java.util.Random;


public class FOV {	//field of view. belongs to players and monsters.
	
	public FOV(Monster monster){
		this.monster=monster;
		Level level=monster.getCurrentLevel();
		canSee=new boolean[level.xSize+1][level.ySize+1];
		//refreshFOV();	//may or may not want this here
		
		canSee= tilesVisibleBooleans();//this can be used for testing, making everything on the screen visible
	}
	
	public boolean canSee(Tile tile){
		if(level().containsTile(tile))
			return canSee[tile.xCoord][tile.yCoord];
		return false;
	}
	
	public char getTileIcon(int xPos, int yPos) {
		if(level().containsTile(xPos,yPos)){
			Tile tile=level().getTile(xPos, yPos);
			if(canSee[xPos][yPos])
				return tile.getCurrentIcon();
			else
				return tile.getPermanentIcon();
		}
		return 0;
	}
	
	
	//current problems: monster colors not displaying
	
	public void devRefreshFOV(){	//a developer version of refreshFOV, which makes the entire level visible to the player.
		canSee=tilesVisibleBooleans();
	}
	
	public void refreshFOV(){
		
		boolean[][] tilesVisible=tilesVisibleBooleans();
		boolean[][] tilesChecked=tilesCheckedBooleans();
		
		int xCenter=monster.getXPos();
		int yCenter=monster.getYPos();
		
		Tile startTile=new Tile(level().getTile(xCenter, yCenter));
		Tile checkTile=new Tile(startTile);
		
		int radius=15;	//TOOD: make sure radius is used. (and based on perception stat for player.)
		
		char direction='8';
		
		int tempIndex=0; //temporary, doesn't measure anything useful really
		while(tempIndex<3000){	//3000 is a temporary value. replace with a value
			
				if(direction=='8'||checkTile.xCoord==startTile.xCoord||checkTile.yCoord==startTile.yCoord){	//time to change directions
					if(direction=='9')
						direction='8';
					else if(direction=='8'){	//if not on the first iteration, the tile checked immediately after "up" should  be "up-left"
						if(tempIndex>1)
							direction='7';
						else if(tempIndex==1)
							direction='1';
					}
					else
						direction=Movement.ninetyDegLeftOf(direction);
				}
				
				Tile tempTile=Movement.tileInDirection(level(), checkTile, direction);
				
				if(level().containsTile(tempTile)){
				
				tilesChecked[checkTile.xCoord][checkTile.yCoord]=true;
				checkTile=new Tile(Movement.tileInDirection(level(), checkTile, direction));
				
				if(tilesVisible[checkTile.xCoord][checkTile.yCoord]){
						if(checkTile.monster==(null)&&!checkTile.isPassable){//i.e., if checkTile is a solid object
							
							Tile[] tilesBehind=Movement.tilesBehind(level(), startTile, checkTile);		
							for(int i=0;i<tilesBehind.length;i++){
								if(tilesBehind[i]!=null)
									tilesVisible[tilesBehind[i].xCoord][tilesBehind[i].yCoord]=false;
							}
					}	
				}
					
				else {	//if a tile is already marked as invisible, make all tiles behind it invisible, too.
						
						Tile[] tilesBehind=Movement.tilesBehind(level(), startTile, checkTile);
						for(int i=0;i<tilesBehind.length;i++){
							if(tilesBehind[i]!=null){
							char invisibleDirection=Movement.determineDirection(checkTile.xCoord, checkTile.yCoord,
									tilesBehind[i].xCoord, tilesBehind[i].yCoord);
						
							int tempX=checkTile.xCoord;
							int tempY=checkTile.yCoord;
							
							int xOffset=Movement.numpadToX(invisibleDirection);
							int yOffset=Movement.numpadToY(invisibleDirection);
							
							while(tempX+xOffset>=0&&tempX+xOffset<tilesVisible.length
								&&tempY+yOffset>=0&&tempY+yOffset<tilesVisible[0].length
								&&invisibleDirection!='5'){
								tempX+=xOffset;
								tempY+=yOffset;
								tilesVisible[tempX][tempY]=false;
							}
						}
					}		
				}
					tempIndex++;
			}else{	//handle checked tiles that go outside the level boundaries here.
				
				int directionChangeIndex=0;
			
				tempTile=Movement.dummyTileInDirection(checkTile, direction);
				while(!level().containsTile(tempTile.xCoord,tempTile.yCoord)&&directionChangeIndex<10){	// <10 check helps prevent long loops (though a different number may be better)
					if(direction=='8'||tempTile.xCoord==startTile.xCoord||tempTile.yCoord==startTile.yCoord){	//time to change directions
						if(direction=='9')
							direction='8';
						else if(direction=='8'){	//if not on the first iteration, the tile checked immediately after "up" should  be "up-left"
							if(tempIndex>1)
								direction='7';
							else if(tempIndex==1)
								direction='1';
						}
						else
							direction=Movement.ninetyDegLeftOf(direction);
						directionChangeIndex++;
					}
					tempTile=Movement.dummyTileInDirection(tempTile, direction);
					tempIndex++;
				}

				if(level().containsTile(tempTile.xCoord,tempTile.yCoord)&&directionChangeIndex!=5){
					checkTile=level().getTile(tempTile.xCoord,tempTile.yCoord);
					tempIndex++;
					continue;
				}
				else{
					canSee=tilesVisible;
					if(monster.getClass().equals(Player.class))
						updateMap();
					return;
				}

			}
		}
		canSee=tilesVisible;
		if(monster.getClass().equals(Player.class))
			updateMap();
	}
	
	private void updateMap() {
		for(int i=0;i<canSee.length;i++){
			for(int j=0;j<canSee[0].length;j++){
				if(canSee[i][j])
					level().mapTile(i,j);
			}
		}
	}

	private boolean[][] tilesCheckedBooleans() {	//same as tiles visible, but keeps track of tiles that have already been checked.
		Level level=level();
		boolean[][] tilesVisible=new boolean[level.xSize+1][level.ySize+1];
		for(int i=0;i<level.xSize;i++){
			for(int j=0;j<level.ySize;j++){
				tilesVisible[i][j]=false;
			}
		}
		return tilesVisible;
	}
	
	public void mapAllLevel() {	//NOTE: this doesn't apply to monsters. Then again, does it need to?
		Level level=level();
		for(int i=0;i<level.xSize;i++){
			for(int j=0;j<level.ySize;j++){
				level.mapTile(i, j);
			}
		}
	}
	
	public void forgetMap() {
		Level level=level();
		for(int i=0;i<level.xSize;i++){
			for(int j=0;j<level.ySize;j++){
				level.unmapTile(i, j);
			}
		}
	}

	private boolean[][] tilesVisibleBooleans(){	//a special set of booleans used in determining FOV
		Level level=monster.getCurrentLevel();
		boolean[][] tilesVisible=new boolean[level.xSize][level.ySize];
		for(int i=0;i<level.xSize;i++){
			for(int j=0;j<level.ySize;j++){
				tilesVisible[i][j]=true;
			}
		}
		return tilesVisible;
	}
	
	private Level level(){
		return monster.getCurrentLevel();
	}
	
	public Tile[] visibleTiles(){
		refreshFOV();	//not sure if this is necessary here. move it if it causes bugs or lag.
		Tile[] tiles=new Tile[1000];
		int index=0;
		Level level=level();
		for(int i=0;i<level.xSize;i++){
			for(int j=0;j<level.ySize;j++){
				if(canSee[i][j]){
					tiles[index]=level.getTile(i,j);
					index++;
				}
			}
		}
		return tiles;
	}
	
	public Monster[] visibleMonsters() {
		refreshFOV();	//not sure if this is necessary here. move it if it causes bugs or lag.
		Monster[] monsters=new Monster[1000];
		int index=0;
		for(int i=0;i<level().xSize;i++){
			for(int j=0;j<level().ySize;j++){
				if(canSee[i][j]){
					Monster nextMonster=level().getTile(i,j).monster;
					if(nextMonster!=null){
						monsters[index]=nextMonster;
						index++;
					}
				}
			}
		}
		return monsters;
	}
	
	public boolean playerInView() {
		refreshFOV();	//not sure if this is necessary here. move it if it causes bugs or lag.
		for(int i=0;i<level().xSize;i++){
			for(int j=0;j<level().ySize;j++){
				if(canSee[i][j]){
					Monster checkPlayer=level().getTile(i,j).monster;
					if(checkPlayer!=null&&checkPlayer.getClass().equals(Player.class)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Tile randomEmptyTileInView() {
		Tile[] tilesInView=visibleTiles();
		int tileIndex=dice.nextInt(tilesInView.length);
		while(tileIndex>tilesInView.length
				||tilesInView[tileIndex]==null||!tilesInView[tileIndex].isEmpty()||
				(tilesInView[tileIndex].xCoord==monster.currentTile.xCoord&&
				tilesInView[tileIndex].yCoord==monster.currentTile.yCoord))
			tileIndex=dice.nextInt(tilesInView.length);
		return tilesInView[tileIndex];
	}

	private Random dice=new Random();
	private Monster monster;
	private boolean canSee[][];
}