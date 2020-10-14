package view;

import java.io.File;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
 
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.event.*;

import java.util.*;

public class ImageShow {  
	//private JFrame frame;  
	private Mat mat = null;
	private VideoCapture videoCapture;
	//private JLabel label;
	//private JLabel labelText;
	private Timer captureTimer;
	private Timer execTimer;
	private CascadeClassifier faceDetector;
	private Imgcodecs outDevice;
	private BufferedReader inputBufferedReader;
	private BufferedReader errBufferedReader;
	private String resFromFer;
	private GameViewManager gameViewManager;
	public BufferedImage faceImage;
	
	public ImageShow() throws IOException { 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
		//ScheduledExecutorService scheduledExecutorService;
		VideoCapture videoCapture = new VideoCapture();
		
		videoCapture.open(0);
	    videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 500);
	    videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 500);
	     
	    faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
			
	    /*
		frame = new JFrame();  
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.getContentPane().setLayout(null);  
		
		label = new JLabel();
		label.setLocation(50, 50);
		label.setSize(500, 500);
		label.setBackground(new Color(20, 20, 20));
		frame.add(label);  
		
		labelText = new JLabel();
		labelText.setLocation(600,200);
		labelText.setSize(150,150);
		frame.add(labelText); 
		*/
		
		ImageSave imgProcess = new ImageSave();//��蕭 python(fer.py) ���蕭嚙� + jpg�嚙�
		
		captureTimer = new Timer();
	    captureTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Mat mat1 = new Mat();
					videoCapture.read(mat1); // get image from camera
					if(imgProcess.getMessage().equals("ready")) { // exec if fer.py is ready
						imgProcess.setMat(mat1); // save out.jpg and write "go" signal to fer.py
						imgProcess.SetNone(); // reset msg to none to avoid exec multiple times
					}
					if(gameViewManager != null) {
						gameViewManager.setImage(new MatToBufImg(mat1, ".jpg").getImage());
					}
					
					//initialize(mat1); // set image to frame
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 500, 30);
	    
	    execTimer = new Timer();
	    execTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					//labelText.setText(imgProcess.getResult()); // set detect result to lebel
					if(gameViewManager != null) {
						if(imgProcess.getResult().equals("happy")) 
							gameViewManager.setResult(Face.HAPPY);
						else if(imgProcess.getResult().equals("surprise")) 
							gameViewManager.setResult(Face.SURPRISE);
						else if(imgProcess.getResult().equals("sad")) 
							gameViewManager.setResult(Face.SAD);
						else if(imgProcess.getResult().equals("angry")) 
							gameViewManager.setResult(Face.ANGRY);
						else
							gameViewManager.setResult(Face.NONE);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}  
			}
		}, 1000, 200);
		
		//frame.setVisible(true);
	}  
	
	public void getGameViewManager(GameViewManager g) {
		gameViewManager = g;
	}
	
	/*
	private void initialize(Mat mat) {  
		//label.setIcon(new ImageIcon(new MatToBufImg(mat, ".jpg").getImage()));  
	}  
	
	public Mat detectFace(Mat image) throws Exception
    {
        // 嚙踐祗嚙踐郎嚙踝蕭����蕭����蕭�
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        Rect[] rects = faceDetections.toArray();
        
        if(rects != null && rects.length > 0){
        	for(int i = 0 ; i < rects.length; ++i) {
            	Imgproc.rectangle(image, new Point(rects[i].x-2, rects[i].y-2),
                        new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height),
                        new Scalar(127, 189, 0), 1);
            }
        } 
        return image;
    }
	
	public JFrame getFrame() {
		return frame;
	}
	*/
}
