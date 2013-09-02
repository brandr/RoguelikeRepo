
public class Branch {

	public static final int MAX_BRANCH_SIZE=50;
	
	//modifier order: Monster,Ammo,Armor,Food,Potion,Weapon
	public static final int[] ENTRANCE_HALL_MODIFIERS={-10,-10,-10,-10,-10,-10};	//store these differently if branches get more complex.
	public static final int[] LABYRINTH_MODIFIERS={3,4,2,8,5,0};
	public static final int[] CAVERN_MODIFIERS={0,6,8,0,0,7};	//TODO: change as necessary.
	public static final int[] GLITCH_MODIFIERS={0,6,8,0,0,7};
	
	public static final int[][] BRANCH_MODIFIERS={ENTRANCE_HALL_MODIFIERS,LABYRINTH_MODIFIERS,CAVERN_MODIFIERS,GLITCH_MODIFIERS};	//do for every branch.
	public static final String[] BRANCH_NAMES={"Entrance hall\n","The Great Labyrinth\n","The Voluminous Caverns","e&  ^ %$AVERN \n944rogQRzT12"};	//TODO: try to get these to fit on one line.
	//Branch names are displayed when the player is in a branch
	
	public Branch(){		
		startDepth=0;
		branchLength=MAX_BRANCH_SIZE;
		branchLevels=new Level[branchLength];
		levelType=LevelGenerator.STANDARD;
		setModifiers();
	}

	public Branch(String levelType,int levelCount){
		startDepth=0;
		branchLength=levelCount;
		branchLevels=new Level[branchLength];
		this.levelType=levelType;
		setModifiers();
	}
	
	public Branch(Level fromLevel, String levelType, int levelCount){
		if(levelCount<0)
			buildUp(fromLevel, levelType, levelCount);
		else if(levelCount>0)
			buildDown(fromLevel,levelType,levelCount);
		setModifiers();
	}
	
	public String getName(){
		return BRANCH_NAMES[branchIndex];
	}
	
	 private void buildDown(Level fromLevel, String levelType, int levelCount) {
		 	startDepth=fromLevel.floor+1;
			branchLength=levelCount;
			branchLevels=new Level[branchLength];
			this.levelType=levelType;
			
			fromLevel.addRandomDownStairs(this);	
			
			branchLevels[0]=new Level(startDepth,this);		
			branchLevels[0].addRandomUpStairs(fromLevel.getBranch());
			branchLevels[0].levelDungeon=fromLevel.levelDungeon;
			int index=1;
			for (int i = startDepth+1; i <levelCount+startDepth; i++) {	//doing depth-1 for now, since I am putting in a temp final level. (NOTE: is this the correct number?)
				branchLevels[index] =  new Level(i,this);		//TODO: instead of just setting level depth, should also set level branch.
				branchLevels[index].levelDungeon=fromLevel.levelDungeon;
				index++;
			}
	}
	 
	 private void buildUp(Level fromLevel, String levelType, int levelCount) {	
		startDepth=fromLevel.floor+levelCount-1;
		branchLength=-1*levelCount+1;
		branchLevels=new Level[branchLength];
		this.levelType=levelType;
			
		fromLevel.addRandomUpStairs(this);
		
		branchLevels[0]=new Level(startDepth,this);		
		branchLevels[0].levelDungeon=fromLevel.levelDungeon;
		int index=1;	
		for (int i = startDepth+1; i<(-1*levelCount)+startDepth+1; i++) {	//doing depth-1 for now, since I am putting in a temp final level. (NOTE: is this the correct number?)			
			branchLevels[index] =  new Level(i,this);		//TODO: instead of just setting level depth, should also set level branch.
			branchLevels[index].levelDungeon=fromLevel.levelDungeon;
			index++;
		}
			
		branchLevels[index-1].addRandomDownStairs(fromLevel.getBranch());
	}

	public String getDungeonMap(int levelIndex){	//TODO: make this use global index for "levelIndex" to determine where in branch this should be.
		 int index=relativeIndex(levelIndex);  
		 if(containsLevel(index))
			   return branchLevels[index].getLevel();
		   return "";
	   }
	 
