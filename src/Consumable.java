
public abstract class Consumable extends Item{	//currently written assuming potions.
	
	public Consumable(){	//TODO: set effects for food. (will need to implement hunger.)
		name = null;
		consumable=true;
		//effect = new Effect();
		//setIcon(STANDARDCONSUMABLEICON);
	}
	
	public Consumable(String name){
		this.genericName=name;
		consumable=true;
		//effect = new Effect();
		//setIcon(STANDARDCONSUMABLEICON);
	}
	
	public Consumable(String name, int effectIndex, int value){
		this.genericName=name;
		consumable=true;
		//effect=new Effect(effectIndex);
		//effect.value=value;
		//setIcon(STANDARDCONSUMABLEICON);
	}
	
	public String toString(){
		return genericName;
	}
	
	@Override
	public void use(Monster target) {
		getEffect().takeEffect(target);
	}

	public Effect getEffect() {
		return null;
	}

	//protected Effect effect;	//both potions and food have effects.
}
