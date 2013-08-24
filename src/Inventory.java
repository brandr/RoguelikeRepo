
public class Inventory {

	public static final int MAX_ITEMS=30;
	public final int MAX_GOLD=999999;	//most gold an inventory can hold
	
	public Inventory(){
		maxItems=MAX_ITEMS;
		items = new Item[maxItems];
	}
	
	public Inventory(int maxItems){
		this.maxItems=maxItems;
		items = new Item[maxItems];
	}
	
	public Inventory(Item[] items){
		maxItems=MAX_ITEMS;
		this.items=new Item[maxItems];
		for(int i=0;i<items.length&&items[i]!=null;i++){
			this.items[i]=items[i];
		}
	}
	
	public Inventory(Inventory inventory) {
		maxItems=inventory.maxItems;
		items = new Item[maxItems];
		for(int i=0;i<inventory.getItemCount()&&inventory.getItem(i)!=null;i++){
			Item item=inventory.getItem(i);
			String itemClass=item.getClass().getName();
			switch(itemClass){	//copies items using appropriate copy constructors. TODO: add more with each new item type.
			case("Ammo"):
				items[i]=new Ammo((Ammo)inventory.getItem(i));
				break;
			case("Armor"):
				items[i]=new Armor((Armor)inventory.getItem(i));
				break;
			case("Food"):
				items[i]=new Food((Food)inventory.getItem(i));
				break;
			case("Potion"):
				items[i]=new Potion((Potion)inventory.getItem(i));
				break;
			case("Weapon"):
				items[i]=new Weapon((Weapon)inventory.getItem(i));
				break;
			}
			//items[i]=inventory.getItem(i);	//this might not work since it's not a copy constructor.
		}
	}
	
	public String[][] columnString(Player player, int columns){		
		int columnSize=maxItems/columns;
		String[][] retVal = new String[columns][columnSize];
		int itemIndex=0;
		for(int i=0;i<columns;i++)
		{
			for(int j=0;j<columnSize&&itemIndex<maxItems&&items[itemIndex]!=null;j++){
				boolean equipString=items[itemIndex].equippable();
				retVal[i][j]=player.displayItemName(items[itemIndex],equipString);		
				if(items[itemIndex].stackable()&&items[itemIndex].enoughInStack(2))
					retVal[i][j]=Item.plural(retVal[i][j])+"fff ("+items[itemIndex].getAmount()+") "+equipString(items[itemIndex]);
				itemIndex++;
				}
				
			}
		return retVal;
		}
	
	public String columnToString(String[] column, Player player, int columnIndex){
		
		if(isEmpty())
			return "(empty)";		
		
		String columnString="";
		int index=columnIndex*columnSize();
		while (index<maxItems&&index<columnSize()*(columnIndex+1)&&items[index]!=null){
			columnString+="("+Item.getCharForNumber(index)+") ";
			String nextItemString=player.displayItemName(items[index],false);
			if(items[index].stackable()&&items[index].enoughInStack(2))
					nextItemString=Item.plural(nextItemString)+" ("+items[index].getAmount()+")";
			nextItemString+=" "+equipString(items[index]);
			columnString+=nextItemString+"\n";
			index++;
		}
		return columnString;
	}
	
		//toString methods for item options
	
	public String[] showItemsOfType(String itemType, Player player) {
		return showItemsOfType(itemType, player, 0);
	}
	
	public String[] showItemsOfType(String itemType, Player player, int offset) {	//is offset used? what was it even used for?
		Inventory itemsOfType=getItemsOfType(itemType);
		char[] itemLetters=lettersForItemsOfType(itemType);
		int count = itemsOfType.getItemCount();
		String []showItems=new String[count];
		for(int i=0;i<count;i++){
			Item nextItem=itemsOfType.getItem(i);
			showItems[i]="("+itemLetters[i]+") ";
			String itemName=player.displayItemName(itemsOfType.getItem(i),false);
			if(nextItem.stackable()&&nextItem.enoughInStack(2))
				showItems[i]+=Item.plural(itemName)+ " ("+nextItem.getAmount()+")";
			else
				showItems[i]+=itemName;
			showItems[i]+= " "+equipString(nextItem);
			}
		return showItems;
	}

	public String getItemName(Player player, int index) {
		return player.displayItemName(items[index],false);	
	}
	
	private String equipString(Item item) {
		if((item.getClass().equals(Armor.class)||item.getClass().equals(Weapon.class))&&((Equipment)(item)).equipped)
			return "[E]";
		return "";
	}
	
	//booleans for empty/full inventory
	
	public boolean isEmpty(){
		return items[0]==null;
	}
	
	public boolean isFull(){
		return items[maxItems-1]!=null;
	}
	
	//gold-related methods
	
			public int getGold(){
				return gold;
			}
			
			public void setGold(int gold){
				if(validGoldAmount(gold))
					this.gold=gold;
			}
			
			public void addGold(int adjustment){	//increase or decrease gold amount
				if (validGoldAmount(gold+adjustment))
					setGold(gold+adjustment);
			}
			
			public int takeGold(int takenGold){
				if(validGoldAmount(gold-takenGold)){
					addGold(-1*takenGold);
					return takenGold;
				}
				return 0;		//should this return 0, or should it return gold? main goal is to avoid error cases.
			}
			
