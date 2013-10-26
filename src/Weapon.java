import java.util.Random;


public class Weapon extends Equipment{

	public static final char STANDARDWEAPONICON=')';
	
	//STR weapons
	public static final String L_BLADE="lBlade";
	public static final String MACE="mace";		//maces and hamemrs are stored separately here, but are processed the same for skill purposes.
	public static final String HAMMER="hammer";
	public static final String AXE="axe";
	public static final String STAFF="staff";
	
	public static final String[] STR_WEAPONS={L_BLADE,MACE,HAMMER,AXE,STAFF};
	
	//DEX weapons
	public static final String S_BLADE="sBlade";
	public static final String BOW="bow";
	public static final String WHIP="whip";
	
	public static final String[] DEX_WEAPONS={S_BLADE, BOW, WHIP};
	
	//STR/DEX weapons
	
	public static final String FLAIL="flail";
	public static final String POLEARM="polearm";
	public static final String FISTS="fists";
	
	public static final String[] STR_DEX_WEAPONS={FLAIL, POLEARM,FISTS};

	
	//all weapons. "Fists" is included here for the convenience of setting up the Weapon SKill Category.
	public static final String[] ALL_WEAPONS={L_BLADE,MACE,HAMMER,AXE,STAFF, S_BLADE, BOW, WHIP, FLAIL, POLEARM,FISTS};
	
	//TODO: too many weapon constructors. figure out which ones are most necessary and remove the rest..

	public Weapon(){	//TODO: plan on removing
		name=null;
		setIcon(STANDARDWEAPONICON);
	}
	
	public Weapon(String name, String familyName, int power, String weaponCategory) {	//this constructor is only for artifact weapons.
		this.name=name;
		genericName=familyName;
		weaponFamily=familyName;
		setCategory(weaponCategory);
		setPower(power);
	}
	
	public Weapon(String name, int power, String weaponCategory, boolean twoHanded,int quality,double variance) {
		this.name=name;
		this.genericName=name;
		this.weaponFamily=name;
		setCategory(weaponCategory);
		setPower(power);
		this.twoHanded=twoHanded;
		this.quality=quality;
		this.variance=variance;
	}
	
	public Weapon(String name, int power, String weaponCategory, Material material) {
		this.name=name;
		this.genericName=name;
		setCategory(weaponCategory);
		setPower(power);
		setMaterial(material,false);
	}
	
	//copy constructor
		public Weapon(Weapon toCopy) {		//NOTE: may need to add more "statboosts" or otherwise change this as weapons become more complex.
			//equipped=toCopy.equipped;
			equipped=false; //is this right?
			
			name=toCopy.name;		//TODO: consider "setName" methods that handle all of these steps.
			weaponCategory=toCopy.weaponCategory;
			genericName=toCopy.genericName;
			weaponFamily=toCopy.weaponFamily;
			
			identified=toCopy.identified;
			cursed=toCopy.cursed;
			
			setAvailableBranches(toCopy.getAvailableBranches());
			
			setIcon(toCopy.getIcon());
			spawnChance=toCopy.spawnChance;
			
			this.meleeDamage=toCopy.meleeDamage;
			setRangedDamage(toCopy.getRangedDamage());
			quality= toCopy.quality;
			variance=toCopy.variance;
			weaponEnchantments=toCopy.weaponEnchantments;
			
			piercing=toCopy.piercing;
			ranged=toCopy.ranged; 
			baseThrownDamage=toCopy.baseThrownDamage; 
			twoHanded = toCopy.twoHanded;
			
			setStackSize(toCopy.getStackSize());
			setAmount(toCopy.getAmount());
			setWeight(toCopy.getSingleWeight());
			this.material=toCopy.getMaterial();		//not using setMaterial, because the copied weapon should already have its stats adjusted.
			setExcludedMaterials(toCopy.getExcludedMaterials());
		}
	
