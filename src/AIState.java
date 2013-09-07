import java.util.Random;


public class AIState {

	public final static String PURSUIT="pursuit";	//the act of chasing a monster that is in view.
	public final static String HUNTING="hunting";	//searching for a monster that has disappeared from view.
	public final static String IDLE="idle";		//wandering around.

	public AIState(String state, Monster monster){
		this.monster=monster;
		this.state=state;
	}
	
	public void switchStates(String state){
		this.state=state;
	}
	
	private Level level(){
		return monster.getCurrentLevel();
	}
	
	private void monsterLostResponse(){
		switch(state){
		case(PURSUIT):
			state=HUNTING;
			monsterLostResponse();
		break;
		case(HUNTING):	//TODO: after reaching target tile, guess a new target tile, up to a point. (define what that point is) Eventually resume wandering.
			if(targetTile!=null&&!targetTile.equalTo(monster.currentTile))
				monster.moveTowards(targetTile);
			else{
				state=IDLE;
				monsterLostResponse();
				}
			break;
		case(IDLE):
			wander();
			break;
		}
	}
	
	public void decideMove() {
		Monster target=nearestEnemy();
		if(target==null)
			monsterLostResponse();
		else{
			switchStates(PURSUIT);
			targetTile=new Tile(target.currentTile);
			monster.moveTowards(target);
		}
		//System.out.println(state);
	}
	
	private void wander(){	//choose an empty, adjacent tile to move into.
		Tile[] choices=monster.adjacentEmptyTiles();
		int randomIndex=dice.nextInt(choices.length);
		while(choices[randomIndex]==null){
			randomIndex=dice.nextInt(choices.length);
		}
		monster.moveTowards(choices[randomIndex]);
	}
	
	private Monster nearestEnemy(){
		return determineClosest(monster.allEnemiesInSight());
	}
	
	private Monster determineClosest(Monster[] availableMonsters) {
		if(availableMonsters[0]==null)
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

	private String state;
	private Monster monster;
	private Tile targetTile=null;
	private Random dice=new Random();
	
	
}
