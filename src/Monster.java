import java.util.Random;

//TODO: sort this class so that methods which also apply to the player are in a section
//separate from monster-only methods. (maybe even a separate class!)

public class Monster extends Entity{

	public final int MAX_INVENTORY_SIZE=30;	//maximum inventory size
	public final int INVENTORY_SLOTS=6;		//number of places to wear equipment
	public final Weapon UNARMED=new Weapon("hands",0,Weapon.FISTS,Material.FLESH);
	
	Random randGenerator=new Random();
	
	public Monster(){
		name = null;
	}
	
	public Monster(String name){
		this.name = name;
	}
	
	public Monster(String name, char icon){
		this.name = name;
		setIcon(icon);
	}
	
	public Monster (String name, char icon, int hitPoints, int baseDamage){
		this.name=name;
		setIcon(icon);
		setHitPoints(hitPoints);
		setBaseDamage(baseDamage);
	}
	public Monster (String name, char icon, int hitPoints, int baseDamage, int baseArmor, int toHitValue, int dodgeValue){
		this.name=name;
		setIcon(icon);
		setHitPoints(hitPoints);
		setBaseDamage(baseDamage);
		this.baseArmor=baseArmor;
		this.toHitValue=toHitValue;
		this.dodgeValue=dodgeValue;
	}

	public Monster(Monster monster) {		//TODO: keep this updated as monsters get more info.
		name=monster.name;
		setIcon(monster.getIcon());
		color=monster.color;
		monsterAIState=new AIState(AIState.IDLE,this);
		monsterAIState.setIntelligence(monster.monsterAIState.getIntelligence());
		//monsterAIState=monster.monsterAIState;
				//new AIState(monster.monsterAIState);	//TODO: finish
		
		setHitPoints(monster.maxHitPoints());
		setBaseDamage(monster.baseDamage);
		rangedBaseDamage=(monster.rangedBaseDamage);
		baseArmor=monster.baseArmor;
		toHitValue=monster.toHitValue;
		dodgeValue=monster.dodgeValue;
		turnDelay=monster.turnDelay;
		
		stunChance=monster.stunChance;
		critChance=monster.critChance;
		multipleAttackChance=monster.multipleAttackChance;
		
		xp=monster.xp;
		
		spawnChance=monster.spawnChance;
		availableBranches=monster.availableBranches;
		inventory=new Inventory(monster.inventory);
	}
	
	//toString methods
	
	public String toString(){
		return name;
	}
	
	public String currentMessageName() {	//refer to the player as "you" in some situations. May need to consider capitalization later.
		if (getClass().equals(Player.class))
				return("You");
		else
			return name;
	}
	
	public String reflexivePronoun(){
		if (getClass().equals(Player.class))
			return "yourself";
		else
			return "itself";
	}
	
	public String presentHelpingVerb() {
		if (getClass().equals(Player.class))
			return("are");
	else
		return "is";
	}
	
	public String pastHelpingVerb(){
		if (getClass().equals(Player.class))
			return("were");
	else
		return "was";
	}

	public void arrowTrapMessage(){	//consider making a more general way of accessing this.
		changeCurrentMessage(currentMessageName()+" "+pastHelpingVerb()+" hit by an arrow!",currentTile,false);
	}
	
	public String displayItemName(Item item, boolean equipString) {
		return item.genericName();
	}
	
	public String showHitPoints(){
		return hitPoints[0]+"/"+hitPoints[1];
	}
	
	public String showEquipment(){
		return equippedItems.showEquipment();
	}
	
	public String immobilityMessage() {	//if the monster cannot move, explain why. 
		String message="";
		if(!mobile){
			int index = 0;
			while(statuses.getStatus(index)!=null){
				if(statuses.getStatus(index).getDuration()>0
				&&(statuses.getStatus(index).getStatusType().equals(Status.IMMOBILE))){	
					/*if(statuses[index].firstTurn&&this.getClass()!=Player.class){
						message+=statuses[index].effectMessage(this);	//TODO: if monsters should display stun messages, set it so here.
						return message;
					}*/
					if(!(this.getClass()!=Player.class
						&&statuses.getStatus(index).getName().equals("stunned"))){	//this is a rough solution to a problem with stun messages.
						message+=statuses.getStatus(index).effectMessage(this);
						return message;
					}
				}
				index++;
			}
		}
		return message;
	}
	
	//basic AI
	
	public void turn(){	//monster decides what to do this turn		
		
		if(currentHp()>0){
			statusesOccur();		//should this go here?
			if(currentHp()<=0)
				return;
			boolean equippedSomething=false;
			
			if(!allBestEquipped()){		//monsters equip their best equipment automatically.
				considerEquipment();
				equippedSomething=true;
			}
			if(!equippedSomething){
				decideMove();
			}
				
			if(stunCountDown>0){
				stunCountDown--;
			}	
		
			if(name=="Blue Eyes White Dragon"){	//temporary for boss testing
				if(dice.nextInt(16)>13){
					changeCurrentMessage("Blue eyes shouts: 'You cannot best the power of the cards!'",currentTile,false);
				}
			}
		}
		fov.refreshFOV();	//TODO: make sure this definitely goes here. (it might belong elsewhere)
	}
	
	private void decideMove(){
		monsterAIState.decideMove();
	}

	public boolean canSee(Tile tile){
		return fov.canSee(tile);
	}
	
	public void detectAdjacentMonster(){	//see if there is a monster in adjacent squares
		for(int i=-1;i<2;i++){
			for(int j=-1;j<2;j++){
				if(!(i==0&&j==0)
				&&currentLevel.getTile(getXPos()+i,getYPos()+j).monster!=null){
					reactToAdjacentMonster(currentLevel.getTile(getXPos()+i,getYPos()+j).monster);
				}
			}
		}
	}
	
	private Tile[] adjacentTiles(){
		Tile[] tiles=new Tile[8];
		int index=0;
		for(int i=-1;i<2;i++){
			for(int j=-1;j<2;j++){
				if(!(i==0&&j==0)
				&&currentLevel.containsTile(getXPos()+i,getYPos()+j)){
				tiles[index]=currentLevel.getTile(getXPos()+i,getYPos()+j);
				index++;
				}
			}
		}
		return tiles;
	}
	
	public Tile[] adjacentEmptyTiles(){
		Tile[] emptyTiles=new Tile[8];
		Tile[] options=adjacentTiles();
		int index=0;
		for(int i=0;i<8&&options[i]!=null;i++){
			if(options[i].isPassable){
				emptyTiles[index]=options[i];
				index++;
			}
		}
		return emptyTiles;
	}
	
