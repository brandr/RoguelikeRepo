
public class Targeting {

	public final static String SELF="self";
	public final static String AREA_ALL="areaAll";
	public final static String AREA_EXCLUDE_SELF="areaExcludeSelf";
	public final static String FIELD_OF_VIEW="fieldOfView";
	public final static String ITEM_CHOOSE="itemChoose";
	public final static String ITEM_RANDOM="itemRandom";
	
	public final static String[] TARGETING_TYPES={SELF,AREA_ALL,AREA_EXCLUDE_SELF,FIELD_OF_VIEW,
		ITEM_CHOOSE,ITEM_RANDOM};
	
	public final static String BALL="ball";
	public final static String CONE="cone";
	
	public final static String[] AREA_SHAPES={BALL,CONE};
	
	public final static String UNIDENTIFIED="unidentified";	//an item constraint type

	public Targeting(String targetingType) {
		this.targetingType=targetingType;
	}

	public Targeting(String targetingType, String constraint) {
		this.targetingType=targetingType;
		this.constraint=constraint;
	}
	
	public Targeting(String targetingType, String areaShape, int radius) {
		this.targetingType=targetingType;
		this.areaShape=areaShape;
		this.radius=radius;
	}

	public Monster[] getTargets(Monster monster) {
		Monster[] monsters=new Monster[1000];
		switch(targetingType){
		case(SELF):
			monsters[0]=monster;
			return monsters;
		case(FIELD_OF_VIEW):
			int index=0;
			Monster[] visibleMonsters=monster.fov.visibleMonsters();
			for(int i=0;i<visibleMonsters.length&&visibleMonsters[i]!=null;i++){
				if(!visibleMonsters[i].equals(monster)){
					monsters[index]=visibleMonsters[i];
					index++;
				}
			}
			return monsters;
		}
		return null;
	}
	
	public TileList getAffectedArea(Level level, Tile centerTile) {	//"centerTile" isn't always the center, but it is the origin.
		//IDEA: use a method that returns Tile[], but generates the necessary visual effects along the way.
		switch(areaShape){
		case(BALL):
			return(ballAffectedArea(level,centerTile));
		default:
			return null;
		}
	}
	
	private TileList ballAffectedArea(Level level, Tile centerTile) {
		
		TileList area = new TileList();
		TileList[] nextTiles = new TileList[radius];
		TileList.initialize(nextTiles);
		area.addTile(centerTile);
		nextTiles[0].addTile(centerTile);
		
		if(radius==0){
			RogueLikeGui.frame.flashTiles(nextTiles,icon,color);
			return area;
		}
		return ballAffectedArea(level,centerTile,area,nextTiles,1);
	}

	private TileList ballAffectedArea(Level level, Tile centerTile,TileList area,TileList[] nextTileLists, int iteration) {

		//nextTileLists[iteration-1]=new TileList();
		TileList nextTiles=new TileList();
		

		int xCenter=centerTile.xCoord;
		int yCenter=centerTile.yCoord;
		
		int xDif=iteration;
		//IDEA: forbid "tilesBehind" blocked tiles using a forbidTile(Tile) method in TileList that prevents it from being added.
		//IDEA: instead, use monster fov (only works if a. centertile is the monster's location and b. the radius of the monster's fov is not less than the blast radius
		//TODO: make another method for adding each of these tiles
		//TODO: this method gets laggy for large areas of effect.
		
		if(iteration==1){
			nextTiles=new TileList(Movement.adjacentPassableTiles(level,centerTile));	//TODO: still need an additional check for open tiles that are blocked by walls.
		}
		else{
			for(int yDif=0;yDif<xDif;yDif++){
				xDif=sideLength(iteration,yDif);
				TileList tempTiles=new TileList();
				
				tempTiles.addTile(level.getTile(xCenter+xDif,yCenter+yDif));
				tempTiles.addTile(level.getTile(xCenter+xDif,yCenter-yDif));
				tempTiles.addTile(level.getTile(xCenter-xDif,yCenter+yDif));
				tempTiles.addTile(level.getTile(xCenter-xDif,yCenter-yDif));
			
				tempTiles.addTile(level.getTile(xCenter+yDif,yCenter+xDif));
				tempTiles.addTile(level.getTile(xCenter+yDif,yCenter-xDif));
				tempTiles.addTile(level.getTile(xCenter-yDif,yCenter+xDif));
				tempTiles.addTile(level.getTile(xCenter-yDif,yCenter-xDif));
				
				int tempLength=tempTiles.length();
				for(int j=0; j<tempLength;j++){
					Tile tempTile=tempTiles.getTile(j);
					
					char backDirection=Movement.determineDirection(tempTile, centerTile);
					int xOffset=Movement.numpadToX(backDirection);
					Tile xBackTile=level.getTile(tempTile.xCoord+xOffset, tempTile.yCoord);	//this extra tile is sometimes necessary to avoid "gaps" in the area of effect.
					
					if(level.containsTile(tempTile)
					&& Movement.clearLine(level,tempTile,centerTile))
						nextTiles.addTile(tempTile);	
					if(level.containsTile(xBackTile)
					&& Movement.clearLine(level,xBackTile,centerTile))	
						nextTiles.addTile(xBackTile);	
				}
			}
		}
		 
		if(nextTiles.getTile(0)!=null){
			area.addTiles(nextTiles);
		}
		if(iteration==radius){
			RogueLikeGui.frame.flashTiles(nextTileLists,icon,color);
			return area;
		}
		
		nextTileLists[iteration-1].addTiles(nextTiles);
		return ballAffectedArea(level, centerTile, area, nextTileLists, iteration+1);
	}
	
	public static int sideLength(int radius, int otherSide){
		double radiusSquared=Math.pow(radius, 2);
		double otherSideSquared=Math.pow(otherSide,2);
		double length=Math.sqrt(Math.abs(radiusSquared-otherSideSquared));
		return (int) Math.round(length);
	}

	public String getTargetingType() {
		return targetingType;
	}

	public void setTargetingType(String targetingType) {
		this.targetingType = targetingType;
	}
	
	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	private String targetingType=null;
	private String areaShape=null;
	private int radius=0;
	
	private String constraint=null;
	private char icon='*';
	private String color="FF0000";	//TODO: set default color

}
