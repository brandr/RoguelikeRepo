import java.util.Random;


public abstract class Item {		//other classes should interface, not inherit (since effects/functionality may differ greatly)

	public final static Class<?>[] ITEM_CLASSES={Ammo.class,Armor.class,Food.class,Potion.class,Scroll.class,Spellbook.class,Weapon.class};
	public final static int MAX_STACK_SIZE=99;
	public final static char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	//constructors
public Item(){
	this.name=null;
	setAmountSingle();
}

public Item(Item copyItem){}

public Item(String name){	//TODO: decide if this is necessary
	this.name=name;
	this.genericName=name;
	setAmountSingle();
}

public Item copyItem(Item toCopy){
	switch(toCopy.getClass().getCanonicalName()){	//cases are arranged alphabetically. add more as more item types are added.
		case("Ammo"):
			return new Ammo((Ammo)toCopy);
		case("Armor"):
			return new Armor((Armor)toCopy);
		case("Food"):
			return new Food((Food)toCopy);		
		case("Potion"):
			return new Potion((Potion)toCopy);
		case("Scroll"):
			return new Scroll((Scroll)toCopy);
		case("Spellbook"):
			return new Spellbook((Spellbook)toCopy);
		case("Weapon"):
			return new Weapon((Weapon)toCopy);	
		default:	
			return this;
	}
}

public Item singleShot(Inventory itemLocation, char direction, Weapon firedWeapon) {//used for throwing items which stack
	if(getAmount()>=1){
		decrementStack(itemLocation);
		Item shot=copyItem(this);
		shot.setAmount(1);
		
		shot.setTempThrownDamage(getTempThrownDamage());
		shot.setTempThrownToHit(getTempThrownToHit());
		shot.setThrownDistance(getThrownDistance());
		return shot;
	}
	else			
		return null;
}

	//absract methods

public abstract void initialize(Level level);
public abstract void use(Monster target);
public abstract boolean stackEquivalent(Item otherItem);
public abstract int getOverallValue();

