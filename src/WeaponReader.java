import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class WeaponReader extends ItemReader {
	
	public final static String FAMILY="family";
	public final static String FAMILY_NAME="familyName";
	public final static String POWER="power";
	public final static String THROWN_DAMAGE="thrownDamage";
	public final static String QUALITY="quality";
	public final static String VARIANCE="variance";
	public final static String HANDS="hands";
	public final static String STACK_SIZE="stackSize";
	
	public final static String ARTIFACT_WEAPON="artifactWeapon";
	public final static String WEAPON_CATEGORY="weaponCategory";
		//could add an "ArtifactWeapons()" Weapon array, which would be useful for map editor.
public static Weapon[][] dungeonWeapons(Dungeon dungeon){		//add monsters to the appropriate level in the dungeon
		//NOTE: do I need the dungeon argument?
	
		//NOTE: the "categories" tag is not being used yet. Keep this in mind when setting up artifacts.
		try { 
			InputStream weaponInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("WeaponManual.xml");	//TODO: eventually take this as an arg.
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(weaponInput);
			
			Weapon[][] weapons = new Weapon[100][100];	//probably doesn't need to be this big, because this is like 10,000 weapons.
			
			int categoryIndex=0;
			int familyIndex=0;
			
			String genericName = "";
	    	
	    	int hands = 1;
	    	String weaponCategory="";	//Do these values need to be stored here? (there are no longer defaults)
	    	
	    	int power=-1;
	    	int thrownDamage=-1;
	    	int quality=-1;
	    	double variance=-1;	
			double weight =-1;
			Material[] excludedMaterials=new Material[100];
			
			int stackSize=1;
	    	
			while(reader.hasNext()) {	
				XMLEvent event = reader.nextEvent();

	        	if(event.isStartElement()){
	        		String elementName=startElementName(event);
	        		
	        		switch(elementName){
	        			case(WEAPONS):
	        			while(readUntil(reader, event,WEAPONS)){
	        				event = reader.nextEvent();
	        				if(event.isStartElement()){
	        					
	        					if(isWeaponCategory(startElementName(event)))
	        						weaponCategory=startElementName(event);
	        					
	        					else if(startElementName(event).equals(FAMILY)){
	        						
	        						Branch[] branches=null;
	        						if(dungeon!=null)
	        							branches=new Branch[Dungeon.BRANCH_COUNT];
	        						
	        						while(readUntil(reader,event,FAMILY)){
	        							event = reader.nextEvent();
	        							if(event.isStartElement()){
	        								switch(startElementName(event)){
	        								case(FAMILY_NAME):
	        									genericName=reader.nextEvent().toString();break;
	        								case(POWER):
	        									power=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(THROWN_DAMAGE):
	        									thrownDamage=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(QUALITY):
	        									quality=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(VARIANCE):
	        									variance=Double.parseDouble(reader.nextEvent().toString());break;
	        								case(HANDS):
	        									hands=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(WEIGHT):
	        									weight=Double.parseDouble(reader.nextEvent().toString());break;
	        								case(EXCLUDED_MATERIAL):
	        									int materialIndex=0;
	        									while(materialIndex<excludedMaterials.length&&excludedMaterials[materialIndex]!=null)
	        										materialIndex++;
	        									excludedMaterials[materialIndex]=Material.getMaterial(reader.nextEvent().toString());
	        									break;
	        								case(BRANCH):
	        									int index=0;
	        									while(branches!=null&&index<branches.length&&branches[index]!=null)
	        										index++;
	        									//need error handling for improper branch indeces
	        									if(dungeon!=null)
	        										branches[index]=dungeon.getBranch(Integer.parseInt(reader.nextEvent().toString()));
	        									break;
	        								case(STACK_SIZE):
	        									stackSize=Integer.parseInt(reader.nextEvent().toString());break;
	        								}
	        								
	        							}
	        							else if(event.isEndElement()){
	        								switch(endElementName(event)){
	        								case(FAMILY):
	        									Weapon addedWeapon=new Weapon(genericName, genericName, power, weaponCategory);	//having genericName twice is intentional.
            		        				
            		        					addedWeapon.twoHanded=(hands==2);
            		        					addedWeapon.setBaseThrownDamage(thrownDamage);
            		        					addedWeapon.quality=quality;
            		        					addedWeapon.variance=variance;
            		        					addedWeapon.setWeight(weight);
            		        					addedWeapon.setStackSize(stackSize);
            		        					
            		        					stackSize=1; //necessary here or not? test with it removed
            		        					
            		        					addedWeapon.setIcon(Weapon.STANDARDWEAPONICON);
            		        					addedWeapon.setExcludedMaterials(excludedMaterials);
            		        					if(branches!=null
            		        					 &&branches[0]==null
            		        					 &&dungeon!=null)	//TODO: why is the second check the way it is? Try changing it to != and test weapon spawning.
            		        						branches=dungeon.allBranches();
            		        					addedWeapon.setAvailableBranches(branches);
            		        					weapons[categoryIndex][familyIndex]=addedWeapon;
            		        					familyIndex++;
            		        					break;
	        									}
	        								}
	        							}
	        						}
	        					} else if(event.isEndElement()&&isWeaponCategory(endElementName(event))){
	        						categoryIndex++;
	        						familyIndex=0;
	        					}
	        				}
	        			}
	        		}
	        	}
			
			return weapons;
			
		} catch(XMLStreamException e){
			return null;
		}
}

public static Weapon[] artifactWeapons(){	//artifact weapons are always built off of generic weapons
	try { 
		InputStream weaponInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("WeaponManual.xml");	//TODO: eventually take this as an arg.
		XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
		XMLEventReader reader = xmlInFact.createXMLEventReader(weaponInput);
		
		Weapon[] artifactWeapons = new Weapon[500];	 
		int artifactIndex=0;

		String weaponCategory="";	//Do these values need to be stored here? (there are no longer defaults)
		String genericName = "";
    	String trueName="";
		
    	Material material=null;
    	
    	//int hands = 1;	//put hands back if we want 1-handed artifact greatswords or 2-handed hand axes or something.
    	int power=-1;
    	int thrownDamage=-1;	//TODO
    	int quality=-1;
    	double variance=-1;	
		int weight =-1;
    	
		while(reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

        	if(event.isStartElement()){
        		String elementName=startElementName(event);
        		
        		switch(elementName){
        			case(ARTIFACTS):
        			while(readUntil(reader, event,ARTIFACTS)){
        				event = reader.nextEvent();
        				if(event.isStartElement()){       					
        					if(startElementName(event)==ARTIFACT_WEAPON){						
        						while(readUntil(reader,event,ARTIFACT_WEAPON)){
        							event = reader.nextEvent();
        							if(event.isStartElement()){
        								switch(startElementName(event)){
        									case(TRUE_NAME):
        										trueName=reader.nextEvent().toString();break;
        									case(WEAPON_CATEGORY):
        										weaponCategory=reader.nextEvent().toString();break;
        									case(FAMILY_NAME):
        										genericName=reader.nextEvent().toString();break;
        									case(MATERIAL):
        										String materialName=reader.nextEvent().toString();
        										material=Material.getMaterial(materialName);
        										break;
        									case(POWER):
        										power=Integer.parseInt(reader.nextEvent().toString());break;
        									case(THROWN_DAMAGE):
        										thrownDamage=Integer.parseInt(reader.nextEvent().toString());break;
        									case(QUALITY):
        										quality=Integer.parseInt(reader.nextEvent().toString());break;
        									case(VARIANCE):
        										variance=Double.parseDouble(reader.nextEvent().toString());break;
        									//case(HANDS):
        									//	hands=Integer.parseInt(reader.nextEvent().toString());break;
        									case(WEIGHT):
        										weight=Integer.parseInt(reader.nextEvent().toString());break;
        								}
        							}
        							else if(event.isEndElement()){
        								switch(endElementName(event)){
        										case(ARTIFACT_WEAPON):
        										Weapon addedArtifactWeapon=new Weapon(createWeapon(weaponCategory, genericName, trueName, material,1));
        										//NOTE: should quality, variance, power, and weight be modifiers rather than set values? (seems too easy to accidentally make a weaker weapon.)
        										if(power>0)
        											addedArtifactWeapon.setPower(power);	//is this right?
        										if(thrownDamage>0)
        											addedArtifactWeapon.setBaseThrownDamage(thrownDamage);
        										if(quality>0)
        											addedArtifactWeapon.quality=quality;
        										if(variance>0)
        											addedArtifactWeapon.variance=variance;
        										if(weight>0)
        											addedArtifactWeapon.setWeight(weight);
        										addedArtifactWeapon.setIcon(Weapon.STANDARDWEAPONICON);
        										
        										artifactWeapons[artifactIndex]=addedArtifactWeapon;			
        										artifactIndex++;
        									
        										weaponCategory="";	//defaults are set here
        										genericName = "";
        										trueName="";
        									
        										material=null;
        							    	
        										//hands = 1;	//put hands back if we want 1-handed artifact greatswords or 2-handed hand axes or something.
        										power=-1;
        										quality=-1;
        										variance=-1;	
        										weight =-1;
        									
        										break;
        									}
        								}
        							}
        						}
        					}
        				//something important might go here
        				}
        			}
        		}
        	}
		
		return artifactWeapons;
		
	} catch(XMLStreamException e){
		return null;
	}
}

private static boolean isWeaponCategory(String name) {
	for(int i=0;i<Weapon.ALL_WEAPONS.length;i++){
		if(name.equals(Weapon.ALL_WEAPONS[i]))
			return true;
	}
	return false;
}

private static int weaponCategoryIndex(String name){
	for(int i=0;i<Weapon.ALL_WEAPONS.length;i++){
		if(name.equals(Weapon.ALL_WEAPONS[i]))
			return i;
	}
	return -1;
}

//methods for adding weapons to dungeon

public static void addDungeonWeapons(Dungeon dungeon, Weapon[][] weapons) {
	for(int i=0;i<Dungeon.BRANCH_COUNT&&dungeon.getBranch(i)!=null;i++){
		addBranchWeapons(dungeon, dungeon.getBranch(i),weapons);
	}
}

private static void addBranchWeapons(Dungeon dungeon, Branch branch, Weapon[][] weapons) {
	
	for(int i=branch.startDepth();i<branch.endDepth();i++){
		Level currentLevel=branch.getLevel(i);
		int weaponDepth=currentLevel.weaponDepth();
		for(int j=0;j<weapons.length&&weapons[j][0]!=null;j++){	//changed an 'i' to 'j'. make sure it's right.
			for(int k=0;k<weapons[j].length&&weapons[j][k]!=null;k++){
				int value=weapons[j][k].getOverallValue();
	//			Material[] materials=Material.suitableMaterials(branch, weaponDepth);	//TODO: set weapon material/amount only on its placement in a level.
				//for(int m=0;m<materials.length&&materials[m]!=null;m++){
					Weapon addedWeapon=new Weapon(weapons[j][k]);
					if(addedWeapon.availableInBranch(branch)){
				//		addedWeapon.setMaterial(new Material(materials[m]),true);
				//		if(addedWeapon.stackable())
				//			addedWeapon.setAmount(addedWeapon.randomAmount());
						currentLevel.addAvailableItem(addedWeapon, Math.abs(value-weaponDepth));
						
				//	}
				}	
			}	
		}
	}	
}

public static Weapon createWeapon(String weaponCategory, String weaponFamily, String name, Material material, int amount){	//Do I really want a "name" argument for non-artifacts?
	if(isWeaponCategory(weaponCategory)){
		int categoryIndex=weaponCategoryIndex(weaponCategory);
			for(int i=0;i<genericWeapons[categoryIndex].length&&genericWeapons[categoryIndex][i]!=null;i++){
				if(weaponFamily.equals(genericWeapons[categoryIndex][i].genericName)){
					Weapon createdWeapon=genericWeapons[categoryIndex][i];
					createdWeapon.name=name;
					createdWeapon.setMaterial(material,false);
					if(createdWeapon.getStackSize()>1)
						createdWeapon.setAmount(amount);
					return createdWeapon;
				}
			}
	}
	return null;
}

public static Weapon createArtifactWeapon(String trueName){	//Do I really want a "name" argument for non-artifacts?
	
	if(trueName!=null){
		
			for(int i=0;i<artifactWeapons.length&&artifactWeapons[i]!=null;i++){
				if(trueName.equals(artifactWeapons[i].toString()))
					return artifactWeapons[i];
		}
	}
	return null;
}

public static void loadWeapons(){
	genericWeapons=dungeonWeapons(null);
}

private static Weapon[][] genericWeapons=dungeonWeapons(null);
private static Weapon[] artifactWeapons=artifactWeapons();
}
