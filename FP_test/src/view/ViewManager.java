package view;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewManager {

	private static final int HEIGHT = 768;
	private static final int WIDTH = 1024;
	private static final int MENU_BUTTONS_START_X = 100;
	private static final int MENU_BUTTONS_START_Y = 300;
	private final String FONT_PATH = this.getClass().getResource("resources/kenvector_future.ttf").toExternalForm();
	AudioClip pick = new AudioClip(getClass().getResource("resources/color-X.mp3").toString());

	private AnchorPane mainPane;
	private Scene mainScene;
	private Stage mainStage;
	private FaceDanceSubScene connectSubScene;
	private FaceDanceSubScene creditsSubScene;
	private FaceDanceSubScene sceneToHide;
	private ImageShow imageShow;
	
	ArrayList<FDButton> menuButtons;
	ArrayList<ConnectingChoice> choices;
	private String chosenConnectingWay;
	
	private String IP_address;
	
	public ViewManager() {
		try {
			imageShow = new ImageShow();
		} catch (IOException e) {
			e.printStackTrace();
		}
		menuButtons = new ArrayList<>();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		mainStage = new Stage();
		mainStage.setScene(mainScene);
		mainStage.setTitle("FACE DANCE CHALLENGE");
		createBackground();
		createLogo();
		createButtons();
		createSubScenes();
		pick.play();
	}

	private void createButtons() {
		createPlayButton();
		//createConnectButton();
		createCreditsButton();
		createExitButton();
	}
	
	private void addMenuButton(FDButton button) {
		button.setLayoutX(MENU_BUTTONS_START_X);
		button.setLayoutY(MENU_BUTTONS_START_Y + menuButtons.size() * 100);
		menuButtons.add(button);
		mainPane.getChildren().add(button);
	}

	private void createPlayButton() {
		FDButton playButton = new FDButton("PLAY");
		addMenuButton(playButton);
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showSubScene(connectSubScene);
			}
		});
		
	}
	
	private void createCreditsButton() {  //authors
		FDButton creditsButton = new FDButton("CREDITS");
		addMenuButton(creditsButton);
		creditsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showSubScene(creditsSubScene);
			}
		});
	}

	private void createExitButton() {
		FDButton exitButton = new FDButton("EXIT");
		addMenuButton(exitButton);
		
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				mainStage.close();
				
			}
		});
	}
	
	private void createSubScenes() {
		createCreditsSubScene();
		createConnectSubScene();
	}
	
	private void createCreditsSubScene() {
		creditsSubScene = new FaceDanceSubScene();
		mainPane.getChildren().add(creditsSubScene);
		creditsSubScene.getPane().getChildren().add(createAuthors());
	}
	
	private VBox createAuthors() {
		String[] authors = {"GAME CREATED BY", "Ching Wei Su_F64051083", "Xin Yu Lin_F64056172 ", "Jia Liang Guo_E64056122", "He Shan LIN_F94054011"};
		VBox box = new VBox();
		box.setSpacing(20);
		for(int i=0; i<authors.length; ++i)
		{
			Label label = new Label(authors[i]);
			label.setFont(Font.loadFont(FONT_PATH, 30));
			box.getChildren().add(label);
		}
		box.setAlignment(Pos.CENTER_LEFT);
		box.setLayoutX(62);
		box.setLayoutY(50);
		return box;
	}
	
	private void showSubScene(FaceDanceSubScene subScene) {
		if(sceneToHide!=null)
			sceneToHide.moveSubScene();
		
		subScene.moveSubScene();
		sceneToHide = subScene;
	}
	
	private void createConnectSubScene() {
		connectSubScene = new FaceDanceSubScene();
		mainPane.getChildren().add(connectSubScene);
		connectSubScene.getPane().getChildren().add(createConnectingWaysToChoose());
		connectSubScene.getPane().getChildren().add(createOKButton());
	}

	private VBox createConnectingWaysToChoose() {
		VBox box = new VBox();
		box.setSpacing(20);
		choices = new ArrayList<>(); 
		ConnectingChoice choice1 = new ConnectingChoice("SERVER");
		choices.add(choice1);
		box.getChildren().add(choice1);
		choice1.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for(ConnectingChoice choice: choices) {
					choice.setIsCircleChoosen(false);
				}
				choice1.setIsCircleChoosen(true);
				Label ip = new Label();
				ip.setText(NetModule.getLocalAddress());
				ip.setLayoutX(243);
				ip.setLayoutY(143);
				connectSubScene.getPane().getChildren().add(ip);
				chosenConnectingWay = "server";
				IP_address = NetModule.getLocalAddress();
			}
		});
		
		ConnectingChoice choice2 = new ConnectingChoice("CLIENT");
		choices.add(choice2);
		box.getChildren().add(choice2);
		choice2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for(ConnectingChoice choice: choices) {
					choice.setIsCircleChoosen(false);
				}
				choice2.setIsCircleChoosen(true);
				chosenConnectingWay = "client";
				TextField field = new TextField("");
				field.setPromptText("Enter Server's IP address");
				field.setLayoutX(243);
				field.setLayoutY(163);
				connectSubScene.getPane().getChildren().add(field);
				field.setOnKeyPressed(new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent e) {
						if(e.getCode() == KeyCode.ENTER) //press enter if complete input
						{
							IP_address = field.getText();
							System.out.println(IP_address);
						}		
					}
				});
			}
		});
			
		box.setLayoutX(62);
		box.setLayoutY(100);
		return box;
	}
	
	private FDButton createOKButton() {
		FDButton OKButton = new FDButton("OK!");
		OKButton.setLayoutX(350);
		OKButton.setLayoutY(300);
		
		OKButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(chosenConnectingWay!=null){
					GameViewManager gameManager = new GameViewManager(chosenConnectingWay, IP_address);
					imageShow.getGameViewManager(gameManager);
					gameManager.createNewGame(mainStage);
					pick.stop();
				}
			}
		});
		
		return OKButton;
	}

	public Stage getMainStage() {
		return mainStage;
	}
	
	private void createBackground() {
		Image backgroundImage = new Image(getClass().getResource("resources/background.jpg").toString(), 1024, 768, false, true);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		mainPane.setBackground(new Background(background));
	}
	
	private void createLogo() {
		ImageView logo = new ImageView(getClass().getResource("resources/logo2.png").toString());
		logo.setLayoutX(320);
		logo.setLayoutY(50);
		logo.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				logo.setEffect(new Glow(1));
			}
		});
		logo.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				logo.setEffect(null);
			}
		});
		mainPane.getChildren().add(logo);
	}

}
