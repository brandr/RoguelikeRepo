
public class Tunnel extends Tile{
	public static final int MINIMUM_TUNNEL_DIFFICULTY=180;
	
	public Tunnel(Tile originalTile, boolean isVisible){	
		if(isVisible){
			permanentIcon=Level.TUNNEL_ICON;
			icon=Level.TUNNEL_ICON;
			isPassable=true;
		}
		else{
			permanentIcon=' ';	//uncomment when done testing invisible tunnels.
			icon=' ';
			randomizeSearchDifficulty(MINIMUM_TUNNEL_DIFFICULTY);
			isPassable=false;
			//permanentIcon='Q';	//uncomment when testing invisible tunnels.
			//icon='Q';
		}
		this.isVisible=isVisible;
		
		isRoom=false;
		
		floorTag=originalTile.floorTag;
		xCoord=originalTile.xCoord;
		yCoord=originalTile.yCoord;
	}
	
	@Override
	public void setVisible(){
		isVisible=true;
		isPassable=true;
		permanentIcon=Level.TUNNEL_ICON;
		icon=Level.TUNNEL_ICON;
	}
	
}
