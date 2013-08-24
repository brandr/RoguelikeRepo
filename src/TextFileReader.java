import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;


public class TextFileReader {
	
	
	
	private static String textFileToString(InputStream input) {
		if(input==null)
			return "File not found.";
		try{
			InputStreamReader reader = new InputStreamReader(input);
			String text="";
			int data=reader.read();
			while(data!=-1){
				text+=(char)data;
				data=reader.read();
			}
			return text;
		}catch(IOException e){
			return "File not found.";
		}
	 }
	
	private static String mapSaveToString(InputStream input){
		if(input==null)
			return LevelReader.MAP_ERROR;
		try{
			InputStreamReader reader = new InputStreamReader(input);
			String text="";
			int data=reader.read();
			while(data!=-1){
				if(Level.isValidIcon(data))
					text+=(char)data;
				data=reader.read();
			}
			return text;
		}catch(IOException e){
			return LevelReader.MAP_ERROR;
		}
	}

	public static String instructionManual(){
		InputStream instructions = Thread.currentThread().getContextClassLoader().getResourceAsStream("Instructions.txt");
		return textFileToString(instructions);
	}

	public static String mapLoad(int saveIndex){	//TODO:	once there are multiple saves possible, make an argument to determine which map is loaded.
		FileInputStream map;
		try {
			map = new FileInputStream("mapSave"+saveIndex+".sav");
		} catch (FileNotFoundException e) {
			return LevelReader.MAP_ERROR;
		}
		return mapSaveToString(map);
	}
	
}
