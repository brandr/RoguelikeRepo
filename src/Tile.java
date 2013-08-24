import java.awt.Color;
import java.util.Random;

public class Tile {

    public static final char GOLD_ICON='$';
	
	//Constructor
	public Tile(char c, boolean p, boolean v, int x, int y, int z) {
	icon = c;
	permanentIcon=c;
	isPassable = p;
	isVisible = v;
	floorTag = x;
	xCoord = y;
	yCoord = z;
	}
	
	 public Tile(Tile copyTile) {
	    	icon=copyTile.icon;
	    	permanentIcon=copyTile.icon;
	    	isPassable=copyTile.isPassable;
	    	isVisible=copyTile.isVisible;
	    	floorTag=copyTile.floorTag;
	    	xCoord=copyTile.xCoord;
	    	yCoord=copyTile.yCoord;
	    	isRoom=copyTile.isRoom;
	    	
	    	playerSeen=copyTile.playerSeen;
	    	playerMapped=copyTile.playerMapped;
	    	if(copyTile.monster!=null)
	    		monster=new Monster(copyTile.monster);
	    	//tileItems=new ItemStack(copyTile.tileItems);
		}
	 //Constructor
	public Tile() {
		icon = 'Z';
		}
		
	  //Set isRoom variable 
	public void setIsRoom(boolean b) {
	isRoom = b;
	}	
	
     

	//Set icon for a tile
	public void setIcon(char c) {
	icon = c;
	}
	
	public void setPermanentIcon(char c) {
		icon=c;
		permanentIcon = c;
	}
	
	public char getPermanentIcon() {//the icon of the tile itself (i.e., wall, door, etc.)
		//IDEA: consider making playerMapped and playerSeen getters instead of variables. 
		if(!playerMapped)
			return ' ';
		return permanentIcon;	//TODO: this the part that will need to change if the permanent icon isn't the right one to display.)
	}
	
	public char getCurrentIcon(){	//this may become useful as fov and tile representation is adjusted.
		return icon;
	}
	
	public static boolean isEmptySpaceIcon(char icon) {
		return icon==' '
			||icon==Level.EMPTY_TILE_ICON
			||icon==Level.TUNNEL_ICON
			||icon==Level.HORIZONTAL_OPEN_DOOR_ICON
			||icon==Level.VERTICAL_OPEN_DOOR_ICON
			||icon==Level.DOWN_STAIRS_ICON
			||icon==Level.UP_STAIRS_ICON
			||icon==Level.TRAP_ICON;
	}
	
	public void enterView(Monster viewer){
		if(viewer.getClass()==Player.class)
			enterPlayerView();
		//TODO: add other information depending on the viewer, as necessary. 
		//(alternately, maybe a monster viewer stores visible tiles, or checks whether tiles of interest are within vision.)
	}
	
	public void enterPlayerView(){	//the tile enters the player's view. NOTE: could make this more general for monsters.
		playerSeen=true;
		playerMapped=true;
	}
	
	public void exitPlayerView(){
		playerSeen=false;
	}
	
	//Print function
	public void printTile() {
	System.out.print(icon);
	}

	public void clear() {
		monster=null;
		displayIcon();
		isPassable=true;
	}
	
	public void displayIcon() {
		if(monster==null){
			if(tileItems.isEmpty()){
				if(tileItems.noGold())
					setIcon(permanentIcon);
				else
					setIcon(GOLD_ICON);
			}
			else{
				setIcon(tileItems.stackChar());
			}
		}
		else
			setIcon(monster.getIcon());
	}
	
	public void addItem(Item newItem){	//TODO: consider checking for stack equivalence here or moving it to the inventory class (the latter would affect monster pickup methods)
		if(newItem!=null){
			if(newItem.stackable()){
				addItemStack(newItem);
				displayIcon();
				return;
			}
			addFullStack(newItem);
			displayIcon();
		}
	}
	
