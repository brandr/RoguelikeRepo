import java.io.InputStream;
import java.util.Random;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
//import java.io.File;
/*import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;		//many imports aren't used, but i'm holding onto them until I have a better idea of how this class will work.
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.Location;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;*/

//TODO: remove the part of this that reads in weapons, now that it is obsolete.

public class ItemReader {
	//all items
	static final String ITEM_NAME="itemName";
	
	static final String BRANCH="branch";
	static final String MINIMUM_DEPTH="minimumDepth";
	static final String MAXIMUM_DEPTH="maximumDepth";
	static final String SPAWN_CHANCE = "spawnChance";
	
	static final String MATERIAL="material";
	static final String WEIGHT="weight";
	
	static final String ARTIFACTS = "artifacts";
	static final String TRUE_NAME="trueName";
	static final String EXCLUDED_MATERIAL="excludedMaterial";
	
	//equipment	
	static final String POWER = "power";
	
	//weapons
	static final String WEAPONS="weapons";
	
	static final String WEAPON="weapon";
	static final String WEAPON_CATEGORY="weaponCategory";
	static final String HANDS="hands";
	static final String QUALITY="quality";
	static final String VARIANCE="variance";
		
		//armor
	static final String ARMORS="armors";
		
	static final String ARMOR="armor";
	static final String ARMOR_TYPE="armorType";
	
		//ammo
	
	public final static String STR="str";
	public final static String DEX="dex";
	public final static String STR_DEX="strDex";
	
		//shootable ammo
	
	public final static String ARROW="arrow";
	public final static String [] ARROW_WEAPONS={"longbow","shortbow"};
	public final static int[] ARROW_DAMAGE={0,3};
	
	public final static String BOLT="bolt";
	public final static String [] BOLT_WEAPONS={"crossbow"};
	public final static int[] BOLT_DAMAGE={0,3};
	
		//throwable ammo
	
	public final static String DART="dart";
	public final static String [] DART_WEAPONS={};	//TODO: consider adding blowguns.
	public final static int[] DART_DAMAGE={3,3};
	
	/*public final static String ROCK="rock";	//TODO: since rocks are special, hardcode them somehow. 
	 * (NICK'S IDEA: make a "Rock" class that inherits from Ammo and overrides "setMaterial". 
	 * Also be careful when using "getClass" related to ammo.)
	public final static String [] ROCK_WEAPONS={"sling"};
	public final static int[] ROCK_DAMAGE={2,4};*/
	
	public final static Ammo[] GENERIC_AMMOS={
		new Ammo(ARROW, ARROW_WEAPONS, DEX, 30, ARROW_DAMAGE, 1)
		,new Ammo(BOLT, BOLT_WEAPONS, DEX, 20, BOLT_DAMAGE, 1)
		,new Ammo(DART, DART_WEAPONS, DEX, 25, DART_DAMAGE, 1)
	};
	
	//potions
	static final String POTIONS="potions";		//potions should be added differently, since they are too few in number to be added by .xml files.
	
	public static void addDungeonItems(Dungeon dungeon){		//add monsters to the appropriate level in the dungeon
		//NOTE: this method doesn't seem to do much anymore. Eventually I should get rid of it.
		try {
			InputStream itemInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("ItemManual.xml");
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(itemInput);
			
			//TODO: keep splitting items into categories.
	    	   	
	    	//Inventory inventory=new Inventory();		//this might be useful, but I'm not sure
	    	
			while(reader.hasNext()) {
				
				XMLEvent event = reader.nextEvent();
				
	        	if(event.isStartElement()){		
	        		String elementName=startElementName(event);
	        		
	        		switch(elementName){	//TODO: wait until each item reading part definitely works before removing commented section.
	        		case(POTIONS):
	        			while(reader.hasNext()&&!(event.isEndElement()
		        			&&event.asEndElement().getName().getLocalPart().equals(POTIONS))) {
	        				event = reader.nextEvent();
	        				if(event.isStartElement()){
	        	        		switch(startElementName(event)){
	        	        			//TODO? or put potions in their own file?
	        	        		}
	        				}
	        				else if (event.isEndElement()){
	        	        		switch(endElementName(event)){
	        	        		case(ARMORS):
	        	        			break;
	        	        		default:
	        	        			break;
	        	        		}
	        				}
	        			}
	        		}
	        	}
	        	else if(event.isEndElement()){	//NOTES: this is for the outermost while loop, so model it after the outermost while loop in monsterReader if that helps.
	        		//EndElement endElement = event.asEndElement();
	        		//String elementName=endElement.getName().getLocalPart();
	        	switch(endElementName(event)){
	        	case(WEAPONS):
        			break;
        		default:
        			break;
	        		}
	        	}
			}		
			}catch(XMLStreamException exc) {
	    }
		addDungeonAmmo(dungeon);
		addDungeonPotions(dungeon);
	}

	

	protected static boolean readUntil(XMLEventReader reader, XMLEvent event,
			String endString) {
		return reader.hasNext()&&!(event.isEndElement()
        	&&event.asEndElement().getName().getLocalPart()==endString);
	}
	
