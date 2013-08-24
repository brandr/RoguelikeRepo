
public class Door extends Tile{

	public static final int MINIMUM_DOOR_DIFFICULTY=200;		//used for determining how hard an invisible door is to detect.
	
	public Door(Tile originalTile, boolean isVisible){	
		if(isVisible){
			permanentIcon=Level.CLOSED_DOOR_ICON;
			icon=Level.CLOSED_DOOR_ICON;
		}
		else{
			permanentIcon=Level.WALL_ICON;	//uncomment when done testing invisible doors.
			icon=Level.WALL_ICON;
			randomizeSearchDifficulty(MINIMUM_DOOR_DIFFICULTY);
			//permanentIcon='Q';	//uncomment when testing invisible doors.
			//icon='Q';
		}
		this.isVisible=isVisible;
		isPassable=false;
		isRoom=false;
		
		floorTag=originalTile.floorTag;
		xCoord=originalTile.xCoord;
		yCoord=originalTile.yCoord;
	}
	
	@Override
	public void setVisible(){
		isVisible=true;
		permanentIcon=Level.CLOSED_DOOR_ICON;
		icon=Level.CLOSED_DOOR_ICON;
	}
	
	public void toggleOpen(){
		if(!isOpen){
			if(horizontal){
				setPermanentIcon(Level.HORIZONTAL_OPEN_DOOR_ICON);
				displayIcon();
			}
			else{
				setPermanentIcon(Level.VERTICAL_OPEN_DOOR_ICON);
				displayIcon();
			}
			isOpen= true;
			isPassable=true;
		}
		else{
			setPermanentIcon(Level.CLOSED_DOOR_ICON);
			displayIcon();
			isOpen= false;
			isPassable=false;
			}
	}

	public boolean horizontal=false;
	public boolean isOpen=false;
	//private boolean visible;
}
