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
			dungeonLevel.addMonster(getRandomMonster());
		}
	}
	
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
	
	public Monster getRandomMonster(){		//chooses a random known monster.
		if(knownMonsterCount()>0){
			boolean monsterChosen=false;
			while(!monsterChosen){
				int monsterIndex=monsterRNG.nextInt(knownMonsterCount());
				if(getMonster(monsterIndex)!=null
				&& considerMonster(getMonster(monsterIndex))){	//roll randomly to select a monster. monsters with lower spawn rates are less likely to be chosen.
					monsterChosen=true;
					return getMonster(monsterIndex);
				}
			}
		}
		return null;
	}
	
	private boolean considerMonster(Monster monster){		//consider selecting a monster based on its spawn rate.
		if(((double)((double)monster.spawnChance*100.0))>monsterRNG.nextInt(100))
			return true;
		return false;
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
