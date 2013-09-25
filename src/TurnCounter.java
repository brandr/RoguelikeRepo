
public class TurnCounter {
	public TurnCounter(Level level) {
		this.level=level;
	}

	public void startTurnCounter(Level level) {
		boolean playerTurn=false;
		Monster[] monsters=level.levelMonsters;
		do {
			ticks++;
			for(int i=0;i<monsters.length&&monsters[i]!=null;i++){
				Monster nextMonster=monsters[i];
				if(nextMonster!=null){
					int delay=nextMonster.getTurnDelay();
					if(ticks%delay==0){
						if(nextMonster.getClass().equals(Player.class))
							playerTurn=true;
						else
							nextMonster.turn();
					}
					nextMonster.decrementAllDurations();	//TODO: strongly consider moving this to a shared "turn" method of monsters and players
				}
			}
		} while(!playerTurn);
	}
	
	private Monster[] levelMonsters(){
		return level.levelMonsters;
	}
	
	private int ticks=0;
	private Level level;
}
