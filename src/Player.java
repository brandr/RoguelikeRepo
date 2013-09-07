import java.util.Random;

public class Player extends Monster{

	public static final char PLAYER_ICON='@';
	public static final String PLAYER_COLOR="931D1D";
	
	public static final String[] BURDEN_STATES={"","burdened","strained","overtaxed"};
	public static final String[] HUNGER_STATES={"starving", "near starving", "very hungry", "hungry", "", "satiated", "engorged"}; 
	
	
	public Player() {
		name = null;
		enemyMonsters=null;
		setIcon(PLAYER_ICON);
		this.color=PLAYER_COLOR;
		setUpSkills();
	}
	
	public Player(String name) {
		this.name = name;		
		enemyMonsters=null;
		setIcon(PLAYER_ICON);
		this.color=PLAYER_COLOR;
		setUpSkills();
	}
	
	public String primaryInfoDisplay(){
		String info="";
		if(name!=null)
			info+="Name: " + name+"\n";
		info+=playerRace.toString()+" "+playerClass.toString()+"\n";
		info+="Level "+currentExpLevel()+"\n";
		info+=currentLevel.getBranch().getName()+"\n";
		info+="Floor "+(currentLevel.floor)+"\n";
		info+="Gold: "+currentGold()+"\n";
		return info;
	}
	
	public String secondaryInfoDisplay(){
		String info="";
		if(currentHp()>0)
			info+="Hitpoints: "+showHitPoints()+"\n";
		else
			info+="Hitpoints: 0/"+maxHitPoints()+"\n";
		info+="MP: "+showMagicPoints()+"\n";
		info+="Armor: "+armorRating()+"\n";
		info+=burdenState();
		info+=hungerState();
		return info;
	}
	
	public String statsDisplay(){
		String statString="";
		for(int i=0;i<stats.length;i++){	
			statString+=statAbbreviation(i)+": "+stats[i]+"\n";
		}
		return statString;
	}
	
	public String showMagicPoints(){
		return magicPoints[0]+"/"+magicPoints[1];
	}
	
	public String showAllSkills(){
		String skillString="";
		for(int i=0;i<Skill.CATEGORIES.length;i++){
			skillString+=Skill.CATEGORIES[i]+":\n\n";
			for(int j=0;j<Skill.ALL_SKILLS[i].length;j++){
				skillString+="   "+Skill.ALL_SKILLS[i][j]
							+" ("+skillLevel(Skill.CATEGORIES[i],Skill.ALL_SKILLS[i][j])+")\n";
			}
			skillString+="\n";
		}
		return skillString;
	}
	
	//move functions either override monster moves or work the same way but are called by keyboard input
	
	public void endPlayerTurn(){
		
		if(gameStarted){
			fov.devRefreshFOV();	//for testing only. Useful for testing monster AI.
			//fov.refreshFOV();
		
		if(stunCountDown>0)		//cannot stun repeatedly with STR-based stuns.
			stunCountDown--;
		regenStep();
		hungerStep();
		
		currentLevel.startTurnCounter();
		if(currentLevel.monsterGenerator!=null)	//TODO: this should be in some sort of tick method for turnCounter or Level.
			currentLevel.monsterGenerator.step();	//chance to produce a random monster. placed after the "other monsters" turn so a monster cannot spawn right next to the player and then attack.
		statusesOccur(); //TODO: decrement statuses based on ticks, not turns. (also maybe do status effects this way?)
		RogueLikeGui.refreshScreen();
		}
	}
	
	
	private void regenStep() {		//a chance to restore 1 HP/MP this turn.
		if(currentHp()>0){
			if(FOR()>dice.nextInt(100))
				restoreHp(1);
			if(WIL()>dice.nextInt(100))
				restoreMp(1);
		}
	}
	
	public void attemptEquip(int itemIndex){		//consider breaking this up into multiple methods if I need to work with it again.
		if((inventory.getItem(itemIndex)).equippable()){		//checks to make sure the item is actually equipment
			Class<?>equipmentType=inventory.getItem(itemIndex).getClass();
			Item equippingItem=inventory.getItem(itemIndex);
			equippedItems.attemptEquip((Equipment)equippingItem,equipmentType);
		}	
		else
			changeCurrentMessage("You can't equip that "+inventory.getItem(itemIndex)+".",currentTile,true);
	}

