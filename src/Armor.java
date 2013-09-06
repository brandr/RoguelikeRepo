import java.util.Random;

public class Armor extends Equipment{

	public static final char STANDARD_ARMOR_ICON='[';
	
	public Armor(){
		name=null;
		setIcon(STANDARD_ARMOR_ICON);
	}
	
	public Armor(String name, String type, int armorValue, int armorQuality){	//TODO: replace evasionpenalty with quality and make evasionpenalty a getter.
		this.name=name;
		this.genericName=name;
		setArmorValue(armorValue);
		setArmorQuality(armorQuality);
		setArmorType(type);
		setIcon(STANDARD_ARMOR_ICON);
		
		equipped=false;
	}

	public Armor(Armor toCopy) {	//TODO: add attributes as necessary.
		equipped=toCopy.equipped;
		
		name=toCopy.name;
		genericName=toCopy.genericName;
		armorType=toCopy.armorType;
		identified=toCopy.identified;
		setAvailableBranches(toCopy.getAvailableBranches());
		setIcon(toCopy.getIcon());
		setWeight(toCopy.getWeight());
		
		armorValue=toCopy.armorValue;
		armorQuality=toCopy.armorQuality;
		
		material=toCopy.getMaterial();
		setExcludedMaterials(toCopy.getExcludedMaterials());
	}
	
	@Override
	public void initialize(Level level) {
		Branch branch=level.getBranch();
		int armorDepth=level.armorDepth();
		
		Material[] materials=Material.suitableMaterials(this, branch, armorDepth);	//the smaller this array is, the less variable materials will be.
		if(materials==null)
			return;
		int materialIndex=dice.nextInt(materials.length);
		while(materials[materialIndex]==null)
			materialIndex=dice.nextInt(materials.length);
		setMaterial(materials[materialIndex],true);
	}
	
	public void setArmorType(String armorType) {	//determines the armor index based on a string instead. May need to become more general if more armor slots are added.
		this.armorType=armorType;
	}
	
	@Override
	public int getPower(){
		return armorValue;
	}
	
	public int getArmorValue(){
		return armorValue;
	}
	
	public int getArmorQuality() {
		return armorQuality;
	}
	
	public int evasionPenalty(){		//TODO: make this a function of weight and quality.
		return Math.max(0,(int)((double)getWeight()/8.0-armorQuality));
	}
	
	public void setArmorValue(int armorValue){
		this.armorValue=armorValue;
	}
	
	public void setArmorQuality(int armorQuality){
		this.armorQuality=armorQuality;
	}

	public String getArmorType(){
		return armorType;
	}
	
	public int armorRoll() {
		return dice.nextInt(armorValue+1);
	}
	
	public double coverage(){	//represents how much of the wearer's body is covered by this piece of armor
		switch(armorType){
		case(HEAD):
			return 0.25;
		case(CHEST):
			return 0.3;
		case(CLOAK):
			return 0.15;
		case(LEGS):
			return 0.2;
		case(FEET):
			return 0.1;
		default:
			return 0;
		}
	}
	
	public int getOverallValue(){
		double value=50.0*((double)getArmorValue()/10.0)+15.0*((double)getArmorQuality()/10.0)-10.0;
		return (int)value;
	}
	
	public void setMaterial(Material material, boolean adjustStats) {	//formulas are temporary
		this.material=material;
		if(adjustStats){
			setArmorValue(material.adjustedPower(getArmorValue()));
			setArmorQuality(material.adjustedQuality(getArmorQuality()));
			setWeight(material.adjustedWeight(getWeight()));	
			}
		}
	
	public boolean shieldBlockAttempt(Material material, int damage) {
		if(armorType.equals(Equipment.OFF_HAND)
		&& material!=null
		&& !material.relationship(getMaterial()).equals(Material.INVINCIBLE)){		//if this is not a shield or it the opposing material ignores this shield's material, it is impossible to block. 
			double multiplier=10.0;
			switch(getMaterial().relationship(material)){
				case(Material.INVINCIBLE):
					return true;
				case(Material.WEAK):	//shield is weak against opposing damage.
					multiplier=12.0;
					break;
				case(Material.STRONG):	//shield is strong against opposing damage.
					multiplier=8.0;
					break;
				default:
					break;
			}
			int average=(int)((getArmorValue()+damage)/2.0);
			int difference=getArmorValue()-damage;
			int roll=dice.nextInt((int)(average*multiplier)+average+difference);
			if(roll>=average*multiplier)
				return true;
				
		}
		return false;
	}

	private Random dice=new Random();
	
	private String armorType=null;	//head,chest, etc.
	private int armorValue=0;
	private int armorQuality=0;
}
