
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//TODO: player initializes all spells and can only access/see ones with level>0. 
public class Spell {

	public static final Spell[] all_spells = {new Spell("Lightning", Skill.EVOCATIONS, "Cone", 5, 0), 
												new Spell("Fire", Skill.EVOCATIONS, "Ball", 5, 0),
												new Spell("Healing", Skill.EVOCATIONS, "Self", 5, 0), 
												new Spell("Teleport", Skill.EVOCATIONS, "Self", 5, 0)};

	
	
	public Spell(){
		//TODO
	}
	
	/*public Spell(String name, String school){
		this.name=name;
		this.school=school;
	}*/
	public Spell(String name, String school, String castStyle, int MPCost, int poison){
		this.name=name;
		if(isValidSchool(school))
			this.school=school;
		else{
			this.school = null;
		}
		this.castStyle=castStyle;
		setMPCost(MPCost);
		if(poison > 0){this.poison_missle = new Effect("Poison", Effect.DAMAGE, 5, 3);}//Set damage that we use for debugging
	}
	
	public static void setAllSpells()
	{
		int iterator;
		addSchools();
		for(iterator = 0; iterator < all_spells.length; iterator++)
		{
			spell_map.put(all_spells[iterator].name, all_spells[iterator]);
			school_to_spell_map.get(all_spells[iterator].school).add(all_spells[iterator].name);
		}
		return;
	}
	
	public static String[] getSchools()
	{
		return Skill.MAGIC_SKILLS;
	}
	public static void addSchools()
	{
		String[] schools = getSchools();
		int iterator;
		for(iterator = 0; iterator < schools.length; iterator++)
		{
			LinkedList<String> emptyList = new LinkedList<String>();
			school_to_spell_map.put(schools[iterator], emptyList);
		}
	}
	
	public Boolean isValidSchool(String school)
	{
		int iterator;
		String[] schools = getSchools();
		for(iterator = 0; iterator < schools.length; iterator++)
		{
			if(schools[iterator]==school)
			{
				return true;
			}
		}
		return false;
	}
	
	public static Spell getSpell(String spell)
	{
		return spell_map.get(spell);
	}

	public String toString(){
		return name;
	}
	
	public String school(){
		return school;
	}
	
	public static String[] magicSchools(){
		return Skill.MAGIC_SKILLS;
	}
	
	public char getIcon() {
		// TODO: decide other important information about spell icon. (maybe different icons?) Im drunk sorry
		return '*';
	}
	
	public int getMPCost(){
		return MPCost;
	}
	
	public void setMPCost(int cost){
		if(cost>0)
			MPCost=cost;
	}
	
	public void collide(Monster caster, Monster target) {		//a spell (which can only be a missile at this point) collides with a monster.
		int spellDamage = missileDamage(caster);	//TODO: add more complexities for spell damage somewhere (should be a getter based on more general information)
		target.takeDamage(spellDamage, caster,null);	//TODO: separate magic damage from physical. IDEA: have two separate takeDamage() methods OR a damage object.
		poison_missle.takeEffect(target);										
		//TODO: in the case of a damaging spell, damage should be a getter based on spell information.	
	}
	
	private int missileDamage(Monster caster){
		switch(name){
		case("Magic Missile"):	//TODO: later, consider replacing this with xml reading or final string arrays. Also, decide how it will be different if the caster is not the player.
			int damage=spellLevel*3;
			if(caster.getClass()==Player.class){
				Skill skill=((Player)caster).spellSkill(this);
				damage+=skill.skillLevel*2;
			}
			System.out.println(damage);
			return damage;
		case("Poison Missile"):
			return 5;
		default:
			return 0;
		}
	}
	
	public int spellLevel=1;
	private int MPCost=0;
	private String name="";
	private String school="";		//dominations, abjurations, etc.
	public String castStyle="";	//missile, self-targeting, etc.
	private static Map<String, LinkedList<String>> school_to_spell_map = new HashMap<String, LinkedList<String>>(); 
	private static Map<String, Spell> spell_map = new HashMap<String, Spell>();
	private Effect poison_missle = null;
	
}
