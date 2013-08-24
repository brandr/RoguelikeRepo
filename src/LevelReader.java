
public class LevelReader {		//reads save files made with map editor.
	
	//TODO: need a way to add unique levels to dungeon, using branch/level index args. (consider using overwrite method from level,
			//combined with loadLevel method.
	
	public static final String MAP_ERROR="Map not found.";
	
	public static Level loadLevel(int index){
		return loadLevel(readLevel(index));
	}
	
	public static Level loadLevel(String map){
		if(map!=MAP_ERROR){
			Level level = new Level();
			int xSize=level.xSize;
			int ySize=level.ySize;
			int iconIndex=0;
			while(iconIndex<xSize*ySize+(ySize-1)){
				if(map.charAt(iconIndex)!='\n')
					level.addTile(map.charAt(iconIndex),iconIndex%(xSize+1),iconIndex/(xSize+1));
				iconIndex++;
			}
			return level;
		}
		return null;
	}
	
	public static String readLevel(int index){	
		TextFileReader mapReader=new TextFileReader();
		return mapReader.mapLoad(index);
	}
}
