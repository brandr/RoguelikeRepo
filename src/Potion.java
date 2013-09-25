import java.util.Random;


public class Potion extends Consumable{	//IDEA: a potion of confusion might make movements random, or cause the list of enemy monsters to change wildly
	public final static char STANDARD_POTION_ICON='!';
	public final static double STANDARD_POTION_WEIGHT=2.0;
	public final static int STANDARD_POTION_STACK_SIZE=9;
	
	public final static String CURING="curing";				 	public final static String HEALING="healing";
	public final static String RESTORATION="restoration";	 	public final static String POISON="poison";
	public final static String STRONG_POISON="strong poison";	public final static String CHICKEN_SOUP="chicken soup";
	
	public final static String GAIN_ABILITY="gain ability";		public final static String LOSE_ABILITY="lose ability";
	public final static String METAMORPHOSIS="metamorphosis";	public final static String GAIN_LEVEL="gain level";
	
	public final static String BOOST_ABILITY="boost ability";	public final static String HEROISM="heroism";
	
	//public final static String[] ABILITY_TYPES={"strength","dexterity","fortitude","",""};
	
	public final static String WIZARDRY="wizardry";				public final static String RAGE="rage";
	
	public final static String CONFUSION="confusion";			public final static String SILENCING="silencing";
	
	//is this array needed?
	public final static String[] POTION_TYPES={CURING,HEALING,RESTORATION,POISON,STRONG_POISON,CHICKEN_SOUP
												,GAIN_ABILITY,LOSE_ABILITY,METAMORPHOSIS,GAIN_LEVEL
												,BOOST_ABILITY,HEROISM,WIZARDRY,RAGE
												,CONFUSION,SILENCING
												};
	
	public final static String[] POTION_COLORS=
		{"chunky red","vaporous orange","off-yellow","pea green","sky blue"
		,"watery purple","oily black","chalky white","neon pink", "sludgy grey"
		,"syrupy turquoise","speckled brown","rocky road","silky silver","goldenrod"
		,"scented lavender"
		};
	
	//Healing/damage effects
	public final static Effect CURING_EFFECT=new Effect("Cure",Effect.CURING,8,0);
	public final static Effect HEALING_EFFECT=new Effect("Heal",Effect.HEALING,22,0);
	public final static Effect RESTORATION_EFFECT=new Effect("Restore",Effect.CURING,999,0);	//TODO: create some value that means "max value"
	public final static Effect POISON_EFFECT=new Effect("Posion",Effect.POISON,1,250);
	public final static Effect STRONG_POISON_EFFECT=new Effect("Strong Posion",Effect.POISON,5,300);
	public final static Effect CHICKEN_SOUP_EFFECT=new Effect("Chicken Soup",Effect.CHICKEN_SOUP,150,0);
	
	//gain ability effects
	public final static Effect GAIN_STRENGTH_EFFECT=new Effect("Gain Strength",Effect.GAIN_STRENGTH,1,0);
	public final static Effect GAIN_DEXTERITY_EFFECT=new Effect("Gain Dexterity",Effect.GAIN_DEXTERITY,1,0);
	public final static Effect GAIN_FORTITUDE_EFFECT=new Effect("Gain Fortitude",Effect.GAIN_FORTITUDE,1,0);
	public final static Effect GAIN_WILLPOWER_EFFECT=new Effect("Gain Willpower",Effect.GAIN_WILLPOWER,1,0);
	public final static Effect GAIN_INTELLIGENCE_EFFECT=new Effect("Gain Intelligence",Effect.GAIN_INTELLIGENCE,1,0);
	
	//lose ability effects
	public final static Effect LOSE_STRENGTH_EFFECT=new Effect("Lose Strength",Effect.LOSE_STRENGTH,1,0);
	public final static Effect LOSE_DEXTERITY_EFFECT=new Effect("Lose Dexterity",Effect.LOSE_DEXTERITY,1,0);
	public final static Effect LOSE_FORTITUDE_EFFECT=new Effect("Lose Fortitude",Effect.LOSE_FORTITUDE,1,0);
	public final static Effect LOSE_WILLPOWER_EFFECT=new Effect("Lose Willpower",Effect.LOSE_WILLPOWER,1,0);
	public final static Effect LOSE_INTELLIGENCE_EFFECT=new Effect("Lose Intelligence",Effect.LOSE_INTELLIGENCE,1,0);
	//hold off on potions of gain/lose/boost luck for now, since luck might be an invisible stat
	//temporary boost ability effects (TODO:make the acutal effects)
	public final static Effect BOOST_STRENGTH_EFFECT=new Effect("Boost Strength",Effect.BOOST_STRENGTH,7,750);
	public final static Effect BOOST_DEXTERITY_EFFECT=new Effect("Boost Dexterity",Effect.BOOST_DEXTERITY,7,750);
	public final static Effect BOOST_FORTITUDE_EFFECT=new Effect("Boost Fortitude",Effect.BOOST_FORTITUDE,7,750);
	public final static Effect BOOST_WILLPOWER_EFFECT=new Effect("Boost Willpower",Effect.BOOST_WILLPOWER,7,750);
	public final static Effect BOOST_INTELLIGENCE_EFFECT=new Effect("Boost Intelligence",Effect.BOOST_INTELLIGENCE,7,750);
	
