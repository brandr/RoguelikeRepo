
public class Spellbook extends Readable{

	public Spellbook(Spellbook toCopy) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void use(Monster target) {
		// TODO Auto-generated method stub
		
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
	
	public Spell getSpell() {
		return spell;
	}

	public void setSpell(Spell spell) {
		this.spell = spell;
	}

	private Spell spell;
}