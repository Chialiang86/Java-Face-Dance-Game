package view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class NetModule implements Runnable {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private GameViewManager ui;
	private boolean isServer;
	
	NetModule(GameViewManager ui){
		this.ui = ui;
	}
	
	public boolean isServer() {
		return isServer;
	}
	
	public static String getLocalAddress() { //server call
		String ip = null;
		try (final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 8000);
			ip = socket.getLocalAddress().getHostAddress();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public void connect(String ipAddr, int port) {
		try {
			isServer = false;
			clientSocket = new Socket(ipAddr, port);
			System.out.println("Connected");
			Thread listener = new Thread(this);
			listener.start();
			outputStream = clientSocket.getOutputStream();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listen(int port) {
		try {
			isServer = true;
			serverSocket = new ServerSocket(port);
			System.out.print("Server running at port ");
			System.out.println(port);
			clientSocket = serverSocket.accept();
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
			Thread listener = new Thread(this);
			listener.start();
			serverSocket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void run() {
		try {
			inputStream = clientSocket.getInputStream();
			byte[] type = new byte[Character.BYTES];
			byte[] size = new byte[Integer.BYTES];
			char typeChar;
			while(true) {					// payload format: [typeChar][sizeInt][dataBytes]
				inputStream.read(type);
				typeChar = ByteBuffer.wrap(type).asCharBuffer().get();
				if(typeChar == 'i') {		// image
					inputStream.read(size);
					byte[] imgBytes = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
					inputStream.read(imgBytes);
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
					ImageIO.write(img, "jpg", new File("C:\\Users\\arcti\\Desktop\\test3.jpg"));
				}
				else if(typeChar == 'd') {	// data
					inputStream.read(size);
					byte[] strBytes = new byte[ByteBuffer.wrap(size).asIntBuffer().get()];
					inputStream.read(strBytes);
					//System.out.println(new String(strBytes));
					handleData(new String(strBytes));
				}
				else if(typeChar == 'e') {	// end connection
					break;
				}
			}
			inputStream.close();
			outputStream.close();
			clientSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String str) {
		try {
			byte[] type = ByteBuffer.allocate(Character.BYTES).putChar('d').array();
			byte[] strBytes = str.getBytes();
			byte[] size = ByteBuffer.allocate(Integer.BYTES).putInt(strBytes.length).array();
			outputStream.write(type);
			outputStream.write(size);
			outputStream.write(strBytes);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendImage(BufferedImage img) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			byte[] type = ByteBuffer.allocate(Character.BYTES).putChar('i').array();
			byte[] size = ByteBuffer.allocate(Integer.BYTES).putInt(baos.size()).array();
			outputStream.write(type);
			outputStream.write(size);
			outputStream.write(baos.toByteArray());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startGame() {
		send("start\n");
	}
	
	public void endGame() {
		send("end\n");
		ui.inGame = false;
	}
	
	public void end() {
		try {
			outputStream.write(ByteBuffer.allocate(Character.BYTES).putChar('e').array());
			outputStream.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleData(String str) {
		String[] data = str.split("\n");
		if(data[0].equals("new")) {
			for(int i = 1; i < data.length; ++i) {
				Emoji e = Emoji.parseString(data[i]);
				e.setX(e.getX() + GameViewManager.myOffsetX);
				e.setY(e.getY() + GameViewManager.myOffsetY);
				ui.myEmojiAdd(e);
			}
		}
		else if(data[0].equals("start")) {
			ui.inGame = true;
		}
		else if(data[0].equals("end")) {
			ui.inGame = false;
		}
		else if(data[0].equals("score")) {
			ui.setEnemyScore(Integer.parseInt(data[1]));
		}
	}
}