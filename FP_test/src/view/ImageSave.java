package view;
import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageSave {
	private Process p;
	private Thread iThread;
	private BufferedWriter out;
	private BufferedReader in;
	private String msg;
	private String arg;
	private String result;
	
	public ImageSave() {
		result = "loading...";
		msg = "none";
		
		// fer.py  whilch python3 > /Library/Frameworks/Python.framework/Versions/3.7/bin/python3
		System.out.println(System.getProperty("user.dir"));
		arg = new String("python Fer.py");
		
		try {
			/** 隞乩�瘜�
			 * https://stackoverflow.com/questions/4112470/java-how-to-both-read-and-write-to-from-process-thru-pipe-stdin-stdout
			 */
			p = Runtime.getRuntime().exec(arg);
			//result = "loading cnn model...";
			in = new BufferedReader(new InputStreamReader(p.getInputStream())); // get input from fer.py
			out  = new BufferedWriter(new OutputStreamWriter(p.getOutputStream())); // set output to fer.py
			new Thread() {
				public void run() {
					while(true){
						try {
							//System.out.println("test1");
							//out.write("hello\n");
							//out.flush();
							//System.out.println("test2");
							//while((msg=in.readLine())==null);
							msg = in.readLine(); // get output (not busy waiting)
							//System.out.println("test3");
							
							if(!msg.equals("ready"))
								result = msg;
							System.out.println(result);
							//System.out.println(in.readLine());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				
			}.start();
		
		    
		    
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	* if get "ready" from fer.py do this function
	* function : save out.jpg and write "go" message to fer.py
	*/  
	public void setMat(Mat src) {
		iThread =  new Thread(new Runnable() {
			@Override
			public void run(){
				try {
					Imgcodecs.imwrite("out.jpg", src);
					out.write("go\n");
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		try {
			iThread.start(); // asyn run, save out.jpg and write "go" message to fer.py
			iThread.join(); // avoid process conflict
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMessage() {
		return msg;
	}
	
	public String getResult() {
		return result;
	}
	
	public void SetNone() {
		msg = "none";
	}
	
	private boolean isFe(String s) {
		return s.equals("angry") || s.equals("disgust") || s.equals("fear") || s.equals("happy") || 
			s.equals("neutral") || s.equals("sad") || s.equals("surprise");
	}
}
