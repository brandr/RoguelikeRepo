import java.util.Random;


public class Potion extends Consumable{	//IDEA: a potion of confusion might make movements random, or cause the list of enemy monsters to change wildly
	public final static char STANDARD_POTION_ICON='!';
	public final static double STANDARD_POTION_WEIGHT=2.0;
	public final static int STANDARD_POTION_STACK_SIZE=9;
	
	public final static String[] POTION_COLORS={"red","orange","yellow","green","blue"};
	
	public final static String HEALING="healing";
	public final static String PARALYSIS="paralysis";
	public final static String GAIN_ABILITY="gain ability";
	public final static String RAGE="rage";
	public final static String HARM="harm";
	
	public final static String[] POTION_NAMES={HEALING,PARALYSIS,GAIN_ABILITY,RAGE,HARM};	//rage gives a temporary melee attack boost.
	public final static String[] POTION_EFFECT_NAMES={"regenerating","paralyzed","gaining ability","enraged","taking damage"};	//are these necessary given setEffect()?

	//TODO: once effects are figured out, make final "effects" which are linked with "true" potion names.
	
	public Potion(String type,String color, int baseValue, int baseDuration, int count) {		//TODO: set effect according to type.
		setName(type);
		this.color=color;
		genericName=colorName();
		setWeight(STANDARD_POTION_WEIGHT);
		setStackSize(STANDARD_POTION_STACK_SIZE);
		setAmount(count);
		consumable=true;
		
		this.baseValue=baseValue;
		this.baseDuration=baseDuration;
		potionType=type;
		setEffect(type);
		setIcon(STANDARD_POTION_ICON);
	}

	public Potion(Potion copyPotion) {	//NOTE: this copy constructor does NOT make exact copies. (value and duration will vary slightly) However, I don't think we will ever want exact copies of potions.
		name=copyPotion.name;
		color=copyPotion.color;
		genericName=copyPotion.colorName();
		setWeight(copyPotion.getSingleWeight());
		setStackSize(STANDARD_POTION_STACK_SIZE);
		setAmount(copyPotion.getAmount());
		identified=copyPotion.identified;
		setAvailableBranches(copyPotion.getAvailableBranches());
		consumable=true;
		
		baseValue=copyPotion.baseValue;
		baseDuration=copyPotion.baseDuration;
		this.potionType=copyPotion.potionType;
		setEffect(copyPotion.potionType);
		setIcon(copyPotion.getIcon());
	}
	
	@Override
	public void initialize(Level level) {	//TODO: set stack size here.
		setAmount(1+dice.nextInt(getStackSize()));
	}
	
	@Override
	public String genericName(){
		return colorName();
	}
	
	public String colorName(){
		return color+" potion";
	}
	
	//should a "descriptive name" (with a PER int arg) be here, too?
	
	@Override
	public String descriptiveName(int perception){
		return trueName();	//TODO: consider adding even more info with high PER.
	}
	
	public String trueName(){
		return name;
	}

	public static String[] randomColors(){	//put the colors in a random order. TODO: assign potions colors at the start of the game.
		int[] indeces=new int[POTION_COLORS.length];
		String[]colors=new String[indeces.length];
		for(int i=0;i<POTION_COLORS.length;i++){
			indeces[i]=i;
		}
		int colorIndex=0;
		for(int i=0;i<indeces.length;i++){
			int index=dice.nextInt(indeces.length);
			while(indeces[index]==-1)
				index=dice.nextInt(indeces.length);
			indeces[index]=-1;
			colors[colorIndex]=POTION_COLORS[index];
			colorIndex++;
		}
		return colors;
	}
	
	private void setEffect(String type) {		//IDEA: how to make instantaneous and non-instantaneous effect names? (might not be necessary)
		
		String effectName="";
		String effectType="";
		switch(type){
		case HEALING:
			effectName="regenerating";
			effectType=Effect.HEALING;
			break;
		case PARALYSIS:
			effectName="paralyzed";
			effectType=Effect.IMMOBILITY;
			break;
		case GAIN_ABILITY:
			effectName="gaining ability";
			effectType=Effect.GAIN_ABILITY;
			break;
		case RAGE:
			effectName="enraged";
			effectType=Effect.ATTACK_BOOST;
			break;
		case HARM:
			effectName="taking damage";
			effectType=Effect.DAMAGE;
			break;
		}
		effect=new Effect(effectName,effectType,baseValue,baseDuration);	//is "type" the correct second arg?
	}
	
	private void setName(String type) {
		name="potion of "+type;
	}
	
	public String getPotionType(){
		return potionType;
	}
	
	public void setDuration(int duration){
		baseDuration=duration;
		effect.duration=duration;
	}
	
	public void setColor(String color){	//only for use in special cases, like the player class.
		this.color=color;
	}
	
	@Override
	public void collide(Monster thrower, Monster target){
		//boolean successfulHit=getTempThrownToHit()>target.evasionValue();
		String potionName="";
		if(thrower.getClass().equals(Player.class))	//if the player threw a potion at a monster
			potionName=((Player)thrower).displayItemName(this, false);
		else if(target.getClass().equals(Player.class))	//if a monster threw a potion at the player
			potionName=((Player)target).displayItemName(this, false);
		else
			potionName=genericName();
		
		//if(successfulHit){
		target.changeCurrentMessage(target.currentMessageName()+" was hit by "+article()+" "+potionName+"!", target.currentTile, true);
		
		this.use(target);
		if(thrower.getClass().equals(Player.class)){
			Player player=(Player)thrower;
			Skill trainedSkill=null;
			trainedSkill=player.getSkill(Skill.FIGHTING,Weapon.BOW);	//TODO: change this to throwing skill once it is created.
			player.trainSkill(trainedSkill);
		}//else
		//	target.changeCurrentMessage("The "+potionName+" missed.", thrower.currentTile, false);	//TODO: use player interface here and above.
	}
	
	@Override
	public boolean stackEquivalent(Item otherItem) {// checks if a potion can stack with another.
		if(otherItem.getClass().equals(Potion.class)){
			Potion otherPotion = (Potion)otherItem;
			if(otherPotion.name.equals(name)
			&& otherPotion.color.equals(color)
			&& otherPotion.identified==(identified)
			&& otherPotion.baseValue==baseValue
			&& otherPotion.baseDuration==baseDuration){		//TODO: add more conditions as necessary.			
				return true;
			}		
		}
		return false;
	}
	
	@Override
	public int getOverallValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private String potionType;
	boolean consumable=true;
	private int baseValue = 0;	//value used for the potion's effect.
	private int baseDuration=0;
	private static Random dice=new Random();
	//private double spawnChance;	//TODO: set this in final potion array. 
	private String color;		//TODO: set this for a potion type at the start of the game. (consider putting a final potion array in dungeon
								//and cloning a potion whenever necessary. 



}
