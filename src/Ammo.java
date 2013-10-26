import java.util.Random;


public class Ammo extends Item{
	
	public static final Material[] AMMO_EXCLUDED_MATERIALS={Material.getMaterial("cloth")};
	//Don't make ammo with more than 5 power
	public static final int AMMO_STACK_SIZE=99;
	public static final char VERTICAL_AMMO_ICON='|';
	public static final char HORIZONTAL_AMMO_ICON='-';
	//public static final char LEFTDIAGONALICON='\';
	public static final char DIAGONAL_AMMO_ICON='/';
	
	//constructors 
	public Ammo(){
		setStackSize(AMMO_STACK_SIZE);
		setAmount(1);
		setIcon(HORIZONTAL_AMMO_ICON);
	}
	
	public Ammo(String name, String[] properWeapons, String ammoStat, int amount, int[] damage, int weight){ 	//main constructor for base ammos
		this.name=name;
		this.genericName=name;
		this.properWeapons=properWeapons;
		this.ammoStat=ammoStat;
		
		setStackSize(AMMO_STACK_SIZE);
		setAmount(amount);
		setIcon(HORIZONTAL_AMMO_ICON);
		setDamage(damage[0],damage[1]);	//non-proper damage set to 0 for now, since I have haven't implemented "throwable" ammo like darts.
		setWeight(weight);
		setExcludedMaterials(AMMO_EXCLUDED_MATERIALS);
	}
	
	//copy constructors
	
	public Ammo(Ammo copyAmmo) {	
		name=copyAmmo.name;
		genericName=copyAmmo.genericName;
		properWeapons=copyAmmo.properWeapons;
		ammoStat=copyAmmo.ammoStat;
		
		identified=copyAmmo.identified;
		
		setAvailableBranches(copyAmmo.getAvailableBranches());
		
		setStackSize(copyAmmo.getStackSize());
		setAmount(copyAmmo.getAmount());
		setWeight(copyAmmo.getSingleWeight());
		setIcon(copyAmmo.getIcon());
		
		baseDamage=copyAmmo.baseDamage;
		properlyFiredDamage=copyAmmo.properlyFiredDamage;
		setMaterial(copyAmmo.getMaterial());
		setExcludedMaterials(copyAmmo.getExcludedMaterials());
	}
	
	//constructor for ammo in midair
	
	public Ammo(Ammo copyAmmo, int amount, char direction) {	//TODO: add current damage if necessary. Also make sure attributes from the above copy contructor carry over.
		name=copyAmmo.name;
		genericName=copyAmmo.genericName;
		properWeapons=copyAmmo.properWeapons;
		ammoStat=copyAmmo.ammoStat;
		identified=copyAmmo.identified;
		
		setStackSize(copyAmmo.getStackSize());
		setAmount(amount);
		setWeight(copyAmmo.getSingleWeight());
		setIcon(copyAmmo.getIcon());
		
		baseDamage=copyAmmo.baseDamage;
		properlyFiredDamage=copyAmmo.properlyFiredDamage;
		setMaterial(copyAmmo.getMaterial());
		
		setIcon(getIcon(direction));
		
		setThrownDistance(copyAmmo.getThrownDistance());
	}
	
	@Override
	public Item singleShot(Inventory ammoLocation, char direction,Weapon firedWeapon) { //TODO: test for problems when throwing last in stack.
		if(getAmount()>=1){
			decrementStack(ammoLocation);
			Ammo shot=new Ammo(this,1,direction);
			
			shot.setTempThrownDamage(getTempThrownDamage());//TODO: test arrows/daggers/rocks
			shot.setTempThrownToHit(getToHit());
			shot.setThrownDistance(getThrownDistance());
			return shot;
		}
		else			
			return null;
	}
	
	@Override
	public void initialize(Level level) {	//upon ammo's placement in a level, it chooses a material and amount.
		Branch branch=level.getBranch();
		int ammoDepth=level.ammoDepth();
		
		Material[] materials=Material.suitableMaterials(this, branch, ammoDepth);	//the smaller this array is, the less variable materials will be.
		if(materials==null)
			return;
		int materialIndex=dice.nextInt(materials.length);
		while(materials[materialIndex]==null)
			materialIndex=dice.nextInt(materials.length);
		setMaterial(materials[materialIndex],true);
		setAmount(createdAmmoAmount(ammoDepth));	
	}
	
	private int createdAmmoAmount(int ammoDepth) {
		return Math.min(99,10+dice.nextInt(ammoDepth/2+15));
	}

	@Override
	public String descriptiveName(){	//TODO: make more descriptive as ammo gets more attributes.
		return "+0 "+getMaterial()+" "+name;
	}
	
	@Override
	public String trueName(){//TODO: override for every item type.
		return getMaterial()+" "+genericName;
	}
	
	@Override
	public String genericName(){	//TODO: make more ways of setting this.
		return genericName;
	}
	
	@Override
	public String plural(Player player){	//TODO: override for each item type
		if(identified)
			return "+0 "+getMaterial()+" "+name+"s";
		else if(player.itemKnown(this))
			return getMaterial()+" "+genericName+"s";
		else
			return genericName+"s";
	}
	
	public void setHorizontal(){
		setIcon(HORIZONTAL_AMMO_ICON);
	}
	
	public void setVertical(){
		setIcon(VERTICAL_AMMO_ICON);
	}
	
	public void setDiagonal(){
		setIcon(DIAGONAL_AMMO_ICON);
	}
	