	@Override
	public void moveTo(int xPos, int yPos){
		//TODO: may need more error handling to prevent moving past solid objects, or out of the room.
			//should this be in the monster move command?
		if(currentLevel.containsTile(xPos, yPos)){
			Tile tile=currentLevel.getTile(xPos, yPos);
			
			if(currentLevel.isPassable(xPos,yPos)){		//idea: some of the code in this if statement might belong better inside of the "setposition" method
				currentTile.clear();
				setPosition(xPos, yPos);
				currentTile.monster=this;
				if((currentTile.containsItems()
						|| currentTile.getGold()!=0)
						&& RogueLikeGui.autoPickUp)
							pickUpAllTileItems();
				if(currentTile.getClass()==Trap.class)
					((Trap)currentTile).trigger(this);	//TODO: if the trap can be dodged, switch this with another method for dodging traps.
				decrementHungerPoints();
				return;
				}
		else if(tile.monster!=null){
			setCurrentMessage("");
			attack(tile.monster);
		}
			
		else if(tile.getClass()==Door.class&&tile.isVisible){
			openDoor((Door)currentLevel.getTile(xPos, yPos));
		}
		}
	}
	
	//damage methods. TODO: try to keep as many steps as possible in the monster class.
	
	@Override
	public int determineMeleeDamage(){	//TODO: add a determined ranged damage method.
		double roll=0;
		int weaponRoll;
		weaponRoll=currentWeapon().meleeDamageRoll();
		roll+=weaponRoll;
		int weaponStat=determineWeaponStat(currentWeapon());
		int statDivisor=weaponDivisor();
		double minRoll=roll;
		double maxRoll=roll;
		String damageRange=currentWeapon().determineDamageRange();
		switch(damageRange){
		case("low"):
			minRoll-=(1.2*(roll)*currentWeapon().variance);
			maxRoll+=(0.8*(roll)*currentWeapon().variance);
			break;
		case("medium"):
			minRoll-=((roll)*currentWeapon().variance);
			maxRoll+=((roll)*currentWeapon().variance);
			break;
		case("high"):
			minRoll-=(0.8*(roll)*currentWeapon().variance);
			maxRoll+=(1.2*(roll)*currentWeapon().variance);
			break;
			}
			
			minRoll+=2*weaponSkillBonus();
			maxRoll+=weaponStat/statDivisor;
			
			while(maxRoll<=minRoll)
				maxRoll++;
			
			roll=minRoll+dice.nextInt((int)(maxRoll-minRoll+1));		//the final roll is made.
			roll+=attackModifier;	//TODO: make this step more complex as necessary, since "attackModifier" is a very vague concept.
		if(roll<=1)
			roll=1;
		return (int)roll;	
	}
	
	@Override
	public void adjustBaseDamage(int damage){
		attackModifier+=damage;
	}
	
	@Override
	public Weapon bareHands(){
		Weapon hands=new Weapon("hands",getSkill(Skill.FIGHTING,Weapon.FISTS).skillLevel,Weapon.FISTS,Material.FLESH);
		hands.quality=2;
		return hands;
		//TODO: determine fist quality/variance/other attributes
	}
	
	@Override
	public int meleeChanceToHit(){	//determine whether monster hits or misses.
		//ranges between Weaponskill and (weaponskill+racemod+weapon stat/2)
		return weaponSkillBonus()+dice.nextInt(toHitRaceMod()+determineWeaponStat(currentWeapon())/2+1);
	}
	
	//ammo throwing methods
	
	@Override
	protected int thrownDamage(Ammo projectile) {	//TODO: change to include proper data.
		int damage=projectile.getDamage(currentWeapon());
		if(projectile.properlyFired(currentWeapon()))
			return damage+rangedBonus(projectile)+dice.nextInt(currentWeapon().getPower()+1);
		else if(damage==0)
			return 0;
		return damage+rangedBonus(projectile)+dice.nextInt(rangedBonus(projectile)*2+1);
	}
	