	private void reactToAdjacentMonster(Monster adjacentMonster) {		//determine reaction to an adjacent monster. Ideally much of this can be set by default (don't attack own species, etc.)
		if(hostileTowards(adjacentMonster)){
			attack(adjacentMonster);
		}
	}
	
	private boolean hostileTowards(Monster otherMonster) {	//determine if this monster is hostile towards another one
		for(int i=0;i<enemyMonsters.length;i++){
			if(enemyMonsters[i]!=null&&			//might need something more sophisticated than == to see if two monsters are the same monster
				enemyMonsters[i]==otherMonster)
				return true;
		}
		return false;
	}
	
	public void addEnemy(Monster newEnemy){	//add a monster to list of enemies
		int index=0;
		while(index<enemyMonsters.length&&enemyMonsters[index]!=null)
			index++;
		enemyMonsters[index]=newEnemy;	
	}
	
	public void drawAllMonsterAggro(){	//does levelMonsters properly add and remove monsters?
		Monster[] allMonstersOnLevel=currentLevel.levelMonsters;
		for(int i=0; i<allMonstersOnLevel.length&&allMonstersOnLevel[i]!=null;i++){
			if(this!=allMonstersOnLevel[i])
				allMonstersOnLevel[i].addEnemy(this);
			}
		}
	
	public Monster[] allEnemiesOnLevel(){ //returns all enemies on the current level;
		Monster[] enemiesOnLevel=new Monster[100];
		Monster[] allMonstersOnLevel=currentLevel.levelMonsters;
		int index=0;
		for(int i=0; i<allMonstersOnLevel.length;i++){
			if(allMonstersOnLevel[i]!=null&&hostileTowards((allMonstersOnLevel[i]))){
					enemiesOnLevel[index]=allMonstersOnLevel[i];
					index++;
			}
		}
		return enemiesOnLevel;
	}
	
	public Monster[] allEnemiesInSight(){ //returns all enemies in sight
		Monster[] enemiesInSight=new Monster[100];
		if(fov==null)
			return null;
		Monster[] allMonstersInSight=fov.visibleMonsters();	//TODO: this is the only part that needs to be changed
		int index=0;
		for(int i=0; i<allMonstersInSight.length;i++){
			if(allMonstersInSight[i]!=null&&hostileTowards((allMonstersInSight[i]))){
				enemiesInSight[index]=allMonstersInSight[i];
					index++;
			}
		}
		return enemiesInSight;
	}
	
	private boolean playerInView() {
		if(fov!=null)
			return fov.playerInView();
		else
			return false;
	}
	
	public void mapAllLevel() {
		fov.mapAllLevel();
		if(this.getClass().equals(Player.class)){
			changeCurrentMessage("You suddenly feel well-oriented.",currentTile,false);
		}
	}

	//equipment consideration AI
	
	//TODO: decide more about monster equipment preferences and equipment AI.
	public void considerEquipment(){	//Monster decides whether or not to change its equipment. TODO: if the monster equips an item, its turn ends.
		if(mobile&&getClass()!=Player.class){
			for(int i=0;i<Equipment.EQUIPMENT_SLOTS.length;i++){
				String slot=Equipment.EQUIPMENT_SLOTS[i];
				Equipment best = bestEquipment(slot);
				if(!bestEquipped(slot)){
					if(equippedItems.equipmentSlotFree(slot)){
						if(best.getClass()==Weapon.class	//check to see if monster is trying to equip a two-handed weapon over a shield.
						&& ((Weapon)best).twoHanded
						&& !equipmentSlotFree(Equipment.OFF_HAND)){
							equippedItems.unequipItem(Equipment.OFF_HAND);
							return;
						}
						equippedItems.equip(best);
						return;
					}
					else
						equippedItems.unequipItem(Equipment.EQUIPMENT_SLOTS[i]);
					return;
				}
			}
		}
	}
	
	Equipment bestEquipment(String slot){	//best equipment of a type
		Equipment[] slotEquipment=equipmentOfType(slot);
		if(inventory.isEmpty()||slotEquipment.length<0||slotEquipment==null)
			return null;
		Equipment best=slotEquipment[0];
		for(int i=1;i<slotEquipment.length&&slotEquipment[i]!=null;i++){
			if(slotEquipment[i].getEffectivePower()>best.getEffectivePower())
				best = slotEquipment[i];
		}
		return best;
	}
	
	private boolean bestEquipped(String slot){	//see if the best equipment for this slot is already equipped.
		if(slot.equals(Equipment.OFF_HAND)
		&& bestEquipment(Equipment.WEAPON)!=null
		&& ((Weapon)bestEquipment(Equipment.WEAPON)).twoHanded)//if slot is shield, get bestEquipment(Equipment.WEAPON_SLOT) and see if it is two-handed. if so, return "true".
			return true;
		if(bestEquipment(slot)==null)
			return true;
		return bestEquipment(slot).equals(equippedItems.getEquipmentInSlot(slot));
	}
	
	private boolean allBestEquipped(){		
		for(int i=0;i<Equipment.EQUIPMENT_SLOTS.length;i++){
			if(!bestEquipped(Equipment.EQUIPMENT_SLOTS[i]))
				return false;
		}
		return true;
	}
	
	private Equipment[] equipmentOfType(String slot){
		Equipment[] possibleEquipment=inventory.getAllEquipment();
		Equipment[] slotEquipment=new Equipment[possibleEquipment.length];
		int index=0;
		for(int i=0;i<possibleEquipment.length&&possibleEquipment[i]!=null;i++){
			if(possibleEquipment[i].getSlot().equals(slot)){
				slotEquipment[index]=possibleEquipment[i];
				index++;
			}
		}
		return slotEquipment;
	}
	
	//TODO: (DO NOT REMOVE YET) only use shouldEquip() to replace current equipment process if the current system causes undue lag.
	/*private boolean shouldEquip(Equipment newEquipment) {//TODO: once it is finished, call this when the monster obtains an item.
		if(!newEquipment.equipped){
			int slot=newEquipment.wornIndex;
			if(equipmentSlotFree(slot))
				return true;
			Equipment currentEquipment=getEquipmentInSlot(slot);
			//TODO: compare currentEquipment to newEquipment using bestEquipment()
		}
		return false;
	}*/

	//AI setters/getters
	
	public String getIntelligence() {
		return monsterAIState.getIntelligence();
	}
	