	 public boolean roomForLevel(int index){
		 return(index>=0&&index<branchLevels.length&&branchLevels[index]==null);
	 }
	 
	 public boolean containsLevel(int index){
		 return(index>=0&&index<branchLevels.length&&branchLevels[index]!=null);
	 }
	 
	 public Level getFirstLevel(){
		 if(branchLevels[0].floor<=branchLevels[branchLength-1].floor)
		 return branchLevels[0];
		 else
			 return branchLevels[branchLength-1];
	 }
	 
	 public Level getLevel(int levelIndex){
		 int index=relativeIndex(levelIndex);
		 if(containsLevel(index))
			 return branchLevels[index];
		 return null;
	 }
	 
	 public Level getRelativeLevel(int index){
		 if(containsLevel(index))
			 return branchLevels[index];
		 return null;
	 }
	 
	 public void setLevels(Level[] lvlList) {
		 	startDepth=lvlList[0].floor;
			branchLevels=lvlList;
			for(int i=0;i<lvlList.length&&lvlList[i]!=null;i++){
				lvlList[i].setBranch(this);
			}
		}
	 
	 public void addLevel(Level level, int index){
		 level.setBranch(this);
		 branchLevels[index]=level;
	 }
	 
	 public void addLevel(Level level){
		 int index=0;
		 while(index<branchLevels.length&&branchLevels[index]!=null)
			 index++;
		 branchLevels[index]=level;
		 if(index==0)
			 startDepth=level.floor;
		 level.setBranch(this);
	 }
	 
	 public int absoluteIndex(Level level){
		 return startDepth+level.floor;
	 }
	 
	 public int relativeIndex(int absoluteIndex){
		 return (absoluteIndex-startDepth);
	 }
	 
	 public int startDepth(){
		 return startDepth;
	 }
	 
	 public int endDepth(){
		 return startDepth+branchLength-1;
	 }
	
	/* public void addAvailableMonster(Monster monster, int levelIndex) {		//this doesn't seem right, so why does it work for the main branch?
		int index=relativeIndex(levelIndex);
		 if(containsLevel(index)&&getLevel(levelIndex)!=null)
			 getLevel(levelIndex).addAvailableMonster(monster);
		 
	}*/
	 
	 public void addAvailableItem(Item item, int levelIndex) {
		 int index=relativeIndex(levelIndex);
		 if(containsLevel(index)&&getLevel(levelIndex)!=null)
			 getLevel(levelIndex).addAvailableItem(item,0); 	//NOTE: is 0 always the correct offset here? should there be another version of this method?
	}
	 
	//modifier info
	 
	 private void setModifiers() {	//sets modifiers for the branch.
		 int[] modifiers=BRANCH_MODIFIERS[branchIndex];
			monsterModifier=modifiers[0];
			for(int i=1;i<modifiers.length;i++){
				itemModifiers[i-1]=modifiers[i];
			}
		}
	 
	 public int monsterModifier(){
		 return monsterModifier;
	 }
	 
	 public int itemModifier(Class<?> itemClass){
		 Class<?>[] classes=Item.ITEM_CLASSES;
		 for(int i=0;i<classes.length;i++){
			 if(classes[i].equals(itemClass))
				 return itemModifiers[i];
		 }
		 return 0;
	 }
	
	private int monsterModifier=0;	//TODO: same as below
	private int[] itemModifiers=new int[Item.ITEM_CLASSES.length];
		//TODO: make it possible to set all modifiers. (have I done this yet?)
	
	public Material[] availableMaterials=Material.spawnableMaterials;		//TODO: don't make this all spawnable materials for every branch.
	
	public int branchIndex;
	private Level[] branchLevels;
	private int startDepth; 		//how far into the dungeon this branch appears;
	private int branchLength;		//the number of levels in this branch
	public String levelType;		//the type of levels which should be generated in this branch.
	
}