	@Override
	public Weapon singleShot(Inventory ammoLocation, char direction, Weapon firedWeapon) {	//TODO: test for problems when throwing last in stack.
		if(getAmount()>=1){
			decrementStack(ammoLocation);
			Weapon shot=new Weapon(this);
			shot.setAmount(1);
			
			shot.setTempThrownDamage(getTempThrownDamage());
			shot.setTempThrownToHit(getTempThrownToHit());
			shot.setThrownDistance(getThrownDistance());
			return shot;
		}
		else			
			return null;
	}
	
	@Override
	public void initialize(Level level) {	//upon ammo's placement in a level, it chooses a material and amount.
		Branch branch=level.getBranch();
		int weaponDepth=level.weaponDepth();
		
		Material[] materials=Material.suitableMaterials(this, branch, weaponDepth);	//the smaller this array is, the less variable materials will be.
		if(materials==null)
			return;
		int materialIndex=dice.nextInt(materials.length);
		while(materials[materialIndex]==null)
			materialIndex=dice.nextInt(materials.length);
		setMaterial(materials[materialIndex],true);
		if(stackable())
			setAmount(1+dice.nextInt(getStackSize()));	
	}
	
	private void setCategory(String weaponCategory) {	//TODO: add more special stuff that happens in various cases. Consider setting piercing.
		this.weaponCategory=weaponCategory;
		switch(weaponCategory){
		case(BOW):
			twoHanded=true;
			ranged=true;
		break;
		}
	}
	
	//toStrings
	
	public String weaponStat(){
		
			String category=weaponCategory;
			for(int i=0;i<STR_WEAPONS.length;i++){
				if(category==STR_WEAPONS[i])
					return "STR";
			}
			for(int i=0;i<Weapon.DEX_WEAPONS.length;i++){
				if(category==Weapon.DEX_WEAPONS[i])
					return "DEX";
			}
			for(int i=0;i<Weapon.STR_DEX_WEAPONS.length;i++){
				if(category==Weapon.STR_DEX_WEAPONS[i])
					return "STR_DEX";
			}
			return "???";
		
	}
	
	@Override
	public boolean stackEquivalent(Item otherItem) {
		if(otherItem.getClass().equals(Weapon.class)){
			Weapon otherWeapon=(Weapon)otherItem;
			
			if(otherWeapon.name.equals(name)
			&& otherWeapon.getPower()==getPower()
			&& otherWeapon.weaponCategory.equals(weaponCategory)
			&& genericName.equals(otherWeapon.genericName)
			&& identified==otherWeapon.identified
			//weaponFamily=toCopy.weaponFamily
			//setIcon(toCopy.getIcon());
			//spawnChance=toCopy.spawnChance;
			//piercing=toCopy.piercing;
			//ranged=toCopy.ranged;
			//twoHanded = toCopy.twoHanded;
			
			&& otherWeapon.meleeDamage==meleeDamage
			&& otherWeapon.quality==quality
			&& otherWeapon.variance==variance
			&& otherWeapon.rangedDamage==rangedDamage
			&& otherWeapon.baseThrownDamage==baseThrownDamage
			&& otherWeapon.weaponEnchantments==weaponEnchantments
			
			&& getMaterial().equals(otherWeapon.getMaterial())){
				//TODO: add more conditions as necessary. commented conditions are ones I'm not sure are necessary.			
				return true;
			}		
		}
		return false;
	}
	
	//getters
	
	@Override
	public int getPower(){	//TODO: test this if weapon power seems wonky.
		if(ranged)
			return rangedDamage;
		else
			return meleeDamage;
	}
	
	public int getMeleeDamage(){
		return meleeDamage;
	}
	
	public void setPower(int power) {
		if(!ranged)
			meleeDamage=power;
		else{
			setRangedDamage(power);
			meleeDamage=(1);		//TODO: make sure there's a way to set melee damage for something other than 1 for ranged weapons. (should be automatic, however.)
		}
		
	}
	
