import java.util.Random;


public class Trap extends Tile{
	public static final int MINIMUM_TRAP_DIFFICULTY=125;	//traps are easier to search for than doors and tunnels,
	//because the player is much less likely to search every empty tile than to search dead ends/walls.
	
	public final static String ARROW="arrow";
	public final static String BEAR="bear";
	public final static String TELEPORT="teleport";
	
	//public final static String[] TRAP_TYPES={ARROW,BEAR,TELEPORT};
	public final static String[] TRAP_TYPES={ARROW,TELEPORT};
	public Trap(Tile originalTile,boolean isVisible){
		if(isVisible){
			permanentIcon=Level.TRAP_ICON;
			icon=Level.TRAP_ICON;
			setRandomTrapType();
		}
		else{
			permanentIcon=Level.EMPTY_TILE_ICON;	//uncomment when done testing invisible tunnels.
			icon=Level.EMPTY_TILE_ICON;
			randomizeSearchDifficulty(MINIMUM_TRAP_DIFFICULTY);
			//permanentIcon='Q';	//uncomment when testing invisible tunnels.
			//icon='Q';
		}
		this.isVisible=isVisible;
		isPassable=true;
		
		isRoom=true;	//this isRoom setter might not work. (would set some cavern tiles to room tiles, which is incorrect but might not matter in the end.)
		
		floorTag=originalTile.floorTag;
		xCoord=originalTile.xCoord;
		yCoord=originalTile.yCoord;
	}
	
	@Override
	public void setVisible(){
		isVisible=true;
		permanentIcon=Level.TRAP_ICON;
		icon=Level.TRAP_ICON;
	}

	private void setRandomTrapType() {
		int index=dice.nextInt(TRAP_TYPES.length);
		setTrapType(TRAP_TYPES[index]);
	}

	private void setTrapType(String type) {
		trapType=type;
		switch(type){
		case(ARROW):
			setCharges(20,26);
		break;
		case(BEAR):
			setCharges(300,350);
		break;
		case(TELEPORT):
			setCharges(300,350);
		break;
		}
	}
	
	private void setCharges(int min,int max){
		charges=min+dice.nextInt(max-min+1);
	}
	
	private void loseCharge(){
		charges--;
	}

	public void trigger(Monster monster) {
		if(!isVisible)
			setVisible();
		if(charges>0){
		loseCharge();
		takeEffect(monster);
		//if applicable, use level to determine power of trap.
		}
		//TODO: case for a trap that has run out of charges. (since this will involve messages, write this code after monster messages work better.)
	}
	
	private void takeEffect(Monster monster){
		switch(trapType){
		case(ARROW):
			Ammo arrow=new Ammo("arrow",null, trapType, 1, null, 1); //TODO: find a cleaner way to make this arrow.
			arrow.setDamage(2,2);
			arrow.setCurrentDamage(2);
			Monster placeHolder=new Monster("arrow trap");
			arrow.collide(placeHolder, monster);
			monster.currentTile.addItem(arrow);
			monster.arrowTrapMessage();
			break;
		case(BEAR):
			//TODO: organize effects before adding bear traps.
			break;
		case(TELEPORT):
			monster.teleport();
			break;
		}
	}
	
	private Random dice=new Random();
	private String trapType;
	private int charges;	//how many times the trap can go off.
	//TODO: test trap generation first, then implement other stuff.
	//TODO: think of various trap types.
}