	protected static String startElementName(XMLEvent event){
		if(event.isStartElement()){
    		StartElement startElement = event.asStartElement();
    		return startElement.getName().getLocalPart();
		}
		return null;
	}
	
	protected static String endElementName(XMLEvent event){
		if(event.isEndElement()){
    		EndElement endElement = event.asEndElement();
    		return endElement.getName().getLocalPart();
		}
		return null;
	}

	private static void addDungeonAmmo(Dungeon dungeon) {
		for(int i=0;i<Dungeon.BRANCH_COUNT&&dungeon.getBranch(i)!=null;i++){
			addBranchAmmo(dungeon, dungeon.getBranch(i));
		}	
	}
	
	private static void addBranchAmmo(Dungeon dungeon, Branch branch) {
		for(int i=branch.startDepth();i<branch.endDepth();i++){
			Level currentLevel=branch.getLevel(i);
			int ammoDepth=currentLevel.ammoDepth();
			for(int j=0;j<GENERIC_AMMOS.length;j++){
				int value=GENERIC_AMMOS[j].getOverallValue(); //temporary
				//Material[] materials=Material.suitableMaterials(branch, ammoDepth);
				//for(int m=0;m<materials.length&&materials[m]!=null;m++){
					Ammo addedAmmo=new Ammo(GENERIC_AMMOS[j]);
					//addedAmmo.setMaterial(new Material(materials[m]), true); //TODO: set ammo material (and amount) only upon the ammo's creatioon.
				//	addedAmmo.setAmount(ammoAmount(ammoDepth));		//TODO: move this to an ammo item's creation if stack sizes are noticeably repetitive on a level
					currentLevel.addAvailableItem(addedAmmo, Math.abs(value-ammoDepth));
				//}	
					//TODO: ask nick if ammo should be associated with branch. also, set material and amount upon an ammo item's creation. (ask nick how to determine bounds for randomizing amount)
			}
		}	
	}
	
	public static Ammo createAmmo(String trueName, String ammoType, int amount, int[] damage, Material material){	//TODO: should ammoType just be genericName?
		Ammo[] genericAmmos=ItemReader.GENERIC_AMMOS;
		for(int i=0;i<genericAmmos.length;i++){
			if(ammoType.equals(genericAmmos[i].genericName())){
				Ammo ammo=new Ammo(genericAmmos[i]);
				ammo.name=trueName;
				ammo.setAmmoStat(genericAmmos[i].getAmmoStat());
				
				ammo.setAmount(amount);
				ammo.setDamage(damage[0], damage[1]);
				ammo.setMaterial(material,false);//sets materials without changing stats.
				return ammo;
			}
		}
		return null;
	}

	//potion methods
	
	private static void addDungeonPotions(Dungeon dungeon) {
		for(int i=0;i<Dungeon.BRANCH_COUNT&&dungeon.getBranch(i)!=null;i++){
			addBranchPotions(dungeon, dungeon.getBranch(i));
		}
	}
	
	private static void addBranchPotions(Dungeon dungeon, Branch branch) {
		String[] colors=dungeon.dungeonPotionColors;
		for(int i=branch.startDepth();i<branch.endDepth();i++){
			Level currentLevel=branch.getLevel(i);
			for(int j=0;j<colors.length;j++){
				String potionType=Potion.POTION_TYPES[j];
				//TODO: ad potions one 
				currentLevel.addAvailableItem(new Potion(potionType,colors[j]),0);
				//currentLevel.addAvailableItem(new Potion(potionType,colors[j]
				//		,getDefaultPotionValue(potionType,i),getDefaultPotionDuration(potionType,i),randomPotionCount()),0);		//0 is temporary for offset. adjust it as potions get "depths" or "overallValues"
			}
		}	
	}
	
	private static int randomPotionCount() {
		return 1+dice.nextInt(Potion.STANDARD_POTION_STACK_SIZE);
	}

	public static int getDefaultPotionValue(String potionType, int depth){
		switch(potionType){
		case(Potion.HEALING):
			return 5+(int)(depth/3.0)+ dice.nextInt((int)(depth/2.0)+2);	
		//case(Potion.PARALYSIS):
		//	return 0;
		case(Potion.GAIN_ABILITY):	//in this case, "value" is the number of stats increased. (from 1-7)
			return 1+dice.nextInt(7);
		case(Potion.RAGE):
			return 2+(int)(depth/10.5)+ dice.nextInt((int)(depth/6.5)+2);
		}
		return 0;
	}
	
	//some potions last longer depending on player's depth in the dungeon.
	public static int getDefaultPotionDuration(String potionType, int depth) {		//TODO: change this as more potion types are added.
		switch(potionType){
		case(Potion.HEALING):
			return 0;
		//case(Potion.PARALYSIS):
		//	return 2+(int)(depth/9.0)+ dice.nextInt((int)(depth/6.0)+2);
		case(Potion.GAIN_ABILITY):
			return 0;
		case(Potion.RAGE):
			return 4+(int)(depth/5.5)+ dice.nextInt((int)(depth/2.5)+2);
		}
		return 0;
	}

	private static Random dice=new Random();
}