	public void setIntelligence(String intelligence) {
		monsterAIState.setIntelligence(intelligence);
	}
	
	//movement methods		(consider giving some of these their own class)
	
	public void move (char direction){
		if(mobile)
			move(direction, 1);
		else if(this.getClass().equals(Player.class))
			changeCurrentMessage(immobilityMessage(),currentTile,false);
		
	}
	
	public void move(char direction, int magnitude){
		relativeMove(Movement.numpadToX(direction)*magnitude,
				Movement.numpadToY(direction)*magnitude);
	}
	
	public void relativeMove(int relativeXPos, int relativeYPos){
		moveTo(getXPos()+relativeXPos, getYPos()+relativeYPos);
	}
	
	public void moveTo(int xPos, int yPos){		//since the player moveTo() overrides this one, it includes handling for monsters trying to move around obstacles.
		//TODO: if monsters every move two spaces in a turn or teleport, need error handling to prevent moving onto solid objects, or out of the room.
		if(getXPos()==xPos&&getYPos()==yPos)	//if the monster is moving to its own location, this happens.
			return;
		if(currentLevel.containsTile(xPos, yPos)){
			if(currentLevel.isPassable(xPos,yPos)
			&& !(currentLevel.getTile(xPos, yPos).isVisibleTrap())){//the monster will go around visible traps, but not invisible ones.
				currentTile.clear();
				setPosition(xPos, yPos);
				currentTile.monster=this;
				if(currentTile.getClass().equals(Trap.class))
					((Trap)currentTile).trigger(this);	//TODO: if the trap can be dodged, switch this with another method for dodging traps.
				else	//TODO: traps going off should create sounds that are not footsteps, and monsters without feet should not create footsteps.
					currentLevel.addSound(new Sound("footsteps",3), currentTile);
				return;
				}
			else if(currentLevel.getTile(xPos, yPos).monster!=null		//idea: keep some of this code for player, but prompt to decide whether or not to attack non-hostile monsters.
				&&hostileTowards(currentLevel.getTile(xPos, yPos).monster)){
					attack(currentLevel.getTile(xPos, yPos).monster);
					return;
			}
			else{	
				moveAround(Movement.determineDirection(getXPos(),getYPos(),xPos,yPos));
				return;
			}
		}	
	}
	
	public void moveRandom() {
		Tile[] choices=adjacentEmptyTiles();
		int randomIndex=dice.nextInt(choices.length);
		while(choices[randomIndex]==null)
			randomIndex=dice.nextInt(choices.length);
		moveTowards(choices[randomIndex]);
	}
	
	private void moveAround(char direction) {//this is for when the monster's path is blocked by a nonhostile but impassable entity.
		Tile nearestOpen=Movement.nearestOpenTile(this, direction);
		moveTo(nearestOpen.xCoord,nearestOpen.yCoord);
	}
	
	public void moveTowards(Monster targetMonster) {
		char direction = determineDirection(targetMonster);
		move(direction);
	}
	
	public void moveTowards(Tile destination) {
		char direction = determineDirection(destination);
		move(direction);
	}

	private char determineDirection(Monster targetMonster) {	//figure out which direction another monster is in
		
		int targetX=targetMonster.getXPos();
		int targetY=targetMonster.getYPos();
		
		return Movement.determineDirection(this.getXPos(), this.getYPos(),targetX, targetY);
	}
	
	private char determineDirection(Tile destination){
		int targetX=destination.xCoord;
		int targetY=destination.yCoord;
		
		return Movement.determineDirection(this.getXPos(), this.getYPos(),targetX, targetY);
	}
	
	//stairs related methods
	
	//NOTE: Consider moving stairs traversing method to player, (use @Override) and giving monsters a separate method.
	public void goDownStairs(){							//TODO: if monsters other than the player can go down stairs, need to check if the stairs below are blocked.
		if(currentTile.getClass().equals(Stairs.class)){		
			if(!((Stairs)currentTile).goUp()){
								//if there are other ways to go down besides stairs, copy and paste some of this into another method		
				Stairs downStairs=new Stairs((Stairs)currentTile);	//shallow copy problems?
				Level nextLevel=currentLevel.levelDungeon.getNextLevel(currentLevel,downStairs);
				if(nextLevel==null){
					changeCurrentMessage("Those stairs don't go anywhere. Sorry about that.",currentTile,true);
					return;
				}
				currentLevel.removeMonster(this);	
				setCurrentLevel(currentLevel.levelDungeon.getNextLevel(currentLevel,downStairs));	
				Stairs upStairs=currentLevel.getLinkedUpStairs(downStairs);
				currentLevel.addMonster(this, upStairs);				
				currentTile.monster=this;
				
				if(this.getClass().equals(Player.class)){
					if(!((Player)this).hasVisitedLevel(currentLevel))	//if the level hasn't been visited before, add monsters.
						((Player)this).visitLevel(currentLevel);
				}
				
				drawAllMonsterAggro();		//NOTE: a monster going down stairs should not draw all monster aggro. however, no monsters can go down stairs yet, so this is left simplified.
				changeCurrentMessage("",currentTile,true);
				if(currentLevel.getBranch().levelType==LevelGenerator.GLITCH)
					changeCurrentMessage("IM SO SORRY",currentTile,true);
				return;
			}
			else
				changeCurrentMessage("Those stairs go up, not down.",currentTile,true);
		}
		else
			changeCurrentMessage("No stairs here.",currentTile,true);
	}
	
	public void goUpStairs(){
		if(currentTile.getClass().equals(Stairs.class)){		//TODO: just in case, do checks to see if there is a valid floor below.
			if(((Stairs)currentTile).goUp()){
				if(this.getClass().equals(Player.class)
				&&currentLevel.floor==0){
					die("");
					RogueLikeGui.playerDeath.goToDeathScreen(
							"As your kinsmen see you emerge \n " +
							"from the dungeon's entrance,\n " +
							"they quickly realize your \n" +
							"cowardice and charge towards \n" +
							"you, filled with rage. Before \n" +
							"you can even attempt an \n" +
							"escape, their spears pierce \n" +
							"your flesh and your blood \n" +
							"spills across the grass.");
					return;
				}
				Stairs upStairs=new Stairs((Stairs)currentTile);
				Level previousLevel=currentLevel.levelDungeon.getPreviousLevel(currentLevel,upStairs);
				
				if(previousLevel==null){
					changeCurrentMessage("Those stairs don't go anywhere. Sorry about that.",currentTile,true);
					return;
				}
				currentLevel.removeMonster(this);
				setCurrentLevel(currentLevel.levelDungeon.getPreviousLevel(currentLevel,upStairs));	
				Stairs downStairs=currentLevel.getLinkedDownStairs(upStairs);
				currentLevel.addMonster(this, downStairs);	
				currentTile.monster=this;		
				
				if(this.getClass().equals(Player.class)){
					if(!((Player)this).hasVisitedLevel(currentLevel))	//if the level hasn't been visited before, add monsters.
						((Player)this).visitLevel(currentLevel);
				}
				
				drawAllMonsterAggro();
				changeCurrentMessage("",currentTile,true);
				if(currentLevel.getBranch().levelType==LevelGenerator.GLITCH)
					changeCurrentMessage("IM SO SORRY",currentTile,true);
				return;
			}
			else
				changeCurrentMessage("Those stairs go down, not up.",currentTile,true);
		}
		else
			changeCurrentMessage("No stairs here.",currentTile,true);
	}
	
