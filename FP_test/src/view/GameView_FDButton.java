package view;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class GameView_FDButton extends Button{
	
	private final String BUTTON_PRESSED_PATH = this.getClass().getResource("resources/gameView_buttonPressed.png").toExternalForm();
	private final String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent; -fx-background-image: url('" + BUTTON_PRESSED_PATH + "');";
	private final String BUTTON_FREE_PATH = this.getClass().getResource("resources/gameView_buttonFree.png").toExternalForm();
	private final String BUTTON_FREE_STYLE = "-fx-background-color: transparent; -fx-background-image: url('" + BUTTON_FREE_PATH + "');";
	private final String FONT_PATH = this.getClass().getResource("resources/kenvector_future.ttf").toExternalForm();
	
	public GameView_FDButton(String text) {
		setText(text);
		setButtonFont();
		setPrefWidth(143); 
		setPrefHeight(37);
		setStyle(BUTTON_FREE_STYLE);
		initializeButtonListeners();
	}
	
	private void setButtonFont() {
		setFont(Font.loadFont(FONT_PATH, 18));
	}
	
	private void setButtonPressedStyle() {
		setStyle(BUTTON_PRESSED_STYLE);
		setPrefHeight(34); //original image height
		setLayoutY(getLayoutY() + 3); //move the image lower to make you feel you have pressed the button
	}
	
	private void setButtonReleasedStyle() {
		setStyle(BUTTON_FREE_STYLE);
		setPrefHeight(37); //original image height
		setLayoutY(getLayoutY() - 3);
	}
	
	private void initializeButtonListeners() {
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonPressedStyle();
			}
		});
		
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonReleasedStyle();
			}
		});
		
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(new Glow(0.4));
			}
		});
		
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(null);
			}
		});
	}
	
}