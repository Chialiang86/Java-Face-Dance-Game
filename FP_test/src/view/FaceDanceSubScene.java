package view;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.util.Duration;

public class FaceDanceSubScene extends SubScene{

	private final String BACKGROUND_IMAGE = this.getClass().getResource("resources/yellow_panel.png").toExternalForm(); 
	
	private boolean isHidden;
	
	public FaceDanceSubScene() {
		super(new AnchorPane(), 600, 400);
		Image backgroundImage = new Image(BACKGROUND_IMAGE, 600, 400, false, true);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, 
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
		AnchorPane root2 = (AnchorPane) this.getRoot();
		root2.setBackground(new Background(background));
		
		isHidden = true;
		
		setLayoutX(1024); //1024, since the Scene width is 1024, it cannot be seen at first
		setLayoutY(285); //(1024, 180) is the left top corner of the sub scene
	}
	
	public void moveSubScene() {
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this); //this means the calling object, the sub scene's AnchorPane
		
		if(isHidden)
		{
			transition.setToX(-676); //move left by 676 pixels
			isHidden = false;
		} else {
			transition.setToX(0); //move back to original x-coordinate, 1024
			isHidden = true;
		}
		
		transition.play();
	}
	
	public AnchorPane getPane() {
		return (AnchorPane) this.getRoot(); //return the subScene's AnchorPane
	}
	
	
	
}