	//door-related methods
	
	public void openDoor(char direction){
		Tile possibleDoor=Movement.tileInDirection(this,direction);
		if(possibleDoor.getClass()==Door.class&&!possibleDoor.containsMonster()&&possibleDoor.isVisible)
			openDoor((Door)Movement.tileInDirection(this,direction));
		
		else if(getClass().equals(Player.class)){
			if(possibleDoor.getClass().equals(Door.class)&&possibleDoor.isVisible)
				changeCurrentMessage("The door is blocked.",currentTile,true);		//not sure when this should come up.
			else
				changeCurrentMessage("You see no door there.",currentTile,true);
		}
		fov.refreshFOV();
	}
	
	public void openDoor(Door door){
		String message=currentMessageName();
		if(door.isOpen)
			message+=" closed the door.";
		else
			message+=" opened the door.";
		if(getClass()==Player.class)
			((Player)this).endPlayerTurn();
		changeCurrentMessage(message,currentTile,true);
		door.toggleOpen();
	}
	
	public void goToLevel(Level nextLevel, Tile startTile){
		currentLevel.removeMonster(this);
		setCurrentLevel(nextLevel);	
		currentLevel.addMonster(this, startTile);	
		currentTile.monster=this;		
		
		if(this.getClass().equals(Player.class)){
			if(!((Player)this).hasVisitedLevel(currentLevel))	//if the level hasn't been visited before, add monsters.
				((Player)this).visitLevel(currentLevel);
		}
		
		drawAllMonsterAggro();
		changeCurrentMessage("",currentTile,true);
		if(currentLevel.getBranch().levelType.equals(LevelGenerator.GLITCH))
			changeCurrentMessage("IM SO SORRY",currentTile,true);
	}
	
	//damage-related functions
	
	public int determineMeleeDamage(){	//this is how a monster's damage is determined. The process is more complicated for the player.
		int retVal = baseDamage;		//NOTE: should this be a roll? right now it's a constant.
		retVal+=currentWeapon().getMeleeDamage();
		return retVal;
	}
	
	public void attack(Monster target){		//melee attacks a target.
		boolean successfulHit=meleeChanceToHit()>target.evasionValue();
		if(successfulHit){
			int damage = determineMeleeDamage();
			if(criticalRoll()){	//check for a critical hit.
				damage*=2;
				changeCurrentMessage("CRITICAL!",currentTile,false);
			}
			if(this.equals(target))		//for a monster attacking itself
				changeCurrentMessage(currentMessageName()+ " hit "+target.reflexivePronoun()+"!",currentTile,false);
			else
				changeCurrentMessage(currentMessageName()+ " attacked "+target.currentMessageName().toLowerCase()+" for "+damage+" damage!",currentTile,false);
				//target should only take damage if the hit is successful.
			target.takeDamage(damage,this,currentWeapon().getMaterial());
			if(stunCountDown==0&&stunRoll()&&target.currentHp()>0){
				Status oneTurnStun=new Status("stunned",Status.IMMOBILE,0,turnDelay+1);	//TODO: make sure turnDelay+1 is right by testing stuns.
				oneTurnStun.canMove=false;
				target.addStatus(oneTurnStun);
				changeCurrentMessage(currentMessageName()+" stunned " +target.currentMessageName()+"!",currentTile,false);
				stunCountDown=3;
			}
			if(!this.equals(target)&&stunCountDown==0&&multipleAttackRoll(0)&&target.currentHp()>0){	//check for consecutive attack roll.
				consecutiveAttack(target,damage,1);	//1 means "2nd attack" (first attack is iteration 0).
			}
			if(this.getClass()==Player.class)	//may not be necessary if the player overrides
				((Player)this).improveSkill(Skill.FIGHTING,((Player)this).currentWeaponCategory());
		}
		else
			changeCurrentMessage(currentMessageName()+ " missed.",currentTile,false);
	}
	
	private void consecutiveAttack(Monster target, int damage,int iteration){	//once a consecutive attack is confirmed, it always hits. each attack has a lower chance of occurring than the one before it.
		int consecutiveDamage=dice.nextInt(damage);
		if(consecutiveDamage>1){
			target.takeDamage(consecutiveDamage,this,currentWeapon().getMaterial());
			changeCurrentMessage(currentMessageName()+ " attacked "+target.currentMessageName().toLowerCase()+" again for "+consecutiveDamage+" damage!",currentTile,false);
			if(consecutiveDamage>3&&multipleAttackRoll(iteration))
				consecutiveAttack(target,consecutiveDamage,iteration+1);
		}
	}

	public void takeDamage(int damage, Monster attacker, Material material){		//takes damage. TODO: player should use this method, but perhaps add factors that are not present for monsters. (like armor skill)
		
		Armor shield = ((Armor)equippedItems.getEquipmentInSlot(Equipment.OFF_HAND));
		if(shield!=null&&shield.shieldBlockAttempt(material, damage)){	//TODO: make a separate boolean method that checks block attempt, and place it in this if statement.
			changeCurrentMessage(currentMessageName()+" blocked it with "+shield.toString()+"!",currentTile,false);
			return;
		}
		
		Armor struckArmor=equippedItems.defendingArmor();
		Material armorMaterial=struckArmor.getMaterial();
		int armorRoll=struckArmor.armorRoll();
		
		if(material!=null&&armorMaterial!=null){		//A "null" material (like a poisonous potion) ignores all armor.
		
			if(!material.relationship(armorMaterial).equals(Material.INVINCIBLE)){
				String relation="";
				if(armorMaterial.relationship(material)!=null)
					relation=armorMaterial.relationship(material);
			
				switch(relation){
				case(Material.WEAK):
					damage-=armorRoll*0.6;
					break;
				case(Material.STRONG):
					damage-=armorRoll*1.4;
					break;
				case(Material.INVINCIBLE):
					damage=0;
					break;
				default:
					damage-=armorRoll;
					break;
			}
		}
		}
		if(damage<=0){		//if there is more than enough armor to mitigate the damage, no damage is dealt.
			damage = 0;
			changeCurrentMessage(currentMessageName()+" shrugged it off.",currentTile,false);
		}
		hitPoints[0]-=damage;
		if(hitPoints[0]<=0){//case for a monster dying
			if(attacker == null)
				die("an unknown cause");
			else{
				die(attacker.name);
				if(attacker.getClass().equals(Player.class))	//player gains exp for monster's death
					((Player)attacker).gainExp(calculateXpReward());
			}
		}
	}
	
