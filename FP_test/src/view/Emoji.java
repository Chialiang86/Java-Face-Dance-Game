package view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Emoji {
	
	private int x, y;	// emoji's coordinate
	private int type;	// emoji's type (ex: Face.LAUGH)
	//private String imagePath;
	private int status; // 0: new, 1: exist, 2: outs
	
	private ImageView emojiImage;
	
	Emoji(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		if(type == Face.ANGRY)
			emojiImage = new ImageView(new Image(getClass().getResource("resources/angry.png").toExternalForm() , 60, 60, false, true));
		else if(type == Face.HAPPY)
			emojiImage = new ImageView(new Image(getClass().getResource("resources/happy.png").toExternalForm() , 60, 60, false, true));
		else if(type == Face.SAD)
			emojiImage = new ImageView(new Image(getClass().getResource("resources/sad.png").toExternalForm() , 60, 60, false, true));
		else if(type == Face.SURPRISE)
			emojiImage = new ImageView(new Image(getClass().getResource("resources/surprise.png").toExternalForm() , 60, 60, false, true));
		status = 0;
	}
	
	public ImageView getEmojiImage() {
		return emojiImage;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getType() {
		return type;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		return x + " " + y + " " + type;
	}
	
	public static Emoji parseString(String str) {
		String[] data = str.split(" ");
		return new Emoji(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
	}
}