	private void addFullStack(Item itemStack) {
		if(!tileItems.isFull()){	//TODO: is this check necessary?
			if(itemStack.equippable())
				((Equipment)itemStack).equipped=false;
			tileItems.addItem(itemStack);//TODO: figure out what to replace this with	
		}
	}
	
	private void addItemStack(Item itemStack) {//pick up a stack of items off the ground.
		int findStackable = findStackableItem(itemStack);
		if(findStackable!=-1){
			Item heldStack=tileItems.getItem(findStackable);
			while(!heldStack.stackFull()&&	//as long as the current stack isn't full and the stack we're picking up isn't empty
				!itemStack.stackEmpty()){
				heldStack.incrementStack();
				itemStack.decrementStack(tileItems);
				if(itemStack.stackEmpty())	//will this cause an error since the item is removed by the inventory?
					return;
			}
			addItemStack(itemStack);
		}
		else
			addFullStack(itemStack);
	}
	
	public int findStackableItem(Item pickingUpItem){	//this method is largely copied from the monster class, but that's ok because the code is clearer this way.
		if(pickingUpItem.stackable()){
		int index=0;
		
		if(tileItems.getItem(0)!=null	//inital check necessary for index 0
				&& (tileItems.getItem(0).stackEquivalent(pickingUpItem)
				&& !tileItems.getItem(0).stackFull())){				
					return 0;
				}
		
		while(index<tileItems.getItemCount()
			&&!tileItems.getItem(index).stackEquivalent(pickingUpItem)
			&&tileItems.getItem(index)!=null){
			index++;
				if(tileItems.getItem(index)!=null
				&& (tileItems.getItem(index).stackEquivalent(pickingUpItem)
				&& !tileItems.getItem(index).stackFull()))					
					return index;
			}
		}
		return -1;	//means "no stackable item found"
	}
	
	public int getGold(){
		return tileItems.getGold();
	}
	
	public void addGold(int amount){
		tileItems.addGold(amount);
		displayIcon();
	}
	
	public int takeAllGold() {
		int allGold=tileItems.getGold();
		tileItems.setGold(0);
		return allGold;
	}

	public void removeItem(Item removedItem) {
		tileItems.removeItem(removedItem);
		displayIcon();
	}
	
	public Item takeItem(Item itemToBeTaken) {
		Item takenItem=tileItems.takeItem(itemToBeTaken);
		displayIcon();
		return takenItem;
	}
	
	public boolean containsItems() {
		return (!tileItems.isEmpty());
	}
	
	public boolean containsMonster() {
		return monster!=null;
	}
	
	public boolean isEmpty(){		//if a tile has absolutely nothing in it, return true. (returns false for walls and other impassable tiles.)
		return isPassable&&!containsItems()&&!containsMonster()&&tileItems.noGold();
	}
	
	public void setVisible(){
		isVisible=true;
	}
	
	public boolean isVisibleTrap(){
	return this.getClass()==Trap.class&&isVisible;
}	

	public int getSearchDifficulty() {
		return searchDifficulty;
	}

	public void setSearchDifficulty(int searchDifficulty) {
		if(searchDifficulty>0)
			this.searchDifficulty = searchDifficulty;
	}
	
	public void randomizeSearchDifficulty(int minimum){		//change formula if searching is too easy or too hard in some levels.
		int roll=minimum+dice.nextInt(floorTag+20);
		setSearchDifficulty(roll);
	}

	public Random dice=new Random();
	
	public char icon;
	public char permanentIcon; //icon shown when nothing is on the tile
    
	public String color="000000";
	public String permanentColor="000000";
	
	public boolean isPassable=true;
	public boolean isVisible=true;	//will be useful for traps and secret doors, though it might be more complicated than a boolean.
	private int searchDifficulty=150; 	//if the tile has something hidden, this is the difficulty of searching for it. TODO: consider making some things harder to search for than others.
	
	public boolean playerSeen=false;
	public boolean playerMapped=false;
	
	public int floorTag;
	public int xCoord;
	public int yCoord;
	public boolean isRoom;
	
	public Monster monster=null;
	public ItemStack tileItems=new ItemStack();

	

	

}