//making a public class called "dice" that will make rolling with the xdy method more viable.
public class Dice {
	   
	public static void main(int args[]){
	xdy(4, 2);
	}

	public static int xdy(int num1, int num2) {
		int rollnum;   
		rollnum = (int)(Math.random()*num2 + 1);	   
		System.out.println(rollnum);
		return rollnum;
		
}
	}