	@Override
	protected int thrownDamage(Weapon thrownWeapon) {
		int damage=thrownWeapon.getBaseThrownDamage();
		if(damage==0)
			return 0;
		return damage+rangedBonus(thrownWeapon)+dice.nextInt(rangedBonus(thrownWeapon)*2+1);
	}
	
	//general throwing methods
	
	@Override
	protected int thrownToHit(Item projectile){
		if(projectile.properlyFired(currentWeapon()))
			return rangedBonus(projectile)+dice.nextInt(rangedToHitRaceMod()+currentWeapon().quality+1);
		return rangedBonus(projectile)+dice.nextInt(rangedToHitRaceMod()+rangedBonus(projectile)*2+1);
	}
	
	@Override
	protected int thrownDistance(Item projectile) {	//distance (in tiles) a thrown ammo can travel. (TODO: expand so it applies to items in general.)
		double weight=projectile.getSingleWeight();	//if multiple items are thrown at once (and not in succession in a single turn) then change this
		int distance=0;
		if(projectile.properlyFired(currentWeapon()))
			return distance+rangedBonus(projectile)+currentWeapon().getPower()+(int) (15-weight/2);
		else
			return Math.max(1, distance+rangedBonus(projectile)+(int) (15-weight/2));
	}
	
		//ranged bonus
	
	private int rangedBonus(Item projectile){
		if(projectile.properlyFired(currentWeapon())){
			return determineWeaponStat(currentWeapon())/6+weaponSkillBonus();
		}
		return determineAmmoStat(projectile)/6+rangedSkillBonus();
	}
	
	@Override
	public int evasionValue(){	//dodge skill+racemod and racemod+dodgeskill+dex/4
		int minEvade=2*dodgeSkill()+dodgeRaceMod();
		int EV=minEvade+ dice.nextInt(DEX()/4+1)-equippedItems.totalEvasionPenalty();
		return EV;
	}
	
	public int dodgeSkill(){
		if(getSkill(Skill.DEFENSIVE,Skill.DODGE)!=null)
			return getSkill(Skill.DEFENSIVE,Skill.DODGE).skillLevel;
		else
			return 0;
	}
	
	public int dodgeRaceMod(){
		return playerRace.dodgeMod;
	}
	
	public int toHitRaceMod(){
		return playerRace.toHitMod[0];
	}
	
	private int rangedToHitRaceMod(){
		return playerRace.toHitMod[1];
	}
	
	public int determineWeaponStat(Weapon weapon){	//TODO: make sure this is chosen properly
		if(weapon!=null&&!weapon.weaponCategory.equals(Weapon.FISTS)){
			switch(currentWeapon().weaponStat()){
			case "STR":
				return STR();
			case "DEX":
				return DEX();
			case "STR_DEX":
				return Math.max(STR(), DEX());
			default:
				return Math.max(STR(), DEX());
			}
		}
		else return Math.max(STR(), DEX());
	}
	public String determineWeaponStat(){
		if(!currentWeapon().weaponCategory.equals(Weapon.FISTS)){
			switch(currentWeapon().weaponStat()){
			case "STR":
				return "STR";
			case "DEX":
				return "DEX";
			case "STR_DEX":
				return "STR_DEX";
			default:
				return "STR_DEX";
			}
		}
		else return "STR_DEX";
	}
	
	public int determineAmmoStat(Item projectile){
		switch(projectile.getAmmoStat()){
		case(ItemReader.STR):
			return STR();
		case(ItemReader.DEX):
			return DEX();
		case(ItemReader.STR_DEX):
			return Math.max(STR(), DEX());
		}
		return 0;
	}

	//experience level methods
	
	public void levelUp(){
		playerExperience.levelUp();
		adjustMaxHitPoints(4);
		
		//TODO: store stat gains in the "experience" class, or derive them from there with getters.
	}
	