	//toString

public String equippedToString() {
	if(equippable())
		return ((Equipment)this).equippedToString();
	return toString();
}

public String toString(){	
	if(!stackable())
		return genericName();	//temporary
	else
		return genericName()+" ("+getAmount()+")";	//temporary
}

public String name(){
	return genericName();//temporary
}

public String genericName(){//TODO: override for every item type.
	return genericName;
}

public String trueName(){//TODO: override for every item type.
	return name;
}

public String descriptiveName(){	//TODO: override for every item type
	return name;
}

public String singular(){	//returns the singular of the item. ("arrows" becomes "arrow")
	String name=genericName(); //temporary
	if(name.charAt(name.length()-1)=='s')
		return name.substring(0,name.length()-1);
	else
		return name;
}

public static String singular(String name){	//returns the singular of the projectile. ("arrows" becomes "arrow")
	if(name.charAt(name.length()-1)=='s')
		return name.substring(0,name.length()-1);
	else
		return name;
}

public String plural(){
	String name=genericName(); //temporary
	if(name.charAt(name.length()-1)=='s')
		return name+"es";
	else
		return name+"s";
}

public static String plural(String name){
	if(name.charAt(name.length()-1)=='s')
		return name+"es";
	else
		return name+"s";
}

public String article() {
	if(genericName==null)
		return "a";
	switch(genericName.charAt(0)){
	case 'a':
		return "an";
	case 'e':
		return "an";
	case 'i':
		return "an";
	case 'o':
		return "an";
	case 'u':
		return "an";
	default:
		return "a";
	}
}
	//could make this an abstract method to ensure that all items can collide properly
public void collide(Monster thrower, Monster target) {		//item collides with a monster. should be overridden for weapons, which deal damage when they strike a monster.
	if(thrower.getClass().equals(Player.class))
		thrower.changeCurrentMessage("The "+thrower.displayItemName(this, false)+" bounced off.", thrower.currentTile, true);	//should overwrite really be true here?
	else	//this works evern if both thrower and target are monsters, at least for now.
		target.changeCurrentMessage("The "+thrower.displayItemName(this, false)+" bounced off.", target.currentTile, false);
}

public int getStackSize() {		//the maximum number of items that can fit in a stack
	return stack[1];
}

public void setStackSize(int stackSize) {	//only to be done on intialization.
	if(stackSize>0&&stackSize<=MAX_STACK_SIZE)
	this.stack[1] = stackSize;
}

public boolean stackable(){	//can the item stack?	//there are problems here, I think
	return stack[1]>1;
}

public boolean stackFull(){	//check if any more of this item can be added to the stack.
	return stack[0]==stack[1];
}

public boolean stackEmpty(){
	return stack[0]==0;
}

public boolean enoughInStack(int amount) {		//see if there is enough of this stack to be dropped
	return stack[0]>=amount;
}

public boolean equippable(){
	return(getClass().equals(Weapon.class)||getClass().equals(Armor.class));
}

public char getIcon(){
	if(this.icon!=0)
	return icon;
	else
		return Level.EMPTY_TILE_ICON;
}

public void setIcon(char icon){
	this.icon=icon;
}

//stack methods

public int getAmount(){	//set the quantity of this item. for nonstackable items, this should always be 1.
	return stack[0];
}

public void setAmount(int amount){	//set the quantity of this item. for nonstackable items, this should always be 1.
	if(amount>=0&&amount<=stack[1])
		stack[0]=amount;
}

public void adjustAmount(int amount) {		//changes current amount by "amount", doing nothing if the adjustment is invalid.
	int newAmount=getAmount()-amount;
	if(newAmount<0
	|| newAmount>MAX_STACK_SIZE)	//newAmount==0 is not handled as a special case, so isEmpty() checks should be made based on the context.
		return;
	stack[0]+=amount;
}

public void setAmountSingle(){
	stack[0]=1;
	stack[1]=1;
}

public int randomAmount() {
	return 1+dice.nextInt(getStackSize());
}

public void decrementStack(Inventory stackLocation){	//NOTE: since this allows an amount to reach 0, will need stackEmpty() checks for methods in other classes which decrement amounts.
	if (stack[0]>0)
		stack[0]--;
	if(stack[0]==0)		//if a stack runs out, it is removed.
		stackLocation.removeItem(this);
}

public void incrementStack(){
	if (stack[0]<stack[1])
		stack[0]++;
}

public void fillStack(){
	stack[0]=stack[1];
}

public static int getNumForCharacter(char character) {	//for the purposes of equipping items, associate numbers with letters
    for(int i=0;i<ALPHABET.length;i++){
    	if(character==ALPHABET[i]){
    		return i;
    	}
    }
    return -1;
}

public static char getCharForNumber(int number){
	if(number>=0&&number<ALPHABET.length)
		return ALPHABET[number];
	else
		return 0;
}

public static String itemNameForLetterCommand(char letterCommand) {	//for use with the rogueliketester item commands: determines valid item type
	switch(letterCommand){
	//case('d'):		//can drop any item
	//	return "Item";
	//case(','):
	//	return "Item";	//uncomment these if I want an error case for the default instead.
	case('e'):		//can only eat food
		return "Food";
	case('q'):		//can only drink potions
		return "Potion";
	case('r'):
		return "Readable";
	case('E'):		//can only equip equipment
		return "Equipment";
	default:
		return "Item";
	}
}

public static String noOptionsMessage(String itemType) {	//messages for when the player wants to use some type of item, but has no item of that type.
	switch(itemType){
	case("Item"):		//can only drink potions
		return "No items in inventory.";	//may not always apply to inventory
	case("Potion"):
		return "No potions to drink.";
	case("Food"):		
		return "Nothing to eat.";
	case("Readable"):
		return "Nothing to read.";
	case("Equipment"):
		return "No items can be equipped.";
	default:
		return "You can't do that.";	//this should be very rare, as far as I can tell. As more methods are implemented I may need to replace it with a more sophisticated message getter.
	}
}

public static String noConstrainedOptionsMessage(String constraint) {	//this might replace the above method if things get too complicated
	switch(constraint){
	case(Targeting.UNIDENTIFIED):
		return "All your items are already idenitfied.";
	/*case("Item"):		//can only drink potions		//NOTE: don't delete this code block because it might be useful later.
		return "No items in inventory.";	//may not always apply to inventory
	case("Potion"):
		return "No potions to drink.";
	case("Food"):		
		return "Nothing to eat.";
	case("Readable"):
		return "Nothing to read.";
	case("Equipment"):
//		return "No items can be equipped.";*/
	default:
		return "You can't do that.";	//this should be very rare, as far as I can tell. As more methods are implemented I may need to replace it with a more sophisticated message getter.
	}
}

//available branch methods

public void addAvailableBranch(Branch branch){
	int index=0;
	while(index<availableBranches.length&&availableBranches[index]!=null)
		index++;
	if(index<availableBranches.length)
		availableBranches[index]=branch;
}

public Branch[] getAvailableBranches(){
	return availableBranches;
}

public void setAvailableBranches(Branch[] branches){
	availableBranches=branches;
}

public boolean availableInBranch(Branch branch){
	for(int i=0;i<availableBranches.length;i++){		//no need to check for availableBraches[i]=null because null values are placeholders.
		if(branch.equals(availableBranches[i]))
			return true;
	}
	return false;
}

//weight methods

public double getWeight() {
	if(stackable())
		return weight*(double)getAmount();
	return weight;
}

public double getSingleWeight(){
	return weight;
}

public void setWeight(double weight) {
	if(weight>0)
		this.weight = weight;
}

//identification methods

public boolean identified(){return identified;}

public void identify(){identified=true;}
public void unidentify() {identified=false;}	

//throw methods

public boolean properlyFired(Weapon currentWeapon) {	//this is necessary so that ammo can override it and it can return false for non-ammo items.
		return false;
} 

public String getAmmoStat() {
	return ItemReader.STR;
}

//temp thrown getters/setters (no need to include these in copy constructors)

