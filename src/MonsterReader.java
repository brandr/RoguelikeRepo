import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;		//many imports aren't used, but i'm holding onto them until I have a better idea of how this class will work.
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

//TODO: add a section for branches.

public class MonsterReader {	//reads in monsters from the monster manual .xml file
	
	static final String MONSTER = "monster";
	
	static final String NAME = "name";
	static final String ICON = "icon";
	static final String COLOR = "color";
	
	static final String HIT_POINTS = "hitPoints";
	static final String BASE_DAMAGE = "baseDamage";
	static final String BASE_ARMOR = "baseArmor";
	
	static final String TO_HIT = "toHit";
	static final String EVASION_VALUE = "evasionValue";
	
	static final String XP_REWARD = "xpReward";	//TODO: remove this.
	
	static final String SPAWN_CHANCE = "spawnChance";
	
	static final String BRANCH="branch";
	static final String MINIMUM_DEPTH="minimumDepth";
	static final String MAXIMUM_DEPTH="maximumDepth";
	
	static final String INVENTORY="inventory";
	
	static final String WEAPON="weapon";
	static final String ARMOR="armor";
	static final String WEAPON_CATEGORY="weaponCateory";
	
	static final String ITEM_NAME="itemName";
	static final String HANDS="hands";
	static final String POWER="power";
	static final String ARMOR_TYPE="armorType";
	
