import java.util.Random;

public class Movement {	//contains many of the movement functions. TODO: transfer more from player/monster.

	static Random randGenerator=new Random();
	
	public static int distance(int x1, int y1, int x2, int y2){
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
	
	public static int numpadToX(char num){	//returns the x direction of each keypad input. default is zero because a non-directional key doesn't allow movement.
		switch(num){
		case('1'):
			return -1;
		case('2'):
			return 0;
		case('3'):
			return 1;
		case('4'):
			return -1;
		case('6'):
			return 1;
		case('7'):
			return -1;
		case('8'):
			return 0;
		case('9'):
			return 1;
		default:
			return 0;
		}
	}
	
	public static int numpadToY(char num){	//returns the y direction of each keypad input.  
		switch(num){
		case('1'):
			return 1;
		case('2'):
			return 1;
		case('3'):
			return 1;
		case('4'):
			return 0;
		case('6'):
			return 0;
		case('7'):
			return -1;
		case('8'):
			return -1;
		case('9'):
			return -1;
		default:
			return 0;
		}
	}
	
	public static char leftOf(char direction){	//determine the direction to the left of this one
		switch(direction){					//TODO: figure out a better way to handle directions than these commands
		case '1':
			return '2';
		case '2':
			return'3';
		case '3':
			return '6';
		case '4':
			return '1';
		case '6':
			return '9';
		case '7':
			return '4';
		case '8':
			return '7';
		case '9':
			return '8';
		default:		
			return '0';
		}
	}
	
	public static char rightOf(char direction){		//determine the direction to the right of this one
		switch(direction){
		case '1':
			return '4';
		case '2':
			return'1';
		case '3':
			return '2';
		case '4':
			return '7';
		case '6':
			return '3';
		case '7':
			return '8';
		case '8':
			return '9';
		case '9':
			return '6';
		default:		
			return '0';
		}
	}
	
	public static char ninetyDegRightOf(char direction){	//find the direction 90 degrees right of this one.
		switch(direction){
		case '1':
			return '7';
		case '2':
			return'4';
		case '3':
			return '1';
		case '4':
			return '8';
		case '6':
			return '2';
		case '7':
			return '9';
		case '8':
			return '6';
		case '9':
			return '3';
		default:		
			return 0;
		}
		
	}
	
	public static char ninetyDegLeftOf(char direction){	//find the direction 90 degrees left of this one.
		switch(direction){
		case '1':
			return '3';
		case '2':
			return'6';
		case '3':
			return '9';
		case '4':
			return '2';
		case '6':
			return '8';
		case '7':
			return '1';
		case '8':
			return '4';
		case '9':
			return '7';
		default:		
			return 0;
		}
		
	}
	
	public static char determineDirection(int x1, int y1, int x2, int y2){		//figure out which direction something at x1, y1 is from something at x2, y2
		if(y1==y2){	//4 or 6
			if(x1>x2)		
				return '4';		//left
			if(x1<x2)
				return '6';		//right
		}
		else if(x1==x2){//8 or 2
			if(y1>y2)
				return '8';		//up
			else if(y1<y2)
				return '2';		//down
		}
		else if(x1>x2){
			if(y1>y2)
				return '7';		//up-left
			else
				return '1';		//down-left
		}
		else{
			if(y1>y2)
				return '9';		//up-right
			else
				return '3';		//down-right
		}
		return '5';		//only remaining possibility: monster is in same square as target
	}
	
	public static char determineDirection(Tile fromTile, Tile toTile){
		return determineDirection(fromTile.xCoord,fromTile.yCoord,toTile.xCoord,toTile.yCoord);
	}
	
	public static char oppositeDirection(char direction){
		switch(direction){		//notice that each direction is equal to 10 minus its opposite. if the Movement class grows very long, could rewrite this, casting back and forth from int to char to still get char as a result type.
		case '1':
			return '9';
		case '2':
			return '8';
		case '3':
			return '7';
		case '4':
			return '6';
		case '5':		//need a better "opposite" of 5, which means "center"
			return '5';
		case '6':
			return '4';
		case '7':
			return '3';
		case '8':
			return '2';
		case '9':
			return '1';
		}
		return 0;
	}
	
	//tiles
	
	public static Tile[] adjacentTiles(Level level,Tile centerTile){	//TODO: test this by making a "search" method for the player to use.
		Tile[] tiles=new Tile[10];
		int index=0;
		for(int i=1;i<tiles.length;i++){
			char direction=Character.forDigit(i, 10);
			if(tileInDirection(level,centerTile,direction)!=null&&i!=5){
				tiles[index]=tileInDirection(level,centerTile,direction);
				index++;
			}
		}
		return tiles;
	}
	
	public static Tile[] adjacentTiles(Monster monster){	//TODO: test this by making a "search" method for the player to use.
		Tile[] tiles=new Tile[10];
		int index=0;
		for(int i=1;i<tiles.length;i++){
			char direction=Character.forDigit(i, 10);
			if(tileInDirection(monster,direction)!=null&&i!=5){
				tiles[index]=tileInDirection(monster,direction);
				index++;
			}
		}
		return tiles;
	}
	
	public static Tile nearestOpenTile(Monster movingMonster, char direction){
		
		char left=direction;
		char right=direction;
		while(!safeToMove(movingMonster, direction)){
			switch(randGenerator.nextInt(2)){		//at random, choose one of two actions:
			case(0):		//action 1: try moving left. otherwise, move right.
				left=leftOf(left);
				if(safeToMove(movingMonster, left))
					return tileInDirection(movingMonster, left);
				else{
					right=rightOf(right);
					if(safeToMove(movingMonster, right))
						return tileInDirection(movingMonster, right);
				}
			case(1):
				right=rightOf(right);
				if(safeToMove(movingMonster, right))
					return tileInDirection(movingMonster, right);
				else{
					left=leftOf(left);
					if(safeToMove(movingMonster, left))
						return tileInDirection(movingMonster, left);
				}
				if(left==direction||right==direction)	//this indicates a full cirlce, meaning the monster is surrounded and cannot move.
					return movingMonster.currentTile;
			}
		}
		return tileInDirection(movingMonster, direction);
	}
	
	public static Tile tileInDirection(Monster movingMonster, char direction) {
		return tileInDirection(movingMonster.currentLevel,movingMonster.currentTile,direction);
	}
	
	public static Tile tileInDirection(Level currentLevel, Tile currentTile, char direction) {
		int tileX=currentTile.xCoord+numpadToX(direction);
		int tileY=currentTile.yCoord+numpadToY(direction);
		if(currentLevel.containsTile(tileX,tileY))
			return currentLevel.getTile(tileX, tileY);
		else
			return null;
	}
	
	public static Tile[] tilesBehind(Level level, Tile observerTile, Tile frontTile){
		char firstDirection= determineDirection(observerTile,frontTile);
		if(straightLine(observerTile,frontTile)){
			int xDist=frontTile.xCoord-observerTile.xCoord;
			int yDist=frontTile.yCoord-observerTile.yCoord;
			
			int greatestDist=Math.max(Math.abs(xDist), Math.abs(yDist));
			if(greatestDist>1){	
				Tile[] behind = {tileInDirection(level,frontTile,firstDirection)};
				return behind;//this is returned if the line is straight and the observer is not adjacent to frontTile.
			}
			else{
				Tile[] behind = {tileInDirection(level,frontTile,firstDirection)
						,tileInDirection(level,frontTile,leftOf(firstDirection))
						,tileInDirection(level,frontTile,rightOf(firstDirection))};
				return behind;	//this is returned if the line is straight and the observer is adjacent to frontTile.
			}
		}
		
		Tile[] behinds=new Tile[2];
		behinds[0]=tileInDirection(level,frontTile,firstDirection);
		
		char secondDirection=0;
		
		int xDist=frontTile.xCoord-observerTile.xCoord;
		int yDist=frontTile.yCoord-observerTile.yCoord;
		
		if((xDist*yDist>0&&Math.abs(xDist)>Math.abs(yDist))
		|| xDist*yDist<0&&Math.abs(xDist)<Math.abs(yDist))
			secondDirection=leftOf(firstDirection);
		else
			secondDirection=rightOf(firstDirection);
		behinds[1]=tileInDirection(level,frontTile,secondDirection);
	
		return behinds;	//this is returned if line is not straight.
	}
	
	private static boolean safeToMove(Monster movingMonster,char direction){
		return canMove(movingMonster,direction)&&!tileInDirection(movingMonster,direction).isVisibleTrap();
	}
	
	public static boolean canMove(Monster movingMonster, char direction){
		return tileInDirection(movingMonster,direction).isPassable;
	}

	public static boolean straightLine(int x1, int y1, int x2, int y2){	//determines if the line between two points is perfectly vertical, horizontal, or diagonal. For use in FOV.
		if(x1==x2||y1==y2
		|| Math.abs(x1-x2)==Math.abs(y1-y2))
			return true;
		return false;
	}
	
	public static boolean straightLine(Tile tile1, Tile tile2){	//determines if the line between two points is perfectly vertical, horizontal, or diagonal. For use in FOV.
		return straightLine(tile1.xCoord,tile1.yCoord,tile2.xCoord,tile2.yCoord);
	}
	
	//dummy tiles
	
	public static Tile dummyTileInDirection(Tile startTile, char direction){
		Tile dummy=new Tile(startTile);
		dummy.xCoord+=numpadToX(direction);
		dummy.yCoord+=numpadToY(direction);
		return dummy;
	}
	
}
