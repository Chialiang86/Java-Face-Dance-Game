package view;

public class Execution {
	public static void main(String[] args) throws Exception{
		//face_dict = {'angry' : 0, 'disqust' : 1, 'fear' : 2, 'happy' : 3, 'neutral' : 4, 'sad' : 5, 'surprise' : 6}
		//ImageShow igImageShow = new ImageShow();
		displayScore(987);
		
		displayScore(65);
		
		displayScore(4);
		
		
	}
	
	public static void displayScore(int score) {
		// clear
		int unitDigit = score%10;
		int tenDigit = (score%100)/10;
		int hundredDigit = (score%1000)/100;
		
		System.out.println(hundredDigit);
		System.out.println(tenDigit);
		System.out.println(unitDigit);
		// set
	}
}