	//misc ability effects
	public final static Effect METAMORPHOSIS_EFFECT=new Effect("Metamorphosis",Effect.METAMORPHOSIS,0,0);
	public final static Effect GAIN_LEVEL_EFFECT=new Effect("Gain Level",Effect.GAIN_LEVEL,1,0);
	
	//misc temporary boost effects (TODO)
	public final static Effect HEROISM_EFFECT=new Effect("Heroism",Effect.HEROISM,3,600);
	public final static Effect RAGE_EFFECT=new Effect("Rage",Effect.RAGE,8,600);	
	public final static Effect WIZARDRY_EFFECT=new Effect("Wizardry",Effect.WIZARDRY,8,600);	
	
	public final static Effect CONFUSION_EFFECT=new Effect("Confusion",Effect.CONFUSION,0,200);	
	public final static Effect SILENCING_EFFECT=new Effect("Silencing",Effect.SILENCING,0,400);	
	
	
	//TODO: make potion "colors" into soup descriptions like chunky, watery, etc
	//TODO: implement hashmaps for everything in this class. (only if it helps organize things)
	
	public Potion(String potionType,String potionColor, String statType, int amount){
		this.potionType=potionType;
		this.color=potionColor;
		this.statType=statType;
		genericName=colorName();
		setWeight(STANDARD_POTION_WEIGHT);
		setStackSize(STANDARD_POTION_STACK_SIZE);
		setAmount(amount);
		consumable=true;
		setIcon(STANDARD_POTION_ICON);
	}
	
	public Potion(String potionType,String potionColor){
		this.potionType=potionType;
		this.color=potionColor;
		genericName=colorName();
		setWeight(STANDARD_POTION_WEIGHT);
		setStackSize(STANDARD_POTION_STACK_SIZE);
		setAmount(1);
		consumable=true;
		setIcon(STANDARD_POTION_ICON);
	}

	public Potion(Potion copyPotion) {	//NOTE: this copy constructor does NOT make exact copies. (value and duration will vary slightly) However, I don't think we will ever want exact copies of potions.
		color=copyPotion.color;
		genericName=copyPotion.colorName();
		this.statType=copyPotion.statType;
		setWeight(copyPotion.getSingleWeight());
		setStackSize(STANDARD_POTION_STACK_SIZE);
		setAmount(copyPotion.getAmount());
		identified=copyPotion.identified;
		setAvailableBranches(copyPotion.getAvailableBranches());
		consumable=true;

		potionType=copyPotion.potionType;
		setIcon(copyPotion.getIcon());
	}
		
	@Override
	public String descriptiveName(){
		switch(potionType)
		{
		case(GAIN_ABILITY): return "potion of gain "+statType;
		case(LOSE_ABILITY): return "potion of lose "+statType;
		case(BOOST_ABILITY): return "potion of boost "+statType;
		default:
			return trueName();
		}
	}
	
	@Override
	public String trueName(){
		return "potion of "+potionType;
	}
	
	@Override
	public String genericName(){
		return colorName();
	}
	
	public String colorName(){
		return color+" potion";
	}
	
