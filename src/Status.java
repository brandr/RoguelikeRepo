
public class Status {	//asleep, paraylzed, etc.
	
	public static final String RECOVERING="recovering";
	public static final String TAKING_DAMAGE="taking damage";
	public static final String IMMOBILE="immobile";
	public static final String BLIND="blind";
	public static final String ATTACK_BOOSTED="attack boosted";
	
	public static final String BOOSTED_STRENGTH = "boosted strength";
	public static final String BOOSTED_DEXTERITY = "boosted dexterity";
	public static final String BOOSTED_FORTITUDE = "boosted fortitude";
	public static final String BOOSTED_WILLPOWER = "boosted willpower";
	public static final String BOOSTED_INTELLIGENCE = "boosted intelligence";
	
	public static final String HEROISM = "heroism";
	public static final String RAGE = "rage";
	public static final String WIZARDRY = "wizardry";
	
	public static final String CONFUSION = "confusion";	//TODO
	public static final String SILENCING = "silencing";	//TODO

	public Status(String name, String statusType, int value, int duration) {	//main constructor for potions
		this.name=name;
		setType(statusType);
		this.value=value;
		setDuration(duration);
	}
	
	public String effectMessage(Monster monster){
		return monster.currentMessageName()+" "+monster.presentHelpingVerb()+" "+name+".";
	}
	//TODO: consider making an "effectMessage" toString.
	
	private void setType(String statusType){
		this.statusType=statusType;
	}
	
	public void beginEffect(Monster target){	//what happens when an effect begins. (TODO: consider adding some message senders here.)
		
		switch(statusType){
		case RECOVERING:
			return;		
		case TAKING_DAMAGE:
			return;
		case IMMOBILE:
			target.immobilize();
			target.changeCurrentMessage(target.immobilityMessage(), target.currentTile, false);
			return;
			//stat boosters
		case BOOSTED_STRENGTH:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.STRENGTH,value);return;
		case BOOSTED_DEXTERITY:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.DEXTERITY,value);return;
		case BOOSTED_FORTITUDE:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.FORTITUDE,value);return;
		case BOOSTED_WILLPOWER:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.WILLPOWER,value);return;
		case BOOSTED_INTELLIGENCE:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.INTELLIGENCE,value);return;
		case HEROISM:
			target.adjustBaseDamage(value);
			//TODO: adjust toHit, too?
			return;
		case RAGE:
			target.adjustBaseDamage(value);
			//TODO: adjust toHit, too?
			//TODO: speed up by a function of value
			return;
		case WIZARDRY:
			//TODO: wizardry effect applied here.
			return;
		case SILENCING:
			//TODO: target becomes silent.
			return;
		case BLIND:
			//TODO
			return;
		}
		/*case ATTACK_BOOSTED:
			target.adjustBaseDamage(value);
			return;
		}*/
		
	}
	
	public void takeEffect(Monster target){		//what happens each turn of an effect. TODO: test
		switch(statusType){
		case RECOVERING:
			target.restoreHealth(value);
			return;		
		case TAKING_DAMAGE:
			target.takeDamage(value, new Monster("a harmful potion"),null);	//TODO: replace this with another takeDamage() method. (won't need material)
			return;
		case IMMOBILE:
			target.immobilize();
			return;
		case BLIND:
			//TODO: will need FOV implemented first.
			return;
		//case ATTACK_BOOSTED:
		//	return;
		}
	}
	
	public void endEffect(Monster target){	//TODO: may need to call some gui messages here.
		switch(statusType){
		case RECOVERING:
			return;		
		case TAKING_DAMAGE:
			return;
		case IMMOBILE:
			target.mobilize();
			return;
			//stat boosters
		case BOOSTED_STRENGTH:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.STRENGTH,value);return;
		case BOOSTED_DEXTERITY:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.DEXTERITY,value);return;
		case BOOSTED_FORTITUDE:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.FORTITUDE,value);return;
		case BOOSTED_WILLPOWER:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.WILLPOWER,value);return;
		case BOOSTED_INTELLIGENCE:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.INTELLIGENCE,value);return;
		case HEROISM:
			target.adjustBaseDamage(-1*value);
			return;
		case RAGE:
			target.adjustBaseDamage(-1*value);
			if(target.getClass().equals(Player.class))
				((Player)target).loseHungerPoints(value*25);
			//TODO: adjust toHit, too?
			return;
		case WIZARDRY:
			//TODO: wizardry effect removed here.
			return;
		case CONFUSION:
			if(target.getClass().equals(Player.class))
				((Player)target).changeCurrentMessage("You feel less confused.", target.currentTile, false);
			return;
		case SILENCING:
			//TODO: target stops being silent.
			return;
		case BLIND:
			return;
		//case ATTACK_BOOSTED:
		//	target.adjustBaseDamage(-1*value);
			//TODO: attack power goes back to normal.
		//	return;
		}
	}

	public void immobilize(){
		canMove=false;
	}
	
	public int getValue(){
		return value;
	}
	
	public String getName(){
		return name;
	}
	
	public String getStatusType(){
		return statusType;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		if(duration>0)
			this.duration = duration;
		else
			this.duration=0;
	}
	
	public void decrementDuration() {
		if(duration>0)
			duration--;
	}

	public boolean canMove=true;
	public boolean firstTurn=true;	//if the status is on its first turn, some things are done slightly differently.
	private String name="";		//the name should always be an adjective (ex: asleep, confused, paralyzed, etc.)
	private String statusType;
	private int value=0;	//strength of effects like poison, recovery, etc. 
	private int duration = 0;
	

}