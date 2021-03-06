
public class Sound {	//a sound occurring on one or more tiles.
	
	public Sound(String description, int volume){
		setDescription(description);
		setVolume(volume);
		//this.setDuration(duration);
	}
	
	public String toString(){
		return description;
	}
	//getters and setters
	
		//description (how the sound is described through text)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
		//volume (how far away the sound can be heard from)
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}

	private String description="";
	private int volume=0;
	//private int duration=0;

}