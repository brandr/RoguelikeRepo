
public class Stairs extends Tile {
	
	
	public Stairs(boolean goesUp, Branch fromBranch, Branch toBranch, int xPos, int yPos){
		if(goesUp)
			setPermanentIcon(Level.UP_STAIRS_ICON);
		else
			setPermanentIcon(Level.DOWN_STAIRS_ICON);
		up=goesUp;
		isPassable=true;
		xCoord=xPos;
		yCoord=yPos;
		setFromBranch(fromBranch);
		setToBranch(toBranch);
	}

	public Stairs(Stairs toCopy) {
		if(toCopy.goUp())
			setPermanentIcon(Level.UP_STAIRS_ICON);
		else
			setPermanentIcon(Level.DOWN_STAIRS_ICON);
		up=toCopy.goUp();
		isPassable=true;
		xCoord=toCopy.xCoord;
		yCoord=toCopy.yCoord;
		setFromBranch(toCopy.fromBranch);
		setToBranch(toCopy.toBranch);
	}

	public boolean goUp(){
		return up;
	}
	
	public Branch toBranch(){
		return toBranch;
	}
	
	public Branch fromBranch(){
		return fromBranch;
	}
	
	public void setBranches(Branch branch){
		toBranch=branch;
		fromBranch=branch;
	}
	
	public void setToBranch(Branch branch){
		toBranch=branch;
	}
	
	public void setFromBranch(Branch branch){
		fromBranch=branch;
	}

	private Branch fromBranch;
	private Branch toBranch;
	
	private boolean up;	//whether a staircase goes up or down should only be set when the staircase is created.
}
