import java.util.Random;


public class EquipmentSet {	//a set of equipment belonging to a player or monster
	
	public EquipmentSet(Monster monster){
		this.monster=monster;
		for(int i=0;i<equipment.length;i++){
			equipment[i]=null;
		}
	}
	
	
	public String showEquipment() {
		String retVal="Equipped items:"+"\n"+"\n";
		if(monster.getClass().equals(Player.class)){
		boolean noEquipment=true;
		for(int i=0;i<equipment.length;i++){
			if(equipment[i]!=null){		//if an item is equipped in the slot
				noEquipment=false;
				String slotName="";
				switch(i){
				case(0):
					slotName="Head";
					break;
				case(1):
					slotName="Chest";
					break;
				case(2):
					slotName="Cloak";
					break;
				case(3):
					slotName="LH";
					break;
				case(4):
					slotName="RH";
					break;
				case(5):
					slotName="Pants";
					break;
				case(6):
					slotName="Boots";
					break;
				default:
					break;
				}
				retVal+=(slotName+": "+((Player)monster).displayItemName(equipment[i],true)+"\n");
			}
		}
		if(noEquipment)
			retVal+="none";	
		}
		return retVal;
	}
	
	public static int maxEquipmentSlots(){
		return Equipment.EQUIPMENT_SLOTS.length;
	}
	
	public Equipment getEquipmentInSlot(String slot){
		int index=getSlotIndex(slot);
		if(index>=0&&index<maxEquipmentSlots())
			return equipment[index];
		return null;
	}
	
	public Boolean equipmentSlotFree(String slot) {
		int index=getSlotIndex(slot);
		if(index>=0&&equipment[index]==(null))
			return true;
		return false;
	}
	
	public Boolean equipmentSlotFree(int index) {
		if(index>=0&&index<maxEquipmentSlots()&&equipment[index]==(null))
			return true;
		return false;
	}
	
	public static int getSlotIndex(String slot){
		for(int i=0;i<maxEquipmentSlots();i++){
			if(slot.equals(Equipment.EQUIPMENT_SLOTS[i]))
				return i;
		}
		return -1;
	}
	
	String getSlot(int index){
		if(index>=0&&index<maxEquipmentSlots())
			return Equipment.EQUIPMENT_SLOTS[index];
		return null;
	}
	
	public Weapon currentWeapon() {
		if (equipment[getSlotIndex(Equipment.WEAPON)]!=null&&getEquipmentInSlot(Equipment.WEAPON).getClass()==Weapon.class)
			return (Weapon)getEquipmentInSlot(Equipment.WEAPON);
		return monster.bareHands();
	}
	
	public void equip(Equipment item){				
		 
		String slot = null;
		if(item.getClass()==Weapon.class)
			slot=Equipment.WEAPON;
		else
			slot=((Armor)item).getSlot();
		
		if(item.equipped==false){						//case for equipping an item
			item.equipped=true;
		int index=getSlotIndex(slot);
		equipment[index]=item;
			return;	
			}	
		else											//case for unequipping an item
			unequipItem(Equipment.EQUIPMENT_SLOTS[getSlotIndex(slot)]);
	}
	
	public void unequipItem(String slot) {
		int index=getSlotIndex(slot);
		if(index<0)
			return;
		if(equipment[index]!=null){
			equipment[index].equipped=false;		
			equipment[index]=null;
		}
	}
	
	public void unequipAll() {
		for(int i=0;i<equipment.length;i++)
			unequipItem(Equipment.EQUIPMENT_SLOTS[i]);
	}
	
	public int armorRating() {	//subject to change as armor becomes more sophisticated
		int rating=monster.baseArmor;
		for(int i = 0;i< maxEquipmentSlots();i++){
			if(equipment[i]!=null&&equipment[i].getClass().equals(Armor.class))
				rating+=((Armor)equipment[i]).getArmorValue();
		}
		return rating;
	}
	
	public void attemptEquip(Equipment equippingItem, Class<?> equipmentType) {	//TODO: test once armor is more implemented.
		if(equipmentType.equals(Weapon.class))
			attemptEquipWeapon((Weapon)equippingItem);
		else if(equipmentType.equals(Armor.class))
			attemptEquipArmor((Armor)equippingItem);	
	}
	