	public static void addDungeonMonsters(Dungeon dungeon){		//add monsters to the appropriate level in the dungeon
		try { 
			
			//write();
			
			InputStream monsterInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("MonsterManual.xml");
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(monsterInput);
			
			String name = "";
	    	char icon = 0;
	    	String color="000000";
	    	
	    	int hitPoints=-1;
	    	int baseDamage=-1;
	    	int baseArmor=-1;		//TODO: set defaults for all of these based on dungeon depth.
	    	int toHit=-1;
	    	int evasionValue=-1;
	    	int xp=-1;
	    	
	    	double spawnChance=1.0;
	    	
	    	int branch=1;
	    	int minDepth=0;
	    	int maxDepth=0;
	    	
	    	Inventory inventory=new Inventory();
	    	
			while(reader.hasNext()) {
				
	        	XMLEvent event = reader.nextEvent();
	        	if(event.isStartElement()){
	        		StartElement startElement = event.asStartElement();
	        		String elementName=startElement.getName().getLocalPart();
	        		switch(elementName){
	        		case(BRANCH):
	        			branch=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(MINIMUM_DEPTH):
	        			minDepth=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(MAXIMUM_DEPTH):
	        			maxDepth=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(NAME):
	        			name=(reader.nextEvent().toString());
	        			break;
	        		case(ICON):
	        			icon=(reader.nextEvent().toString().charAt(0));
	        			break;
	        		case(COLOR):
	        			color=(reader.nextEvent().toString());
	        			break;
	        		case(HIT_POINTS):
	        			hitPoints=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(BASE_DAMAGE):
	        			baseDamage=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(BASE_ARMOR):
	        			baseArmor=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(TO_HIT):
	        			toHit=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(EVASION_VALUE):
	        			evasionValue=Integer.parseInt(reader.nextEvent().toString());
	        			break;
	        		case(SPAWN_CHANCE):
	        			spawnChance=Double.parseDouble(reader.nextEvent().toString());
	        			break;
	        		case(INVENTORY):		//idea: could read in all items from another xml file, then give monsters inventories based on indexed items.
	        			while(reader.hasNext()&&!(event.isEndElement()
	        				&&event.asEndElement().getName().getLocalPart()==INVENTORY)) {
	        	        	event = reader.nextEvent();
	        	        	if(event.isStartElement()){
	        	        		startElement=event.asStartElement();
	        	        		elementName=startElement.getName().getLocalPart();
	        	        		switch(elementName){
	        	        		case(WEAPON):
	        	        			Weapon weapon=new Weapon();
	    	        			while(reader.hasNext()&&!(event.isEndElement()
	    		        				&&event.asEndElement().getName().getLocalPart()==WEAPON)){
	    	        					event = reader.nextEvent();				        	        					
	    	        					if(event.isStartElement()){
	    		        					startElement = event.asStartElement();
	    		        	        		elementName=startElement.getName().getLocalPart();
	    		        	        		switch(elementName){
	    		        	        		case ITEM_NAME:						        	        			
	    		        	        			weapon.name=(reader.nextEvent().toString());
	    		        	        			break;
	    		        	        		case HANDS:
	    		        	        			if(Integer.parseInt(reader.nextEvent().toString())==2)
	    		        	        				weapon.twoHanded=true;
	    		        	        			break;
	    		        	        		case POWER:
	    		        	        			weapon.setPower(Integer.parseInt(reader.nextEvent().toString()));		
	    		        	        			break;
	    		        	        		case WEAPON_CATEGORY:
	    		        	        			weapon.weaponCategory=(reader.nextEvent().toString());
	    		        	        			break;
	    		        	        		default:
	    		        	        			break;
	    		        	        		}
	    	        					}
	    	        					else if(event.isEndElement()&&(event.asEndElement().getName().getLocalPart()==WEAPON)){
	    	        						if(weapon.name!=null)
	    	        							inventory.addItem(weapon);		
	    	        						break;
	    	        					}
	    	        				}
	        	        			break;
	        	        		case(ARMOR):
	        	        			Armor armor=new Armor();
	        	        		while(reader.hasNext()&&!(event.isEndElement()
	        	        				&&event.asEndElement().getName().getLocalPart()==ARMOR)){
	            					event = reader.nextEvent();	
	               					if(event.isStartElement()){
	        	        					startElement = event.asStartElement();
	        	        	        		elementName=startElement.getName().getLocalPart();
	        	        	        		switch(elementName){
	        	        	        		case ITEM_NAME:
	        	        	        			armor.name=(reader.nextEvent().toString());
	        	        	        			break;
	        	        	        		case ARMOR_TYPE:
	        	        	          			armor.setArmorType(reader.nextEvent().toString());
	        	        	        			break;
	        	        	        		case POWER:
	        	        	        			armor.setArmorValue(Integer.parseInt(reader.nextEvent().toString()));
	        	        	        			break;
	        	        	        		default:
	        	        	        			break;
	        	        	        		}
	               						}
	               					else if(event.isEndElement()&&(event.asEndElement().getName().getLocalPart()==ARMOR)){
	           						if(armor.name!=null){
	           							armor.setMaterial(Material.getMaterial("iron"),false);	//temporary. Will be removed when monter items are changed.
	           							inventory.addItem(armor);
	           						}
	           						break;		
	               					}
	        	        		}
	        	        			break;
	        	        		default:
	        	        			break;
	        	        		}
	        	        	}
	        	        	else if (event.isEndElement()){
	        	        		EndElement endElement = event.asEndElement();
	        	        		elementName=endElement.getName().getLocalPart();
	        	        		switch(elementName){
	        	        		case(INVENTORY):
	        	        			break;
	        	        		default:
	        	        			break;
	        	        		}
	        	        	}
	        			}
	        			break;
	        		default:
	        			break;
	        		}
	        	}
	        	else if(event.isEndElement()){
	        		EndElement endElement = event.asEndElement();
	        		String elementName=endElement.getName().getLocalPart();
	        	switch(elementName){
	        	
	        	case(MONSTER):
	        		if(dungeon.depth>maxDepth){		//off by one because the first level is shown as 1, but processed as 0.
	        			
	        			Monster addedMonster=new Monster();
        				//TODO: set all defaults here
        				if(hitPoints==-1)
        					hitPoints=getDefaultHitPoints(minDepth,maxDepth);
        				if(baseDamage==-1)
        					baseDamage=getDefaultBaseDamage(minDepth,maxDepth);
        				if(baseArmor==-1)
        					baseArmor=getDefaultBaseArmor(minDepth,maxDepth);
        		    	if(toHit==-1)
        		    		toHit=getDefaultToHit(minDepth,maxDepth);
        		    	if(evasionValue==-1)
        		    		evasionValue=getDefaultEvasionValue(minDepth,maxDepth);
        		    	if(xp==-1)
        		    		xp=getDefaultXp(minDepth,maxDepth);
        		    	
        		    	
        		    	
        		    	addedMonster=new Monster(name,icon,hitPoints,baseDamage,baseArmor,toHit,evasionValue);		//TODO: make sure monster constructors (including copy constructor) match up!
        				addedMonster.color=color;
        								
        		    	addedMonster.xp=xp;
        		    	addedMonster.spawnChance=spawnChance;

	        			for(int i=minDepth;i<=maxDepth;i++){	
	        				addedMonster.inventory=new Inventory(inventory);
	        				dungeon.addAvailableMonster(addedMonster, dungeon.getBranch(branch), i);
	        			//	if(branch==2)
	        			//		System.out.println(addedMonster);
	        			}
	        			
	        			inventory.setEmpty();  
	        			//the only values not reset  here are name, icon, minlevel and maxlevel. That is because these have no defaults.
	        			branch=1;
	        			color="000000";
        				
        				hitPoints=-1;
        		    	baseDamage=-1;
        		    	baseArmor=-1;		//TODO: reset all defaults here. Add more as more defaults are added to the game.
        		    	toHit=-1;
        		    	evasionValue=-1;
        		    	
        		    	xp=-1;
        		    	spawnChance=1.0;
	        		}
        			break;
        		default:
        			break;
	        		}
	        	}
			}
			
			}catch(XMLStreamException exc) {
	    }
	}
	