	public int currentExpLevel(){
		return playerExperience.level;
	}
	
	public void gainExp(int exp){		//should this be a roll?
		playerExperience.gainExp((int) (exp*INTExpBonus()));
		while(canLevelUp()){
			levelUp();
			appendToCurrentMessage("You are now level "+currentExpLevel()+"!");
		}
	}
	
	private double INTExpBonus(){	//this formula ensures that INT is useful, but not overpowered, for leveling up.
		return 0.3*Math.sin(INT()*(2.0*Math.PI/360.0))+0.95;
	}
	
	public boolean canLevelUp(){
		return playerExperience.experiencePoints[0]>=playerExperience.experiencePoints[1];
	}
	
	//search/identify methods
	
	public void search() {	//searching improves with higher perception.
		Tile[] tiles=Movement.adjacentTiles(this);
		for(int i=0;i<tiles.length&&tiles[i]!=null;i++){
			
			if(!tiles[i].isVisible){
				searchRoll(tiles[i]);
				break;
			}
		}
	}
	
	private void searchRoll(Tile tile){
		
		int difficulty=tile.getSearchDifficulty();
		
		int minRoll=5*skillLevel(Skill.UTILITY,Skill.SEARCHING);
		int maxRoll=Math.max(minRoll+1, difficulty+2*PER());	//every tile is searchable, no matter how hard, but some may take many tries.
		
		int roll=minRoll+dice.nextInt(maxRoll-minRoll);	//doesn't need a +1, since PER() cannot be lower than 7.
		
		if(roll>difficulty){
			improveSkill(Skill.UTILITY,Skill.SEARCHING);
			tile.setVisible();
		}
	}
	
	//skill methods
	
	public void setUpSkills(){
		skillSets=new SkillCategory[Skill.CATEGORIES.length];
		for(int i=0;i<skillSets.length;i++){
			skillSets[i]=new SkillCategory(Skill.CATEGORIES[i]);
		}
	}
	
	public void setSkill(Skill skill){
		for(int i=0;i<skillSets.length&&skillSets[i]!=null;i++){
			if(skill.skillCategory==skillSets[i].toString())
				skillSets[i].addSkill(skill);
		}
	}
	
	public Skill getSkill(String category, String name){
		for(int i=0;i<skillSets.length&&skillSets[i]!=null;i++){
			if(skillSets[i].toString()==category){
				Skill[] skills=skillSets[i].getSkills();
				for(int j=0;j<skills.length;j++){
					if(skills[j].toString().equals(name))
						return skills[j];
				}
				return null;
			}
		}
		return null;
	}
	
	public int skillLevel(String category, String name){
		Skill skill =getSkill(category, name);
		if(skill!=null)
			return skill.skillLevel;
		return 0;
	}
	
	public SkillCategory getSkillCategory(String category){
		for(int i=0;i<skillSets.length;i++){
			if(skillSets[i].toString()==category)
				return skillSets[i];
		}
		return null;
	}
	
	public void trainSkill(Skill skill){
		skill.train();
		if(skill.canLevelUp()){
			skill.levelUpSkill();
			appendToCurrentMessage("Reached skill level "+skill.skillLevel+" at "+skill.toString()+"!");
		}
	}
	
	public void improveSkill(String category, String name){
		Skill skill=getSkill(category,name);
		trainSkill(skill);
	}
	
	public int weaponSkillBonus(){	
		return getSkill(Skill.FIGHTING,currentWeaponCategory()).skillLevel;
	}
	
	public int rangedSkillBonus(){
		return getSkill(Skill.FIGHTING,Weapon.BOW).skillLevel;
	}
	
	public String currentWeaponCategory(){
		String weaponCategory="";
		weaponCategory=new String(currentWeapon().weaponCategory);
		return weaponCategory;
	}
	
	public int weaponDivisor(){	//this is a divisor used to lower the effect of weapon skill. The higher the skill, the lower the divisor.
		int skillLevel=weaponSkillBonus();
			if(skillLevel>=0){
				if(skillLevel==0)
					return 5;
				else if(skillLevel>=1&&skillLevel<5)
					return 4;
				else if(skillLevel>=5&&skillLevel<10)
					return 3;
				else if(skillLevel>=10)
					return 2;
			}
			return 5;
	}
	
