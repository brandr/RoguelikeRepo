
public class Effect {		//effects that happen to monsters, players, and items
					
	//TODO: separate status-yielding effects from instantaneous ones and separate
		//item-affecting effects from monster-affecting effects
	
	public static final String DAMAGE="damage";
	public static final String IMMOBILITY="immobility";
	public static final String BLINDNESS="blindness";
	public static final String ATTACK_BOOST="attack boost";
	//public static final String GAIN_ABILITY="gain ability";
	public static final String RESTORE_FULLNESS="restore fullness";
	
		//potion-specific effects here
	
	public static final String CURING = "curing";			//healing and damage
	public static final String HEALING="healing";
	public static final String POISON="poison";
	public static final String CHICKEN_SOUP = "chicken soup";
	
	public static final String GAIN_STRENGTH = "gain strength";		//ability-gain effects
	public static final String GAIN_DEXTERITY= "gain dexterity";
	public static final String GAIN_FORTITUDE = "gain fortitude";
	public static final String GAIN_WILLPOWER = "gain willpower";
	public static final String GAIN_INTELLIGENCE = "gain intelligence";
	
	public static final String LOSE_STRENGTH = "lose strength";		//ability-loss effects
	public static final String LOSE_DEXTERITY= "lose dexterity";
	public static final String LOSE_FORTITUDE = "lose fortitude";
	public static final String LOSE_WILLPOWER = "lose willpower";
	public static final String LOSE_INTELLIGENCE = "lose intelligence";
	
	public static final String BOOST_STRENGTH = "boost strength";		//ability-boost effects
	public static final String BOOST_DEXTERITY= "boost dexterity";
	public static final String BOOST_FORTITUDE = "boost fortitude";
	public static final String BOOST_WILLPOWER = "boost willpower";
	public static final String BOOST_INTELLIGENCE = "boost intelligence";
	
	public static final String METAMORPHOSIS = "metamorphosis";		//misc. ability effects
	public static final String GAIN_LEVEL = "gain level";
	
	public static final String HEROISM = "heroism";					//misc temporary boost effects
	
		//scroll-specific effects here
	public static final String TELEPORT="teleport";			//teleport effects
	public static final String LEVEL_TELEPORT="level teleport";
	public static final String BLINK="blink";
		
	public static final String IDENTIFY="identify";			//knowledge effects
	public static final String MAPPING = "mapping";
	public static final String AMNESIA = "amnesia";
	
	public static final String IMMOLATION = "immolation";
	public static final String RAGE = "rage";
	public static final String WIZARDRY = "wizardry";
	
	public static final String CONFUSION = "confusion";
	public static final String SILENCING = "silencing";
	
	
	
	
	
	/*public static final String[] EFFECT_TYPES=

{HEALING,DAMAGE,IMMOBILITY,BLINDNESS,ATTACK_BOOST	//TODO: associate these with statuses via hashmap
,RESTORE_FULLNESS
,TELEPORT,LEVEL_TELEPORT,BLINK
,IDENTIFY,MAPPING,AMNESIA
,IMMOLATION
};*/
	
	
	public Effect(String name, String effectType, int value,int duration){	//TODO: all names should come from final strings.
		this.name=name;
		setType(effectType);
		this.value=value;
		this.duration=duration;
	}
	
	public Effect(Effect copyEffect) {
		name=copyEffect.name;
		value=copyEffect.value;
		duration=copyEffect.duration;
		effectIndex=copyEffect.effectIndex;
	}
	
	public String toString(){
		return name;
	}

	private void setType(String effectType){
		this.effectType=effectType;
	}

