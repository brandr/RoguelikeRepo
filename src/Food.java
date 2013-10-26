
public class Food extends Consumable{	//TODO: implement food better.
	public final static char STANDARD_FOOD_ICON='%';

	public Food(String name, int hungerPoints){
		genericName=name;
		setHungerPoints(hungerPoints);
		//setEffect();
		setIcon(STANDARD_FOOD_ICON);
	}

	public Food(Food toCopy) {
		name=toCopy.name;
		genericName=toCopy.genericName;
		setWeight(toCopy.getSingleWeight());
		identified=toCopy.identified;
		setAvailableBranches(toCopy.getAvailableBranches());
		setStackSize(toCopy.getStackSize());
		setAmount(toCopy.getAmount());
		
		//effect=new Effect(toCopy.effect);
		hungerPoints=toCopy.hungerPoints;
		setIcon(toCopy.getIcon());
	}
	
	@Override
	public void initialize(Level level) {
		setAmount(1+dice.nextInt(getStackSize()));
	}
	
	@Override
	public Effect getEffect(){
		return new Effect("fill hunger",Effect.RESTORE_FULLNESS,hungerPoints,0);
	}

	@Override
	public boolean stackEquivalent(Item otherItem) {// TODO: food can stack, so determine how here.
		
		return false;
	}
	
	public int getHungerPoints() {
		return hungerPoints;
	}

	public void setHungerPoints(int hungerPoints) {
		if(hungerPoints>=0)
			this.hungerPoints = hungerPoints;
	}

	@Override
	public int getOverallValue() {
		// TODO: determine value once non-corpse foods are implemented
		return 0;
	}
	
	private int hungerPoints=0;	//hunger restored by food
	public boolean consumable=true;

	
}