	//stun methods
	@Override
	public int getStunChance(){
		return 5+(weaponSkillBonus())+getStunBoost();
	}	
	
	private int getStunBoost(){		//this is the stun boost from stats.
		String weaponStat=determineWeaponStat();
		switch (weaponStat){
		case "STR": return STR();
		case "DEX": return 0;
		case "STR_DEX":
			if(STR()<DEX())
				return 0;
			else
				return getStunChance(STR());
		default:
			if(STR()<DEX())
				return 0;
			else
				return getStunChance(STR());	
		}
	}
	protected int getStunChance(int STR){
		return STR/7;
	}
	
	//crit methods
	@Override
	protected int getCritChance(){
		return 5+(weaponSkillBonus())+getCritBoost();
	}
	private int getCritBoost(){
		String weaponStat=determineWeaponStat();
		switch (weaponStat){
		case "STR": return 0;
		case "DEX": return DEX();
		case "STR_DEX":
			if(STR()>DEX())
				return 0;
			else
				return getCritChance(DEX());
		default:
			if(STR()>DEX())
				return 0;
			else
				return getCritChance(DEX());	
		}
	}
	
	private int getCritChance(int DEX){
		return DEX/6;
	}
	
	//multi-attack methods
	
	@Override
	protected int getMultipleAttackChance(){
		return (DEX()/2)+(2*weaponSkillBonus());	//Dex/2+weaponskill*2
	}
	
	//spell methods
	
	public boolean knowsSpells(){
		return (spells[0]!=null);
	}
	
	public boolean knowsSpell(int index){
		return index>=0&&index<spells.length&&spells[index]!=null;
	}
	
	public boolean enoughMP(Spell spell){
		return currentMP()>=spell.getMPCost();
	}
	
	public Spell getSpell(int index){
		if(!knowsSpell(index))
			return null;
		return spells[index];
	}
	
	public void learnSpell(Spell newSpell){		//TODO: make sure learned spells are ordered properly. Also, change current message when a spell is learned.
		int index=0;
		while(index<spells.length&&spells[index]!=null){
			index++;
		}
		spells[index]=newSpell;
	}
	
	public void castSpell(int spellIndex){
		if(spellIndex>=0&&spellIndex<spells.length
		&&spells[spellIndex]!=null){
			Spell spell=spells[spellIndex];
			if(enoughMP(spell)){
				//TODO: case for casting spell. (it seems this might not be necessary. not sure yet, though.)
			}
			else{
				//TODO: case for not enough MP. (might not belong here.)
			}
		}
	}
	
	public void castSpell(Spell missile, char direction){	//used to shoot a spell, and only once it is confirmed that the player can cast it. (therefore, few or no checks needed.)
		trainSkill(spellSkill(missile));	//TODO: move this to a central place once spell code is more organized.
		RogueLikeGui.frame.createProjectile(this, missile,direction,currentTile);
		loseMP(missile.getMPCost());
	}
	
	public Skill spellSkill(Spell spell){	//TODO:test
		for(int i=0;i<Spell.magicSchools().length&&Spell.magicSchools()[i]!=null;i++){
			if(Spell.magicSchools()[i].equals(spell.school()))
				return getSkill(Skill.MAGIC, Skill.MAGIC_SKILLS[i]);
		}
		return null;
	}
	
	//mana methods
	
	public int[] getMagicPoints() {
		return magicPoints;
	}
	
	public int maxMagicPoints() {
		return magicPoints[1];
	}
	
	public int currentMP(){
		return magicPoints[0];
	}
	
	public void setMagicPoints(int magicPoints) {		//only meant for initially creating a monster with full health.
		this.magicPoints[0] = magicPoints;
		this.magicPoints[1] = magicPoints;
	}
	
	public void fillMagicPoints(){					//restore to full HP
		magicPoints[0]=magicPoints[1];
	}
	