	public void takeEffect(Monster target){
		switch(effectType){
		case CURING:
			if(instantaneous()){
				target.restoreHealth(value);
				target.removeStatus(Status.TAKING_DAMAGE);
				//TODO: remove negative status effects. (Test with poison)
				return;
			}
			break;
		case HEALING:
			if(instantaneous()){
				target.restoreHealth(value);
				return;
			}
			break;
		case CHICKEN_SOUP:
			if(target.getClass().equals(Player.class)){
				Player player=((Player)target);
				player.gainHungerPoints(value);
			}
			target.removeStatus(Status.TAKING_DAMAGE);
			break;
		
		case GAIN_STRENGTH:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.STRENGTH);return;
		case GAIN_DEXTERITY:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.DEXTERITY);return;
		case GAIN_FORTITUDE:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.FORTITUDE);return;
		case GAIN_WILLPOWER:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.WILLPOWER);return;
		case GAIN_INTELLIGENCE:
			if(target.getClass().equals(Player.class))
				((Player)target).incrementStat(Player.INTELLIGENCE);return;
			//lose ability effects
		case LOSE_STRENGTH:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.STRENGTH);return;
		case LOSE_DEXTERITY:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.DEXTERITY);return;
		case LOSE_FORTITUDE:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.FORTITUDE);return;
		case LOSE_WILLPOWER:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.WILLPOWER);return;
		case LOSE_INTELLIGENCE:
			if(target.getClass().equals(Player.class))
				((Player)target).decrementStat(Player.INTELLIGENCE);return;
			//metamorphosis effect
		case METAMORPHOSIS:
			if(target.getClass().equals(Player.class))
				((Player)target).rearrangeStats();return;
			//gain level effect
		case GAIN_LEVEL:
			if(target.getClass().equals(Player.class)){
				int neededExp=((Player)target).expToNextLevel();
				((Player)target).gainExp(neededExp);return;
			}
			//restore fullness effect
		case RESTORE_FULLNESS:
			if(target.getClass().equals(Player.class)){
				Player player=((Player)target);
				player.gainHungerPoints(value);
			}
			return;
		case ATTACK_BOOST:	//should always have a duration
			break;
		case IMMOBILITY:
			break;
		case BLINDNESS:	//should always have a duration.
			break;
			//gain ability effects
			//scroll effects below here
		case TELEPORT:
			target.teleport();
			break;
		case LEVEL_TELEPORT:
			if(target.getClass().equals(Player.class)){
				((Player)target).levelTeleportPrompt();
				//TODO: make case for monster level teleport
			}
			break;
		case BLINK:
			target.blink();
			break;
		case MAPPING:
			if(target.getClass().equals(Player.class)){
				target.mapAllLevel();
			}
			//monsters shouldn't be able to map the level, because this would actually reveal it to the player.
			break;
		case AMNESIA:
			if(target.getClass().equals(Player.class)){
				((Player)target).sufferAmnesia();
				//TODO: make case for monster amnesia
			}
			break;
		case IMMOLATION:
			int damage=value;
			target.changeCurrentMessage(target.currentMessageName()+" "
										+ target.pastHelpingVerb()+
										" engulfed by flames and took "
										+damage+" damage!",target.currentTile, false);
			target.takeDamage(damage, null, Material.getMaterial("fire"));//this is sort of a placeholder
			break;
		}
		target.addStatus(getStatus());//since getStatus works in its own way, it can be called no matter what the case (as long as the effect isn't instantaneous.)
	}
	
	public void takeEffect(Monster user, Item target){
		switch(effectType){
		case(IDENTIFY):
			if(user.getClass().equals(Player.class))
				((Player)user).identify(target);
			break;
		}
	}
	private Status getStatus(){	//TODO: maybe match these with 
		//for(int i=0;i<EFFECT_TYPES.length;i++){
			switch(effectType){
			case(POISON):return new Status("poisoned",Status.TAKING_DAMAGE,value,duration);
			case BOOST_STRENGTH:return new Status("mighty",Status.BOOSTED_STRENGTH,value,duration);
			case BOOST_DEXTERITY:return new Status("nimble",Status.BOOSTED_DEXTERITY,value,duration);
			case BOOST_FORTITUDE:return new Status("sturdy",Status.BOOSTED_FORTITUDE,value,duration);
			case BOOST_WILLPOWER:return new Status("resolute",Status.BOOSTED_WILLPOWER,value,duration);
			case BOOST_INTELLIGENCE:return new Status("brilliant",Status.BOOSTED_INTELLIGENCE,value,duration);
			case HEROISM: return new Status("heroic",Status.HEROISM,value,duration);
			case RAGE: return new Status("enraged",Status.RAGE,value,duration);
			case WIZARDRY: return new Status("wizardly",Status.WIZARDRY,value,duration);
			case CONFUSION: return new Status("confused",Status.CONFUSION,value,duration);
			case SILENCING: return new Status("silent",Status.SILENCING,value,duration);
			
			}
			//if(effectType.equals(EFFECT_TYPES[i])&&i<Status.STATUS_TYPES.length)
			//	return new Status(name,Status.STATUS_TYPES[i],value,duration);
		//}
	return null;
	}
	
	private boolean instantaneous(){	//tells if potion should take effect right away or not
		return duration==0;
	}
	
	//effect types: healing, harming, boosting/lowering stats like attack, armor or speed, insta-killing, teleporting, etc
	protected String name;
	private String effectType;
	protected int value=0;	//amount healed, increase in speed, etc.
	protected int duration=0; //zero is for instantaneous
	protected int effectIndex;
	
}
