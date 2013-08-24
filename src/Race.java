
public class Race {		//TODO: the only constructor should take a string argument and set all variables based on that.

	//TODO: make final values here.
	public final static String HUMAN="Human";
	public final static String GREYLING="Greyling";
	public final static String DWARF="Dwarf";
	public final static String ELF="Elf";
	//public final static String SCHWARZENEGGER="Schwarzenegger";
	//public final static String FIFTY_CENT="50 Cent";
	
	public final static String[] RACES={HUMAN,GREYLING,DWARF,ELF};
	
	public Race(String raceName){
		this.raceName=raceName;
		switch(raceName){
		case(HUMAN):
			HPMod=1;
		break;
		case(GREYLING):
			HPMod=1.2;
			dodgeMod=2;
		break;
		case(DWARF):
			HPMod=1.4;
			dodgeMod=-1;	//can this be negative? ideally itshould be possible.
		break;
		case(ELF):
			HPMod=0.8;
			dodgeMod=3;
		break;
		}
	}
	
	public String toString(){
		return raceName;
	}
	
	public String raceName;

	public double HPMod() {
		return HPMod;
	}
	
	public double MPMod() {
		return MPMod;
	}
	
	private double HPMod=1;
	private double MPMod=0.7;
	public int dodgeMod=0;
	public int [] toHitMod={0,0};

	
}