	private char getIcon(char direction) {
		switch(direction){
		case('1'):
			return DIAGONAL_AMMO_ICON;
		case('2'):
			return VERTICAL_AMMO_ICON;
		case('3'):
			return DIAGONAL_AMMO_ICON;
		case('4'):
			return HORIZONTAL_AMMO_ICON;
		case('5'):
			return DIAGONAL_AMMO_ICON;
		case('6'):
			return HORIZONTAL_AMMO_ICON;
		case('7'):
			return DIAGONAL_AMMO_ICON;
		case('8'):
			return VERTICAL_AMMO_ICON;
		case('9'):
			return DIAGONAL_AMMO_ICON;
		default:
			return 0;
		}
	}
	
	@Override
	public void use(Monster target) {	//only for ammo with special effects. Otherwise, use "collide".
		
	}

	@Override
	public boolean stackEquivalent(Item otherItem) {
		if(otherItem.getClass().equals(Ammo.class)){
			Ammo otherAmmo = (Ammo)otherItem;
			if(otherAmmo.name.equals(name)
			&& otherAmmo.genericName.equals(genericName)
			&& otherAmmo.identified==(identified)
			&& otherAmmo.baseDamage==baseDamage
			&& otherAmmo.properlyFiredDamage==properlyFiredDamage
			&& otherAmmo.getMaterial().equals(getMaterial())){		//TODO: add more conditions as necessary.			
				return true;
			}		
		}
		return false;
	}
	
	public boolean throwable(){
		return getBaseDamage()>0;
	}
	
	public int getDamage(Weapon firedWeapon){
		if(properlyFired(firedWeapon))
			return getProperlyFiredDamage();
		else
			return getBaseDamage();
	}

	public void setDamage(int base, int proper){
		setBaseDamage(base);
		setProperlyFiredDamage(proper);
	}
	
	public void setCurrentDamage(int damage){
		if(damage>=0)
			setTempThrownDamage(damage);
	}
	
	private int getBaseDamage() {
		return baseDamage;
	}

	private void setBaseDamage(int damage) {
		if(damage>=0)
			this.baseDamage = damage;
	}

	private int getProperlyFiredDamage() {
		return properlyFiredDamage;
	}

	private void setProperlyFiredDamage(int properlyFiredDamage) {
		if(properlyFiredDamage>=0)
			this.properlyFiredDamage = properlyFiredDamage;
	}
	
	@Override
	public boolean properlyFired(Weapon firedWeapon) {
		if(firedWeapon!=null){
			for(int i=0;i<properWeapons.length&&properWeapons[i]!=null;i++){
				if(properWeapons[i].equals(firedWeapon.genericName()))
					return true;
			}
		}
		return false;
	}
	
	//material

	public void setMaterial(Material material, boolean adjustStats){
		this.material=new Material(material);
		if(adjustStats){
			setDamage((material.adjustedPower(getBaseDamage())),(material.adjustedPower(getProperlyFiredDamage())));
			setWeight(material.adjustedWeight(getSingleWeight()));
		}
	}
	
	@Override
	public void decrementThrownDistance() {	//ammo travels one square through the air
		
		if(dice.nextInt(2)>0){
			decrementDamage();
			decrementToHit();
		}
			
		thrownDistance = Math.max(0,thrownDistance-1);
	}
	
	private void decrementDamage(){
		if(getTempThrownDamage()>0)
			setTempThrownDamage(Math.max(1, getTempThrownDamage()-1));
	}
	
	private void decrementToHit(){
		setTempThrownToHit(Math.max(0, getTempThrownToHit()-1));
	}
	
	@Override
	public void collide(Monster thrower, Monster target){	//hits a target. TODO: case for the player getting hit (consider messages)
		boolean successfulHit=getTempThrownToHit()>target.evasionValue();
		if(successfulHit){
			target.changeCurrentMessage(target.currentMessageName()+" was hit by "+article()+" "+genericName()+" for "+getTempThrownDamage()+" damage!", target.currentTile, true);
			target.takeDamage(getTempThrownDamage(),thrower,getMaterial());
			if(thrower.getClass().equals(Player.class)){
				Player player=(Player)thrower;
				Skill trainedSkill=null;
				if(this.properlyFired(player.currentWeapon()))
					trainedSkill=player.getSkill(Skill.FIGHTING ,player.currentWeaponCategory());	//consider making an accessor method for player's current weapon skill. (does a private one already exist?)
				else
					trainedSkill=player.getSkill(Skill.FIGHTING,Weapon.BOW);	//TODO: change this to throwing skill once it is implemented.
				player.trainSkill(trainedSkill);
			}
		}
		else
			target.changeCurrentMessage("The "+genericName()+" missed.", thrower.currentTile, false);
	}

	//toHit
	public int getToHit() {
		return getTempThrownToHit();
	}

	public void setToHit(int toHit) {
		setTempThrownToHit(Math.max(toHit,0));
	}

	//ammo stat
	@Override
	public String getAmmoStat() {
		Ammo[] ammos=ItemReader.GENERIC_AMMOS;
		for(int i=0;i<ammos.length;i++){
			if(genericName.equals(ammos[i].genericName))
				return ammos[i].ammoStat;
		}
		return null;
	}

	public void setAmmoStat(String ammoStat) {
		this.ammoStat = ammoStat;
	}
	
	public boolean breakRoll() {	//chance for the ammo to break
		double breakChance=getMaterial().breakRate();
		return (((double)dice.nextInt(1001)/1000)<breakChance);
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getOverallValue() {
		return baseDamage*8+properlyFiredDamage*8;
	}
	
	private static Random dice=new Random();
	private String[] properWeapons=new String[100];
	private String ammoStat="";
	
	private int baseDamage=0;				//damage if thrown
	private int properlyFiredDamage=0;		//damage if fired using a weapon
	
	private Material material;	//TODO: make stuff that interacts with this
}