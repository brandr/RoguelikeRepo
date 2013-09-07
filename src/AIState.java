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
	
	private void targetLostResponse(){	//TODO: make this more streamlined and clear. Figure out a good monster action cycle before coding more.
		switch(state){
		case(PURSUIT):
			state=HUNTING;
			Tile[] targetChoices=adjacentUnseenTiles(level(),targetTile);
			if(targetChoices[0]==null)
				state=IDLE;
			else
				plannedPath[0]=targetChoices[0];//TODO: choose planned path more randomly.
			targetLostResponse();
			break;
		case(HUNTING):	//TODO: after reaching target tile, guess a new target tile, up to a point. (define what that point is) Eventually resume wandering.
			if(targetTile!=null){
				if(!targetTile.equalTo(monster.currentTile)){
					saveMove();
					monster.moveTowards(targetTile);
				}
				else{
					if(!monster.currentTile.equalTo(plannedPath[0]))
						monster.moveTowards(plannedPath[0]);
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

	public void decideMove() {
		Monster target=nearestEnemy();
		if(target==null)
			targetLostResponse();
		else{
			switchStates(PURSUIT);
			targetTile=new Tile(target.currentTile);
			saveMove();
			monster.moveTowards(target);
		}
	}
	
	private void wander(){	//choose an empty, adjacent tile to move into.
		Tile[] choices=monster.adjacentEmptyTiles();
		int randomIndex=dice.nextInt(choices.length);
		while(choices[randomIndex]==null){
			randomIndex=dice.nextInt(choices.length);
		}
		saveMove();
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
	
	private void saveMove(){	//saves monster's current tile location before moving
		previousTile=monster.currentTile;
	}

	private String state;
	private Monster monster;
	private Tile targetTile=null;
	private Tile previousTile;
	private Tile[] plannedPath=new Tile[20];
	private Random dice=new Random();
	
	
}