	@Override
	public void initialize(Level level) {	//TODO: set stack size here.
		setAmount(1+dice.nextInt(getStackSize()));
		setPotionType();
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

	@Override
	public Effect getEffect(){
		switch(potionType){
		case(CURING):return CURING_EFFECT;
		case(HEALING):return HEALING_EFFECT;
		case(RESTORATION):return RESTORATION_EFFECT;
		case(POISON):return POISON_EFFECT;
		case(STRONG_POISON):return STRONG_POISON_EFFECT;
		case(CHICKEN_SOUP):return CHICKEN_SOUP_EFFECT;
		case(GAIN_ABILITY):return getGainAbilityEffect();
		case(LOSE_ABILITY):return getLoseAbilityEffect();
		case(BOOST_ABILITY):return getBoostAbilityEffect();
		case(METAMORPHOSIS):return METAMORPHOSIS_EFFECT;
		case(GAIN_LEVEL):return GAIN_LEVEL_EFFECT;
		case(HEROISM):return HEROISM_EFFECT;
		case(RAGE):return RAGE_EFFECT;
		case(WIZARDRY):return WIZARDRY_EFFECT;
		case(CONFUSION):return CONFUSION_EFFECT;
		case(SILENCING):return SILENCING_EFFECT;
		default:
			return null;
		}
	}

	private Effect getGainAbilityEffect() {
		switch(statType){
		case(Player.STRENGTH):return GAIN_STRENGTH_EFFECT;
		case(Player.DEXTERITY):return GAIN_DEXTERITY_EFFECT;
		case(Player.FORTITUDE):return GAIN_FORTITUDE_EFFECT;
		case(Player.WILLPOWER):return GAIN_WILLPOWER_EFFECT;
		case(Player.INTELLIGENCE):return GAIN_INTELLIGENCE_EFFECT;
		default:return null;
		}
	}
	
	private Effect getLoseAbilityEffect() {
		switch(statType){
		case(Player.STRENGTH):return LOSE_STRENGTH_EFFECT;
		case(Player.DEXTERITY):return LOSE_DEXTERITY_EFFECT;
		case(Player.FORTITUDE):return LOSE_FORTITUDE_EFFECT;
		case(Player.WILLPOWER):return LOSE_WILLPOWER_EFFECT;
		case(Player.INTELLIGENCE):return LOSE_INTELLIGENCE_EFFECT;
		default:return null;
		}
	}
	
	private Effect getBoostAbilityEffect() {
		switch(statType){
		case(Player.STRENGTH):return BOOST_STRENGTH_EFFECT;
		case(Player.DEXTERITY):return BOOST_DEXTERITY_EFFECT;
		case(Player.FORTITUDE):return BOOST_FORTITUDE_EFFECT;
		case(Player.WILLPOWER):return BOOST_WILLPOWER_EFFECT;
		case(Player.INTELLIGENCE):return BOOST_INTELLIGENCE_EFFECT;
		default:return null;
		}
	}

	private void setPotionType(){
		int index=0;
		switch(potionType)
		{
		case(GAIN_ABILITY):
			index=dice.nextInt(Player.STAT_NAMES.length);	//needed to set which type of gain ability potion this is
			statType=Player.STAT_NAMES[index];
			break;
		case(LOSE_ABILITY):
			index=dice.nextInt(Player.STAT_NAMES.length);	//needed to set which type of gain ability potion this is
			statType=Player.STAT_NAMES[index];
			break;
		case(BOOST_ABILITY):
			index=dice.nextInt(Player.STAT_NAMES.length);	//needed to set which type of gain ability potion this is
			statType=Player.STAT_NAMES[index];
			break;
		}
	}
	
	public String getPotionType(){
		return potionType;
	}
	
	public void setColor(String color){	//only for use in special cases, like the player class.
		this.color=color;
	}
	
	@Override
	public void collide(Monster thrower, Monster target){
		String potionName="";
		if(thrower.getClass().equals(Player.class))	//if the player threw a potion at a monster
			potionName=((Player)thrower).displayItemName(this, false);
		else if(target.getClass().equals(Player.class))	//if a monster threw a potion at the player
			potionName=((Player)target).displayItemName(this, false);
		else
			potionName=genericName();
		
		target.changeCurrentMessage(target.currentMessageName()+" was hit by "+article()+" "+potionName+"!", target.currentTile, true);
		
		this.use(target);
		if(thrower.getClass().equals(Player.class)){
			Player player=(Player)thrower;
			Skill trainedSkill=null;
			trainedSkill=player.getSkill(Skill.FIGHTING,Weapon.BOW);	//TODO: change this to throwing skill once it is created.
			player.trainSkill(trainedSkill);
		}
	}
	
	@Override
	public boolean stackEquivalent(Item otherItem) {// checks if a potion can stack with another.
		if(otherItem.getClass().equals(Potion.class)){
			Potion otherPotion = (Potion)otherItem;
			if(
			otherPotion.descriptiveName().equals(descriptiveName())
			&& otherPotion.color.equals(color)
			&& otherPotion.identified==(identified)
			&& otherPotion.statType.equals(statType)
			){		//TODO: add more conditions as necessary.			
				return true;
			}		
		}
		return false;
	}
	
	@Override
	public int getOverallValue() {
		// TODO: either decide a potion's value based on its type, make type affect spawnchace, or both.
		return 0;
	}
	
	private String potionType;
	boolean consumable=true;
	private String statType=null; //only for potions that influence stats
	private static Random dice=new Random();
	private String color;		//TODO: set this for a potion type at the start of the game. (consider putting a final potion array in dungeon
								//and cloning a potion whenever necessary. 



}
