import java.util.Random;



public class CharacterClass {	
	
	//TODO: make sure all starting equipment is read in, as in "barbarian" example.
	
	//barbarian
	public static final String BARBARIAN="Barbarian";	//barbarian final values
	public static final Skill[] BARBARIAN_SKILLS=		//starting barbarian skills
		{new Skill(Weapon.FISTS,1,Skill.FIGHTING),
		new Skill(Weapon.L_BLADE,1,Skill.FIGHTING),
		new Skill(Weapon.HAMMER,1,Skill.FIGHTING)};
	public static final Item[] BARBARIAN_ITEMS={
		WeaponReader.createWeapon(Weapon.L_BLADE,"longsword","Nordic longsword",Material.getMaterial("iron"),1)
		,ArmorReader.createArmor(Equipment.HEAD,"full helm","Horned helmet",Material.getMaterial("iron"))
		};
	
	//fighter
	public static final String FIGHTER="Fighter";		//fighter final values
	public static final Skill[] FIGHTER_SKILLS=		//starting fighter skills
		{new Skill(Weapon.L_BLADE,1,Skill.FIGHTING),
		new Skill(Weapon.S_BLADE,1,Skill.FIGHTING),
		new Skill(Weapon.MACE,1,Skill.FIGHTING),
		new Skill(Weapon.FLAIL,1,Skill.FIGHTING),
		new Skill(Weapon.POLEARM,1,Skill.FIGHTING),
		new Skill(Skill.DODGE,1,Skill.DEFENSIVE)};
	public static final Item[] FIGHTER_ITEMS={
		WeaponReader.createWeapon(Weapon.L_BLADE,"longsword","Soldier's longsword",Material.getMaterial("iron"),1)
		,ArmorReader.createArmor(Equipment.HEAD,"full helm","Soldier's helmet",Material.getMaterial("iron"))
		,ArmorReader.createArmor(Equipment.CHEST,"chestplate","Empire chestplate",Material.getMaterial("iron"))
		,ArmorReader.createArmor(Equipment.OFF_HAND,"buckler","Wooden buckler",Material.getMaterial("wood"))
		};
	
	//rogue
	public static final String ROGUE="Rogue";		//rogue final values
	public static final Skill[] ROGUE_SKILLS=		//starting rogue skills
		{new Skill(Weapon.S_BLADE,1,Skill.FIGHTING),
		new Skill(Skill.DODGE,1,Skill.DEFENSIVE),
		new Skill(Skill.SEARCHING,1,Skill.UTILITY),
		new Skill(Skill.APPRAISAL,1,Skill.UTILITY)};
	public static final Item[] ROGUE_ITEMS={
		WeaponReader.createWeapon(Weapon.S_BLADE,"dagger","thief's dagger",Material.getMaterial("iron"),3)
		,WeaponReader.createWeapon(Weapon.BOW,"longbow","hunting bow",Material.getMaterial("wood"),1)
		,new Ammo(ItemReader.createAmmo("hunting arrow", ItemReader.ARROW, 35, ItemReader.ARROW_DAMAGE, Material.getMaterial("wood")))
	};	
	
	//TODO: make wizards start with magic (preferably according to Nick's class ideas)
	//wizard
		public static final String WIZARD="Wizard";	
		public static final Skill[] WIZARD_SKILLS=		//starting wizard skills
			{new Skill(Weapon.STAFF,1,Skill.FIGHTING),
			new Skill(Skill.MAGIC,1,Skill.HINDRANCES),
			new Skill(Skill.MAGIC,1,Skill.EVOCATIONS)};
		public static final Item[]	WIZARD_ITEMS={	//TODO: add potions
			WeaponReader.createWeapon(Weapon.STAFF,"longstaff","Wizard's staff",Material.getMaterial("wood"),1)
			,ArmorReader.createArmor(Equipment.HEAD,"cornuthaum","Wizard's cap",Material.getMaterial("cloth"))
			,ArmorReader.createArmor(Equipment.CLOAK,"cloak","Magic cloak",Material.getMaterial("cloth"))//NOTE: the magic cloak is not actually magic.
			,new Potion(Potion.HEALING,"",8,0,3)
			,new Potion(Potion.HARM,"",4,0,5)};	//consider testing damage-over-time poison potions here.
	
	public static final String[] CLASSES={BARBARIAN,FIGHTER,ROGUE,WIZARD};
	public static final Skill[][] CLASS_SKILLS={BARBARIAN_SKILLS,FIGHTER_SKILLS,ROGUE_SKILLS,WIZARD_SKILLS};
	public static final Item[][] CLASS_ITEMS={BARBARIAN_ITEMS,FIGHTER_ITEMS,ROGUE_ITEMS,WIZARD_ITEMS};
	
	public CharacterClass(String className){//string-arg constructor should be the only constructor. (unless it is replaced with another)
		this.className=className;	
		switch (className){
		case(BARBARIAN):	//everything about the class is set based on the name chosen when it is created.
			maxHPPool=12;
			maxMPPool=8;
			break;
		case("Fighter"):
			maxHPPool=10;
			maxMPPool=8;
			break;
		}
	}
	
	public String toString(){
		return className;
	}

	public int HPRoll() {
		return 1+dice.nextInt(maxHPPool);
	}
	
	public double MPRoll() {
		return 1+dice.nextInt(maxMPPool);
	}
	
	public Inventory getInventory() {
		for(int i=0;i<CLASSES.length;i++){
			if(className.equals(CLASSES[i])){
				return new Inventory(CLASS_ITEMS[i]);
			}
		}
		return null;
	}
	
	public String className;
	private Random dice=new Random();
	
	private int maxHPPool=10;
	private int maxMPPool=10;
}