	public void setMaxMagicPoints(int newMax){
		if(newMax>0)
			magicPoints[1]=newMax;
	}
	
	public void restoreMp(int mp){	//restore mp. Not to be used for taking away MP, to avoid confusion.
		if(mp>0&&magicPoints[0]+mp<=magicPoints[1])
			magicPoints[0]+=mp;
	}
	
	public void loseMP(int mp){
		if(mp>0&&magicPoints[0]-mp>=0)
			magicPoints[0]-=mp;
	}
	
	public void adjustMaxMagicPoints(int mp){
		if(magicPoints[1]+mp>0)
			magicPoints[1]+=mp;
	}
	
	//dungeon level-related methods
	
	public void visitLevel(Level level){
		int index=0;
		while(index<levelsVisited.length&&levelsVisited[index]!=null)
			index++;
		levelsVisited[index]=level;
		level.addTraps();
		level.placeInitialMonsters();
		level.placeItems();
	}
	
	public boolean hasVisitedLevel(Level level){
		for(int i=0;i<levelsVisited.length&&levelsVisited[i]!=null;i++){
			if(levelsVisited[i]==level)
				return true;
		}
		return false;
	}
	
	//stat methods. TODO: make leveling up increase stats. also, make all stats affect gameplay.
	
		public String statAbbreviation(int index){
			switch(index){
			case 0: return "STR";
			case 1: return "DEX";
			case 2: return "FOR";
			case 3: return "PER";
			case 4: return "WIL";
			case 5: return "INT";
			case 6: return "LCK";
			}
			return "";
		}
		
		public int STR(){return stats[0];}	public int DEX(){return stats[1];}	//getters
		public int FOR(){return stats[2];}	public int PER(){return stats[3];}
		public int WIL(){return stats[4];}	public int INT(){return stats[5];}
		public int LCK(){return stats[6];}
		
		public void adjustSTR(int a){stats[0]+=a;}	public void adjustDEX(int a){stats[1]+=a;}	//mutators
		public void adjustFOR(int a){stats[2]+=a;}	public void adjustPER(int a){stats[3]+=a;}
		public void adjustWIL(int a){stats[4]+=a;}	public void adjustINT(int a){stats[5]+=a;}
		public void adjustLCK(int a){stats[6]+=a;}
		
		public void setStats(int[] startingStats){	//since this is only done on the player's creation and checks are made before it happens,  no error checks are needed here.
			stats=startingStats;
			setHitPoints((int)((5.0+(playerRace.HPMod()*(FOR()/5.0)))+playerClass.HPRoll()));		//starting HP is set here.
			setMagicPoints((int)((5.0+(playerRace.MPMod()*(WIL()/6.0)))+playerClass.MPRoll()));
			setHungerPoints(850);	//TODO: this is temporary. set hunger points differently if they vary or have a different constant.
		}
		
		public void gainStatsRandom(int value) {
			boolean[] statsRepeated={false,false,false,false,false,false,false};	//need to change the number of "false"s if number of stats changes.
			for(int i=0;i<value&&i<stats.length;i++){
				int statIndex=dice.nextInt(statsRepeated.length);
				while(statsRepeated[statIndex])
					statIndex=dice.nextInt(value);
				statsRepeated[statIndex]=true;
				incrementStat(statIndex);
			}
		}
		
		private void incrementStat(int index) {	//TODO: append-to messages
			switch(index){
			case(0): adjustSTR(1); changeCurrentMessage("You feel strong!",currentTile,false); return;
			case(1): adjustDEX(1); changeCurrentMessage("You feel nimble!",currentTile,false); return;
			case(2): adjustFOR(1); changeCurrentMessage("You feel healthy!",currentTile,false); return;
			case(3): adjustPER(1); changeCurrentMessage("You feel aware!",currentTile,false); return;
			case(4): adjustWIL(1); changeCurrentMessage("You feel motivated!",currentTile,false); return;
			case(5): adjustINT(1); changeCurrentMessage("You feel smart!",currentTile,false); return;
			case(6): adjustLCK(1); changeCurrentMessage("You feel lucky!",currentTile,false); return;
			}
		}
		//race methods
	
