package view;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class FDButton extends Button{
	
	private final String BUTTON_PRESSED_PATH = this.getClass().getResource("resources/buttonPressed.png").toExternalForm();
	private final String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent; -fx-background-image: url('" + BUTTON_PRESSED_PATH + "');";
	private final String BUTTON_FREE_PATH = this.getClass().getResource("resources/buttonFree.png").toExternalForm();
	private final String BUTTON_FREE_STYLE = "-fx-background-color: transparent; -fx-background-image: url('" + BUTTON_FREE_PATH + "');";
	private final String FONT_PATH = this.getClass().getResource("resources/kenvector_future.ttf").toExternalForm();
	
	public FDButton(String text) {
		setText(text);
		setButtonFont();
		setPrefWidth(190);
		setPrefHeight(49);
		setStyle(BUTTON_FREE_STYLE);
		initializeButtonListeners();
	}
	
	private void setButtonFont() {
		setFont(Font.loadFont(FONT_PATH, 23));
	}
	
	private void setButtonPressedStyle() {
		setStyle(BUTTON_PRESSED_STYLE);
		setPrefHeight(45); //original image height
		setLayoutY(getLayoutY() + 4); //move the image lower to make you feel you have pressed the button
	}
	
	private void setButtonReleasedStyle() {
		setStyle(BUTTON_FREE_STYLE);
		setPrefHeight(49); //original image height
		setLayoutY(getLayoutY() - 4);
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
