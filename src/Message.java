import java.util.Random;


public class Message {

	public static final char[] PUNCTUATIONS={'.',':',';',',','!','?','(',')'};
	
	public static String article(String word) {	//TODO: this should take an argument so that the correct string is analyzed.
		//EXAMPLE: "an uncursed bow" vs "a bow"
		//consider making this a static method that takes a string arg, and moving it to Message class.
	if(word==null)
		return "a";
	switch(word.charAt(0)){
	case 'a':
		return "an";
	case 'e':
		return "an";
	case 'i':
		return "an";
	case 'o':
		return "an";
	case 'u':
		return "an";
	default:
		return "a";
	}
}
	
	//benifications
	public static String benifyMessage(String currentMessage) {	//you know exactly what this method does. Stop reading this comment.
		currentMessage+=" ";
		String[] originalWords = currentMessage.split(" ");
		String benWords="";
		
		for(int i=0;i<originalWords.length&&originalWords[i]!=null;i++){
			String nextWord=originalWords[i];
			benWords+=benifyWord(nextWord)+" ";
		}
		return benWords;
	}

	private static String benifyWord(String nextWord) {	//keep a word's first and last letters the same, but rearrange the other letters.
		int wordLength=nextWord.length(); //is this necessary?
		
		if(wordLength<4||isPunctuation(nextWord.charAt(0)))
			return nextWord;
		
		char firstLetter=nextWord.charAt(0);
		String middleLetters=middleLetters(nextWord);
		char lastLetter=lastLetter(nextWord);
		String punctuation=punctutation(nextWord);
		
		if(middleLetters.length()<2)
			return nextWord;
		
		middleLetters=randomScrambleCharacters(middleLetters.toCharArray());
		return firstLetter+middleLetters+lastLetter+punctuation;
	}

	static String middleLetters(String word) {
		String letters="";
		int wordLength=word.length();
		for(int i=1;i<wordLength-1&&!isPunctuation(word.charAt(i+1));i++){
			letters+=word.charAt(i);
		}
		return letters;
	}
	
	static char lastLetter(String word) {
		int wordLength=word.length();
		int index=0;
		while(index+1<wordLength&&!isPunctuation(word.charAt(index+1))){
			index++;
		}
		return word.charAt(index);
	}
	
	static String punctutation(String word) {
		int wordLength=word.length();
		String punctuation="";
		int index=0;
		while(index+1<wordLength&&!isPunctuation(word.charAt(index))){
			index++;
		}
		while(index<wordLength&&isPunctuation(word.charAt(index))){
			punctuation+=word.charAt(index);
			index++;
		}
		return punctuation;
	}

	private static boolean isPunctuation(char character) {
		for(int i=0;i<PUNCTUATIONS.length;i++){
			if(character==PUNCTUATIONS[i])
				return true;
		}
		return false;
	}

	private static String randomScrambleCharacters(char[] characters) {
		Random rng=new Random();
		for(int i=characters.length-1;i>0;i--){	//this for loop is the part that actually scrambles the letters
			int index=rng.nextInt(i+1);
			char c=characters[index];
			characters[index]=characters[i];
			characters[i]=c;
		}
		return new String(characters);
	}

}