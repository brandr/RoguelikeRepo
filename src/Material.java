

public class Material {

	public final static int MATERIAL_COUNT=10;
	
	public final static String WEAK="weak";
	public final static String STRONG="strong";
	public final static String INVINCIBLE="invincible";
	
	public final static String NONE="none";	//means that a material affects another normally
	public static Material[] allMaterials=MaterialReader.allMaterials();	//TODO: decide whether this should include "flesh"
	public static Material[] spawnableMaterials=spawnableMaterials();
	public final static Material BONE=getMaterial("bone");
	public final static Material FLESH=getMaterial("flesh");

	public Material(String name, double[] multipliers, String[][] relatedMaterials){
		this.name=name;
		setMultipliers(multipliers);
		setRelationships(relatedMaterials);
	}

	public Material(Material copy) {
		name=copy.name;
		setMultipliers(copy.getMultipliers());
		setRelationships(copy.getRelationships());
	}

	public String toString(){
		return name;
	}
	
	public String relationship(Material other){	//NOTE: this is probably not going to be useful, since there will likely be multiple possiblle relations.
		
		if(weakAgainst(other))
			return WEAK;
		else if(strongAgainst(other))
			return STRONG;
		else if(invincibleAgainst(other))
			return INVINCIBLE;
		return NONE;
	}
	
	public boolean weakAgainst(Material other){
		if(other==null)
			return false;
		for(int i=0;i<weakAgainst.length&&weakAgainst[i]!=null;i++){
			if(other.toString().equals(weakAgainst[i]))	//is this the right equality check?
				return true;
		}
		return false;
	}
	
	public boolean strongAgainst(Material other){
		if(other==null)
			return false;
		for(int i=0;i<strongAgainst.length&&strongAgainst[i]!=null;i++){
			if(other.toString().equals(strongAgainst[i]))	//is this the right equality check?
				return true;
		}
		return false;
	}
	
	public boolean invincibleAgainst(Material other){
		if(other==null)
			return false;
		for(int i=0;i<invincibleAgainst.length&&invincibleAgainst[i]!=null;i++){
			if(other.toString().equals(invincibleAgainst[i]))	//is this the right equality check?
				return true;
		}
		return false;
	}
	
	public double[] getMultipliers() {
		double[] multipliers={powerMultiplier,qualityMultiplier,weightMultiplier};
		return multipliers;
	}
	
	private String[][] getRelationships(){
		String[][] relationships={weakAgainst,strongAgainst,invincibleAgainst};
		return relationships;
	}
	
	public void setMultipliers(double[] multipliers){
		powerMultiplier=multipliers[0];
		qualityMultiplier=multipliers[1];
		weightMultiplier=multipliers[2];
	}
	
	public double breakRate()
	{
		return Math.pow(Math.E, -1.609*qualityMultiplier);
	}
	
	private void setRelationships(String[][] relatedMaterials) {
		weakAgainst=relatedMaterials[0];
		strongAgainst=relatedMaterials[1];
		invincibleAgainst=relatedMaterials[2];
	}
	
	public int adjustedPower(int initialPower){
		return (int)((initialPower)*powerMultiplier);
	}
	public int adjustedQuality(int initialQuality){
		return (int)((double)(initialQuality)*qualityMultiplier);
	}
	
	public double adjustedWeight(double initialWeight){
		return (initialWeight)*weightMultiplier;
	}
	
	//getters for list of all materials
	
	  public static Material getMaterial(int index){
		   if(index>=0&&index<allMaterials.length)
			   return allMaterials[index];
		   return null;
	   }
	  
	  public static Material getMaterial(String name){
		  if(allMaterials==null)	//necessary to avoid an error. (consider moving)..
			  allMaterials=MaterialReader.allMaterials();
		  for(int i=0;i<allMaterials.length&&allMaterials[i]!=null;i++){
			  if(name.equals(allMaterials[i].toString()))
				  return allMaterials[i];
		  }
		  return null;
	  }
	
	  //special getter methods (think of better name for method group)
	  
	  public static Material[] suitableMaterials(Item item, Branch branch, int depth){
		  
		  Material[] excludedMaterials=item.getExcludedMaterials();
		  Material[] availableMaterials=filteredMaterials(branch.availableMaterials,excludedMaterials);
		  
		  if(availableMaterials[0]==null)	//some branches don't have materials because they don't have items, I guess.
			  return null;
		  Material[] materials=new Material[availableMaterials.length];
		  materials[0]=bestMaterial(availableMaterials,depth);
		  int closest=Math.abs(materials[0].getOverallValue()-depth);
		  int index=1;
		  for(int i=1;i<materials.length&&availableMaterials[i]!=null;i++){
			  int offset=Math.abs(availableMaterials[i].getOverallValue()-depth);
			  if(offset<closest+10){		//this check is subject to change.
				  materials[index]=availableMaterials[i];
				  index++;
			  }
		  }
		  return materials;
	  }
	  
	private static Material[] filteredMaterials(Material[] availableMaterials,Material[] excludedMaterials) {	//filters the excluded materials from the available ones
		Material[] filteredMaterials=new Material[availableMaterials.length];
		int index=0;
		for(int i=0;i<availableMaterials.length&&availableMaterials[i]!=null;i++){
			boolean allowed=true;
			Material nextMaterial=availableMaterials[i];
			for(int j=0;j<excludedMaterials.length&&excludedMaterials[j]!=null;j++){
				if(excludedMaterials[j].name.equals(nextMaterial.name))
					allowed=false;
			}
			if(allowed){
				filteredMaterials[index]=nextMaterial;
				index++;
			}
		}
		return filteredMaterials;
	}

	public static Material bestMaterial(Material[] materials, int depth){	//the best material of these for the depth
		  if(materials[0]==null)
			  return null;
		  Material bestMaterial=materials[0];
		  for(int i=1;i<materials.length&&materials[i]!=null;i++){
			  int currentClosest=Math.abs(bestMaterial.getOverallValue()-depth);
			  int potentialClosest=Math.abs(materials[i].getOverallValue()-depth);  
			  if(potentialClosest<currentClosest)
				  bestMaterial=materials[i];
		  }	  
		  return bestMaterial;
	  }
	  
	  //spawnable methods
	  
	  private static Material[] spawnableMaterials() {
		  Material[] materials=new Material[100];
		  int index=0;
			for(int i=0;i<allMaterials.length&&allMaterials[i]!=null;i++){
				if(allMaterials[i].spawnable()){
					materials[index]=allMaterials[i];
					index++;
				}
			}
			return materials;
		}
	  
	public void setSpawnable(boolean spawnable) {	
		this.spawnable=spawnable;
	}
	
	public boolean spawnable() {	
		return spawnable;
	}
	  
	public int getOverallValue(){		//a value related to depth. (should therefore range between about 1 and 50.)
		return (int)(Math.pow(powerMultiplier*5.0,2.0)+qualityMultiplier*5.0-Math.pow(weightMultiplier*25.0,0.5));	//TODO: adjust as necessary.
	}
	  
	private String name;
	private double powerMultiplier=1.0;
	private double qualityMultiplier=1.0;
	private double weightMultiplier=1.0;	//the weight of an item made of a material will be multiplied by this value
	
	private String[] weakAgainst=new String[MATERIAL_COUNT];
	private String[] strongAgainst=new String[MATERIAL_COUNT];
	private String[] invincibleAgainst=new String[MATERIAL_COUNT];
	
	private boolean spawnable=true;//determines whether an item can spawn with this material
	
}
