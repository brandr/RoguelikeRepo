
public class Skill {

	//when possible, test in fightTester before testing in gui.
	
	//TODO:determine and implement:
	//	*max skill levels
	//	*skill progress towards improvement
	//	*whether skills improve automatically, or through player choice
	
	//skill categories/names stored here.
	public static final String FIGHTING="Fighting";
	public static final String[] FIGHTING_SKILLS=Weapon.ALL_WEAPONS;		//TODO: make a translator from weapon type to printed skill String. (EX: "lBlade" becomes "Long Blade")
																			//OR just change weapon type names so they are capitalized and have spaces
	public static final String DEFENSIVE="Defensive";
		public static final String ARMOR="Armor";
		public static final String DODGE="Dodge";
	public static final String[] DEFENSIVE_SKILLS={ARMOR,DODGE};

	public static final String UTILITY="Utility";
		public static final String SEARCHING="Searching";
		public static final String APPRAISAL="Appraisal";
	public static final String[] UTILITY_SKILLS={SEARCHING,APPRAISAL};

	//TODO: consider storing magic skill categories separately (and copying them here) if magic schools become very complex in nature.
	public static final String MAGIC="Magic";
	//TODO: decide whether or not to include "spellcasting" here.
		public static final String DIVINATIONS="Divinations";
		public static final String HINDRANCES="Hindrances";
		public static final String TRANSMUTATIONS="Transmutations";
		public static final String EVOCATIONS="Evocations";
		public static final String DOMINATIONS="Dominations";
		public static final String ABJURATIONS="Abjurations";
	public static final String[] MAGIC_SKILLS={DIVINATIONS,HINDRANCES,TRANSMUTATIONS,EVOCATIONS,DOMINATIONS,ABJURATIONS};
		
	public static final String ELEMENTAL="Elemental";		//TODO: implement elements
		public static final String FIRE="Fire";
		public static final String ICE="Ice";
		public static final String AIR="Air";
		public static final String EARTH="Earth";
		public static final String FORCE="Force";
		public static final String NECROMANCY="Necromancy";
		
	public static final String[] ELEMENTAL_SKILLS={FIRE,ICE,AIR,EARTH,FORCE,NECROMANCY};
	
	public static final String[] CATEGORIES={FIGHTING,DEFENSIVE,UTILITY,MAGIC,ELEMENTAL};
	public static final String[][] ALL_SKILLS={FIGHTING_SKILLS,DEFENSIVE_SKILLS,UTILITY_SKILLS,MAGIC_SKILLS,ELEMENTAL_SKILLS};
	
	private static final int FIRST_PROGRESS_GOAL=50;	//tweak this as necessary. Also, maybe some skills should level up faster than others.
	private static final int SKILL_LEVEL_CAP=6;
	
	public Skill(){
		
	}
	
	public Skill(String name, String skillCategory){
		this.name=name;
		this.skillCategory=skillCategory;
		startProgress();
	}
	
	public Skill(String name, int skillLevel, String skillCategory){
		this.name=name;
		this.skillLevel=skillLevel;
		this.skillCategory=skillCategory;
		startProgress(skillLevel);	
	}
	
	public Skill(Skill copySkill){
		name=copySkill.name;
		skillLevel=copySkill.skillLevel;
		skillCategory=copySkill.skillCategory;
		startProgress(copySkill.skillLevel);
	}
	
	public String toString(){
		return name;
	}
	
	private void startProgress(){			//skill progress set up for level 0.
		progress[0]=0;
		progress[1]=FIRST_PROGRESS_GOAL;		
	}
	
	private void startProgress(int level){	//progress set up for a level greater than 0.
		progress[0]=0;
		progress[1]=FIRST_PROGRESS_GOAL*(level+1);
	}
	
	public void train() {	//if some actions improve skill more than others, make this take an int arg.
		if(skillLevel<SKILL_LEVEL_CAP)
			progress[0]++;
	}
	
	public void levelUpSkill(){
		if(skillLevel<SKILL_LEVEL_CAP){
			skillLevel+=1;
			progress[0]=progress[1];	//skill improvement does not carry on to the next skill level.
			progress[1]+=FIRST_PROGRESS_GOAL*skillLevel;	//tweak as necessary.
		}
	}
	
	public boolean canLevelUp(){
		return progress[0]>=progress[1];
	}
	
	public String name = null;
	public int skillLevel=0;
	public String skillCategory="";
	private int[] progress=new int[2];
}