	protected int meleeChanceToHit(){	//determine whether monster hits or misses. TODO: consult nick about this roll, along with evasionValue().
		int variability = Math.max(1, (int)(Math.pow(toHitValue,0.5)));
		int hitChance=(toHitValue-variability)+dice.nextInt(2*variability+1);
		return hitChance;
	}
	
	protected int rangedChanceToHit(){	//determine whether monster hits or misses. TODO: consult nick about this roll, along with evasionValue().
		int variability = Math.max(1, (int)(Math.pow(toHitValue,0.5)));
		int hitChance=(toHitValue-variability)+dice.nextInt(2*variability+1);
		return hitChance;
	}
	
	protected int evasionValue(){
		int variability = Math.max(1, (int)(Math.pow(toHitValue,0.5)));
		int evadeChance=(dodgeValue-variability)+dice.nextInt(2*variability+1)-equippedItems.totalEvasionPenalty();
		
		return evadeChance;
	}
	
	protected boolean stunRoll(){
		return getStunChance()>dice.nextInt(100);
	}
	
	protected boolean criticalRoll(){
		return getCritChance()>dice.nextInt(100);
	}
	
	protected boolean multipleAttackRoll(int iteration){
		int chance=Math.max(getMultipleAttackChance()-iteration*10, 0);
		return chance>dice.nextInt(100);
	}

	protected void restoreHealth(int health){	//restores health
		hitPoints[0]+=health;
		if(hitPoints[0]>maxHitPoints()){
			fillHitPoints();
		}
	}
	
	public void die(String causeOfDeath){				//develop this more		(drop items, can no longer perform actions, object gets deleted, more specific message appears, etc.)	
		
		if(getClass().equals(Player.class)){		//if the player dies, call a separate method.
			RogueLikeGui.playerDeath.playerDies(causeOfDeath);	
		}
			
		else{		//if a monster dies, the player gets experience (not yet implemented) and the monster drops its inventory.
			Tile deathTile=new Tile(currentTile);
			changeCurrentMessage(name+" died.",deathTile,false);
			equippedItems.unequipAll();
			dropAllItems();	//will want to make monsters drop their gold, too.
			currentTile.addGold(inventory.takeAllGold());
			currentTile.addItem(new Food(name+" corpse",(20+(hitPoints[1]*4))));	//currently, a monster's corpse's nutritional value is based on its max HP.
			currentTile.clear();
			currentLevel.removeMonster(this);
		}	
			//TODO: figure out how monsters that are hostile towards another monster will handle its death.
			//idea: in the bit of code where this monster is discovered to have a null location, remove it from the list of hostiles.
			//alternate idea: expand the "removeMonster" function in currentLevel.
	}		
	
	//hit point methods

	public int[] getHitPoints() {
		return hitPoints;
	}
	
	public int maxHitPoints() {
		return hitPoints[1];
	}
	
	public int currentHp(){
		return hitPoints[0];
	}
	
	public void setHitPoints(int hitPoints) {		//only meant for initially creating a monster with full health.
		this.hitPoints[0] = hitPoints;
		this.hitPoints[1] = hitPoints;
	}
	
	public void fillHitPoints(){					//restore to full HP
		hitPoints[0]=hitPoints[1];
	}
	
	public void setMaxHitPoints(int newMax){
		if(newMax>0)
			hitPoints[1]=newMax;
	}
	
	public void restoreHp(int hp){	//restore hp. Not to be used for taking away HP, to avoid confusion.
		if(hp>0&&hitPoints[0]+hp<=hitPoints[1])
			hitPoints[0]+=hp;
	}
	
	public void adjustMaxHitPoints(int hp){
		if(hitPoints[1]+hp>0)
			hitPoints[1]+=hp;
	}
	
	//damage getters and setters
	
	public int getBaseDamage() {
		return baseDamage;
	}

	public void setBaseDamage(int baseDamage) {
		this.baseDamage = baseDamage;
	}
	
	public void adjustBaseDamage(int damage){
		if(baseDamage+damage>0)
			baseDamage+=damage;
		else
			baseDamage=0;
	}
	
	//Item related functions
	
	public String pickUpMessageStart(){
		if(getClass().equals((Player.class)))
			return ("Picked up");
		else
			return(name+" picked up");
	}
	
	public void pickUpItem(int index){		//pick up an item off the ground. IDEA: index of -1 means "pick up gold".
		
		Item pickingUpItem=currentTile.tileItems.getItem(index);
		if(pickingUpItem.stackable()){
			pickUpItemStack(index);
			return;
		}
		pickUpFullStack(pickingUpItem,index);
	}
	private void pickUpFullStack(Item pickingUpItem, int index) {
		if(!inventory.isFull()){
			changeCurrentMessage(pickUpMessageStart(),currentTile,false);
			if(pickingUpItem.stackable()&&pickingUpItem.getAmount()!=1)
				changeCurrentMessage(pickingUpItem.getAmount()+" "+Item.plural(displayItemName(pickingUpItem, false))+"."
							,currentTile,false);
			else
				changeCurrentMessage(pickingUpItem.article()+" "+displayItemName(pickingUpItem, false)+"."
						,currentTile,false);	
			if(pickingUpItem.equippable())
				((Equipment)pickingUpItem).equipped=false;
			obtainItem(currentTile.tileItems.takeItem(index));	
		}
		else if(getClass().equals((Player.class)))
			changeCurrentMessage("No room for more items.",currentTile,true);
	}
	
