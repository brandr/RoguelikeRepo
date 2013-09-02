import java.util.Random;
//TODO: make monsters carry random loot.

public class MonsterGenerator {	//creates random monsters. methods should not be static, since there should eventually
								//be different types of monster generators to create different monsters.
								//consider an itemGenerator too.
	public MonsterGenerator(){
			//not sure what to put here
	}
	
	public MonsterGenerator(Level dungeonLevel){		//should add monsters based on current dungeon level, usually.
		this.dungeonLevel=dungeonLevel;
	}
	
	public Monster newMonster(String name, char icon, int hitPoints){	//creates a monster with a definite amount of HP.
		Monster newMonster= new Monster(name, icon);
		newMonster.setHitPoints(hitPoints);
		return newMonster;
	}
	
	public Monster newMonster(String name, char icon, int hitPoints, int baseDamage){	//creates a monster with a definite amount of HP and attack.
		Monster newMonster= new Monster(name, icon);
		newMonster.setHitPoints(hitPoints);
		newMonster.setBaseDamage(baseDamage);
		return newMonster;
	}
	
	public void step(){		//called at the end of the player's turn. has a small chance to randomly add a known monster to the level.
		if(monsterRNG.nextInt(150)>147&&knownMonsterCount()>0){
			dungeonLevel.addMonster(chooseRandomMonster());
		}
	}
	
	//monster adding methods
	
	public void addMonster(Monster newMonster){
		int index=0;
		while(index<knownMonsters.length&&knownMonsters[index]!=null){
			index++;
		}
		knownMonsters[index]=newMonster;
	}
	
	public void addMonster(String name, char icon, int hitPoints, int baseDamage){
		Monster addedMonster=newMonster(name,icon,hitPoints,baseDamage);		//do I need to declare "new" here for this to work properly?
		addMonster(addedMonster);
	}
	
	public Monster getMonster(int index){
		if(index>=0||index<knownMonsters.length)
			return new Monster(knownMonsters[index]);	//the copy constructor for monster will need to be developed
		else
			return null;
	}
	
	//TODO: figure out how to factor in existing monster spawn chance methods neatly.
	
	public Monster chooseRandomMonster(){		//chooses a random known monster appropriate for the level.
		int knownCount=knownMonsterCount();
		if(knownCount<1)
			return null;
		if(knownCount==1)
			return knownMonsters[0];
		else if(knownCount>1){
			int optionCount=(int) Math.max(2, 
								knownCount/(2.1+0.08*dungeonLevel.monsterDepth()));	//TODO: tweak this formula until it seems right.
			Monster[] monsterOptions=new Monster[optionCount];	//possible monsters to choose from
			monsterOptions[0]=randomMonster();
			for(int i=1;i<optionCount;i++){
				Monster nextMonster=randomMonster();
				while(nextMonster.getName().equals(monsterOptions[i-1].getName()))
					nextMonster=randomMonster();
				monsterOptions[i]=nextMonster;
			}
			return bestMonster(monsterOptions, dungeonLevel.monsterDepth());
		}
		return null;
	}
	
	public static Monster bestMonster(Monster[] monsterOptions, int depth) {	//most appropriate monster (out of the options) for the depth.
		int length=monsterOptions.length;
		if(length>1){
			Monster bestMonster=monsterOptions[0];
			 for(int i=1;i<length&&monsterOptions[i]!=null;i++){
				  int currentClosest=Math.abs(bestMonster.getOverallPower()-depth);
				  int potentialClosest=Math.abs(monsterOptions[i].getOverallPower()-depth);  
				  if(potentialClosest<currentClosest)
					  bestMonster=monsterOptions[i];
			 }
			 return bestMonster;
		}
		else if(length==1)
			return monsterOptions[0];
		return null;
	}

	private Monster randomMonster(){
		int count=knownMonsterCount();
		if(count==0)
			return null;
		int monsterIndex=monsterRNG.nextInt(count);
		while(getMonster(monsterIndex)==null)
			monsterIndex=monsterRNG.nextInt(count);
		return getMonster(monsterIndex);
	}
	
	public int knownMonsterCount(){
		int index=0;
		while(index<knownMonsters.length&&knownMonsters[index]!=null){
			index++;
		}
		return index;
	}
	
	public Level getDungeonLevel(){
		return dungeonLevel;
	}
	
	private Level dungeonLevel;	//the level on which this generator is creating monsters.
	private Random monsterRNG=new Random();
	private Monster[] knownMonsters=new Monster[200];	//a list of monster types that this generator stores. When it adds a monster to a level, 
	
	
			//the number of turns the player has spent on the level that has this generator.
								//ALTERNATELY, could have a "step" method which is called whenever the player's turn ends, which has a small chance
								//of randomly placing a monster in the dungeon.
}