		public void setRace(String race) {
			playerRace=new Race(race);
		}
		
		//class methods
		
		public void setClass(String pClass) {
			playerClass=new CharacterClass(pClass);
			String[] classes=CharacterClass.CLASSES;
			for(int i=0;i<classes.length;i++){		//could divide these into separte methods if this section gets longer.
				if(playerClass.toString()==classes[i]){
					Skill[] skills=CharacterClass.CLASS_SKILLS[i];
					for(int j=0;j<skills.length;j++){
						setSkill(skills[j]);
					}
					//Item[] items=CharacterClass.CLASS_ITEMS[i];
					inventory=new Inventory(playerClass.getInventory());
					Item [] items=inventory.items;
					for(int j=0;j<items.length&&items[j]!=null;j++){	//TODO: need either a "new" constructor here, or some kind of getter that creates the class items from scratch.
						//(necessary to fix an equipment bug)
						if(items[j].equippable())
							attemptEquip(j);
					}
				}
			}
		}
	
	public void createCharacter(String playerRace, String playerClass,int[] stats) {	//called when the player's character is first created
		//WeaponReader.loadWeapons();	//sort of a clunky place to put this, but it should work as a quick fix.
		
		setRace(playerRace);
		setClass(playerClass);
		setStats(stats);
	}
	
	//food methods
	
	@Override
	public void eat(Food food){		//TODO: figure out where and if the player's turn ends upon eating. Make sure a turn is not consumed by a failed "eat" command.
		if(!full()){
			changeCurrentMessage("Ate "+food.name+".",currentTile,true);
			food.use(this);
			inventory.removeItem((Item)food);
			endPlayerTurn();
		}
		else
			changeCurrentMessage("You're too full to eat.",currentTile,true);	
	}
	
	//hunger methods
	
	//hunger methods

	private void hungerStep() {
	if (currentHp()>0){
		decrementHungerPoints();
	}
}	

public String hungerState(){
	int hunger=hungerPoints[0];
	int index=0;
	while(index<HUNGER_STATES.length-1){
		if(hunger<hungerThresholds()[index])
			return HUNGER_STATES[index];
		index++;
	}
	return HUNGER_STATES[HUNGER_STATES.length-1];
}

public int[] hungerThresholds(){
	int[] thresholds=new int [HUNGER_STATES.length-1];
	thresholds[0]= (int) (hungerPoints[1]*0);
	thresholds[1]= (int) (hungerPoints[1]*.2);
	thresholds[2]= (int) (hungerPoints[1]*.4);
	thresholds[3]= (int) (hungerPoints[1]*.6);
	thresholds[4]= (int) (hungerPoints[1]*.7);
	thresholds[5]= (int) (hungerPoints[1]*.8);
	return thresholds;
}
	
private void setHungerPoints(int points){
	if(points>0){
		hungerPoints[0]=points;
		hungerPoints[1]=points;
	}
}

public void gainHungerPoints(int points){
	if(points>0){
		hungerPoints[0]=Math.min(hungerPoints[0]+points,hungerPoints[1]);		//TODO: do fullness checks and have fullness consequences where necessary.
	}
}

	private void decrementHungerPoints(){		//TODO: figure out how starvation works here. determine proper thresholds.
		System.out.println(hungerPoints[0]);
		if (hungerPoints[0]>0)
		hungerPoints[0]--;
	if (hungerPoints[0]<=0)
		takeDamage(3, null, null);
	//uncomment to help test hunger.
}

public boolean full(){
	return hungerPoints[0]==hungerPoints[1];
}
	//identification-related methods
	
	public void identifyAllItems(){
		for(int i=0;i<inventory.getMaxItems();i++){
			if(inventory.getItem(i)!=null)
				identify(inventory.getItem(i));
		}
	}
	