	private void pickUpItemStack(int index) {//pick up a stack of items off the ground.
		Item pickingUpStack=currentTile.tileItems.getItem(index);
		int findStackable = findStackableItem(pickingUpStack);
		if(findStackable!=-1){
			Item heldStack=inventory.getItem(findStackable);
				int unitsPickedUp=0;
			while(!heldStack.stackFull()&&	//as long as the current stack isn't full and the stack we're picking up isn't empty
				!pickingUpStack.stackEmpty()){
					unitsPickedUp++;
					heldStack.incrementStack();
					if(pickingUpStack.getAmount()==1
					|| heldStack.stackFull()){		//on the last item picked up, send a message.
						currentTile.displayIcon();				
						changeCurrentMessage(pickUpMessageStart(),currentTile,false);
						if(unitsPickedUp!=1)
							changeCurrentMessage(unitsPickedUp+" "+Item.plural(displayItemName(pickingUpStack, false))+"."
									,currentTile,false);
						else
							changeCurrentMessage(pickingUpStack.article()+" "+displayItemName(pickingUpStack, false)+".",currentTile,false);
					}	
					pickingUpStack.decrementStack(currentTile.tileItems);
				
				if(pickingUpStack.stackEmpty())	//will this cause an error since the item is removed by the inventory?
					return;
				
			}
				pickUpItemStack(index);	//this is recursion, so be careful for infinite loops.
		}
		else
			pickUpFullStack(pickingUpStack, index);
	}
	
	public int findStackableItem(Item pickingUpItem){
		if(pickingUpItem.stackable()){
		int index=0;
		
		if(inventory.getItem(0)!=null	//inital check necessary for index 0
				&& (inventory.getItem(0).stackEquivalent(pickingUpItem)
				&& !inventory.getItem(0).stackFull()))
					return 0;
		
		while(index<inventory.getItemCount()
			&&!inventory.getItem(index).stackEquivalent(pickingUpItem)
			&&inventory.getItem(index)!=null){
			index++;
				if(inventory.getItem(index)!=null
				&& (inventory.getItem(index).stackEquivalent(pickingUpItem)	//monster has an item that will stack with the other item
				&& !inventory.getItem(index).stackFull()))					//this same item is not a full stack already
					return index;
			}
		}
		return -1;	//means "no stackable item found"
	}

	public void pickUpAllTileItems(){
		int index=0;
		if(this.getClass()==Player.class)
			changeCurrentMessage("",currentTile,true);
		Inventory tileItems=currentTile.tileItems;
		if(!tileItems.noGold()){	//TODO: add an error case for max gold. (the fact that there isn't one may go unnoticed for a long time, since the max is so high.
			changeCurrentMessage(pickUpMessageStart()+" "+tileItems.getGold()+" gold pieces.",currentTile,false);
			obtainGold(currentTile.takeAllGold());
		}
		while(tileItems.getItem(index)!=null && !inventory.isFull())
			pickUpItem(index);
		if(index==0&&inventory.isFull()&&getClass()==(Player.class))
			changeCurrentMessage("No room for more items.",currentTile,false);
	}
	
	public void pickUpGold(){
		Inventory tileItems=currentTile.tileItems;
		changeCurrentMessage(pickUpMessageStart()+" "+tileItems.getGold()+" gold pieces.",currentTile,false);
		obtainGold(currentTile.takeAllGold());
	}
	
	public int currentGold(){
		return inventory.getGold();
	}
	
	public void	obtainItem(Item obtainedItem){	//TODO: should send a message(to the player only) if inventory is full.
		if(!inventory.isFull())
			inventory.addItem(obtainedItem);
	}
		
	public void obtainGold(int amount){
		inventory.addGold(amount);
	}
	//methods for dropping items
	
	public void dropItem(int index) {
		if(getClass().equals((Player.class))){
			Item droppingItem=inventory.getItem(index);
			changeCurrentMessage("Dropped a "+displayItemName(droppingItem, false)+".",currentTile,true);
		}
		else if(currentHp()>0){		//this check is necessary to make sure the monster is not simply dropping its items upon death.
			//changeCurrentMessage(name+" dropped "+inventory.getItemName(index)+".",currentTile,false);	//TODO: if we want the player to see monsters dropping items first, need to have some checks here to make sure player can see this happening.
		}
		currentTile.addItem(inventory.takeItem(index));		//TODO: index args versus item args are inconsistent. decide which one to use.
	}
	
	public void dropItemAmount(int itemIndex, int amount){	//drop only part of a stack.
		if(hasItem(itemIndex)){
			Item droppingItem=inventory.getItem(itemIndex);
			Item droppedItem=droppingItem.copyItem(droppingItem);	
			if(droppingItem.stackable()
			&& droppingItem.enoughInStack(amount)){
				if(droppingItem.getAmount()==amount)	//if there is exactly enough of this item
					dropItem(itemIndex);
				else{ 		//at this point, amount dropped is definitely less than the amount player has.
					
					if(amount>0){
						if(getClass().equals((Player.class)))
							changeCurrentMessage("Dropped ",currentTile,true);
						else 
							changeCurrentMessage(name+" dropped ",currentTile,false);	//can monsters drop limited item amounts? if so it will come up here
						if(amount>1)
							changeCurrentMessage(amount+" "+Item.plural(displayItemName(droppingItem, false))+".",currentTile,false);
						else
							changeCurrentMessage(droppingItem.article()+" "+displayItemName(droppingItem, false)+".",currentTile,false);
					}
					else
						changeCurrentMessage("",currentTile,true);
					droppingItem.adjustAmount(-1*amount);
					droppedItem.setAmount(amount);
					currentTile.addItem(droppedItem);
				}
			}
		}
	}
	
