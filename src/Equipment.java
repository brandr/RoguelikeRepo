
//TODO: make a standard empty equipment item. This will help make other methods shorter.

public class Equipment extends Item{
	
	//public static final int MAX_EQUIPMENT_SLOTS=6;	//will change if more equipment types are added.
	public static final String HEAD="head";		//TODO: consider arranging these differently.
	public static final String CHEST="chest";
	public static final String CLOAK="cloak";
	public static final String OFF_HAND="offHand";
	public static final String WEAPON="weapon";
	public static final String LEGS="legs";
	public static final String FEET="feet";
	
	public static final String[] EQUIPMENT_SLOTS={HEAD,CHEST,CLOAK,OFF_HAND,WEAPON,LEGS,FEET};
	
	public Equipment(){
		name=null;
		//equippable=true;
	}
	
	@Override
	public void initialize(Level level) {
		// TODO: Weapon and armor should override this method.
		
	}
	
	public String toString(){
		String retVal=name;
		return retVal;
	}
	
	public String equippedToString(){	//if equipped, displays with the [E] in equipment list
		String retVal=name;	//temporary
		if(equipped)
			retVal+=" [E]";
		return retVal;
	}
	
	@Override			//name if this particular weapon has been identified.
	public String descriptiveName(){	//TODO: allow +0 to be another modifier if necessary. also, this probably doesn't need an arg.
		return enchantmentString()+" "+beatitude()+" "+material+" "+name;
	}

	@Override 
	public String trueName(){	//name if a weapon of this type has been identified.
		return material+" "+genericName;
	}
	
	@Override
	public String genericName(){	//name if no weapon of this type has been identified.
		return genericName;
	}
	
	@Override
	public String plural(Player player){	//TODO: override for each item type
		if(identified)
			return "+0 "+beatitude()+" "+getMaterial()+" "+name+"s";
		else if(player.itemKnown(this))
			return getMaterial()+" "+genericName+"s";
		else
			return genericName+"s";
	}
	
	public String enchantmentString() {	//TODO: override for armor (and magic bling, maybe
		return "";
	}
	
	public String beatitude(){	//tells whether the item is cursed or uncursed
		if(cursed)
			return "cursed";
		else
			return "uncursed";
	}
	
	@Override
	public boolean isEquipment(){
		return true;
	}
	
	@Override
	public void use(Monster target) {		//this is only for equipment with special effects.
		
	}
	
	public String getSlot(){
		if(getClass().equals(Weapon.class))
			return "weapon";
		else if(getClass().equals(Armor.class))
			return ((Armor)this).getArmorType();
		return null;
	}
	
	public int getEffectivePower(){		//gets what is probably the equipment's most important stat. 
		if(this.getClass().equals(Armor.class))	//TODO: make this more complex as necessary. (example: decide between ranged/thrown/melee damage)
			return getPower();	//TODO: fix this
		if(this.getClass().equals(Weapon.class))
			return getPower();
		return 0;
	}
	
	protected int getPower() {
		//TODO: set this to something if necessary.
		return 0;
	}

	@Override
	public boolean stackEquivalent(Item otherItem) {	//always false because equipment does not stack.
		return false;
	}
	
	public Material getMaterial() {
		return material;
	}

	protected Material material;

	@Override
	public int getOverallValue() {	//TODO: make sure this gets overridden by armor/weapon versions.
		return 0;
	}
	
	//enchantment methods
	
	
	
	//curse methods
	
	public void curse() {
		cursed = true;
	}

	public void unCurse() {
		cursed = false;
	}
	
	public boolean equipped;
	protected boolean cursed = false;
	}