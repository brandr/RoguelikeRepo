import java.util.Random;


public class ItemGenerator {	//generates random items and gold for the level.
								//should not generate monster inventories, since these are specific to each monster and so should
								//be handled or accessed by the monsterGenerator class.
//NOTE: this class does not need a "step" method because items are not added randomly over time, only on a level's creation.
	
	public ItemGenerator(Level dungeonLevel){		//should add monsters based on current dungeon level, usually.
		this.dungeonLevel=dungeonLevel;
	}
	
	public void addItem(Item newItem){
		Class<?>[] classes=Item.ITEM_CLASSES;
		for(int i=0;i<classes.length;i++){
			if(newItem.getClass().equals(classes[i])){
				int index=0;
				while(index<knownItems[i].length&&knownItems[i][index]!=null)
					index++;
				knownItems[i][index]=newItem;
			}		
		}
	}
	
	
	public Item getItem(Class<?> itemClass,int index){
		for(int i=0;i<Item.ITEM_CLASSES.length;i++){
			if(itemClass==Item.ITEM_CLASSES[i]){
				if(index>=0||index<knownItems.length)
					return (knownItems[i][index]);	//the copy constructor for monster will need to be developed
				else
					return null;
			}		
		}
		return null;
	}
	
	public Item getRandomItem(){
		if(knownItemCount()>0){
			int classIndex=itemRNG.nextInt(Item.ITEM_CLASSES.length);
			while(knownItemCount(classIndex)<=0)
				classIndex=itemRNG.nextInt(Item.ITEM_CLASSES.length);
			Class<?> itemClass=Item.ITEM_CLASSES[classIndex];
			return chooseRandomItem(itemClass);
		}
		return null;
	}
	
	/*public Item chooseRandomItem(Class<?> itemClass) {
		for(int i=0;i<Item.ITEM_CLASSES.length;i++){
			if(itemClass==Item.ITEM_CLASSES[i]&&knownItemCount(i)>0){
				boolean itemChosen=false;
				while(!itemChosen){			
					int itemIndex=itemRNG.nextInt(knownItemCount(i));
					if(getItem(itemClass,itemIndex)!=null
							&& considerItem(getItem(itemClass,itemIndex))){
						itemChosen=true;
						return getItem(itemClass,itemIndex);
					}
				}
			}		
		}
		return null;
	}*/
	
	public Item chooseRandomItem(Class<?> itemClass){		//chooses a random known monster appropriate for the level.
		int knownCount=knownItemCount(itemClass);
		int itemDepth=dungeonLevel.itemDepth(itemClass);
		if(knownCount<1)
			return null;
		if(knownCount==1)
			return getItem(itemClass,0);
		else if(knownCount>1){
			int optionCount=(int) Math.max(2, 
								knownCount/(2.1+0.08*itemDepth));	//TODO: tweak this formula until it seems right.
			Item[] itemOptions=new Item[optionCount];	//possible items to choose from
			itemOptions[0]=randomItem(itemClass);
			for(int i=1;i<optionCount;i++){
				Item nextItem=randomItem(itemClass);
				while(nextItem.genericName().equals(itemOptions[i-1].genericName()))
					nextItem=randomItem(itemClass);
				itemOptions[i]=nextItem;
			}
			return bestItem(itemOptions, itemDepth);
		}
		return null;
	}
	 
	
	private Item bestItem(Item[] itemOptions, int itemDepth) {
		int length=itemOptions.length;
		if(length>1){
			Item bestItem=itemOptions[0];
			 for(int i=1;i<length&&itemOptions[i]!=null;i++){
				  int currentClosest=Math.abs(bestItem.getOverallValue()-itemDepth);
				  int potentialClosest=Math.abs(itemOptions[i].getOverallValue()-itemDepth);  
				  if(potentialClosest<currentClosest)
					  bestItem=itemOptions[i];
			 }
			 return bestItem;
		}
		else if(length==1)
			return itemOptions[0];
		return null;
	}

	private Item randomItem(Class<?> itemClass) {
		int count=knownItemCount(itemClass);
		if(count==0)
			return null;
		int itemIndex=itemRNG.nextInt(count);
		while(getItem(itemClass,itemIndex)==null)
			itemIndex=itemRNG.nextInt(count);
		return getItem(itemClass, itemIndex);
	}

	/*private boolean considerItem(Item item){		//considers selecting an item based on its spawn rate.
		if(((double)((double)item.spawnChance*100.0))>itemRNG.nextInt(100))
			return true;
		return false;
	}*/
	
	public int randomGoldAmount(){	//a random amount of gold appropriate for this level.
		if(goldMin==goldMax)
			return goldMin;
		return goldMin+itemRNG.nextInt(goldMax-goldMin+1);
	}
	
	public void setGoldRange(int min, int max){  
		if(min>0&&max>0&&max>=min){
			goldMin=min;
			goldMax=max;
		}
	}
	
	public int knownItemCount(){
		int index=0;
		for(int i=0;i<Item.ITEM_CLASSES.length;i++){
			while(index<knownItems[i].length&&knownItems[i][index]!=null)
				index++;
		}
		return index;
	}
	
	public int knownItemCount(int classIndex){
		int index=0;
			while(index<knownItems[classIndex].length&&knownItems[classIndex][index]!=null)
				index++;
		return index;
	}
	
	public int knownItemCount(Class<?> itemClass){
		Class<?>[] classes=Item.ITEM_CLASSES;
		for(int i=0;i<classes.length;i++){
			//System.out.println(itemClass+", "+classes[i]);
			if(itemClass.equals(classes[i])){
				//if(itemClass.equals(Weapon.class))
				//	System.out.println("WEAPON");
				int index=0;
				while(index<knownItems[i].length&&knownItems[i][index]!=null)
					index++;
				return index;
			}		
		}
		return 0;
	}
	
	public Level getDungeonLevel(){
		return dungeonLevel;
	}
	
	private Level dungeonLevel;
	private Random itemRNG=new Random();
	private Item[][] knownItems= new Item[Item.ITEM_CLASSES.length][200];
	
	private int goldMin=0; //size of gold stacks created randomly
	private int goldMax=0;
	
}
