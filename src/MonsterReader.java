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
	static final String MONSTERS = "monsters";
	
	static final String NAME = "name";
	static final String ICON = "icon";
	static final String COLOR = "color";
	
	static final String HIT_POINTS = "hitPoints";
	static final String BASE_DAMAGE = "baseDamage";
	static final String BASE_ARMOR = "baseArmor";
	
	static final String TO_HIT = "toHit";
	static final String EVASION_VALUE = "evasionValue";
	
	static final String XP_REWARD = "xpReward";	//TODO: remove this.
	
	//static final String SPAWN_CHANCE = "spawnChance";
	
	static final String BRANCH="branch";
	//static final String MINIMUM_DEPTH="minimumDepth";
	//static final String MAXIMUM_DEPTH="maximumDepth";
	
	static final String INVENTORY="inventory";
	
	static final String WEAPON="weapon";
	static final String ARMOR="armor";
	static final String WEAPON_CATEGORY="weaponCateory";
	
	static final String ITEM_NAME="itemName";
	static final String HANDS="hands";
	static final String POWER="power";
	static final String ARMOR_TYPE="armorType";
	
	public static Monster[] allDungeonMonsters(Dungeon dungeon){		//add monsters to the appropriate level in the dungeon
		try { 
			
			//write();
			
			InputStream monsterInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("MonsterManual.xml");
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(monsterInput);
			
			Monster[] monsters=new Monster[2000];
			
			int monsterIndex=0;
			
			String name = "";
	    	char icon = 0;
	    	String color="000000";
	    	
	    	int hitPoints=-1;
	    	int baseDamage=-1;
	    	int baseArmor=-1;		//TODO: set defaults for all of these based on dungeon depth.
	    	int toHit=-1;
	    	int evasionValue=-1;
	    	int xp=-1;
	    	
	    	//double spawnChance=1.0;
	    	
	    	//Inventory inventory=new Inventory();	//TODO redesign monster inventory system.
			while(reader.hasNext()) {
				
	        	XMLEvent event = reader.nextEvent();
	        	if(event.isStartElement()){
	        		String elementName=startElementName(event);
	        		switch(elementName){
        				case(MONSTERS):
	        			while(readUntil(reader, event,MONSTERS)){
	        				
	        				event = reader.nextEvent();
	        					if(event.isStartElement()
	        					&&startElementName(event).equals(MONSTER)){
	        						
	        						Branch[] branches=null;
	        						if(dungeon!=null)
	        							branches=new Branch[Dungeon.BRANCH_COUNT];
						
	        						while(readUntil(reader,event,MONSTER)){
	        							event = reader.nextEvent();
	        							if(event.isStartElement()){
	        								switch(startElementName(event)){
	        								case(BRANCH):
	        									int index=0;
	        									while(branches!=null&&index<branches.length&&branches[index]!=null)
	        										index++;
	        									if(dungeon!=null)
	        										branches[index]=dungeon.getBranch(Integer.parseInt(reader.nextEvent().toString()));
	        									break;
	        								case(NAME):
	        									name=(reader.nextEvent().toString());break;
	        								case(ICON):
	        									icon=(reader.nextEvent().toString().charAt(0));break;
	        								case(COLOR):
	        									color=(reader.nextEvent().toString());break;
	        								case(HIT_POINTS):
	        									hitPoints=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(BASE_DAMAGE):
	        									baseDamage=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(BASE_ARMOR):
	        									baseArmor=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(TO_HIT):
	        									toHit=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(EVASION_VALUE):
	        									evasionValue=Integer.parseInt(reader.nextEvent().toString());break;
	        								default:
	        									break;
	        								}
	        							}	
	        							else if(event.isEndElement()){
	        								switch(endElementName(event)){
	        								case(MONSTER):        			
	        									Monster addedMonster=new Monster();
	        									
	        								//TODO: set all defaults here
												if(toHit==-1)
													toHit=getDefaultToHit(baseDamage);
												if(evasionValue==-1)
													evasionValue=getDefaultEvasionValue(toHit);
												if(xp==-1)
													xp=baseDamage;
	        			
												addedMonster=new Monster(name,icon,hitPoints,baseDamage,baseArmor,toHit,evasionValue);		//TODO: make sure monster constructors (including copy constructor) match up!
												addedMonster.color=color;
												addedMonster.xp=xp;
												if(branches!=null		//this *should* ensure than no branch input means a monster is available on all branches.
													&&branches[0]==null
													&&dungeon!=null)
													branches=dungeon.allBranches();
						
												//TODO: figure out what to do with XML-inputted spawnchance and available braches.
	        								//addedMonster.spawnChance=spawnChance;
												addedMonster.setAvailableBranches(branches);
												monsters[monsterIndex]=addedMonster;
												monsterIndex++;
	        							
	        								//defaults still necessary?
												//branch=1;
	        			        				color="000000";
	        		        				
	        			        				hitPoints=-1;
	        			        				baseDamage=-1;
	        			        				baseArmor=-1;		//TODO: reset all defaults here. Add more as more defaults are added to the game.
	        			        				toHit=-1;
	        			        				evasionValue=-1;
	        		        		    	
	        			        				xp=-1;
	        		        		    	//defaults still necessary?
	        			        				break;
	        								default:
	        									break;
	        								}
	        							}
	        						}
	        					}
	        				}
	        			}
	        		}
	        	}
			
			return monsters;
			
		}catch(XMLStreamException exc) {
			return null;
	    }
		
	}

	public static void addDungeonMonsters(Dungeon dungeon, Monster[] monsters) {
		for(int i=0;i<Dungeon.BRANCH_COUNT&&dungeon.getBranch(i)!=null;i++){
			addBranchMonsters(dungeon, dungeon.getBranch(i),monsters);
		}
	}
	
	private static void addBranchMonsters(Dungeon dungeon, Branch branch, Monster[] monsters) {
		for(int i=branch.startDepth();i<branch.endDepth();i++){
			
			Level currentLevel=branch.getLevel(i);
			int monsterDepth=currentLevel.monsterDepth();
			for(int j=0;j<monsters.length&&monsters[j]!=null;j++){	//this probably isn't the most efficient way to check which monsters are available in which branches.
				if(monsters[j].availableInBranch(branch)){
					int value=monsters[j].getOverallPower();
					Monster addedMonster=new Monster(monsters[j]);
					addedMonster.inventory=new Inventory();	//TODO: implement monster inventories, but probably not here.
					currentLevel.addAvailableMonster(addedMonster, Math.abs(value-monsterDepth));
				}
			}
		}	
	}

	//default attribute getters below. TODO: put sensible return values in.
	//Consider using monster variance to make these vary more.
/*	private static int getDefaultHitPoints(int minDepth, int maxDepth) {
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
	}*/
	
	private static int getDefaultToHit(int baseDamage) {		//this and evade (both 5) are too low for level 1.
		return 4+baseDamage/4;
	}
	private static int getDefaultEvasionValue(int toHit) {
		return 2+toHit/2;
	}
	
	//xml shortcut methods (based on ItemReader, should maybe be moved to a more general "Reader" class)
	
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

public static Monster[] genericMonsters=allDungeonMonsters(null);	//TODO: make private after testing

}