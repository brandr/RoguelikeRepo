
public class Effect {		//effects that happen to players
							//TODO: implement food effects

	public static final String HEALING="healing";
	public static final String DAMAGE="damage";
	public static final String IMMOBILITY="immobility";
	public static final String BLINDNESS="blindness";
	public static final String ATTACK_BOOST="attack boost";
	public static final String GAIN_ABILITY="gain ability";
	public static final String RESTORE_FULLNESS="restore fullness";
	
	public static final String[] EFFECT_TYPES={HEALING,DAMAGE,IMMOBILITY,BLINDNESS,ATTACK_BOOST,GAIN_ABILITY,RESTORE_FULLNESS};
	
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
		//switch(potionType){
		//TODO: add more only if necessary.
		//}
	}

	public void takeEffect(Monster target){
		switch(effectType){
		case HEALING:
			if(instantaneous()){
				target.restoreHealth(value);
				return;
			}
			break;
		case DAMAGE:
			if(instantaneous()){
				target.takeDamage(value, new Monster("a harmful potion"),null);	//TODO: replace this with another takeDamage() method. (ignore armor.
				return;
			}
				//TODO: else case: damage over time until effect runs out. (cannot kill? or can?)
			break;
		case IMMOBILITY:
			break;
		case BLINDNESS:	//should always have a duration.
			break;
		case GAIN_ABILITY:
			if(target.getClass()==Player.class)
				((Player)target).gainStatsRandom(value);
			return;
		case RESTORE_FULLNESS:
			if(target.getClass()==Player.class){
				Player player=((Player)target);
				player.gainHungerPoints(value);
			}
			return;
		case ATTACK_BOOST:	//should always have a duration
			break;
		}
		target.addStatus(getStatus());//since getStatus works in its own way, it can be called no matter what the case (as long as the effect isn't instantaneous.)
	}
	
	private Status getStatus(){	//TODO: this may be causing errors. investigate and fix.
		for(int i=0;i<EFFECT_TYPES.length;i++){
			if(effectType==EFFECT_TYPES[i])
				return new Status(name,Status.STATUS_TYPES[i],value,duration);
		}
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
