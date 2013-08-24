//experience stored by the player (and possibly other characters) for leveling up.
public class Experience {

	public static final int FIRST_LEVEL_AT=10;
	public static final int MAX_LEVEL=99;
	//TODO: if there are constants which determine the formula for required experience at different levels, put them here.
	public Experience(){
		
	}
	
	public void levelUp(){
		if(level+1<=MAX_LEVEL)
			level++;
		experiencePoints[1]+=3*(int)Math.pow(1.8, level);	//this is a temporary formula
	}
	
	public void gainExp(int exp){
		experiencePoints[0]+=exp;
	}
	
	protected int level=1;
	protected int[] experiencePoints={0,FIRST_LEVEL_AT};
}
