import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class ArmorReader extends ItemReader{
	
	public final static String SLOT="slot";
	public final static String FAMILY="family";
	public final static String FAMILY_NAME="familyName";
	public final static String ARMOR_VALUE="armorValue";
	public final static String ARMOR_QUALITY="armorQuality";
	public final static String ARTIFACT_ARMOR="artifactArmor";
	
	public static Armor[][] dungeonArmors(Dungeon dungeon){
		try { 
			InputStream weaponInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("ArmorManual.xml");	//TODO: eventually take this as an arg.
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(weaponInput);
			
			Armor[][] armors = new Armor[100][100];	//probably doesn't need to be this big, because this is like 10,000 weapons.
			
			int slotIndex=0;
			int familyIndex=0;
			
			String genericName = "";
    	
	    	String slot="";	//Do these values need to be stored here? (there are no longer defaults)
	    	
	    	int armorValue=-1;
	    	int armorQuality=-1;
			double weight =-1;
	    	Material[] excludedMaterials=new Material[100];
			
			while(reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

	        	if(event.isStartElement()){
	        		String elementName=startElementName(event);
	        		
	        		switch(elementName){
	        		case(ARMORS):
	        			while(readUntil(reader, event,ARMORS)){
	        				event = reader.nextEvent();
	        				if(event.isStartElement()){
	        					if(isSlotName(startElementName(event)))
	        						slot=startElementName(event);
	        					else if(startElementName(event)==FAMILY){
	        						Branch[] branches=null;
	        						if(dungeon!=null)
	        							branches=new Branch[Dungeon.BRANCH_COUNT];
	        						while(readUntil(reader,event,FAMILY)){
	        							event = reader.nextEvent();
	        							if(event.isStartElement()){
	        								switch(startElementName(event)){
	        								case(FAMILY_NAME):
	        									genericName=reader.nextEvent().toString();break;
	        								case(ARMOR_VALUE):
	        									armorValue=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(ARMOR_QUALITY):
	        									armorQuality=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(WEIGHT):
	        									weight=Integer.parseInt(reader.nextEvent().toString());break;
	        								case(EXCLUDED_MATERIAL):
	        									int materialIndex=0;
	        									while(materialIndex<excludedMaterials.length&&excludedMaterials[materialIndex]!=null)
	        										materialIndex++;
	        									excludedMaterials[materialIndex]=Material.getMaterial(reader.nextEvent().toString());
	        									break;
	        								case(BRANCH):
	        									int branchIndex=0;
	        									while(branches!=null&&branchIndex<branches.length&&branches[branchIndex]!=null)
	        										branchIndex++;
	        									if(dungeon!=null)
	        										branches[branchIndex]=dungeon.getBranch(Integer.parseInt(reader.nextEvent().toString()));
	        									break;
	        								}
	        							}
	        							else if(event.isEndElement()){
	        							
	        								switch(endElementName(event)){
	        								case(FAMILY):
	        									Armor addedArmor=new Armor(genericName, slot, armorValue, armorQuality);
	        									addedArmor.setWeight(weight);
	        									addedArmor.setIcon(Armor.STANDARD_ARMOR_ICON);
	        									addedArmor.setExcludedMaterials(excludedMaterials);
            		        					if(branches!=null
            		        					&&branches[0]==null
            		        					&&dungeon!=null)
            		        						branches=dungeon.allBranches();
            		        					addedArmor.setAvailableBranches(branches);
            		        					armors[slotIndex][familyIndex]=addedArmor;
            		        					familyIndex++;
            		        					break;
	        								}
	        							}
	        						}
	        					}
	        				}
	        				else if(event.isEndElement()&&isSlotName(endElementName(event))){
	        					slotIndex++;
        						familyIndex=0;
        					}
	        			}
	        		}
	        	}
			}
			
			return armors;
			
		}catch(XMLStreamException e){
			return null;
		}
		
	}
	
	public static Armor[] artifactArmors(){	//artifact armors are always built off of generic weapons
		try { 
			InputStream armorInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("ArmorManual.xml");	//TODO: eventually take this as an arg.
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(armorInput);
			
			Armor[] artifactArmors = new Armor[500];	 
			int artifactIndex=0;

			String genericName = "";
			String trueName="";
			
	    	String slot="";	//Do these values need to be stored here? (there are no longer defaults)
	    	
	    	int armorValue=-1;
	    	int armorQuality=-1;
			int weight =-1;
			
	    	Material material=null;
	    	
			while(reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

	        	if(event.isStartElement()){
	        		String elementName=startElementName(event);
	        		
	        		switch(elementName){
	        			case(ARTIFACTS):
	        			while(readUntil(reader, event,ARTIFACTS)){
	        				event = reader.nextEvent();
	        				if(event.isStartElement()){       					
	        					if(startElementName(event).equals(ARTIFACT_ARMOR)){						
	        						while(readUntil(reader,event,ARTIFACT_ARMOR)){
	        							event = reader.nextEvent();
	        							if(event.isStartElement()){
	        								switch(startElementName(event)){
	        									case(TRUE_NAME):
	        										trueName=reader.nextEvent().toString();break;
	        									case(SLOT):
	        										slot=reader.nextEvent().toString();break;
	        									case(FAMILY_NAME):
	        										genericName=reader.nextEvent().toString();break;
	        									case(MATERIAL):
	        										String materialName=reader.nextEvent().toString();
	        										material=Material.getMaterial(materialName);
	        										break;
	        									case(ARMOR_VALUE):
	        										armorValue=Integer.parseInt(reader.nextEvent().toString());break;
	        									case(ARMOR_QUALITY):
	        										armorQuality=Integer.parseInt(reader.nextEvent().toString());break;
	        									case(WEIGHT):
	        										weight=Integer.parseInt(reader.nextEvent().toString());break;
	        								}
	        							}
	        							else if(event.isEndElement()){
	        								switch(endElementName(event)){
	        										case(ARTIFACT_ARMOR):
	        										Armor createArmor=createArmor(slot, genericName, trueName, material);
	        										Armor addedArtifactArmor=null;
	        										if(createArmor!=null)
	        											addedArtifactArmor=new Armor(createArmor);
	        										//else
	        										//	System.out.println(slot);
	        										if(armorValue>0)
	        											addedArtifactArmor.setArmorValue(armorValue);	//is this right?
	        										if(armorQuality>0)
	        											addedArtifactArmor.setArmorQuality(armorQuality);
	        										if(weight>0)
	        											addedArtifactArmor.setWeight(weight);
	        										addedArtifactArmor.setIcon(Weapon.STANDARDWEAPONICON);
	        										
	        										artifactArmors[artifactIndex]=addedArtifactArmor;			
	        										artifactIndex++;
	        									
	        										slot="";	//defaults are set here
	        										genericName = "";
	        										trueName="";
	        									
	        										material=null;
	        
	        										armorValue=-1;
	        										armorQuality=-1;
	        										weight =-1;
	        									
	        										break;
	        									}
	        								}
	        							}
	        						}
	        					}
	        				}
	        			}
	        		}
	        	}
			
			return artifactArmors;
			
		} catch(XMLStreamException e){
			return null;
		}
	}
	
	private static boolean isSlotName(String name) {
		if(name.equals(Equipment.WEAPON))	//this check shouldn't be necessary, but is here just in case.
			return false;
		for(int i=0;i<Equipment.EQUIPMENT_SLOTS.length;i++){
			if(name.equals(Equipment.EQUIPMENT_SLOTS[i]))
				return true;
		}
		return false;
	}
	
	//methods for adding armors to dungeon

	public static void addDungeonArmors(Dungeon dungeon, Armor[][] armors) {
		for(int i=0;i<Dungeon.BRANCH_COUNT&&dungeon.getBranch(i)!=null;i++){
			addBranchArmors(dungeon, dungeon.getBranch(i),armors);
		}
	}

	private static void addBranchArmors(Dungeon dungeon, Branch branch, Armor[][] armors) {
		for(int i=branch.startDepth();i<branch.endDepth();i++){
			
			Level currentLevel=branch.getLevel(i);
			int armorDepth=currentLevel.armorDepth();
			for(int j=0;i<armors.length&&armors[j][0]!=null;j++){
				for(int k=0;k<armors.length&&armors[j][k]!=null;k++){
					int value=armors[j][k].getOverallValue();
				//	Material[] materials=Material.suitableMaterials(branch, armorDepth);	//TODO: set armor materials elsewhere
				//	for(int m=0;m<materials.length&&materials[m]!=null;m++){
						if(armors[j][k].availableInBranch(branch)){
							Armor addedArmor=new Armor(armors[j][k]);
							currentLevel.addAvailableItem(addedArmor, Math.abs(value-armorDepth));
						}
					}	
				}
			}	
		}
	
	public static Armor createArmor(String slot, String armorFamily, String name, Material material){	//Do I really want a "name" argument for non-artifacts?
		if(isSlotName(slot)){
			int slotIndex=EquipmentSet.getSlotIndex(slot);
			if(slotIndex>=EquipmentSet.getSlotIndex(Equipment.WEAPON))	//off-by-one check
				slotIndex-=1;
				for(int i=0;i<genericArmors[slotIndex].length&&genericArmors[slotIndex][i]!=null;i++){
					if(armorFamily.equals(genericArmors[slotIndex][i].genericName)){
						Armor createdArmor=genericArmors[slotIndex][i];
						createdArmor.name=name;
						createdArmor.setMaterial(material, false);
						createdArmor.equipped=false;
						return createdArmor;
					}
				}
		}
		return null;
	}
	
	public static Armor createArtifactArmor(String trueName){	//Do I really want a "name" argument for non-artifacts?
		
		if(trueName!=null){
				for(int i=0;i<artifactArmors.length&&artifactArmors[i]!=null;i++){
					if(trueName.equals(artifactArmors[i].toString()))
						return artifactArmors[i];
			}
		}
		return null;
	}
	
	public static void loadArmors(){	//is this necessary?
		genericArmors=dungeonArmors(null);
	}

	private static Armor[][] genericArmors=dungeonArmors(null);
	private static Armor[] artifactArmors=artifactArmors();
}