	private void dropAllItems() {
	for(int i=0;i<inventory.getItemCount();i++){
		dropItem(i);
		}
	}
	
public void useItem(int index){
			if(inventory.getItem(index)!=null){
				inventory.getItem(index).use(this);
				if(inventory.getItem(index).consumable==true){
					inventory.removeItem(index);
				}
			}
		}
		
public boolean hasItem(int index){
	return (index!=-1 
			&& index<inventory.getMaxItems()
			&& inventory.getItem(index)!=null);
}

//throwing methods

public void throwItem(int itemIndex, char direction) {//throw an item
	if(inventory.containsItem(itemIndex)){
		Item thrownItem=inventory.getItem(itemIndex);
		String itemClass=thrownItem.getClass().getName();
		switch(itemClass){	//checks what type of item is being thrown
			case("Ammo"):
				Ammo firedAmmo=(Ammo)thrownItem;

				firedAmmo.setCurrentDamage(thrownDamage(firedAmmo));
				firedAmmo.setToHit(thrownToHit(firedAmmo));
				firedAmmo.setThrownDistance(thrownDistance(firedAmmo));
		
				RogueLikeGui.frame.createProjectile(this, firedAmmo.singleShot(inventory,direction,currentWeapon()),direction,currentTile);
				break;
			case("Weapon"):
				Weapon thrownWeapon=(Weapon)thrownItem;
			
				thrownWeapon.setTempThrownDamage(thrownDamage(thrownWeapon));
				thrownWeapon.setTempThrownToHit(thrownToHit(thrownWeapon));
				thrownWeapon.setThrownDistance(thrownDistance(thrownWeapon));
	
				RogueLikeGui.frame.createProjectile(this, thrownWeapon.singleShot(inventory,direction,currentWeapon()),direction,currentTile);
				break;
			default:
				thrownItem.setTempThrownToHit(thrownToHit(thrownItem));
				thrownItem.setThrownDistance(thrownDistance(thrownItem));
				RogueLikeGui.frame.createProjectile(this,thrownItem.singleShot(inventory,direction,currentWeapon()),direction,currentTile);
		}
	}
}

//ammo throwing methods

protected int thrownDamage(Ammo firedAmmo) {	//TODO: confirm with nick
	if(firedAmmo.properlyFired(currentWeapon()))
		return rangedBaseDamage+firedAmmo.getDamage(currentWeapon())+(currentWeapon().getRangedDamage());
	return rangedBaseDamage+firedAmmo.getDamage(currentWeapon());
}

//weapon throwing methods

protected int thrownDamage(Weapon thrownWeapon) {
	//change as necessary
	//TODO: "throw" skill, (separate from other ranged), relevant stat, etc
	return rangedBaseDamage+thrownWeapon.getBaseThrownDamage();
}

//general throwing methods

protected int thrownToHit(Item thrownItem) {	//maybe this should be for any item?
	return rangedChanceToHit();
}

protected int thrownDistance(Item thrownItem) {	//maybe this should be for any item?
	double weight=thrownItem.getSingleWeight();	//if multiple items are thrown at once (and not in succession in a single turn) then change this
	int distance=1;
	if(weight>30)
		return distance;
	if(thrownItem.properlyFired(currentWeapon()))
		distance+=currentWeapon().getPower();
	return distance+(int) (15-weight/2);
}
//equipping methods

	public Boolean equipmentSlotFree(String slot) {
		return equippedItems.equipmentSlotFree(slot);
	}
	
	protected int armorRating() {	//get the player's armor rating.
		return equippedItems.armorRating();
		
	}
	
	public Weapon currentWeapon(){
		return equippedItems.currentWeapon();
	}
	
	public Weapon bareHands(){		//TODO: may need to make this more general if the name is ever displayed, or if some monsters become stronger without weapons or something.
		return UNARMED;
	}
	
	public Armor monsterHide() {	//TODO: if a monster can have a tough hide that behaves like iron or some other material, should be noted here.
		return new Armor("hide",Equipment.CHEST,baseArmor,0);
	}
	
	public boolean wieldingRangedWeapon(){
		return(currentWeapon()!=UNARMED&&currentWeapon().ranged);
	}
	
	public boolean canShootProperly(Ammo firedAmmo){
		return(wieldingRangedWeapon()&&firedAmmo.properlyFired(currentWeapon()));
	}
	
	public boolean unarmed(){
		return currentWeapon().weaponCategory.equals(Weapon.FISTS);
	}
	
	//reading methods
	
	public void read(Scroll scroll){
		String scrollName=scroll.genericName();
		Scroll readScroll=(Scroll)scroll.singleShot(inventory,'0',null);
		if(getClass().equals((Player.class))){
			changeCurrentMessage("Read a",currentTile,true);
			scrollName=((Player)this).displayItemName(scroll, false);
		}
		else
			changeCurrentMessage(name+" read a",currentTile,false);
		changeCurrentMessage(scrollName+".",currentTile,false);
		Targeting targeting=readScroll.getTargeting();
		switch(targeting.getTargetingType()){
			case(Targeting.ITEM_CHOOSE):
				if(this.getClass().equals(Player.class))
					((Player)this).chooseItem(scroll.getEffect(),targeting.getConstraint());
				break;
			case(Targeting.AREA_EXCLUDE_SELF):
				TileList affectedArea=targeting.getAffectedArea(currentLevel,this.currentTile);
				int length=affectedArea.length();
				for(int i=0;i<length;i++){
					Tile nextTile=affectedArea.getTile(i);
					if(nextTile.containsMonster())
						scroll.use(nextTile.monster);
				}

				//TODO: make case for monster reading a scroll and choosing an item, if necessary.
				break;
			default:
				Monster[] monsterTargets=targeting.getTargets(this);
				for(int i=0;i<monsterTargets.length&&monsterTargets[i]!=null;i++){
					scroll.use(monsterTargets[i]);
				}
				break;
		}
		//readScroll.use(this);
	}
		
		//consumable methods
		
	public void consume(Consumable consumeStack){	//consumables can be either potions or food.
		//TODO: error handling may be necessary if the player tries to eat something that isn't potion/food.
		//this should be handled before getting to this method, unless there are more "indirect" ways to eat implemented later.
		if (consumeStack.getClass().equals(Potion.class))
			quaff((Potion)consumeStack);
		else if (consumeStack.getClass().equals(Food.class))
			eat((Food)consumeStack);
	}
		
		public void quaff(Potion potionStack) {	//TODO: if we want the player to see the names of potions that monsters drink, then this must be modified. (Will go along with "inPlayerView" boolean for monsters, once that is completed.)
			String potionName=potionStack.colorName();
			Potion quaffedPotion=(Potion) potionStack.singleShot(inventory,'0',null);
			if(getClass().equals((Player.class))){
				changeCurrentMessage("Quaffed",currentTile,true);
				potionName=((Player)this).displayItemName(quaffedPotion, false);
			}
			else
				changeCurrentMessage(name+" quaffed",currentTile,false);
			changeCurrentMessage(potionName+".",currentTile,false);
			quaffedPotion.use(this);
		}
		
