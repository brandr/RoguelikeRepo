import java.util.Random;


public class Tunneler {		//an object (not shown by the game) which digs tunnels once the rooms are already in place.
	
	public Tunneler(Level level, Tile startTile){	//constructor sets the level and starting tile
		this.level=level;
		this.startTile=startTile;
		currentTile=startTile;
		startTile.setPermanentIcon(Level.CLOSED_DOOR_ICON);	//a tunneler always starts in a door in the wall of a room.
		startTile.displayIcon();
		startDigging();	//begin digging the tunnel
		confirmTunnel();	//if the tunnel is valid, add it to the level. (still need more checks to ensure that a tunnel is valid.)
	}
	
	private void startDigging() {	//choose a starting direction and begin digging in a straight line, away from the room.
		char startDirection=level.initialTunnelerDirection(currentTile);
		keepDigging(startDirection);
	}
	
	//TODO: decide what happens if a tunnel hits the corner of a room. (it shouldn't make a door there.)
	private void keepDigging(char direction){	//dig in a straight line until hitting a wall or the edge of the map. this would be a good place to add
		Tile nextTile=Movement.tileInDirection(level, currentTile, direction);	//random turns.
		while(level.containsTile(nextTile)&&level.notEdgeTile(nextTile)){
			
			currentTile=new Tile(nextTile);
			if(tunnelLength<tunnels.length)
				dig();	//dig every valid tile
			else
				return;
			char newDirection=direction;
			if(considerDirectionChange()){
				dig();
				newDirection=changedDirection(direction);//make the tunnel change direction when it hits the edge of the room.
				//keepDigging(newDirection);
			}
			if(Movement.tileInDirection(level, currentTile, newDirection)!=null
			&& Movement.tileInDirection(level, currentTile, newDirection).icon==' '){
					nextTile=new Tile(Movement.tileInDirection(level, currentTile, newDirection));		
			}
			else{
				respondToObstacle(newDirection);
				return;
			}
		}
		respondToObstacle(direction);
	}
	
	private void respondToObstacle(char direction){	//decide what to do when hitting an obstacle.
		if(Movement.tileInDirection(level, currentTile, direction)!=null){
			Tile obstacle=Movement.tileInDirection(level, currentTile, direction);
			char obstacleIcon=obstacle.permanentIcon;	//this check won't work for finding tunnels, only for walls.
			if(obstacleIcon==Level.WALL_ICON&&!isWallCorner(level,obstacle))	//if the tunnel hits a wall, create a door in that wall. TODO: should not apply to corner walls. need a boolean in some class.
				level.addDoor(obstacle.xCoord,obstacle.yCoord);
			else{
				if(tunnelLength<tunnels.length&&!noValidDirections(currentTile,direction)){
					dig();
					char newDirection=changedDirection(direction);//make the tunnel change direction when it hits the edge of the room.
					keepDigging(newDirection);
					}
				}	
			}
		else{
			if(tunnelLength<tunnels.length&&!noValidDirections(currentTile,direction)){
				dig();
				char newDirection=changedDirection(direction);//make the tunnel change direction when it hits the edge of the room.
				keepDigging(newDirection);
				}
			}
	}
	
	private boolean isWallCorner(Level level, Tile wall) {	//checks to see if the tile is the corner of a wall, so that a door won't be made there.
		
		int xPos=wall.xCoord;
		int yPos=wall.yCoord;
		if((level.getTile(xPos-1,yPos).permanentIcon == Level.WALL_ICON	
		&& level.getTile(xPos+1,yPos).permanentIcon == Level.WALL_ICON)
		||(level.getTile(xPos,yPos-1).permanentIcon == Level.WALL_ICON	
		&& level.getTile(xPos,yPos+1).permanentIcon == Level.WALL_ICON))
			return false;
		return true;
	}

	private void changeDirection(char direction){
		direction=changedDirection(direction);
	}
	
	private char changedDirection(char direction){	//this and "changeDirection" are kept separate because we might want to find a "changed direction" without actually changing an existing direction. (example: the tunnel splits two ways.)
		//dig();
		char newDirection=0;
		////char newDirection=0;						//this method randomly changes the tunneler's direction without making it double back on itself.
		int turns=directionRNG.nextInt(2);
		if(turns==0)
			newDirection=Movement.ninetyDegLeftOf(direction);
		else
			newDirection=Movement.ninetyDegRightOf(direction);
		//System.out.println(direction);
			return newDirection;
	}
	
	private boolean considerDirectionChange(){
		if(directionRNG.nextInt(16)>14)
			return true;
		else
			return false;
	}
	
	private boolean noValidDirections(Tile startTile, char startDirection){	//goes with changeDirection and checks to make sure there are valid directions to switch to.
		
		char direction=startDirection;
		Tile checkTile;
		for(int i=0;i<3;i++){
			
			checkTile=Movement.tileInDirection(level, startTile, direction);
			if(level.containsTile(checkTile)&&checkTile!=null 
			&& level.notEdgeTile(checkTile)
			&& !containsTunnel(checkTile) && checkTile.icon==' '){
				return false;
			}
			direction=Movement.ninetyDegRightOf(direction);
		}
		return true;
	}
	
	private boolean containsTunnel(Tile checkTunnel){	//checks to see if this is already part of the tunnel
		for(int i=0;i<tunnels.length&&tunnels[i]!=null;i++){
			if(checkTunnel.xCoord==tunnels[i].xCoord
			&& checkTunnel.yCoord==tunnels[i].yCoord)
				return true;
		}
		return false;
	}
	
	private void dig(){	
		tunnels[tunnelLength]=new Tile(currentTile);
		tunnelLength++;
	}
	
	private void confirmTunnel(){
		for(int i=0;i<tunnels.length&&tunnels[i]!=null;i++){
			level.addTunnel(tunnels[i].xCoord, tunnels[i].yCoord);
		}
	}

	//TODO: write a method to clear tunnels[], for when an invalid tunnel has been created and must be reset. Consider putting this in "noValidDirections" once the length is more limited.
	
	private Level level;
	private Tile startTile;	//the tile where the tunnel begins is stored, in case the tunnel must start over.
	private Tile currentTile;
	private Tile[] tunnels=new Tile[600];	//Keeps track of all the tunnels currently stored. TODO: think of a "max tunnel length" somehow.
	int tunnelLength=0;
	
	Random directionRNG=new Random();
}
