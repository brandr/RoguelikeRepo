import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class MaterialReader extends ItemReader{
//reads in materials from an xml file.
	public static final String MATERIAL="material";
	public static final String MATERIALS="materials";
	
	public static final String MATERIAL_NAME="materialName";
	
	public static final String POWER_MULTIPLIER="powerMultiplier";
	public static final String QUALITY_MULTIPLIER="qualityMultiplier";
	public static final String WEIGHT_MULTIPLIER="weightMultiplier";
	
	public static final String WEAK_AGAINST="weakAgainst";
	public static final String STRONG_AGAINST="strongAgainst";
	public static final String INVINCIBLE_AGAINST="invincibleAgainst";
	
	public static final String SPAWNABLE="spawnable";
	
	public static Material[] allMaterials(){
		try { 
			InputStream materialInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("MaterialManual.xml");	//TODO: eventually take this as an arg.
			XMLInputFactory  xmlInFact = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInFact.createXMLEventReader(materialInput);
			
			Material[] materials=new Material[Material.MATERIAL_COUNT];
			int materialIndex=0;
			
			boolean spawnable=true;
			
			String materialName="";
			//TODO: consider checking for defaults later and setting to proper values (i.e., null relations becomes empty relations)
			
			while(reader.hasNext()){
				XMLEvent event = reader.nextEvent();
				if(event.isStartElement()){
	        		String elementName=startElementName(event);
	        		switch(elementName){
	        		case(MATERIAL):
	        			
	        			double[] multipliers=new double[3];
	        			String[][] relations=new String[3][Material.MATERIAL_COUNT];	//Material's relationships with other materials
	        			
	        			while(readUntil(reader, event,MATERIAL)){
	        				
	        				event = reader.nextEvent();
							if(event.isStartElement()){
								switch(startElementName(event)){
								case MATERIAL_NAME:
									
									materialName=reader.nextEvent().toString();break;
								case POWER_MULTIPLIER:
									multipliers[0]=Double.parseDouble(reader.nextEvent().toString());break;
								case QUALITY_MULTIPLIER:
									multipliers[1]=Double.parseDouble(reader.nextEvent().toString());break;
								case WEIGHT_MULTIPLIER:
									multipliers[2]=Double.parseDouble(reader.nextEvent().toString());break;
								case WEAK_AGAINST:
									addRelation(relations[0],reader.nextEvent().toString());break;
								case STRONG_AGAINST:
									addRelation(relations[1],reader.nextEvent().toString());break;
								case INVINCIBLE_AGAINST:
									addRelation(relations[2],reader.nextEvent().toString());break;
								case SPAWNABLE:
									if(reader.nextEvent().toString().equals("false"))
										spawnable=false;
										break;
								}
							}
		        		}
	        			materials[materialIndex]=new Material(materialName,multipliers,relations);
	        			materials[materialIndex].setSpawnable(spawnable);
						materialIndex++;
						break;
	        		}
				}
				else if(event.isEndElement()){
					switch(endElementName(event)){
					case(MATERIALS):
						return materials;
					}
				}
			}
			
			return materials;
		}catch(XMLStreamException e){
			
			//return null;
		}
		return null;
	}

	private static void addRelation(String[] relations, String relation) {
		int index=0;
		while(index<relations.length&&relations[index]!=null)
			index++;
		relations[index]=relation;
	}
}
