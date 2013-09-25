import java.util.HashMap;
import java.util.Map;


public class Scroll extends Readable{

	public final static char STANDARD_SCROLL_ICON='?';
	public final static double STANDARD_SCROLL_WEIGHT=0.5;
	public final static int STANDARD_SCROLL_STACK_SIZE=9;

	public final static String BLINKING="blinking"; 				public final static String TELEPORT="teleport"; 
	public final static String GREATER_TELEPORT="greater teleport"; public final static String DISPERSAL="disperal";
	public final static String GREATER_DISPERSAL="greater dispersal";
	
	public final static String CURSE="curse"; 				  		public final static String REMOVE_CURSE="remove curse"; 
	public final static String ENCHANT_ARMOR="enchant armor"; 		public final static String ENCHANT_WEAPON="enchant weapon"; 
	public final static String BRAND_ARMOR="brand armor"; 	  		public final static String BRAND_WEAPON="brand weapon";
	
	public final static String MAPPING="mapping"; 			  		public final static String AMNESIA="amnesia";
	public final static String IDENTIFY="identify";
	
	public final static String ACQUIREMENT="acquirement";			public final static String IMMOLATION="immolation";
	public final static String SUMMONING="summoning"; 				public final static String EVOCATION="evocation";
	
	public final static String[] SCROLL_TYPES=
		{BLINKING,TELEPORT,GREATER_TELEPORT,DISPERSAL,GREATER_DISPERSAL				//5
		,CURSE,REMOVE_CURSE,ENCHANT_ARMOR,ENCHANT_WEAPON,BRAND_ARMOR,BRAND_WEAPON	//6
		,MAPPING,AMNESIA,IDENTIFY													//3
		,ACQUIREMENT,IMMOLATION,SUMMONING,EVOCATION};									//4
	
	public final static String[] UNIDENTIFIED_SCROLL_NAMES=
		{"ABAI","DAKARI","DING DING","DRAKENKRACHT","FASTINLOT","FINDOLVOG","FUGGETABOWDIT",	//6
		"GEEF MIJ","HIER RAUS","HUS FO SPO", "JAFFA","KABOOM",									//6
		"KANGAROO","POSISIONERINGSTELSELS","SHOOBEDOO", "SUPERI TIE","TX O OXII","WEENUS"};		//6
	
	//teleportation effects
	public final static Effect BLINK_EFFECT=new Effect("Blink",Effect.BLINK,0,0);	
	public final static Effect TELEPORT_EFFECT=new Effect("Teleport",Effect.TELEPORT,0,0);	
	public final static Effect LEVEL_TELEPORT_EFFECT=new Effect("Level Teleport",Effect.LEVEL_TELEPORT,0,0);
	
	//knowledge effects
	public final static Effect IDENTIFY_EFFECT=new Effect("Identify",Effect.IDENTIFY,0,0);
	public final static Effect MAPPING_EFFECT=new Effect("Mapping",Effect.MAPPING,0,0);
	public final static Effect AMNESIA_EFFECT=new Effect("Amnesia",Effect.AMNESIA,0,0);
	
	//miscellaneous, I guess.
	public static final Effect IMMOLATION_EFFECT = new Effect("Immolation",Effect.IMMOLATION,5,0);
	
	public final static Effect CURSE_EFFECT=null;			//TODO
	public final static Effect REMOVE_CURSE_EFFECT=null;	//TODO
	public final static Effect ENCHANT_EFFECT=null;		//TODO
	//branding?

	//amnesia? identify? acquirment? immolation? summoning? evocation? greater teleport?
	
	//public final static Effect[] SCROLL_EFFECTS=		//NOTE: some of these are used for multiple scrolls, so there need not be 18 of them, nor does the order matter.
	//	{TELEPORT_EFFECT,CURSE_EFFECT,REMOVE_CURSE_EFFECT,ENCHANT_EFFECT,MAPPING_EFFECT};

	

/*TYPES Of SCROLLS LEFT TO MAKE: (9)

NEXT: cursing or enchanting

	affect Items with stuff that hasn't been implemented yet (6)

-cursing (curses random item)
-remove curse

-enchant armor
-enchant weapon

-brand armor
-brand weapon

	misc. (3)
-acquirement
	*need the general structure for really good items of each available type.
	*this is a crawl scroll, so check the wiki for ideas.
-summoning
	*in crawl, summons allied monsters appropriate to current floor.
	*this will be a good context for testing allied monster AI.
-evocation
	*I have no idea what this means and it isn't a crawl scroll. have to wait for nick.
	 */
	
	public Scroll(String scrollType, String scrollText, int count){
		this.scrollType=scrollType;
		this.scrollText=scrollText;
		setStackSize(STANDARD_SCROLL_STACK_SIZE);
		setAmount(count);
		
		setIcon(STANDARD_SCROLL_ICON);
	}
	
