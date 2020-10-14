package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ConnectingChoice extends HBox{
	private final String circleNotChoosen = this.getClass().getResource("resources/grey_circle.png").toExternalForm();
	private final String circleChoosen = this.getClass().getResource("resources/yellow_boxTick.png").toExternalForm();
	private final String FONT_PATH = this.getClass().getResource("resources/kenvector_future.ttf").toExternalForm();
	
	private ImageView circleImage;
	private Label label;
	private boolean isCircleChoosen;
	
	public ConnectingChoice(String text) {
		circleImage = new ImageView(circleNotChoosen);
		label = new Label(text);
		label.setFont(Font.loadFont(FONT_PATH, 23));
		isCircleChoosen = false;
		this.setAlignment(Pos.CENTER_LEFT);
		this.setSpacing(20); 
		this.getChildren().add(circleImage);
		this.getChildren().add(label);
	}
	
	public boolean getIsCircleChoosen() {
		return isCircleChoosen;
	}
	
	public void setIsCircleChoosen(boolean isCircleChoosen) {
		this.isCircleChoosen = isCircleChoosen;
		String imagetoSet = this.isCircleChoosen ? circleChoosen : circleNotChoosen;
		circleImage.setImage(new Image(imagetoSet));
	}

}
