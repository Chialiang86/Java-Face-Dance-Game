package view;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class GameViewManager implements Runnable {
	// game parameters
	private static final int GAME_WIDTH = 1024;
	private static final int GAME_HEIGHT = 768;
	public static final int shift = 2;
	public static final int maxX = 450;
	public static final int maxY = 500;
	public static final int myOffsetX = 500;
	public static final int myOffsetY = 200;
	public static final int maxEmojiGen = 1;
	public static final int enemyScoreX = 700;
	public static final int myScoreX = 130;
	public static final int scoreY = 20;
	public static final int scoreDigitOffset = 70;
	public static final int countdownX = 412;
	public static final int countdownY = 30;
	public static final int countdownOffset = 90;
	public static final long duration = 30000;	// milliseconds
	public static final int delay = 1000;	// milliseconds
	
	private AnchorPane gamePane;
	private Scene gameScene;
	private Stage gameStage;
	private Stage menuStage;
	
	private ArrayList<Emoji> myEmojiList;
	private ArrayList<Emoji> emojiList;	// original problem
	private int matchedEmoji;	// store matched result
	private Random rand;
	public NetModule net;
	private BufferedImage faceImage;
	private ImageView camera;
	
	private ImageView tensDigitImage;
	private ImageView unitDigitImage;
	private NumberDisplay myScoreHundred;
	private NumberDisplay myScoreTen;
	private NumberDisplay myScoreUnit;
	private NumberDisplay enemyScoreHundred;
	private NumberDisplay enemyScoreTen;
	private NumberDisplay enemyScoreUnit;
	private NumberDisplay timerTens = new NumberDisplay(0);
	private NumberDisplay timerUnit = new NumberDisplay(0);
	private int timerTensState = 0;
	private int timerUnitState = 0;
	private long lastUnitDigit = 0;
	private long lastTensDigit = 0;
	
	private long startTime;
	private boolean isServer;
	private String ipAddr;
	public volatile boolean inGame;
	private boolean isLegal;
	
	public AnimationTimer animationTimer;
	private int myScore;
	private int enemyScore;
	private boolean win;
	private boolean lose;
	private boolean tie;
	private boolean connect;
	AudioClip gameMusic = new AudioClip(getClass().getResource("resources/gameMusic.mp3").toString());
	
	public GameViewManager(String character, String server_IP) {
		matchedEmoji = 1;
		connect = false;
		isLegal = true;
		inGame = false;
		myEmojiList = new ArrayList<Emoji>();
		emojiList = new ArrayList<Emoji>();
		net = new NetModule(this);
		rand = new Random();
		myScore = 0;
		enemyScore = 0;
		initializeStage();
		
		// score initialization and display
		myScoreHundred = new NumberDisplay(1);
		myScoreTen = new NumberDisplay(1);
		myScoreUnit = new NumberDisplay(1);
		enemyScoreHundred = new NumberDisplay(2);
		enemyScoreTen = new NumberDisplay(2);
		enemyScoreUnit = new NumberDisplay(2);
		myScoreHundred.setLayoutXY(myScoreX, scoreY);
		myScoreTen.setLayoutXY(myScoreX + scoreDigitOffset, scoreY);
		myScoreUnit.setLayoutXY(myScoreX + 2*scoreDigitOffset, scoreY);
		enemyScoreHundred.setLayoutXY(enemyScoreX, scoreY);
		enemyScoreTen.setLayoutXY(enemyScoreX + scoreDigitOffset, scoreY);
		enemyScoreUnit.setLayoutXY(enemyScoreX + 2*scoreDigitOffset, scoreY);
		gamePane.getChildren().add(myScoreHundred.getNumberImage(0));
		gamePane.getChildren().add(myScoreTen.getNumberImage(0));
		gamePane.getChildren().add(myScoreUnit.getNumberImage(0));
		gamePane.getChildren().add(enemyScoreHundred.getNumberImage(0));
		gamePane.getChildren().add(enemyScoreTen.getNumberImage(0));
		gamePane.getChildren().add(enemyScoreUnit.getNumberImage(0));
		
		// client server parameters setup
		if(character.equals("server"))
			this.isServer = true;
		else if(character.equals("client")) {
			ipAddr = server_IP;
			this.isServer = false;
		}
	}
	
	private void initializeStage() {
		gamePane = new AnchorPane();
		gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
		gameStage = new Stage();
		gameStage.setScene(gameScene);
		gameStage.setTitle("FACE DANCE CHALLENGE");
	}
	
	public void createNewGame(Stage menuStage) {
		this.menuStage = menuStage;
		this.menuStage.hide();
		createBackground();
		gameStage.show();
		Thread actioner = new Thread(this);
		if(isServer) {
			createStartButton();
		}
		actioner.start();
		
		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				if(camera != null) {
					gamePane.getChildren().remove(camera);
				}
				isLegal = false;
				renderImage();
				isLegal = true;
				countDown();
				displayScore();
				if(inGame) {
					for(int i = 0; i < myEmojiList.size(); ++i)
					{
						setEmoji(myEmojiList.get(i));
						emojiOut(myEmojiList.get(i));
					}
				}
				if(win) {
					ImageView winImage = new ImageView(new Image(getClass().getResource("resources/win.png").toExternalForm(), 500, 100, false, true));
					winImage.setLayoutX(520);
					winImage.setLayoutY(300);
					gamePane.getChildren().add(winImage);
					win = false;
				}
				if(lose) {
					ImageView loseImage = new ImageView(new Image(getClass().getResource("resources/lose.png").toExternalForm(), 500, 100, false, true));
					loseImage.setLayoutX(520);
					loseImage.setLayoutY(300);
					gamePane.getChildren().add(loseImage);
					lose = false;
				}
				if(tie) {
					ImageView tieImage = new ImageView(new Image(getClass().getResource("resources/tie.png").toExternalForm(), 400, 100, false, true));
					tieImage.setLayoutX(520);
					tieImage.setLayoutY(300);
					gamePane.getChildren().add(tieImage);
					lose = false;
				}
				
			}
		};
		animationTimer.start();
	}
	
	
	private void createStartButton() {  
		GameView_FDButton startButton = new GameView_FDButton("START");
		startButton.setLayoutX(440);
		startButton.setLayoutY(145);
		gamePane.getChildren().add(startButton);
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				inGame = true;
				gameMusic.play();
				if(connect)
					gamePane.getChildren().remove(startButton);
			}
		});
	}
	
	public void countDown() {
		//System.out.println(System.currentTimeMillis() - startTime);
		
		long remainTime = (duration - (System.currentTimeMillis() - startTime))/1000;
		if(remainTime>=0) {
			//System.out.println(remainTime);
		
		int unitDigit = (int)(remainTime%10);
		int tensDigit = (int)(remainTime/10);
		if(tensDigit == 6) { //tensDigit has changed
			timerTensState = 0;
			timerUnitState = 0;
			lastTensDigit = tensDigit;
		}
		else if((tensDigit != lastTensDigit) && (tensDigit != 6)) {
			gamePane.getChildren().remove(tensDigitImage);
			timerTensState = 0;
			lastTensDigit = tensDigit;
		}
		if(unitDigit != lastUnitDigit) {
			gamePane.getChildren().remove(unitDigitImage);
			timerUnitState = 0;
			lastUnitDigit = unitDigit;
		}
		setTensDigit(tensDigit);
		setUnitDigit(unitDigit);
		}
		//System.out.println(tensDigit + "+" + unitDigit);
		//TimerDisplay timerTest = new TimerDisplay();
		//timerTest.getNumber().setLayoutX(750);
		//timerTest.getNumber().setLayoutY(70);
		//gamePane.getChildren().add(timerTest.getNumberZero());
	}
	
	public void setTensDigit(int digit) {
		//System.out.println(digit);
		tensDigitImage = timerTens.getNumberImage(digit);
		if(timerTensState == 0) {
			tensDigitImage.setLayoutX(countdownX);
			tensDigitImage.setLayoutY(countdownY);
			gamePane.getChildren().add(tensDigitImage);
			timerTensState = 1;
		}
		
	}
	
	public void setUnitDigit(int digit) {
		//System.out.println(digit);
		unitDigitImage = timerUnit.getNumberImage(digit);
		if(timerUnitState == 0) {
			unitDigitImage.setLayoutX(countdownX + countdownOffset);
			unitDigitImage.setLayoutY(countdownY);
			gamePane.getChildren().add(unitDigitImage);
			timerUnitState = 1;
		}
		
	}
	
	public void setResult(int type) {
		matchedEmoji = type;
	}
	
	public void run() {
		if(isServer) {
			net.listen(8080);
			connect = true;
			System.out.println("not inGame");
			while(!inGame) {}
			System.out.println("inGame");
			startTime = System.currentTimeMillis();
			net.startGame();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(System.currentTimeMillis() - startTime < duration) {
						action();
					}
					else {
						// stop timer
						this.cancel();
						// stop gameMusic
						gameMusic.stop();
						// send final score
						net.endGame();
						String str = "score\n" + Integer.toString(myScore) + "\n";
						net.send(str);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// end connection
						net.end();
						if(myScore > enemyScore)
							win = true;
						else if(myScore < enemyScore)
							lose = true;
						else tie = true;
						System.out.println("final score: " + myScore + " " + enemyScore);
						// stop rendering
						//animationTimer.stop();
					}
				}
			}, 0, delay);
		}
		else {
			net.connect(ipAddr, 8080);
			while(!inGame) {}
			startTime = System.currentTimeMillis();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(inGame) {
						action();
					}
					else {
						// stop timer
						this.cancel();
						// send final score
						String str = "score\n" + Integer.toString(myScore) + "\n";
						net.send(str);
						try {
							Thread.sleep(350);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(myScore > enemyScore)
							win = true;
						else if(myScore < enemyScore)
							lose = true;
						else tie = true;
						System.out.println("final score: " + myScore + " " + enemyScore);
						// stop rendering
						//animationTimer.stop();
					}
				}
			}, 0, delay);
		}
	}
	
	// generate new emoji and send all existed emojis to client
	public void action() {
		if(net.isServer()) {	// server side code
			// remove emoji if it exceeds boundary + 20
			int i = 0;
			while(i < myEmojiList.size()) {
				if(myEmojiList.get(i).getY() > maxY + myOffsetY + 20) {
					myEmojiList.remove(i);
					continue;
				}
				i++;
			}
			
			// random generate emoji to emojiList and append to myEmojiList
			randomEmojiGen();
			for(i = 0; i < emojiList.size(); ++i) {
				Emoji newEmoji = new Emoji(emojiList.get(i).getX(), emojiList.get(i).getY(), emojiList.get(i).getType());
				newEmoji.setX(newEmoji.getX() + myOffsetX);
				newEmoji.setY(newEmoji.getY() + myOffsetY);
				myEmojiList.add(newEmoji);
			}
			
			// send exist emojiList
			String str = "new\n";
			for(i = 0; i < emojiList.size(); ++i)
				str = str + emojiList.get(i).toString() + "\n";
			net.send(str);
			// send score to enemy
			str = "score\n" + Integer.toString(myScore) + "\n";
			net.send(str);
		}
		else {
			// remove emoji if it exceeds boundary + 20
			int i = 0;
			while(i < myEmojiList.size()) {
				if(myEmojiList.get(i).getY() > maxY + myOffsetY + 20) {
					myEmojiList.remove(i);
					continue;
				}
				i++;
			}
			// send score to enemy
			String str = "score\n" + Integer.toString(myScore) + "\n";
			net.send(str);
		}
	}
	
	public void myEmojiAdd(Emoji e) {
		myEmojiList.add(e);
	}
	
	private void randomEmojiGen() {
		emojiList.clear();
		int num = maxEmojiGen; //rand.nextInt(maxEmojiGen+1)
		if(myEmojiList.size() > 8)
			return;
		ArrayList<Emoji> elist = new ArrayList<Emoji>();
		for(int i = 0; i < num; ++i)
			elist.add(new Emoji(rand.nextInt(maxX+1), 0, rand.nextInt(Face.SURPRISE+1)));
		emojiList = elist;
	}
	
	public void setEmojiList(ArrayList<Emoji> elist) {
		emojiList = elist;
	}
	
	private void setEmoji(Emoji e) { //add emoji to pane and move emoji
		if(e.getStatus() == 2) {	// out
			e.setY(e.getY() + shift);
			return;
		}
		if(e.getStatus() == 0)	// new
		{
			e.getEmojiImage().setLayoutX(e.getX());
			e.getEmojiImage().setLayoutY(e.getY());
			gamePane.getChildren().add(e.getEmojiImage());
			e.setStatus(1);
		}
		else {					// exist
			e.setY(e.getY() + shift);
			e.getEmojiImage().setLayoutY(e.getY());
		}
	}
	
	private boolean emojiOut(Emoji e)
	{
		if(e.getStatus() == 1 && (e.getY() > maxY + myOffsetY || e.getType() == matchedEmoji))
		{
			if(e.getType() == matchedEmoji) myScore += 10;
			gamePane.getChildren().remove(e.getEmojiImage());
			e.setStatus(2);
			return true;
		}
		else return false;
	}
	
	public void setImage(BufferedImage capture) {
		if(isLegal)
			faceImage = capture;
	}
	
	public void setEnemyScore(int score) {
		this.enemyScore = score;
	}
	
	// display faceImage on screen
	private void renderImage() {
		Image image = SwingFXUtils.toFXImage(faceImage, null);
		camera = new ImageView(image);
		camera.setFitHeight(500); //500
		camera.setFitWidth(500); //500
		camera.setLayoutX(20); //20
		camera.setLayoutY(200); //200
		gamePane.getChildren().add(camera);
	}
	
	public void printEmoji() {
		System.out.println("myEmojiList:");
		for(int i = 0; i < myEmojiList.size(); ++i) {
			System.out.println(myEmojiList.get(i).toString());
		}
		System.out.println("emojiList:");
		for(int i = 0; i < emojiList.size(); ++i) {
			System.out.println(emojiList.get(i).toString());
		}
	}
	
	private void createBackground() {
		Image backgroundImage = new Image(getClass().getResource("resources/game_background.jpg").toString(), 1024, 768, false, true);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		gamePane.setBackground(new Background(background));
	}
	
	
	private void displayScore() {
		// display our score
		gamePane.getChildren().remove(myScoreHundred.getNumberImage(myScoreHundred.getDigit()));
		gamePane.getChildren().remove(myScoreTen.getNumberImage(myScoreTen.getDigit()));
		gamePane.getChildren().remove(myScoreUnit.getNumberImage(myScoreUnit.getDigit()));
		int unitDigit = myScore%10;
		int tenDigit = (myScore%100)/10;
		int hundredDigit = (myScore%1000)/100;
		gamePane.getChildren().add(myScoreUnit.getNumberImage(unitDigit));
		gamePane.getChildren().add(myScoreTen.getNumberImage(tenDigit));
		gamePane.getChildren().add(myScoreHundred.getNumberImage(hundredDigit));
		// display enemy score
		gamePane.getChildren().remove(enemyScoreHundred.getNumberImage(enemyScoreHundred.getDigit()));
		gamePane.getChildren().remove(enemyScoreTen.getNumberImage(enemyScoreTen.getDigit()));
		gamePane.getChildren().remove(enemyScoreUnit.getNumberImage(enemyScoreUnit.getDigit()));
		unitDigit = enemyScore%10;
		tenDigit = (enemyScore%100)/10;
		hundredDigit = (enemyScore%1000)/100;
		gamePane.getChildren().add(enemyScoreUnit.getNumberImage(unitDigit));
		gamePane.getChildren().add(enemyScoreTen.getNumberImage(tenDigit));
		gamePane.getChildren().add(enemyScoreHundred.getNumberImage(hundredDigit));
	}
}
