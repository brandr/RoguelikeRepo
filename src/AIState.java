import java.util.Random;


public class AIState {

	public final static String PURSUIT="pursuit";	//the act of chasing a monster that is in view.
	public final static String HUNTING="hunting";	//searching for a monster that has disappeared from view.
	public final static String IDLE="idle";		//wandering around.

	public static final String UNINTELLIGENT="unintelligent";
	public static final String INTELLIGENT="intelligent";
	
	public static final String[] MONSTER_INTELLIGENCES={UNINTELLIGENT,INTELLIGENT};
	
	public AIState(String state, Monster monster){
		this.monster=monster;
		this.state=state;
	}
	
	/*public AIState(AIState AIstate) {	//is this necessary?
		monster=AIstate.monster;
		intelligence=AIstate.intelligence;
		state=AIstate.state;
	}*/

	public void decideMove() {
		if(monster.confused()){
			monster.moveRandom();
			return;
		}
		switch(intelligence){
		case(INTELLIGENT):
			intelligentDecideMove();
			break;
		case(UNINTELLIGENT):
			unintelligentDecideMove();
			break;
		}
	}
	
	private void intelligentDecideMove() {	//actions are listed in order of priority.
		Monster target=nearestEnemy();
		if(target!=null){				//FIRST PRIORITY: pursue the target if it can be seen.
			switchStates(PURSUIT);
			targetTile=new Tile(target.currentTile);
			//saveMove();
			monster.moveTowards(target);
		}
			
		else{
			targetLostResponse();
			}
	}

	private void unintelligentDecideMove() {	//TODO: this is a little *too* unintelligent.
		wander();
	}
	
	public void switchStates(String state){
		this.state=state;
	}
	
	private Level level(){
		return monster.getCurrentLevel();
	}
	
	private void targetLostResponse(){	//TODO: make this more streamlined and clear.
		//Figure out a good monster action cycle before coding more.
		//or maybe a hierarchy of priorities rather than a cycle?
		switch(state){
		case(PURSUIT):
			state=HUNTING;
			Tile[] targetChoices=adjacentUnseenTiles(level(),targetTile);
			if(targetChoices[0]==null)
				state=IDLE;
			targetLostResponse();
			break;
		case(HUNTING):	//TODO: after reaching target tile, guess a new target tile, up to a point. (define what that point is) Eventually resume wandering.
			if(targetTile!=null){
				if(!targetTile.equalTo(monster.currentTile)){
					saveMove();
					monster.moveTowards(targetTile);
				}
				else{
					//if(!monster.currentTile.equalTo(plannedPath[0]))
					//	monster.moveTowards(plannedPath[0]);
					state=IDLE;
					return;
				}
			}
			else{
				state=IDLE;
				targetLostResponse();
				}
			break;
		case(IDLE):
			wander();
			break;
		}
	}
	
	private Tile[] adjacentUnseenTiles(Level level, Tile targetTile) {
		Tile[] adjacents=Movement.adjacentTiles(level(), targetTile);	//find all tiles adjacent to the target's last known position
		Tile[] unseenTiles=new Tile[adjacents.length];
		int index=0;
		for(int i=0; i<adjacents.length; i++){
			Tile nextTile=adjacents[i];
			if(nextTile!=null&&!monster.canSee(nextTile)){	//if the tile is on the map and not in the monster's view
				unseenTiles[index]=nextTile;
				index++;
			}
		}
		return unseenTiles;
	}
	
	private void wander(){	//choose an empty, adjacent tile to move into.
		monster.moveRandom();
		saveMove();
	}
	
	private Monster nearestEnemy(){
		return determineClosest(monster.allEnemiesInSight());
	}
	
	private Monster determineClosest(Monster[] availableMonsters) {
		if(availableMonsters==null||availableMonsters[0]==null)
			return null;
		Monster closestMonster=availableMonsters[0];
		int index=0;
		while(availableMonsters[index]!=null){
			if(distanceFromMonster(availableMonsters[index])<
				distanceFromMonster(closestMonster))
					closestMonster=availableMonsters[index];
			index++;
		}
		return closestMonster;
	}
	
	private int distanceFromMonster(Monster otherMonster) {
		return Math.max(Math.abs(monster.getXPos()-otherMonster.getXPos()),
				Math.abs(monster.getYPos()-otherMonster.getYPos()));
	}
	
	//sound methods
	
	public void focusSound(Monster source, Sound sound) {	//consider making separate methods for dealing with sounds that don't come from monsters.
		targetTile=source.currentTile;
		state=HUNTING;
	}
	
	private void saveMove(){	//saves monster's current tile location before moving
		previousTile=monster.currentTile;
	}
	
	public String getIntelligence() {
		return intelligence;
	}	
	
	public void setIntelligence(String intelligence) {
		this.intelligence=intelligence;
	}
	
	private String intelligence=INTELLIGENT;
	private String state;
	private Monster monster;
	private Tile targetTile=null;
	private Tile previousTile;
	//private Tile[] plannedPath=new Tile[20];
	private Random dice=new Random();
}