	@Override
	public void collide(Monster thrower, Monster target){
		boolean successfulHit=getTempThrownToHit()>target.evasionValue();
		if(successfulHit){
			target.changeCurrentMessage(target.currentMessageName()+" was hit by "+article()+" "+genericName()+" for "+getTempThrownDamage()+" damage!", target.currentTile, true);
			target.takeDamage(getTempThrownDamage(),thrower,getMaterial());
		}
		else
			target.changeCurrentMessage("The "+genericName()+" missed.", thrower.currentTile, false);
	}
	
	public int meleeDamageRoll(){	//randomize an amount for melee damage.
		if(meleeDamage==0)	
			return 0;
		double varianceAdjustment=(meleeDamage*variance);
		double roll = 1+meleeDamage-varianceAdjustment+dice.nextInt((int)((2*(varianceAdjustment)-1))+1);
		if(roll<0)
			return 0;
		return (int)roll;
	}
	
	public String determineDamageRange() {		//TODO: if I interact with these values more, consider making them final strings.
		String damageRange="medium";
		int qualityRoll=new Integer(1+dice.nextInt(70)+3*quality);	//4-73,16-85,31-100
		if(qualityRoll<33)
			damageRange="low";
		else if(qualityRoll>67)
			damageRange="high";
		return damageRange;
	}
	
	//TODO: consider making this an overridden method from Item class if necessary/useful
	public int getOverallValue(){	//the weapon's overall value
		int power=getPower();
		double value=50.0*((double)power/10.0)+15.0*((double)quality/10.0)-10.0;
		return (int)value;
	}
	
	public int getRangedDamage() {
		return rangedDamage;
	}

	public void setRangedDamage(int rangedDamage) {
		if(rangedDamage>=0)
			this.rangedDamage = rangedDamage;
	}
	
	private void setQuality(int quality){
		//this.quality=Math.min(10, quality);	//TODO: ask nick if we should have this check.
		this.quality=quality;
	}
	
	private int getQuality() {
		return quality;
	}
	
	public int getBaseThrownDamage() {	//unlike ranged damage, thrown damage refers to the damage from throwing the weapon itself.
		return baseThrownDamage;
	}

	public void setBaseThrownDamage(int baseThrownDamage) {
		this.baseThrownDamage = Math.max(0, baseThrownDamage);
	}
	
	//material methods
	
	public void setMaterial(Material material, boolean adjustStats){
		this.material=material;
		if(adjustStats){
			setPower(material.adjustedPower(getPower()));
			setBaseThrownDamage(material.adjustedPower(baseThrownDamage));
			setQuality(material.adjustedQuality(getQuality()));
			setWeight(material.adjustedWeight(getWeight()));
		}
	}

	//enchantment methods
	
	public void setEnchantments(int power,int toHit){	//these values should never be lower than -15 or above +15
		weaponEnchantments[0] = power;
		weaponEnchantments[1] = toHit;
	}
	
	public int enchantPower(){
		return weaponEnchantments[0];
	}
	
	public int enchantToHit(){
		return weaponEnchantments[1];
	}
	
	@Override
	public String enchantmentString(){
		int power = enchantPower();
		int toHit = enchantToHit();
		return plusMinusString(power)+power+", "
				+plusMinusString(toHit)+toHit;
	}
	
	private String plusMinusString(int value) {	//this could be useful elsewhere
		if(value < 0)
			return "-";
		return "+";
	}

	public Random dice = new Random();
		//a ranged weapon can be used as a melee weapon, but most aren't very effective.
	private int meleeDamage=0;
	private int rangedDamage=0; //This is separate from "power", because bows can be used (ineffectively) as melee weapoons.
	private int baseThrownDamage=0; //damage when thrown. Low for most weapons, but higher for some like daggers, darts, etc.
	
	public int quality=5; 	//quality weapons are more likely to choose high ranges upon variant hits
	public double variance=0.3;	//weapon damage variablitity. on average this is neither good nor bad.
	
	private int[] weaponEnchantments = {0,0};
	
	public boolean ranged=false;
	public boolean twoHanded = false;
	public boolean piercing=false;		//piercing still doesn't do anything, as far as I know.
	public String weaponCategory="";
	public String weaponFamily="";
}