		public void eat(Food foodStack) {	//TODO: implement the rest of hunger effects
			Food eatenFood=(Food) foodStack.singleShot(inventory,'0',null);
			if(getClass().equals(Player.class)){
				if(!((Player)this).full())
					changeCurrentMessage("Ate ",currentTile,true);
				else{
					changeCurrentMessage("You are too full to eat that "+(displayItemName(eatenFood,false))+".",currentTile,true);
					return;
				}
			}
			else
				changeCurrentMessage(name+" ate ",currentTile,false);
			changeCurrentMessage(displayItemName(eatenFood,false)+".",currentTile,false);
			if(getClass().equals(Player.class)&&eatenFood.name().equals("Blue Eyes White Dragon corpse")){
				changeCurrentMessage("Holy shit, you just killed the Blue Eyes and ate his corpse. You are officially the hardest.",currentTile,true);
			}
			eatenFood.use(this);
		}
		
		//status-related methods
		protected void statusesOccur(){
			int length=statuses.length();
			for(int i=0;i<length&&statuses.getStatus(i)!=null;i++){
				statuses.getStatus(i).takeEffect(this);
			}
			//decrementAllDurations();
		}
		
		public void decrementAllDurations(){	//every turn, all nonzero status durations go down by 1.
			int index = 0;						
			while(statuses.getStatus(index)!=null){
				Status nextStatus=statuses.getStatus(index);
				if(nextStatus.getDuration()>0){
					if(nextStatus.firstTurn)
						nextStatus.firstTurn=false;
					else
						decrementDuration(index);
				}
				index++;
			}
		}
		
		private void decrementDuration(int index) {
			Status status=statuses.getStatus(index);
			status.decrementDuration();
			if(status.getDuration()<=0){
				status.endEffect(this);
				removeStatus(index);
			}
		}

		public void addStatus(Status newStatus) {	//maybe there should be a statusList? (hashmap, possibly)
			if(newStatus!=null&&newStatus.getDuration()>0){
				statuses.addStatus(newStatus);
				newStatus.beginEffect(this);
			}
		}
		
		public void removeStatus(String statusName){
			statuses.removeStatus(statusName);
		}
		
		public void removeStatus(int index){		//will this always work? not sure.
			statuses.removeStatus(index);
		}
		
		public void mobilize(){
			mobile=true;
		}
		
		public void immobilize(){
			mobile=false;
		}
		
		public boolean confused() {
			return statuses.containsStatus(Status.CONFUSION);
		}
		
		public boolean silenced() {
			return statuses.containsStatus(Status.SILENCING);
		}
		
		//teleport methods
		
		public void teleport() {
			Tile newTile=currentLevel.randomClearTile();
			moveTo(newTile.xCoord,newTile.yCoord);
		}
		
		public void blink() {
			Tile newTile=fov.randomEmptyTileInView();
			moveTo(newTile.xCoord,newTile.yCoord);
			changeCurrentMessage(currentMessageName()+ " blinked!",currentTile,true);
		}
		
		//xp methods
		
		public int calculateXpReward(){	//XP reward for killing this monster. TODO: make more complex to reflect difficulty of killing
			return baseDamage;
		}
		
		//name getters and setters

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		protected int getStunChance(){
			return stunChance;
		}
		
		protected int getCritChance(){
			return critChance;
		}
		
		protected int getMultipleAttackChance() {
			return multipleAttackChance;
		}
		
		protected void changeCurrentMessage(String message, Tile sourceTile,boolean overwrite){	//overwrite only overwrites if this monster is the player.
			if(getClass().equals(Player.class)&&overwrite){
				setCurrentMessage(message);
			}
			else if(playerInView())
				appendToCurrentMessage(message);	
			//only append if player is in monster's view. 
			//(TODO: this doesn't make logical sense if the player can see the monster but not the other way around, or vice versa)
			//change if necessary-- consider searching the level for the player, (might be an easier way to do this, like a boolean to keep track of whether the player is in the level)
			//	then checking to see if this monster is in the player's view.
		}

		protected void setCurrentMessage(String message) {	//TODO:
			RogueLikeGui.currentMessage=message;
		}
		
		protected void appendToCurrentMessage(String message) {
			RogueLikeGui.currentMessage+=" "+message;
		}
		
	public int getOverallPower(){	//temporary. TODO: work out a formula with nick.
		return maxHitPoints()+baseDamage;
	}
	
	//current level methods
		
	@Override
	public void setCurrentLevel(Level currentLevel){
		this.currentLevel=currentLevel;
		fov=new FOV(this);
	}
	
	public void addAvailableBranch(Branch branch){
		int index=0;
		while(index<availableBranches.length&&availableBranches[index]!=null)
			index++;
		if(index<availableBranches.length)
			availableBranches[index]=branch;
	}

	public void setAvailableBranches(Branch[] branches){
		availableBranches=branches;
	}

	public boolean availableInBranch(Branch branch){
		for(int i=0;i<availableBranches.length&&availableBranches[i]!=null;i++){
			if(branch.equals(availableBranches[i]))
				return true;
		}
		return false;
	}
	
	//turn delay methods
	
	public void setTurnDelay(int turnDelay){
		if(turnDelay>0)
			this.turnDelay=turnDelay;
	}
	
	public int getTurnDelay() {
		return turnDelay;
	}
		
	//sound methods
	
	public void hearSound(Monster source, Sound sound) {
		if(!source.equals(this)&&hostileTowards(source))
			monsterAIState.focusSound(source, sound);
	}
	
	private Random dice = new Random();
	public double spawnChance=1.0;

	public FOV fov;
	private int turnDelay=20;
	private AIState monsterAIState=new AIState(AIState.IDLE,this);
	
	protected String name;
	protected int[] hitPoints={0,0};	//two-int array, with first as current and second and maximum
	protected int baseDamage=0;
	protected int rangedBaseDamage=1;
	protected int baseArmor=0;
	
	private int toHitValue=4;
	private int dodgeValue=2;
	private int stunChance=0;			//these values may all be changed as necessary for monsters that can stun, crit, etc.
	private int critChance=0;
	private int multipleAttackChance=0;
	
	public int xp=5;	//xp yield of this monster. (TODO: make this a getter if possible.)
	
	protected Inventory inventory = new Inventory();
	protected EquipmentSet equippedItems= new EquipmentSet(this);
	
	protected Monster[] enemyMonsters= new Monster[300];	//monsters that this monster will attack on sight.
	protected StatusList statuses = new StatusList();
	
	private boolean mobile=true;
	
	public int stunCountDown=0;
	public boolean stunnedThisTurn=false;
	public boolean stunnedLastTurn=false; //keeps track of whether this monster stunned another monster the last turn.

	private Branch[] availableBranches=new Branch[Dungeon.BRANCH_COUNT];
}