	//TODO: ask nick if materials on their own can be identified and other info about identification.
	@Override
	public String displayItemName(Item item, boolean equipString){	//any method that displays item names should interface through this method.
		String name="";
		if(item!=null){
			if(item.identified())
				name=item.descriptiveName(PER());
			else if(itemKnown(item))
				name=item.trueName();	
			else
				name=item.genericName();
			if(equipString&&item.getClass().equals(Equipment.class)&&((Equipment)(item)).equipped)
				name+=" [E]";
		}
		return name;	
	}
	
	public void identify(Item item){	//identifies a particular item, providing detailed information on it and "learning" its generic name.
		
		if(gameStarted){
			String identifyMessage="";
			if(item.identified())
				identifyMessage="You've already identified the ";
			else
				identifyMessage="Identified the ";	
			identifyMessage+=displayItemName(item,false)+".";
			setCurrentMessage(identifyMessage);
			RogueLikeGui.refreshScreen();	//this is temporary. May want to end player turn here, depending on how the identification takes place.
		}
		if(!item.identified()){	//this check will help avoid unneccessary "learning".
			item.identify();
			learnItem(item);
		}
	}
	
	public void learnItem(Item item){
		int index=0;
		while(index<knownItems.length&&knownItems[index]!=null){
			if(knownItems[index].equals(item.genericName()))	//this check avoids duplicates in known item list.
				return;
			index++;
		}
		knownItems[index]=item.genericName();
	}
	
	public boolean itemKnown(Item item){	//checks to see if the player knows this type of item. (NOTE: this method may vary depending on which items allow which other items to be recognized.)
		int index=0;
		while(index<knownItems.length&&knownItems[index]!=null){
			if(knownItems[index].equals(item.genericName()))	//should material be the same too?
				return true;
			index++;
		}
		return false;
	}
	
	//weight/burdening
	
	public String burdenState(){
		int weight=weightCarried();
		int index=0;
		while(index<BURDEN_STATES.length-1){
			if(weight<burdenThresholds()[index])
				return BURDEN_STATES[index];
			index++;
		}
		return BURDEN_STATES[BURDEN_STATES.length-1];
	}
	
	public int[] burdenThresholds(){
		int[] thresholds=new int[BURDEN_STATES.length-1];
		thresholds[0]=STR()*12;
		thresholds[1]=STR()*14;
		thresholds[2]=STR()*16;
		return thresholds;
	}
	
	public int weightCarried(){
		return inventory.totalWeight();
	}
	
	//potion color methods
	
	public void setPotionColors(String[] dungeonPotionColors) {	//sets potion colors based on this particular dungeon's potion color setup
		for(int i=0;i<inventory.getMaxItems()&&inventory.getItem(i)!=null;i++){
			if(inventory.getItem(i).getClass().equals(Potion.class)){
				Potion potion=(Potion)inventory.getItem(i);
				potion.setColor(getPotionColor(dungeonPotionColors, potion));
			}
		}
			
		//Inventory potions=inventory.getItemsOfType("Potion");
		
	}
	
	private String getPotionColor(String[] dungeonPotionColors, Potion potion){
		for(int i=0; i<dungeonPotionColors.length;i++){
			if(potion.getPotionType().equals(Potion.POTION_NAMES[i]))
				return dungeonPotionColors[i];
		}
		return null;
		}
	
	private Random dice=new Random();
	public boolean gameStarted=false;
	private int attackModifier=0;	//modifier to melee attacks. (could make more complex for ranged)
	//public String color="931D1D";
	
	public Race playerRace;
	public CharacterClass playerClass;
	
	public Experience playerExperience=new Experience();
	protected int[] magicPoints={0,0};
	private int[] hungerPoints={0,0};
	private int[] stats= new int[7];			//consider getting the '7' (number of stats) from a more central source.
	public Spell[] spells = new Spell[100];		//should monsters have spells like this, too?
	//idea: could make a double-array of spells for different schools, or just 6 different arrays. (model the structure after Nick's
	//non-code structure after it is confirmed.)
	private SkillCategory[] skillSets;
	public Level[] levelsVisited=new Level[400];	//levels the player has been to
	private String[] knownItems=new String[1000];	//item types the player has identified

}
	
