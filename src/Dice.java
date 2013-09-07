import java.util.Random;

//making a public class called "dice" that will make rolling with the xdy method more viable.
public class Dice {
	   
public static int xdy(int num1, int num2) {
	int rollnum;   
	rollnum = num1+random.nextInt(num1*num2-num1+1);	   
	return rollnum;		
}
	private static Random random=new Random();
	}