	private void attemptEquipWeapon(Weapon equippingWeapon){
		
		boolean overwrite=true;
		
		String slot=Equipment.WEAPON;
		if(equippingWeapon.equipped){
			confirmUnequip(slot,overwrite);
			return;
		}
		if(!equipmentSlotFree(slot)){
			confirmUnequip(slot,overwrite);
			overwrite=false;
		}
		if(equippingWeapon.twoHanded
		&& !equipmentSlotFree(Equipment.OFF_HAND)){
			overwrite=false;
			confirmUnequip(Equipment.OFF_HAND,overwrite);
		}
		confirmEquip(equippingWeapon,overwrite);	
	}
	
	private void attemptEquipArmor(Armor equippingArmor){
		boolean overwrite=true;
		String slot=equippingArmor.getArmorType();
		if(equippingArmor.equipped){
			confirmUnequip(slot,overwrite);
			return;
		}
		if(!equipmentSlotFree(slot)){
			confirmUnequip(slot,overwrite);
			overwrite=false;
		}
		if(slot==Equipment.OFF_HAND
		&& currentWeapon()!=null
		&& currentWeapon().twoHanded){//checks to see if the player is trying to equip a shield while holding a two-handed weapon.
			confirmUnequip(Equipment.WEAPON,overwrite);
			overwrite=false;
		}
		else if(slot==Equipment.CHEST
			&& getEquipmentInSlot(Equipment.CLOAK)!=null){
			confirmUnequip(Equipment.CLOAK,overwrite);
			overwrite=false;
		}
		confirmEquip(equippingArmor,overwrite);
				
	}
	
	private void confirmEquip(Equipment equipment,boolean overwrite) {
		equip(equipment);
		if(monster.getClass().equals(Player.class)){
			String equipmentName=((Player)monster).displayItemName(equipment, false);
			monster.changeCurrentMessage("Equipped "+equipmentName+". ",monster.currentTile,overwrite);
			((Player)monster).endPlayerTurn();
		}
	}

	private void confirmUnequip(String slot,boolean overwrite) {
		if(monster.getClass().equals(Player.class)){
			String equipmentName=((Player)monster).displayItemName(getEquipmentInSlot(slot), false);
			monster.changeCurrentMessage("Unequipped "+equipmentName+". ",monster.currentTile,overwrite);
			((Player)monster).endPlayerTurn();
		}
		unequipItem(slot);
	}

	
	public Armor defendingArmor(){	//determine which piece of armor is hit by an attack.
		double[] cumulativeProbabilities=new double[maxEquipmentSlots()+1];
		cumulativeProbabilities[0]=0;
		int index=1;
		double materialRoll=dice.nextDouble();
		for(int i=1;i<equipment.length+1;i++){					//iterate through all equipment slots
			if(equipment[i-1]!=null								//check that equipment is worn in this slot
			 &&equipment[i-1].getClass().equals(Armor.class)	//check that this equipment is armor
			 &&!((Armor)equipment[i-1]).getSlot().equals(Equipment.OFF_HAND)){	//check that this equipment is not a shield (this method is only reached if shield blocking fails)
				double nextProbability=cumulativeProbabilities[index-1]+((Armor)equipment[i-1]).coverage();
				if(nextProbability>=materialRoll){
					//System.out.println("Hit "+equipment[i-1]+". Roll: "+materialRoll+". Cumulative probability: "+nextProbability);	//for testing
					return (Armor) equipment[i-1];
				}
				cumulativeProbabilities[index]=nextProbability;
				index++;
			}
		}
		//System.out.println("Hit flesh. Roll: "+materialRoll);	//for testing
		return monster.monsterHide();
	}
	
	public int totalEvasionPenalty() {
		int evasionPenalty=0;
		for(int i=0;i<equipment.length;i++){
			if(equipment[i]!=null&&equipment[i].getClass().equals(Armor.class))
				evasionPenalty+=((Armor)equipment[i]).evasionPenalty();
		}
		return evasionPenalty;
	}
	
	private Random dice=new Random();
	private Monster monster;
	private Equipment[] equipment=new Equipment[maxEquipmentSlots()];
	
	
	
}