			public int takeAllGold(){
				int allGold=gold;
				gold=0;
				return allGold;
			}
			
			public boolean noGold(){
				return gold==0;
			}
			
			public boolean validGoldAmount(int goldAmount){
				return goldAmount>=0&&goldAmount<=MAX_GOLD;
			}
	
	//getters, adders, removers, etc.
	
	public Item getItem(int index){
		return items[index];
	}
	
	public void addItem(Item newItem){
		int index=0;
		while(items[index]!=null){
			index++;
			if(index >= maxItems){
				System.out.println("No room.");	//need a way to display in gui.
			}
		}
		items[index]=newItem;
	}
	
	public void removeItem(int index){		//will this always work? not sure. Seems okay so far though.
		if(items[index]!=null){			
			while(index<maxItems-1&&items[index]!= null&&items[index+1]!= null){
				if(index+1==maxItems){
					items[index]=items[index+1];
					items[index+1]=null;
					return;
				}		
				items[index]=items[index+1];
				index++;
			}
			items[index]=null;
		}
	}
	
	public void removeItem(Item removedItem) {	//special function with an item as an arg instead of an int. Will probably be useful.
		int index=0;
		while(items[index]!=removedItem&&items[index]!=null)
			index++;
		removeItem(index);
	}
	
	public Item takeItem(int index){
		if(containsItem(index)){
			Item takenItem=getItem(index);		//will this work? might cause shallow copy/deep copy problems
			removeItem(index);
			return takenItem;
		}
		else
			return null;	//might need a better case for when the index is not correct for an item
	}
	
	public Item takeItem(Item takenItem) {
		int index=0;
		while(items[index]!=takenItem&&items[index]!=null)
			index++;
		return takeItem(index);
	}
	
	public boolean containsItem(int index) {
		if(index>=0&&index<maxItems&&items[index]!=null){
			return true;
		}
		return false;
	}
	
	public boolean containsItem(Item searchedItem) {
		int index=0;
		while(items[index]!=searchedItem&&items[index]!=null){
			if(items[index]==searchedItem)
				return true;
			index++;
			}
		return false;
	}
	
	public Item topItem(){	
		return items[0];
	}
	
	public void setEmpty(){
		while(items[0]!=null)
			removeItem(0);
	}
	
	public int getItemCount() {
		int index=0;
		while(index<maxItems&&items[index]!=null)
			index++;
		return index;
	}
	
	public Inventory getItemsOfType(String itemType){
		Class<?> itemClass;
		switch(itemType){
		case("Item"):
			return this;
		case("Food"):
			itemClass=Food.class;
		break;
		case("Potion"):
			itemClass=Potion.class;
		break;
		case("Equipment"):		//TODO: should either change "getCanonicalName()" to another method, or make separate lists of armor and weapons, then combine them.
			itemClass=Equipment.class;
		break;
		default:
			return this;
		}
		int count=getItemCount();
		Inventory itemsOfType = new Inventory();
		for(int i=0;i<count;i++){
			if(itemClass.isAssignableFrom(getItem(i).getClass()))
				itemsOfType.addItem(getItem(i));
		}
		return itemsOfType;
	}
	
	private char[] lettersForItemsOfType(String itemType){		//should this be public?
		Class<?> itemClass;
		switch(itemType){
		case("Item"):
			return allInventoryLetters();
		case("Food"):
			itemClass=Food.class;
		break;
		case("Potion"):
			itemClass=Potion.class;
		break;
		case("Equipment"):	//TODO: add more cases as necessary.
			itemClass=Equipment.class;
		break;
		default:
			return allInventoryLetters();
		}
		int count=getItemCount();
		char[] lettersForType = new char[getItemCount()];
		int letterIndex=0;
		for(int i=0;i<count;i++){
			
			if(itemClass.isAssignableFrom(getItem(i).getClass())){
				lettersForType[letterIndex]=Item.getCharForNumber(i);
				letterIndex++;
			}
				
		}
		return lettersForType;
	}
	
	private char [] allInventoryLetters(){		//return all letters associated with items in this inventory
		char[] inventoryLetters=new char[getItemCount()];
		for(int i=0;i<getItemCount();i++){
			inventoryLetters[i]=Item.getCharForNumber(i);
		}
		return inventoryLetters;
	}
	
	public int getMaxItems(){
		return maxItems;
	}
	
	public int columnSize(){
		return maxItems/columnCount;
	}
	
	public Equipment[] getAllEquipment() {
		Equipment[] equipment = new Equipment[getItemCount()];
		int index=0;
		for(int i=0;i<getItemCount();i++){
			if(Equipment.class.isAssignableFrom(getItem(i).getClass())){
				equipment[index]=(Equipment) getItem(i);
				index++;
			}
		}
		return equipment;
	}
	
	public int totalWeight() {
		int weight=0;
		for(int i=0;i<items.length&&items[i]!=null;i++){
			weight+=items[i].getWeight();
		}
		return weight;
	}
	
	private int gold=0;
	private int columnCount=2;
	private int maxItems;
	protected Item [] items;
}
