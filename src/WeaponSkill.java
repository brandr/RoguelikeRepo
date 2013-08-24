
public class WeaponSkill extends Skill{
	
	//is this class necessary?
	
	public static final String FISTS="fists"; 	//stored here and not in weapon, because "fists" is determined by currentWeapon() being null.
	
	public WeaponSkill(String weaponCategory){
		this.weaponCategory=weaponCategory;
		skillCategory="weapon";
	}
	
	public int damageBoost() {		//damage boost once an attack is confirmed. (NOTE: consider putting this in the damage-determining formula.)
		return skillLevel*2;
	}
	
	public String weaponCategory="";
	
}
