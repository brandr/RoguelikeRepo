import java.util.Random;

public class Dungeon {
	
	//TODO: add weapons differently.
	
	//TODO: add a final potion array for all the potions in the dungeon.
   public Branch [] dungeonBranches;
   public final static int BRANCH_COUNT=10;		//should this really be a final int? maybe have a "max branch count" final int and a "branch count" variable?
   public int depth =50;  //Dungeon Size(total number of levels). NOTE: this might not be used as much, now that Branches are implemented. Remove if necessary.
  
   public String[] dungeonPotionColors;
   
   public void setMap() {		//TODO: add potions to known level items here, or get them from final array here.
	 dungeonBranches=new Branch[BRANCH_COUNT];

	   //TODO: this should occur in the constructor of a branch.
   
	addBranch(LevelGenerator.EMPTY);
	Level firstLevel=new Level(0, dungeonBranches[0],LevelGenerator.EMPTY);
	firstLevel.addRoom(1,1,34,29);
	Weapon superSword=WeaponReader.createArtifactWeapon("Devblade");	//TODO: make invisible and hide better.
   	superSword.twoHanded=true;
   	superSword.piercing=true;
   	superSword.quality=10;
   	superSword.variance=0.05;
  
   	firstLevel.addItem(superSword,21,24);
	firstLevel.levelDungeon=this;
	if(dice.nextInt(10002)>9999){
   		Monster rareMonster= 
   				new Monster("Tiny egg-shaped Mexican woman", '0', 
   						999999, 9999, 
   						9, 99, 99);
   		rareMonster.color="FFFFFF";
   		rareMonster.obtainGold(99999);
   		firstLevel.addMonster(rareMonster);
   	}


   addLevel(firstLevel,0,0);
   firstLevel.addUpStairs(31, 3);
   addBranch(firstLevel,LevelGenerator.STANDARD,50);
   addBranch(getLevel(1,2),LevelGenerator.CAVERN,16);
   addBranch(getLevel(1,6),LevelGenerator.GLITCH,-2);

   setPotionColors();	//randomizes potion colors for this game session.
   
   //addDungeonMaterials();
   addDungeonItems();
   MonsterReader.addDungeonMonsters(this);
   	
   }
   
public String getDungeonMap(){
	   String map = "";
	   for (int i = 0; i < depth-1&&getDungeonMap(i)!=null; i++) {
		   	map+=getDungeonMap(i);
		   	map+=("\n\n");
			  }
	   return map;
   }
   
   public String getDungeonMap(int levelIndex){
	   return getDungeonMap(0, levelIndex);
   }
   
   public String getDungeonMap(int branchIndex, int levelIndex){
	   if(branchIndex>=0&&branchIndex<dungeonBranches.length&&dungeonBranches[levelIndex]!=null)
		   return dungeonBranches[branchIndex].getDungeonMap(levelIndex);
	   else
		   return "";
   }
   
   private void setPotionColors() {
	   dungeonPotionColors=Potion.randomColors();
   }
   
   public void addBranch(){		//adds a standard branch.
	   int index=0;
	   while(index<dungeonBranches.length&&dungeonBranches[index]!=null)
		   index++;
	   if(dungeonBranches[index]==null){
		   dungeonBranches[index]=new Branch();
		   dungeonBranches[index].branchIndex=index;
	   }
   }
   
   public void addBranch(String levelType){		//adds a special type of branch. (should always use string constants from LevelGenerator.)
	   int index=0;
	   while(index<dungeonBranches.length&&dungeonBranches[index]!=null)
		   index++;
	   if(dungeonBranches[index]==null){
		   dungeonBranches[index]=new Branch(levelType,1);		//why 1? (I forget)
		   dungeonBranches[index].branchIndex=index;
	   }
   }
   
   public void addBranch(Level fromLevel,String levelType, int length){
	   int index=0;
	   while(index<dungeonBranches.length&&dungeonBranches[index]!=null)
		   index++;
	   if(dungeonBranches[index]==null){
		   dungeonBranches[index]=new Branch(fromLevel, levelType, length);
		   dungeonBranches[index].branchIndex=index;
	   }
   }
   
   public boolean containsBranch(int index){
	   return index>=0&&index<dungeonBranches.length&&dungeonBranches[index]!=null;
   }
   
   
   public Branch getBranch(int index) {
		if(containsBranch(index))
			return dungeonBranches[index];
		return null;
	}
   
   public Branch[] allBranches() {
		return dungeonBranches;
	}
   
   public void addLevel(Level level, int branchIndex, int levelIndex){
	   if(containsBranch(branchIndex)&&getBranch(branchIndex).roomForLevel(levelIndex)){
		   dungeonBranches[branchIndex].addLevel(level);
		   level.levelDungeon=this;
	   }
   }
   
   public Level getLevel(int branchIndex, int levelIndex){
	   if(containsBranch(branchIndex)&&getBranch(branchIndex).containsLevel(levelIndex))
		   return getBranch(branchIndex).getLevel(levelIndex);
	   return null;
   }
   
   public Level getNextLevel(Level currentLevel, Stairs stairs) {
	   Branch levelBranch=stairs.toBranch();
		return levelBranch.getLevel(currentLevel.floor+1);
	}
   
   public Level getPreviousLevel(Level currentLevel, Stairs stairs) {
	   Branch levelBranch=stairs.toBranch();
		return levelBranch.getLevel(currentLevel.floor-1);
	}
   
   public void addAvailableMonster(Monster monster, Branch branch, int levelIndex){
	   branch.addAvailableMonster(monster, levelIndex);
   }
   
  /* private void addDungeonMaterials() {
		dungeonMaterials=MaterialReader.allMaterials();
	}*/
   
   private void addDungeonItems() {
	   Weapon[][] genericWeapons=WeaponReader.dungeonWeapons(this);
	   Armor[][] genericArmors=ArmorReader.dungeonArmors(this);
	   WeaponReader.addDungeonWeapons(this,genericWeapons);
	   ArmorReader.addDungeonArmors(this,genericArmors);
	   ItemReader.addDungeonItems(this);	//TODO: break this up into separate methods for each item.
	}
   
   public void addAvailableItem(Item item, Branch branch, int levelIndex) {
	   branch.addAvailableItem(item, levelIndex);	
	}
   
   //Print out map
   public void printMap() {
	   System.out.println(getDungeonMap());
   }
   
   
   private Random dice=new Random();
   //private static Material[] dungeonMaterials=Material.allMaterials;	//TODO: decide whether this should include "flesh"
   //private Weapon[][] genericWeapons;	//TODO: implement getting. also, does this really need to be stored here?

}