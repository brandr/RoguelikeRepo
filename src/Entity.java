
public class Entity {

	public void setPosition(int xPos, int yPos){	//TODO: put in error handling for out-of-bounds areas
		if(currentLevel.containsTile(xPos, yPos)){
			currentTile = currentLevel.layout[xPos][yPos];
			currentTile.setIcon(icon);
			currentTile.isPassable=!solid;
		}
	}
	
	public void setIcon(char icon){
		this.icon=icon;
	}
	
	public char getIcon() {
		return icon;
	}
	
	public int getXPos(){
		return currentTile.xCoord;
	}
	
	public int getYPos(){
		return currentTile.yCoord;
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	public int currentLevelIndex(){
		return currentLevel.floor;
	}

	public boolean solid = true;
	private char icon;
	public String color="000000";
	protected Level currentLevel;
	protected Tile currentTile;
}