	public Scroll(Scroll toCopy) {
		scrollType=toCopy.scrollType;
		scrollText=toCopy.scrollText;
		setWeight(toCopy.getSingleWeight());
		setStackSize(STANDARD_SCROLL_STACK_SIZE);
		setAmount(toCopy.getAmount());
		
		identified=toCopy.identified;
		setIcon(STANDARD_SCROLL_ICON);
	}
	
	@Override
	public String descriptiveName(){
		return "scroll of "+scrollType;
	}
	
	@Override
	public String trueName(){
		return "scroll of "+scrollType;
	}
	
	@Override
	public String genericName(){
		return "scroll titled "+scrollText;
	}
	
	@Override
	public void initialize(Level level) {
		// TODO: set amount and possibly other attributes here.
	}
	
	public static void setAllScrollEffects(){
		if(scrollTypeToEffectMap==null){
			scrollTypeToEffectMap=new HashMap<String,Effect>();
			scrollTypeToTargetingStyleMap=new HashMap<String,Targeting>();
			
			//teleportation scrolls
			scrollTypeToEffectMap.put(TELEPORT, TELEPORT_EFFECT);
			scrollTypeToTargetingStyleMap.put(TELEPORT, new Targeting(Targeting.SELF));
			
			scrollTypeToEffectMap.put(BLINKING, BLINK_EFFECT);
			scrollTypeToTargetingStyleMap.put(BLINKING, new Targeting(Targeting.SELF));
			
			scrollTypeToEffectMap.put(DISPERSAL, BLINK_EFFECT);
			scrollTypeToTargetingStyleMap.put(DISPERSAL, new Targeting(Targeting.FIELD_OF_VIEW));
			
			scrollTypeToEffectMap.put(GREATER_DISPERSAL, TELEPORT_EFFECT);
			scrollTypeToTargetingStyleMap.put(GREATER_DISPERSAL, new Targeting(Targeting.FIELD_OF_VIEW));
			
			scrollTypeToEffectMap.put(GREATER_TELEPORT, LEVEL_TELEPORT_EFFECT);
			scrollTypeToTargetingStyleMap.put(GREATER_TELEPORT, new Targeting(Targeting.SELF));
			
			//knowledge scrolls
			scrollTypeToEffectMap.put(IDENTIFY, IDENTIFY_EFFECT);
			scrollTypeToTargetingStyleMap.put(IDENTIFY, new Targeting(Targeting.ITEM_CHOOSE,Targeting.UNIDENTIFIED));
			
			scrollTypeToEffectMap.put(MAPPING, MAPPING_EFFECT);
			scrollTypeToTargetingStyleMap.put(MAPPING, new Targeting(Targeting.SELF));
			
			scrollTypeToEffectMap.put(AMNESIA, AMNESIA_EFFECT);
			scrollTypeToTargetingStyleMap.put(AMNESIA, new Targeting(Targeting.SELF));
			
			scrollTypeToEffectMap.put(IMMOLATION, IMMOLATION_EFFECT);
			scrollTypeToTargetingStyleMap.put(IMMOLATION, new Targeting(Targeting.AREA_EXCLUDE_SELF,Targeting.BALL,4));	
		}
	}

	@Override
	public void use(Monster target) {
		getEffect().takeEffect(target);
	}
	
	public void use(Monster user, Item target) {
		getEffect().takeEffect(user,target);
	}
	
	public static Effect getScrollEffect(String scrollType){
		return scrollTypeToEffectMap.get(scrollType);
	}

	@Override
	public boolean stackEquivalent(Item otherItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOverallValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Effect getEffect(){
		if(scrollTypeToEffectMap==null)
			setAllScrollEffects();
		return scrollTypeToEffectMap.get(scrollType);
	}
	
	public String getScrollType() {		//what the scroll says afeter it is identified
		return scrollType;
	}

	public void setScrollType(String scrollType) {
		this.scrollType = scrollType;
	}

	public String getScrollText() {		//what the scroll says before it is identified
		return scrollText;
	}

	public void setScrollText(String scrollText) {
		this.scrollText = scrollText;
	}

	public Targeting getTargeting() {	//targeting (how/what the scroll targets)
		if(scrollTypeToEffectMap==null)
			setAllScrollEffects();
		return scrollTypeToTargetingStyleMap.get(scrollType);
	}

	public boolean hasPrompt() {	//returns true if the scroll prompts the player to make a decision.
		switch(scrollType){
		case(GREATER_TELEPORT):
			return true;
		case(IDENTIFY):
			return true;
		default:
			return false;
		}
	}
	
	boolean hasGraphics() {	//return true if the scroll's effect has visual graphics
		switch(scrollType){
		case(IMMOLATION):
			return true;
		default:
			return false;
		}
	}
	
	private static Map<String,Effect> scrollTypeToEffectMap = null;
	private static Map<String,Targeting> scrollTypeToTargetingStyleMap = null;
	private String scrollType;
	private String scrollText;
}