	public int getTempThrownDamage() {
		return tempThrownDamage;
	}

	public void setTempThrownDamage(int tempThrownDamage) {
		this.tempThrownDamage = tempThrownDamage;
	}
	
	public int getTempThrownToHit() {
		return tempThrownToHit;
	}

	public void setTempThrownToHit(int tempThrownToHit) {
		this.tempThrownToHit = tempThrownToHit;
	}

//thrown distance
	public int getThrownDistance() {
		return thrownDistance;
	}

	public void setThrownDistance(int thrownDistance) {
		this.thrownDistance = thrownDistance;
	}
	
	public void decrementThrownDistance() {	//TODO: for weapons and ammo, this reduces effectiveness. (consider override methods)
		thrownDistance = Math.max(thrownDistance-1,0);
	}
	
//materials that the item cannot be made out of.

public boolean materialAllowed(Material material){
	for(int i=0;i<excludedMaterials.length&&excludedMaterials[i]!=null;i++){
		if(excludedMaterials[i].equals(material))
			return false;
	}
	return true;
}
	
public void addExcludedMaterial(Material material){
	int index=0;
	while(index<excludedMaterials.length&&excludedMaterials[index]!=null){
		if(excludedMaterials[index].equals(material))
			return;
		index++;
	}
	excludedMaterials[index]=material;
}
	
public Material[] getExcludedMaterials() {
	return excludedMaterials;
}

public void setExcludedMaterials(Material[] excludedMaterials) {
	this.excludedMaterials = excludedMaterials;
}

protected Random dice=new Random();
	
protected int tempThrownDamage=0;
protected int tempThrownToHit=0;
protected int thrownDistance=0;	//distance the item can travel. This is set when the item is thrown.

//attributes
public String name=null;		//consider making this and generic name private as more getters and setters are implemented.
public String genericName=null;	//TODO: set genericName for all types.
private char icon=0;

protected boolean identified=false;

public double spawnChance=1.0;
private Branch[] availableBranches=new Branch[Dungeon.BRANCH_COUNT];
private Material[] excludedMaterials=new Material[100];

private double weight;

public boolean consumable=false;
private int stack[]=new int [2];
}
