
public class TileList {
	
	public TileList() {
		tiles=new Tile[2000];
	}
	
	public TileList(Tile[] tiles){
		this.tiles=tiles;
	}
	
	public TileList(TileList toCopy){
		int length=toCopy.tiles.length;
		for(int i=0;i<length&&toCopy.tiles[i]!=null;i++){
			tiles[i]=new Tile(toCopy.tiles[i]);
		}
	}
	
	public static void initialize(TileList[] nextTiles) {
		for(int i=0;i<nextTiles.length;i++){
			nextTiles[i]=new TileList();
		}
		
	}

	public void addTile(Tile tile){
		if(tile==null)
			return;
		int index=0;
		while(tiles[index]!=null){
			if(tile.equals(tiles[index]))
				return;
			index++;
		}
		tiles[index]=tile;
	}
	
	public void addTiles(TileList addedTiles) {
		int index=0;
		int length=addedTiles.length();
		while(index<length){
			addTile(addedTiles.getTile(index));
			index++;
		}
	}
	
	public Tile getTile(int index){
		if(index>=0&&index<length())
			return tiles[index];
		return null;
	}
	
	public int length(){
		int length=0;
		while(tiles[length]!=null)
			length++;
		return length;
	}
	
	public Tile[] tiles=new Tile[2000];

	

}