	//default attribute getters below. TODO: put sensible return values in.
	//Consider using monster variance to make these vary more.
	private static int getDefaultHitPoints(int minDepth, int maxDepth) {
		int averageDepth=(minDepth+maxDepth)/2;
		return 10+averageDepth/2;
	}
	
	private static int getDefaultBaseDamage(int minDepth, int maxDepth) {
		int averageDepth=(minDepth+maxDepth)/2;
		return 2+averageDepth/4;
	}
	
	private static int getDefaultBaseArmor(int minDepth,int maxDepth) {
		int averageDepth=(minDepth+maxDepth)/2;
		if(averageDepth<15)
			return 0;
		else if(averageDepth>=15&&averageDepth<30)
			return 2;
		else if(averageDepth>=30&&averageDepth<40)
			return 5;
		else if(averageDepth>=40)
			return 10;
		return 0;
	}
	
	private static int getDefaultToHit(int minDepth, int maxDepth) {		//this and evade (both 5) are too low for level 1.
		int averageDepth=(minDepth+maxDepth)/2;
		return 8+averageDepth/8;
	}
	private static int getDefaultEvasionValue(int minDepth, int maxDepth) {
		int averageDepth=(minDepth+maxDepth)/2;
		return 2+averageDepth/8;
	}
	private static int getDefaultXp(int minDepth, int maxDepth){
		int averageDepth=(minDepth+maxDepth)/2;
		return (int)(Math.pow(1.2, averageDepth));
	}

//xml writing is unused until I figure it out. eventually I want a gui that will xml write for when Nick uses it.
public static void write(){
FileOutputStream fos = null;
    try {
    	
        fos = new FileOutputStream("Test.xml");
        
        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
        
        writer.writeStartDocument();
        
        writer.writeStartElement("test");
        // write stuff
        writer.writeEndElement();
        writer.flush();
        
    }
    catch(IOException exc) {
    }
    catch(XMLStreamException exc) {
    }
    finally {
    }
    return;
}
}